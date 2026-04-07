package sc2002.game.ui;

import java.util.List;
import sc2002.game.domain.combat.Combatant;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.items.Item;
import sc2002.game.engine.BattleState;

public interface BattleUI {
    void showBattleInitialized(BattleState state);

    void showRoundStart(BattleState state, List<Combatant> orderedTurnList);

    void showBattleState(BattleState state);

    int promptPlayerAction(BattleState state, List<String> actionLabels);

    Enemy promptEnemyTarget(List<Enemy> aliveEnemies);

    Item promptItemChoice(List<Item> items);

    void showBattleEnded(boolean playerWon, BattleState state);

    void showInfo(String message);
}
