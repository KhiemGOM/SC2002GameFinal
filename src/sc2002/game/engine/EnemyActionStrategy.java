package sc2002.game.engine;

import sc2002.game.domain.combat.enemy.Enemy;

public interface EnemyActionStrategy {
    void executeTurn(BattleState state, Enemy enemy, EnemyActionSupport support);
}
