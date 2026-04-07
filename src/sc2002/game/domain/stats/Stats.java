package sc2002.game.domain.stats;

public final class Stats {
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int speed;

    public Stats(int maxHp, int attack, int defense, int speed) {
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
    }

    public int maxHp() {
        return maxHp;
    }

    public int attack() {
        return attack;
    }

    public int defense() {
        return defense;
    }

    public int speed() {
        return speed;
    }
}
