package sc2002.game.domain.combat.player;

import java.util.List;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.status.StunEffect;
import sc2002.game.domain.stats.Stats;
import sc2002.game.engine.BattleState;

public final class Warrior extends PlayerCharacter {
    private static final Stats WARRIOR_STATS = new Stats(260, 40, 20, 30);

    public Warrior(String id) {
        super(id, "Warrior", WARRIOR_STATS);
    }

    @Override
    public String specialSkillName() {
        return "Shield Bash";
    }

    @Override
    public boolean performSpecialSkill(BattleState state, PlayerSkillBridge bridge) {
        Enemy target = bridge.promptEnemyTarget(state.aliveEnemies());
        if (target == null) {
            return false;
        }
        int damage = bridge.applyBasicAttack(this, target);
        target.statuses().add(new StunEffect(), target);
        String deathNote = target.isAlive() ? "" : " " + target.displayName() + " dies.";
        bridge.showInfo("Shield Bash hits " + target.displayName() + " for " + damage + " damage and applies Stun." + deathNote);
        return true;
    }

    @Override
    public List<String> asciiArt() {
        return List.of(
                "\\  O     ",
                " X/|[]   ",
                "  / \\    "
        );
    }
}
