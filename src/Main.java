import java.util.Scanner;
import sc2002.game.app.ConsoleSetupFlow;
import sc2002.game.app.EndScreenPreview;
import sc2002.game.app.GameSetup;
import sc2002.game.engine.BattleEngine;
import sc2002.game.engine.DamageCalculator;
import sc2002.game.engine.SpeedTurnOrderStrategy;
import sc2002.game.ui.ConsoleBattleUI;

public class Main {
    public static void main(String[] args) {
        boolean useAnsiClear = true;
        Boolean previewWin = null;
        for (String arg : args) {
            if ("--preview-win".equalsIgnoreCase(arg)) {
                previewWin = true;
                continue;
            }
            if ("--preview-lose".equalsIgnoreCase(arg)) {
                previewWin = false;
                continue;
            }
            if ("--no-ansi-clear".equalsIgnoreCase(arg)) {
                useAnsiClear = false;
                continue;
            }
            if ("--ansi-clear".equalsIgnoreCase(arg)) {
                useAnsiClear = true;
            }
        }

        if (previewWin != null) {
            EndScreenPreview.show(previewWin);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        ConsoleSetupFlow setupFlow = new ConsoleSetupFlow(scanner, useAnsiClear);
        GameSetup setup = setupFlow.promptStartAndSetup();
        if (setup == null) {
            return;
        }

        BattleEngine engine = new BattleEngine(
                new SpeedTurnOrderStrategy(),
                new DamageCalculator(),
                new ConsoleBattleUI(useAnsiClear)
        );
        engine.runInteractiveLevel(setup.level(), setup.player(), setup.items());
    }
}
