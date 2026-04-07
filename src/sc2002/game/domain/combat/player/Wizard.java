package sc2002.game.domain.combat.player;

import java.util.List;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.status.ArcaneBlastAttackBonusEffect;
import sc2002.game.domain.stats.Stats;
import sc2002.game.engine.BattleState;

public final class Wizard extends PlayerCharacter {
    private static final Stats WIZARD_STATS = new Stats(200, 50, 10, 20);

    public Wizard(String id) {
        super(id, "Wizard", WIZARD_STATS);
    }

    @Override
    public String specialSkillName() {
        return "Arcane Blast";
    }

    @Override
    public boolean performSpecialSkill(BattleState state, PlayerSkillBridge bridge) {
        int kills = 0;
        for (Enemy enemy : state.aliveEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }
            int damage = bridge.applyBasicAttack(this, enemy);
            String deathNote = enemy.isAlive() ? "" : " " + enemy.displayName() + " dies.";
            bridge.showInfo("Arcane Blast hits " + enemy.displayName() + " for " + damage + " damage." + deathNote);
            if (!enemy.isAlive()) {
                kills++;
            }
        }
        if (kills > 0) {
            int bonus = kills * 10;
            statuses().add(new ArcaneBlastAttackBonusEffect(bonus), this);
            bridge.showInfo("Arcane Blast defeated " + kills + " enemies. Wizard ATK +" + bonus + ".");
        } else {
            bridge.showInfo("Arcane Blast defeated no enemies. No ATK bonus gained.");
        }
        return true;
    }

    @Override
    public List<String> asciiArt() {
        return List.of(
                "  / \\    ",
                "* (O)    ",
                " \\/|\\    ",
                "  / \\    "
        );
    }
}
