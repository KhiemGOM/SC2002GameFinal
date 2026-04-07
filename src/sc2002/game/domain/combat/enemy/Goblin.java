package sc2002.game.domain.combat.enemy;

import java.util.List;
import sc2002.game.domain.stats.Stats;

public final class Goblin extends Enemy {
    private static final Stats GOBLIN_STATS = new Stats(55, 35, 15, 25);

    public Goblin(String id) {
        super(id, "Goblin", GOBLIN_STATS);
    }

    @Override
    public List<String> asciiArt() {
        return List.of(
                " ,_._,",
                "/_o_o_\\"
        );
    }
}
