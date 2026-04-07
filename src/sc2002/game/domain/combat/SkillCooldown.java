package sc2002.game.domain.combat;

public final class SkillCooldown {
    private int remainingTurns;

    public int remainingTurns() {
        return remainingTurns;
    }

    public boolean isReady() {
        return remainingTurns == 0;
    }

    public void startThreeTurnCooldown() {
        remainingTurns = 3;
    }

    public void tickOnOwnerTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }
}
