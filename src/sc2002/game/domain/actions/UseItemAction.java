package sc2002.game.domain.actions;

import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.items.Item;

public final class UseItemAction implements CombatAction {
    private final Item item;

    public UseItemAction(Item item) {
        this.item = item;
    }

    @Override
    public ActionType type() {
        return ActionType.USE_ITEM;
    }

    @Override
    public String label() {
        return "Use Item (" + item.name() + ")";
    }

    @Override
    public boolean canExecute(ActionContext context) {
        return context.actor() instanceof PlayerCharacter;
    }

    @Override
    public void execute(ActionContext context) {
        PlayerCharacter player = (PlayerCharacter) context.actor();
        item.use(player, context.state());
        context.state().consumeItem(item);
    }
}
