package sc2002.game.level;

import java.util.EnumMap;
import java.util.Map;

public final class SpawnWave {
    private final Map<EnemyType, Integer> entries;

    public SpawnWave(Map<EnemyType, Integer> entries) {
        this.entries = new EnumMap<>(entries);
    }

    public Map<EnemyType, Integer> entries() {
        return Map.copyOf(entries);
    }
}
