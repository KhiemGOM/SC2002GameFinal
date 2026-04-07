package sc2002.game.level;

public final class LevelConfig {
    private final int levelNumber;
    private final String difficulty;
    private final SpawnWave initialWave;
    private final SpawnWave backupWave;

    public LevelConfig(int levelNumber, String difficulty, SpawnWave initialWave, SpawnWave backupWave) {
        this.levelNumber = levelNumber;
        this.difficulty = difficulty;
        this.initialWave = initialWave;
        this.backupWave = backupWave;
    }

    public int levelNumber() {
        return levelNumber;
    }

    public String difficulty() {
        return difficulty;
    }

    public SpawnWave initialWave() {
        return initialWave;
    }

    public SpawnWave backupWave() {
        return backupWave;
    }
}
