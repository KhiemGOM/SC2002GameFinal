package sc2002.game.engine;

import sc2002.game.domain.combat.Combatant;

public final class DamageCalculator {
    public int calculate(Combatant attacker, Combatant defender) {
        
        int raw = Math.max(0, attacker.effectiveAttack() - defender.effectiveDefense());
        return defender.statuses().modifyIncomingDamage(defender, attacker, raw);
    }
}
