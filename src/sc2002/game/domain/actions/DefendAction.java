package sc2002.game.domain.actions;

import sc2002.game.domain.status.DefendBuffEffect;

public final class DefendAction implements CombatAction {
    @Override
    public ActionType type() {
        return ActionType.DEFEND;
    }

    @Override
    public String label() {
        return "Defend";
    }

    @Override
    public boolean canExecute(ActionContext context) {
        return context.actor().isAlive();
    }

    @Override
    public void execute(ActionContext context) {
        context.actor().statuses().add(new DefendBuffEffect(), context.actor());
    }
}
