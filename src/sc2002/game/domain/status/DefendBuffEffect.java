package sc2002.game.domain.status;

import sc2002.game.domain.combat.Combatant;

public final class DefendBuffEffect extends TimedStatusEffect {
    public DefendBuffEffect() {
        super(2);
    }

    @Override
    public String key() {
        return "DEFEND_BUFF";
    }

    @Override
    public String displayName() {
        return "Defend Buff";
    }

    @Override
    public int modifyIncomingDamage(Combatant owner, Combatant attacker, int currentDamage) {
        return Math.max(0, currentDamage - 10);
    }
}
