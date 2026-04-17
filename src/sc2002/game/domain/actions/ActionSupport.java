package sc2002.game.domain.actions;

import sc2002.game.domain.combat.Combatant;
import sc2002.game.domain.combat.player.PlayerCharacter;

public interface ActionSupport {
    int applyBasicAttack(Combatant attacker, Combatant defender);

    boolean executeSpecialSkill(PlayerCharacter player, boolean freeCast);
}
