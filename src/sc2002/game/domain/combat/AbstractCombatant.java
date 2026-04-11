package sc2002.game.domain.combat;

import sc2002.game.domain.stats.Stats;
import sc2002.game.domain.status.StatusTracker;

public abstract class AbstractCombatant implements Combatant {
    private final String id;
    private final String name;
    private final CombatRole role;
    private final Stats baseStats;
    private final StatusTracker statuses = new StatusTracker();
    private final SkillCooldown skillCooldown = new SkillCooldown();
    private int currentHp;
    private int bonusAttack;
    private int bonusDefense;

    protected AbstractCombatant(String id, String name, CombatRole role, Stats baseStats) {
        if (baseStats == null) {
        	throw new IllegalArgumentException("Base stats cannot be null for " + name);
        }
    	this.id = id;
        this.name = name;
        this.role = role;
        this.baseStats = baseStats;
        this.currentHp = baseStats.maxHp();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CombatRole role() {
        return role;
    }

    @Override
    public Stats baseStats() {
        return baseStats;
    }

    @Override
    public int currentHp() {
        return currentHp;
    }

    @Override
    public int effectiveAttack() {
        return baseStats.attack() + bonusAttack;
    }

    @Override
    public int effectiveDefense() {
        return baseStats.defense() + bonusDefense;
    }

    @Override
    public int speed() {
        return baseStats.speed();
    }

    @Override
    public boolean isAlive() {
        return currentHp > 0;
    }

    @Override
    public StatusTracker statuses() {
        return statuses;
    }

    @Override
    public SkillCooldown specialSkillCooldown() {
        return skillCooldown;
    }

    @Override
    public void applyDamage(int amount) {
        int clamped = Math.max(0, amount);
        currentHp = Math.max(0, currentHp - clamped);
    }

    @Override
    public void heal(int amount) {
    	if (!isAlive()) return;
        int clamped = Math.max(0, amount);
        currentHp = Math.min(baseStats.maxHp(), currentHp + clamped);
    }

    @Override
    public void addAttackBonus(int amount) {
        bonusAttack += amount;
    }

    @Override
    public void addDefenseBonus(int amount) {
        bonusDefense += amount;
    }

    @Override
    public void clearRoundBonuses() {
        bonusDefense = 0;
    }
}
