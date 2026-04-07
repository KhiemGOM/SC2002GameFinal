package sc2002.game.domain.actions;

public interface CombatAction {
    ActionType type();

    String label();

    boolean canExecute(ActionContext context);

    void execute(ActionContext context);
}
