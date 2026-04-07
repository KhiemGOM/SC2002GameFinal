package sc2002.game.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sc2002.game.domain.combat.Combatant;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.combat.player.PlayerSkillBridge;
import sc2002.game.domain.items.Item;
import sc2002.game.domain.items.ItemUseSupport;
import sc2002.game.domain.status.DefendBuffEffect;
import sc2002.game.level.EnemyType;
import sc2002.game.level.LevelConfig;
import sc2002.game.ui.BattleUI;

public final class BattleEngine {
    private final TurnOrderStrategy turnOrderStrategy;
    private final DamageCalculator damageCalculator;
    private final BattleUI ui;
    private final EnemyFactory enemyFactory;
    private final List<PlayerTurnAction> playerTurnActions;

    public BattleEngine(TurnOrderStrategy turnOrderStrategy, DamageCalculator damageCalculator, BattleUI ui) {
        this.turnOrderStrategy = turnOrderStrategy;
        this.damageCalculator = damageCalculator;
        this.ui = ui;
        this.enemyFactory = new EnemyFactory();
        this.playerTurnActions = buildPlayerTurnActions();
    }

    public BattleState initializeLevel(LevelConfig level, PlayerCharacter player, List<Item> selectedItems) {
        List<Enemy> initialEnemies = instantiateWave(level.initialWave().entries(), "initial");
        BattleState state = new BattleState(level, player, initialEnemies, selectedItems);
        ui.showBattleInitialized(state);
        return state;
    }

    public void runInteractiveLevel(LevelConfig level, PlayerCharacter player, List<Item> selectedItems) {
        BattleState state = initializeLevel(level, player, selectedItems);
        while (!state.isPlayerDefeated()) {
            runOneRound(state);
            if (!state.areAllEnemiesDefeated()) {
                continue;
            }
            if (state.maybeSpawnBackupWave(enemyFactory)) {
                ui.showInfo("Backup wave arrives!");
                continue;
            }
            break;
        }
        ui.showBattleEnded(!state.isPlayerDefeated(), state);
    }

    public int applyBasicAttack(Combatant attacker, Combatant defender) {
        int damage = damageCalculator.calculate(attacker, defender);
        defender.applyDamage(damage);
        return damage;
    }

    private void runOneRound(BattleState state) {
        List<Combatant> turnOrder = buildAliveTurnOrder(state);
        ui.showRoundStart(state, turnOrder);
        tickStatusesAtRoundStart(state);
        for (Combatant actor : turnOrder) {
            if (!actor.isAlive()) {
                continue;
            }

            if (actor == state.player()) {
                ui.showBattleState(state);
            }

            actor.statuses().onTurnStart(actor);
            if (actor.statuses().blocksAction()) {
                ui.showInfo(actor.name() + " is stunned and skips turn.");
                endActorTurn(actor);
                continue;
            }

            if (actor == state.player()) {
                handlePlayerTurn(state, state.player());
            } else {
                handleEnemyTurn(state, (Enemy) actor);
            }

            endActorTurn(actor);
            if (state.isPlayerDefeated() || state.areAllEnemiesDefeated()) {
                break;
            }
        }
        ui.showBattleState(state);
        state.incrementRound();
    }

    private void handlePlayerTurn(BattleState state, PlayerCharacter player) {
        while (true) {
            List<String> actionLabels = playerTurnActions.stream()
                    .map(action -> action.label(state, player))
                    .toList();
            int option = ui.promptPlayerAction(state, actionLabels);
            if (option < 1 || option > playerTurnActions.size()) {
                ui.showInfo("Invalid action option.");
                continue;
            }
            PlayerTurnAction action = playerTurnActions.get(option - 1);
            if (action.execute(state, player)) {
                return;
            }
        }
    }

    private void handleEnemyTurn(BattleState state, Enemy enemy) {
        if (!state.player().isAlive()) {
            return;
        }
        int damage = applyBasicAttack(enemy, state.player());
        String deathNote = state.player().isAlive() ? "" : " " + state.player().name() + " dies.";
        ui.showInfo(enemy.displayName() + " attacks " + state.player().name() + " for " + damage + " damage." + deathNote);
    }

    private boolean executeSpecialSkill(BattleState state, PlayerCharacter player, boolean freeCast) {
        PlayerSkillBridge bridge = new EnginePlayerSkillBridge();
        if (!player.performSpecialSkill(state, bridge)) {
            return false;
        }
        if (!freeCast) {
            player.specialSkillCooldown().startThreeTurnCooldown();
        }
        return true;
    }

    private void endActorTurn(Combatant actor) {
        actor.statuses().onTurnEnd(actor);
        actor.specialSkillCooldown().tickOnOwnerTurn();
    }

    private void tickStatusesAtRoundStart(BattleState state) {
        state.player().statuses().onRoundEnd();
        for (Enemy enemy : state.aliveEnemies()) {
            enemy.statuses().onRoundEnd();
        }
    }

