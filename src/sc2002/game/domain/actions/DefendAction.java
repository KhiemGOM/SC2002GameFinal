package sc2002.game.domain.actions;

import sc2002.game.domain.status.DefendBuffEffect;

public final class DefendAction implements CombatAction {
    @Override
    public ActionType type() {
        return ActionType.DEFEND;
    }

    @Override
    public String label(ActionContext context) {
        return "Defend";
    }

    @Override
    public boolean canExecute(ActionContext context) {
        return context.actor().isAlive();
    }

    @Override
    public boolean execute(ActionContext context) {
        context.actor().statuses().add(new DefendBuffEffect(), context.actor());
        context.ui().showInfo(context.actor().name() + " uses Defend (+10 DEF for current and next round).");
        return true;
    }
}
