package sc2002.game.engine;

import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.level.EnemyType;

public final class EnemyFactory {
    public Enemy create(EnemyType type, String id) {
        return type.create(id);
    }
}
