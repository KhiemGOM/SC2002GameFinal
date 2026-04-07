package sc2002.game.domain.combat.player;

import java.util.List;
import sc2002.game.domain.combat.AbstractCombatant;
import sc2002.game.domain.combat.CombatRole;
import sc2002.game.domain.stats.Stats;
import sc2002.game.engine.BattleState;

public abstract class PlayerCharacter extends AbstractCombatant {
    protected PlayerCharacter(String id, String name, Stats stats) {
        super(id, name, CombatRole.PLAYER, stats);
    }

    public abstract String specialSkillName();

    public abstract boolean performSpecialSkill(BattleState state, PlayerSkillBridge bridge);

    public abstract List<String> asciiArt();
}
