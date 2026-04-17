package sc2002.game.engine;

import sc2002.game.domain.combat.enemy.Enemy;

public final class BasicAttackEnemyStrategy implements EnemyActionStrategy {
    @Override
    public void executeTurn(BattleState state, Enemy enemy, EnemyActionSupport support) {
        if (!support.player().isAlive()) {
            return;
        }
        int damage = support.applyBasicAttack(enemy, support.player());
        String deathNote = support.player().isAlive() ? "" : " " + support.player().name() + " dies.";
        support.showInfo(enemy.displayName() + " attacks " + support.player().name() + " for " + damage + " damage." + deathNote);
    }


}
