package sc2002.game.domain.combat.player;

import java.util.List;
import sc2002.game.domain.combat.Combatant;
import sc2002.game.domain.combat.enemy.Enemy;

public interface PlayerSkillBridge {
    Enemy promptEnemyTarget(List<Enemy> aliveEnemies);

    int applyBasicAttack(Combatant attacker, Combatant defender);

    void showInfo(String message);
}
