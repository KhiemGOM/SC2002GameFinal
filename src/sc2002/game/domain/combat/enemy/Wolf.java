package sc2002.game.domain.combat.enemy;

import java.util.List;
import sc2002.game.domain.stats.Stats;

public final class Wolf extends Enemy {
    private static final Stats WOLF_STATS = new Stats(40, 45, 5, 35);

    public Wolf(String id) {
        super(id, "Wolf", WOLF_STATS);
    }

    @Override
    public List<String> asciiArt() {
        return List.of(
                " /\\_/\\",
                "( o.o )"
        );
    }
}
