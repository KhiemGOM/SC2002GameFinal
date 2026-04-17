package sc2002.game.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sc2002.game.domain.actions.ActionContext;
import sc2002.game.domain.actions.ActionSupport;
import sc2002.game.domain.actions.BasicAttackAction;
import sc2002.game.domain.actions.CombatAction;
import sc2002.game.domain.actions.DefendAction;
import sc2002.game.domain.actions.SpecialSkillAction;
import sc2002.game.domain.actions.UseItemAction;
import sc2002.game.domain.combat.Combatant;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.combat.player.PlayerSkillBridge;
import sc2002.game.domain.items.Item;
import sc2002.game.level.EnemyType;
import sc2002.game.level.LevelConfig;
import sc2002.game.ui.BattleUI;
import sc2002.game.ui.PostBattleOption;

public final class BattleEngine {
    private final TurnOrderStrategy turnOrderStrategy;
    private final DamageCalculator damageCalculator;
    private final BattleUI ui;
    private final EnemyFactory enemyFactory;
    private final EnemyActionStrategy enemyActionStrategy;
    private final List<CombatAction> playerTurnActions;

    public BattleEngine(TurnOrderStrategy turnOrderStrategy, DamageCalculator damageCalculator, BattleUI ui) {
        this(
                turnOrderStrategy,
                damageCalculator,
                ui,
                new BasicAttackEnemyStrategy(),
                defaultPlayerTurnActions()
        );
    }

    public BattleEngine(
            TurnOrderStrategy turnOrderStrategy,
            DamageCalculator damageCalculator,
            BattleUI ui,
            EnemyActionStrategy enemyActionStrategy,
            List<CombatAction> playerTurnActions
    ) {
        this.turnOrderStrategy = turnOrderStrategy;
        this.damageCalculator = damageCalculator;
        this.ui = ui;
        this.enemyFactory = new EnemyFactory();
        this.enemyActionStrategy = enemyActionStrategy;
        this.playerTurnActions = List.copyOf(playerTurnActions);
    }

    public BattleState initializeLevel(LevelConfig level, PlayerCharacter player, List<Item> selectedItems) {
        List<Enemy> initialEnemies = instantiateWave(level.initialWave().entries(), "initial");
        BattleState state = new BattleState(level, player, initialEnemies, selectedItems);
        ui.showBattleInitialized(state);
        return state;
    }

    public PostBattleOption runInteractiveLevel(LevelConfig level, PlayerCharacter player, List<Item> selectedItems) {
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
        return ui.showBattleEnded(!state.isPlayerDefeated(), state);
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
        ActionSupport support = new EngineActionSupport(state);
        while (true) {
            ActionContext context = new ActionContext(player, state, ui, support);
            List<String> actionLabels = playerTurnActions.stream()
                    .map(action -> action.label(context))
                    .toList();
            int option = ui.promptPlayerAction(state, actionLabels);
            if (option < 1 || option > playerTurnActions.size()) {
                ui.showInfo("Invalid action option.");
                continue;
            }
            CombatAction action = playerTurnActions.get(option - 1);
            if (!action.canExecute(context)) {
                ui.showInfo("Action is currently unavailable.");
                continue;
            }
            if (action.execute(context)) {
                return;
            }
        }
    }

    private void handleEnemyTurn(BattleState state, Enemy enemy) {
        enemyActionStrategy.executeTurn(state, enemy, new EngineEnemyActionSupport(state));
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
        state.player().statuses().onRoundEnd(state.player());
        for (Enemy enemy : state.aliveEnemies()) {
            enemy.statuses().onRoundEnd(enemy);
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

    private static List<CombatAction> defaultPlayerTurnActions() {
        return List.of(
                new BasicAttackAction(),
                new DefendAction(),
                new UseItemAction(),
                new SpecialSkillAction()
        );
    }

    private final class EngineActionSupport implements ActionSupport {
        private final BattleState state;

        private EngineActionSupport(BattleState state) {
            this.state = state;
        }

        @Override
        public int applyBasicAttack(Combatant attacker, Combatant defender) {
            return BattleEngine.this.applyBasicAttack(attacker, defender);
        }

        @Override
        public boolean executeSpecialSkill(PlayerCharacter player, boolean freeCast) {
            return BattleEngine.this.executeSpecialSkill(state, player, freeCast);
        }
    }

    private final class EngineEnemyActionSupport implements EnemyActionSupport {
        private final BattleState state;

        private EngineEnemyActionSupport(BattleState state) {
            this.state = state;
        }

        @Override
        public PlayerCharacter player() {
            return state.player();
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
