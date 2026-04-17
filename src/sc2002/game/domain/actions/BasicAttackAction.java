package sc2002.game.domain.actions;

import sc2002.game.domain.combat.enemy.Enemy;

public final class BasicAttackAction implements CombatAction {
    @Override
    public ActionType type() {
        return ActionType.BASIC_ATTACK;
    }

    @Override
    public String label(ActionContext context) {
        return "Basic Attack";
    }

    @Override
    public boolean canExecute(ActionContext context) {
        return context.actor().isAlive();
    }

    @Override
    public boolean execute(ActionContext context) {
        Enemy target = context.ui().promptEnemyTarget(context.state().aliveEnemies());
        if (target == null) {
            return false;
        }
        int damage = context.support().applyBasicAttack(context.actor(), target);
        String deathNote = target.isAlive() ? "" : " " + target.displayName() + " dies.";
        context.ui().showInfo(context.actor().name() + " hits " + target.displayName() + " for " + damage + " damage." + deathNote);
        return true;
    }
}
