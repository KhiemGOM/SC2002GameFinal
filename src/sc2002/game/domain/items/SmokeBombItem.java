package sc2002.game.domain.items;

import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.status.SmokeBombInvulnerabilityEffect;
import sc2002.game.engine.BattleState;

public final class SmokeBombItem implements Item {
    @Override
    public String name() {
        return "Smoke Bomb";
    }

    @Override
    public void use(PlayerCharacter player, BattleState state) {
        player.statuses().add(new SmokeBombInvulnerabilityEffect(), player);
    }
}
