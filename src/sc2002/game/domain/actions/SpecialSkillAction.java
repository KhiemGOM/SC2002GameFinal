package sc2002.game.domain.actions;

import sc2002.game.domain.combat.player.PlayerCharacter;

public final class SpecialSkillAction implements CombatAction {
    @Override
    public ActionType type() {
        return ActionType.SPECIAL_SKILL;
    }

    @Override
    public String label(ActionContext context) {
        if (!(context.actor() instanceof PlayerCharacter player)) {
            return "Special Skill";
        }
        if (player.specialSkillCooldown().isReady()) {
            return "Special: " + player.specialSkillName();
        }
        int cooldown = player.specialSkillCooldown().remainingTurns();
        return "Special: [EXHAUSTED | CD " + cooldown + "]";
    }

    @Override
    public boolean canExecute(ActionContext context) {
        return context.actor() instanceof PlayerCharacter;
    }

    @Override
    public boolean execute(ActionContext context) {
        if (!(context.actor() instanceof PlayerCharacter player)) {
            return false;
        }
        if (!player.specialSkillCooldown().isReady()) {
            context.ui().showInfo("Special skill cooldown: " + player.specialSkillCooldown().remainingTurns());
            return false;
        }
        return context.support().executeSpecialSkill(player, false);
    }
}