    private List<Combatant> buildAliveTurnOrder(BattleState state) {
        List<Combatant> alive = new ArrayList<>();
        if (state.player().isAlive()) {
            alive.add(state.player());
        }
        alive.addAll(state.aliveEnemies());
        return turnOrderStrategy.orderForRound(alive);
    }

    private List<Enemy> instantiateWave(Map<EnemyType, Integer> entries, String prefix) {
        List<Enemy> enemies = new ArrayList<>();
        for (Map.Entry<EnemyType, Integer> entry : entries.entrySet()) {
            EnemyType type = entry.getKey();
            int count = entry.getValue();
            for (int i = 1; i <= count; i++) {
                String id = prefix + "-" + type.name().toLowerCase() + "-" + i;
                enemies.add(enemyFactory.create(type, id));
            }
        }
        return enemies;
    }

    private List<PlayerTurnAction> buildPlayerTurnActions() {
        return List.of(
                new PlayerTurnAction() {
                    @Override
                    public String label(BattleState state, PlayerCharacter player) {
                        return "Basic Attack";
                    }

                    @Override
                    public boolean execute(BattleState state, PlayerCharacter player) {
                        Enemy target = ui.promptEnemyTarget(state.aliveEnemies());
                        if (target == null) {
                            return false;
                        }
                        int damage = applyBasicAttack(player, target);
                        String deathNote = target.isAlive() ? "" : " " + target.displayName() + " dies.";
                        ui.showInfo(player.name() + " hits " + target.displayName() + " for " + damage + " damage." + deathNote);
                        return true;
                    }
                },
                new PlayerTurnAction() {
                    @Override
                    public String label(BattleState state, PlayerCharacter player) {
                        return "Defend";
                    }

                    @Override
                    public boolean execute(BattleState state, PlayerCharacter player) {
                        player.statuses().add(new DefendBuffEffect(), player);
                        ui.showInfo(player.name() + " uses Defend (+10 damage reduction for 2 turns).");
                        return true;
                    }
                },
                new PlayerTurnAction() {
                    @Override
                    public String label(BattleState state, PlayerCharacter player) {
                        return "Use Item";
                    }

                    @Override
                    public boolean execute(BattleState state, PlayerCharacter player) {
                        Item item = ui.promptItemChoice(state.playerItems());
                        if (item == null) {
                            return false;
                        }
                        boolean isPowerStone = "Power Stone".equals(item.name());
                        if (isPowerStone) {
                            ui.showInfo(player.name() + " uses Power Stone. Effect: Triggered " + player.specialSkillName() + ".");
                        }
                        int hpBefore = player.currentHp();
                        ItemUseSupport support = (owner, freeCast) -> executeSpecialSkill(state, owner, freeCast);
                        if (!item.use(player, state, support)) {
                            return false;
                        }
                        int healed = Math.max(0, player.currentHp() - hpBefore);
                        state.consumeItem(item);
                        if (!isPowerStone) {
                            ui.showInfo(player.name() + " uses " + item.name() + ". " + describeItemOutcome(item, player, healed));
                        }
                        return true;
                    }
                },
                new PlayerTurnAction() {
                    @Override
                    public String label(BattleState state, PlayerCharacter player) {
                        if (player.specialSkillCooldown().isReady()) {
                            return "Special: " + player.specialSkillName();
                        }
                        int cd = player.specialSkillCooldown().remainingTurns();
                        return "Special: [EXHAUSTED | CD " + cd + "]";
                    }

                    @Override
                    public boolean execute(BattleState state, PlayerCharacter player) {
                        if (!player.specialSkillCooldown().isReady()) {
                            ui.showInfo("Special skill cooldown: " + player.specialSkillCooldown().remainingTurns());
                            return false;
                        }
                        return executeSpecialSkill(state, player, false);
                    }
                }
        );
    }

    private interface PlayerTurnAction {
        String label(BattleState state, PlayerCharacter player);

        boolean execute(BattleState state, PlayerCharacter player);
    }

    private String describeItemOutcome(Item item, PlayerCharacter player, int healed) {
        return switch (item.name()) {
            case "Potion" -> "Effect: Healed " + healed + " HP.";
            case "Smoke Bomb" -> "Effect: Invulnerable for 2 turns.";
            case "Power Stone" -> "Effect: Triggered " + player.specialSkillName() + ".";
            default -> "Effect applied.";
        };
    }

    private final class EnginePlayerSkillBridge implements PlayerSkillBridge {
        @Override
        public Enemy promptEnemyTarget(List<Enemy> aliveEnemies) {
            return ui.promptEnemyTarget(aliveEnemies);
        }

        @Override
        public int applyBasicAttack(Combatant attacker, Combatant defender) {
            return BattleEngine.this.applyBasicAttack(attacker, defender);
        }

        @Override
        public void showInfo(String message) {
            ui.showInfo(message);
        }
    }
}
