package sc2002.game.domain.items;

import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.engine.BattleState;

public final class PotionItem implements Item {
    @Override
    public String name() {
        return "Potion";
    }

    @Override
    public void use(PlayerCharacter player, BattleState state) {
        player.heal(100);
    }
        
    @Override
    public boolean use(PlayerCharacter player, BattleState state, ItemUseSupport support) {
    	return player.heal(100);
    }
}
