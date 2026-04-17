package sc2002.game.domain.actions;

import sc2002.game.domain.combat.Combatant;
import sc2002.game.engine.BattleState;
import sc2002.game.ui.BattleUI;

public final class ActionContext {
    private final Combatant actor;
    private final BattleState state;
    private final BattleUI ui;
    private final ActionSupport support;

    public ActionContext(Combatant actor, BattleState state, BattleUI ui, ActionSupport support) {
        this.actor = actor;
        this.state = state;
        this.ui = ui;
        this.support = support;
    }

    public Combatant actor() {
        return actor;
    }

    public BattleState state() {
        return state;
    }

    public BattleUI ui() {
        return ui;
    }

    public ActionSupport support() {
        return support;
    }
}
