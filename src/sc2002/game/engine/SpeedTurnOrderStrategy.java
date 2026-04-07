package sc2002.game.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import sc2002.game.domain.combat.Combatant;

public final class SpeedTurnOrderStrategy implements TurnOrderStrategy {
    @Override
    public List<Combatant> orderForRound(List<Combatant> aliveCombatants) {
        List<Combatant> ordered = new ArrayList<>(aliveCombatants);
        ordered.sort(Comparator.comparingInt(Combatant::speed).reversed());
        return ordered;
    }
}
