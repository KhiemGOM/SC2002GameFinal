package sc2002.game.app;

import java.util.List;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.combat.player.Warrior;
import sc2002.game.domain.items.PotionItem;
import sc2002.game.domain.items.SmokeBombItem;
import sc2002.game.engine.BattleState;
import sc2002.game.level.LevelFactory;
import sc2002.game.ui.ConsoleBattleUI;

public final class EndScreenPreview {
    private EndScreenPreview() {
    }

    public static void show(boolean win) {
        ConsoleBattleUI ui = new ConsoleBattleUI();
        PlayerCharacter player = new Warrior("preview-player");
        if (!win) {
            player.applyDamage(9999);
        }
        BattleState previewState = new BattleState(
                LevelFactory.easy(),
                player,
                List.<Enemy>of(),
                List.of(new PotionItem(), new SmokeBombItem())
        );
        ui.showBattleEnded(win, previewState);
    }
}
