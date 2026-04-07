package sc2002.game.domain.actions;

import java.util.List;
import sc2002.game.domain.combat.Combatant;
import sc2002.game.engine.BattleState;

public final class ActionContext {
    private final Combatant actor;
    private final Combatant primaryTarget;
    private final List<Combatant> allTargets;
    private final BattleState state;

    public ActionContext(
            Combatant actor,
            Combatant primaryTarget,
            List<Combatant> allTargets,
            BattleState state
    ) {
        this.actor = actor;
        this.primaryTarget = primaryTarget;
        this.allTargets = allTargets;
        this.state = state;
    }

    public Combatant actor() {
        return actor;
    }

    public Combatant primaryTarget() {
        return primaryTarget;
    }

    public List<Combatant> allTargets() {
        return allTargets;
    }

    public BattleState state() {
        return state;
    }
}
