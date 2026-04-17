package sc2002.game.domain.actions;

public interface CombatAction {
    ActionType type();

    String label(ActionContext context);

    boolean canExecute(ActionContext context);

    boolean execute(ActionContext context);
}
