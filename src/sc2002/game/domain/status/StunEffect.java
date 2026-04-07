package sc2002.game.domain.status;

public final class StunEffect extends TimedStatusEffect {
    public StunEffect() {
        super(2);
    }

    @Override
    public String key() {
        return "STUN";
    }

    @Override
    public String displayName() {
        return "Stun";
    }

    @Override
    public boolean blocksAction() {
        return true;
    }
}
