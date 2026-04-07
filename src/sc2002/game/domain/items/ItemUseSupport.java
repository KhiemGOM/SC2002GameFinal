package sc2002.game.domain.items;

import sc2002.game.domain.combat.player.PlayerCharacter;

public interface ItemUseSupport {
    boolean castSpecialSkill(PlayerCharacter player, boolean freeCast);

    static ItemUseSupport noop() {
        return (player, freeCast) -> false;
    }
}
