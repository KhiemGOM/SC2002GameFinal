package sc2002.game.domain.status;

import sc2002.game.domain.combat.Combatant;

public abstract class TimedStatusEffect implements StatusEffect {
    public static final int INFINITE_DURATION = -1;
    private int remainingTurns;

    protected TimedStatusEffect(int durationTurns) {
        this.remainingTurns = durationTurns;
    }

    @Override
    public boolean blocksAction() {
        return false;
    }

    @Override
    public int modifyIncomingDamage(Combatant owner, Combatant attacker, int currentDamage) {
        return currentDamage;
    }

    @Override
    public void onApply(Combatant owner) {
        // no-op by default
    }

    @Override
    public void onTurnStart(Combatant owner) {
        // no-op by default
    }

    @Override
    public void onTurnEnd(Combatant owner) {
        // no-op by default
    }

    @Override
    public void tick() {
        if (remainingTurns != INFINITE_DURATION) {
            remainingTurns--;
        }
    }

    @Override
    public boolean isExpired() {
        if (remainingTurns == INFINITE_DURATION) {
            return false;
        }
        return remainingTurns <= 0;
    }

    public int remainingTurns() {
        return remainingTurns;
    }
}
