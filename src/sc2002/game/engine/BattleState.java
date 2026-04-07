package sc2002.game.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.items.Item;
import sc2002.game.level.EnemyType;
import sc2002.game.level.LevelConfig;
import sc2002.game.level.SpawnWave;

public final class BattleState {
    private final LevelConfig levelConfig;
    private final PlayerCharacter player;
    private final List<Enemy> enemies;
    private final List<Item> playerItems;
    private int roundNumber;
    private boolean backupSpawned;

    public BattleState(LevelConfig levelConfig, PlayerCharacter player, List<Enemy> enemies, List<Item> playerItems) {
        this.levelConfig = levelConfig;
        this.player = player;
        this.enemies = new ArrayList<>(enemies);
        this.playerItems = new ArrayList<>(playerItems);
        this.roundNumber = 1;
    }

    public LevelConfig levelConfig() {
        return levelConfig;
    }

    public PlayerCharacter player() {
        return player;
    }

    public List<Enemy> enemies() {
        return List.copyOf(enemies);
    }

    public List<Item> playerItems() {
        return List.copyOf(playerItems);
    }

    public void consumeItem(Item item) {
        playerItems.remove(item);
    }

    public int roundNumber() {
        return roundNumber;
    }

    public void incrementRound() {
        roundNumber++;
    }

    public boolean isPlayerDefeated() {
        return !player.isAlive();
    }

    public boolean areAllEnemiesDefeated() {
        return aliveEnemies().isEmpty();
    }

    public List<Enemy> aliveEnemies() {
        return enemies.stream().filter(Enemy::isAlive).toList();
    }

    public boolean maybeSpawnBackupWave(EnemyFactory factory) {
        if (backupSpawned || !areAllEnemiesDefeated() || levelConfig.backupWave() == null) {
            return false;
        }
        SpawnWave wave = levelConfig.backupWave();
        for (Map.Entry<EnemyType, Integer> entry : wave.entries().entrySet()) {
            EnemyType type = entry.getKey();
            for (int i = 1; i <= entry.getValue(); i++) {
                String id = "backup-" + type.name().toLowerCase() + "-" + i;
                enemies.add(factory.create(type, id));
            }
        }
        backupSpawned = true;
        return true;
    }
}
