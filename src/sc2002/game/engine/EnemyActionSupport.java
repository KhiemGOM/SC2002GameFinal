package sc2002.game.engine;

import sc2002.game.domain.combat.Combatant;
import sc2002.game.domain.combat.player.PlayerCharacter;

public interface EnemyActionSupport {
    PlayerCharacter player();

    int applyBasicAttack(Combatant attacker, Combatant defender);

    void showInfo(String message);
}
