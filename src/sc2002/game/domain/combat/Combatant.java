package sc2002.game.domain.combat;

import sc2002.game.domain.stats.Stats;
import sc2002.game.domain.status.StatusTracker;

public interface Combatant {
    String id();

    String name();

    CombatRole role();

    Stats baseStats();

    int currentHp();

    int effectiveAttack();

    int effectiveDefense();

    int speed();

    boolean isAlive();

    StatusTracker statuses();

    SkillCooldown specialSkillCooldown();

    void applyDamage(int amount);

    void heal(int amount);

    void addAttackBonus(int amount);

    void addDefenseBonus(int amount);

    void clearRoundBonuses();
}
