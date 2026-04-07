package sc2002.game.domain.items;

import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.engine.BattleState;

public final class PowerStoneItem implements Item {
    @Override
    public String name() {
        return "Power Stone";
    }

    @Override
    public void use(PlayerCharacter player, BattleState state) {
        // no-op: special behavior is implemented via use(..., support)
    }

    @Override
    public boolean use(PlayerCharacter player, BattleState state, ItemUseSupport support) {
        return support.castSpecialSkill(player, true);
    }
}
