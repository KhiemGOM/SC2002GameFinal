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
    public void onApply(Combatant owner) {
        owner.addDefenseBonus(10);
    }

    @Override
    public void onRemove(Combatant owner) {
        owner.addDefenseBonus(-10);
    }
}
