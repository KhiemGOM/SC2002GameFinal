package sc2002.game.level;

import java.util.function.Function;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.combat.enemy.Goblin;
import sc2002.game.domain.combat.enemy.Wolf;

public enum EnemyType {
    GOBLIN(Goblin::new),
    WOLF(Wolf::new);

    private final Function<String, Enemy> constructor;

    EnemyType(Function<String, Enemy> constructor) {
        this.constructor = constructor;
    }

    public Enemy create(String id) {
        return constructor.apply(id);
    }
}
