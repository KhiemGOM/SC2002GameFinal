package sc2002.game.domain.actions;

public final class SpecialSkillAction implements CombatAction {
    @Override
    public ActionType type() {
        return ActionType.SPECIAL_SKILL;
    }

    @Override
    public String label() {
        return "Special Skill";
    }

    @Override
    public boolean canExecute(ActionContext context) {
        return context.actor().specialSkillCooldown().isReady();
    }

    @Override
    public void execute(ActionContext context) {
        // Level-1 abstraction placeholder:
        // concrete Warrior/Wizard skill execution flow should be plugged here.
        context.actor().specialSkillCooldown().startThreeTurnCooldown();
    }
}
