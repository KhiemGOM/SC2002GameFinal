package sc2002.game.domain.actions;

import sc2002.game.domain.combat.Combatant;
import sc2002.game.engine.BattleEngine;

public final class BasicAttackAction implements CombatAction {
    private final BattleEngine engine;

    public BasicAttackAction(BattleEngine engine) {
        this.engine = engine;
    }

    @Override
    public ActionType type() {
        return ActionType.BASIC_ATTACK;
    }

    @Override
    public String label() {
        return "Basic Attack";
    }

    @Override
    public boolean canExecute(ActionContext context) {
        Combatant target = context.primaryTarget();
        return target != null && context.actor().isAlive() && target.isAlive();
    }

    @Override
    public void execute(ActionContext context) {
        engine.applyBasicAttack(context.actor(), context.primaryTarget());
    }
}
