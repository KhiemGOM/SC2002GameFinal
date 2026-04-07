package sc2002.game.engine;

import java.util.List;
import sc2002.game.domain.combat.Combatant;

public interface TurnOrderStrategy {
    List<Combatant> orderForRound(List<Combatant> aliveCombatants);
}
