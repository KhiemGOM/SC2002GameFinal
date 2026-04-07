package sc2002.game.domain.status;

import sc2002.game.domain.combat.Combatant;

public final class ArcaneBlastAttackBonusEffect extends TimedStatusEffect {
    private final int bonus;

    public ArcaneBlastAttackBonusEffect(int bonus) {
        super(INFINITE_DURATION);
        this.bonus = bonus;
    }

    @Override
    public String key() {
        return "ARCANE_BLAST_BONUS";
    }

    @Override
    public String displayName() {
        return "Arcane Blast Bonus";
    }

    @Override
    public void onApply(Combatant owner) {
        owner.addAttackBonus(bonus);
    }

    @Override
    public boolean isStackable() {
        return true;
    }
}
