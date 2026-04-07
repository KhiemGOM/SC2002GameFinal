package sc2002.game.domain.items;

import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.engine.BattleState;

public interface Item {
    String name();

    void use(PlayerCharacter player, BattleState state);

    default boolean use(PlayerCharacter player, BattleState state, ItemUseSupport support) {
        use(player, state);
        return true;
    }
}
