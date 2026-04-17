package sc2002.game.domain.actions;

import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.items.Item;
import sc2002.game.domain.items.ItemUseSupport;

public final class UseItemAction implements CombatAction {
    @Override
    public ActionType type() {
        return ActionType.USE_ITEM;
    }

    @Override
    public String label(ActionContext context) {
        return "Use Item";
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

        Item item = context.ui().promptItemChoice(context.state().playerItems());
        if (item == null) {
            return false;
        }

        boolean isPowerStone = "Power Stone".equals(item.name());
        if (isPowerStone) {
            context.ui().showInfo(player.name() + " uses Power Stone. Effect: Triggered " + player.specialSkillName() + ".");
        }

        int hpBefore = player.currentHp();
        ItemUseSupport support = (owner, freeCast) -> context.support().executeSpecialSkill(owner, freeCast);
        if (!item.use(player, context.state(), support)) {
            return false;
        }

        int healed = Math.max(0, player.currentHp() - hpBefore);
        context.state().consumeItem(item);
        if (!isPowerStone) {
            context.ui().showInfo(player.name() + " uses " + item.name() + ". " + describeItemOutcome(item, player, healed));
        }
        return true;
    }

    private String describeItemOutcome(Item item, PlayerCharacter player, int healed) {
        return switch (item.name()) {
            case "Potion" -> "Effect: Healed " + healed + " HP.";
            case "Smoke Bomb" -> "Effect: Invulnerable for 2 turns.";
            case "Power Stone" -> "Effect: Triggered " + player.specialSkillName() + ".";
            default -> "Effect applied.";
        };
    }
}
