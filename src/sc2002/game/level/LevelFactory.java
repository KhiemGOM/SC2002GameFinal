package sc2002.game.level;

import java.util.Map;

public final class LevelFactory {
    private LevelFactory() {
    }

    public static LevelConfig easy() {
        SpawnWave initial = new SpawnWave(Map.of(EnemyType.GOBLIN, 3));
        return new LevelConfig(1, "Easy", initial, null);
    }

    public static LevelConfig medium() {
        SpawnWave initial = new SpawnWave(Map.of(EnemyType.GOBLIN, 1, EnemyType.WOLF, 1));
        SpawnWave backup = new SpawnWave(Map.of(EnemyType.WOLF, 2));
        return new LevelConfig(2, "Medium", initial, backup);
    }

    public static LevelConfig hard() {
        SpawnWave initial = new SpawnWave(Map.of(EnemyType.GOBLIN, 2));
        SpawnWave backup = new SpawnWave(Map.of(EnemyType.GOBLIN, 1, EnemyType.WOLF, 2));
        return new LevelConfig(3, "Hard", initial, backup);
    }
}
