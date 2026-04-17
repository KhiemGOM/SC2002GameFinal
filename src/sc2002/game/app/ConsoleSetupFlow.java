package sc2002.game.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.combat.player.Warrior;
import sc2002.game.domain.combat.player.Wizard;
import sc2002.game.domain.items.Item;
import sc2002.game.domain.items.PotionItem;
import sc2002.game.domain.items.PowerStoneItem;
import sc2002.game.domain.items.SmokeBombItem;
import sc2002.game.level.LevelConfig;
import sc2002.game.level.LevelFactory;

public final class ConsoleSetupFlow {
    private static final int WIDTH = 78;
    private final Scanner scanner;
    private final boolean useAnsiClear;

    public ConsoleSetupFlow(Scanner scanner) {
        this(scanner, true);
    }

    public ConsoleSetupFlow(Scanner scanner, boolean useAnsiClear) {
        this.scanner = scanner;
        this.useAnsiClear = useAnsiClear;
    }

    public GameSetup promptStartAndSetup() {
        int startChoice = promptMenu(
                "SC2002 TURN-BASED ARENA",
                "Use W/S (or A/D) then Enter",
                List.of("START", "QUIT")
        );
        if (startChoice == 1) {
            clearScreen();
            System.out.println(center("Goodbye."));
            return null;
        }
        return promptSetup();
    }

    private GameSetup promptSetup() {
        showOverviewScreen();

        int playerChoice = promptMenu(
                "CHOOSE PLAYER",
                "W/S move, Enter confirm",
                List.of(
                        "Warrior (HP 260, ATK 40, DEF 20, SPD 30)",
                        "Wizard  (HP 200, ATK 50, DEF 10, SPD 20)"
                )
        );

        int levelChoice = promptMenu(
                "CHOOSE LEVEL",
                "W/S move, Enter confirm",
                List.of(
                        "Easy   - 3 Goblins",
                        "Medium - 1 Goblin + 1 Wolf, backup: 2 Wolves",
                        "Hard   - 2 Goblins, backup: 1 Goblin + 2 Wolves"
                )
        );

        List<String> itemOptions = List.of("Potion", "Power Stone", "Smoke Bomb");
        List<Supplier<Item>> itemFactories = List.of(PotionItem::new, PowerStoneItem::new, SmokeBombItem::new);
        List<LevelConfig> levelConfigs = List.of(LevelFactory.easy(), LevelFactory.medium(), LevelFactory.hard());

        List<Item> items = new ArrayList<>();
        int firstItem = promptMenu("CHOOSE ITEM 1/2", "Duplicates allowed", itemOptions);
        items.add(itemFactories.get(firstItem).get());

        int secondItem = promptMenu("CHOOSE ITEM 2/2", "Duplicates allowed", itemOptions);
        items.add(itemFactories.get(secondItem).get());

        PlayerCharacter player = playerChoice == 0 ? new Warrior("player-1") : new Wizard("player-1");
        LevelConfig level = levelConfigs.get(levelChoice);

        clearScreen();
        return new GameSetup(level, player, List.copyOf(items));
    }

    private void showOverviewScreen() {
        clearScreen();
        String top = "+" + "-".repeat(WIDTH - 2) + "+";
        List<String> lines = List.of(
                "PLAYER CLASSES",
                "Warrior: HP 260 | ATK 40 | DEF 20 | SPD 30 | Skill: Shield Bash",
                "Wizard : HP 200 | ATK 50 | DEF 10 | SPD 20 | Skill: Arcane Blast",
                "",
                "ENEMY TYPES",
                "Goblin: HP 55 | ATK 35 | DEF 15 | SPD 25",
                "Wolf  : HP 40 | ATK 45 | DEF  5 | SPD 35",
                "",
                "DIFFICULTY ENEMY POOLS",
                "Easy   - Initial: 3 Goblins",
                "Medium - Initial: 1 Goblin + 1 Wolf | Backup: 2 Wolves",
                "Hard   - Initial: 2 Goblins | Backup: 1 Goblin + 2 Wolves",
                "",
                "Press Enter to continue..."
        );

        System.out.println(top);
        System.out.println("| " + padRight(centerWithin("BATTLE OVERVIEW", WIDTH - 4), WIDTH - 4) + " |");
        System.out.println("|" + "-".repeat(WIDTH - 2) + "|");
        for (String line : lines) {
            System.out.println("| " + padRight(line, WIDTH - 4) + " |");
        }
        System.out.println(top);
        scanner.nextLine();
    }

    private int promptMenu(String title, String subtitle, List<String> options) {
        int selected = 0;
        while (true) {
            drawMenu(title, subtitle, options, selected);
            String cmd = readNavCommand();
            if (cmd.equals("confirm")) {
                return selected;
            }
            if (cmd.equals("prev")) {
                selected = (selected - 1 + options.size()) % options.size();
            }
            if (cmd.equals("next")) {
                selected = (selected + 1) % options.size();
            }
        }
    }

    private void drawMenu(String title, String subtitle, List<String> options, int selected) {
        clearScreen();
        String top = "+" + "-".repeat(WIDTH - 2) + "+";
        System.out.println(top);
        System.out.println("| " + padRight(centerWithin(title, WIDTH - 4), WIDTH - 4) + " |");
        System.out.println("| " + padRight(centerWithin(subtitle, WIDTH - 4), WIDTH - 4) + " |");
        System.out.println("|" + "-".repeat(WIDTH - 2) + "|");

        for (int i = 0; i < options.size(); i++) {
            String line = (i == selected ? "> " : "  ") + options.get(i);
            System.out.println("| " + padRight(line, WIDTH - 4) + " |");
        }
        System.out.println(top);
    }

    private String readNavCommand() {
        String raw = scanner.nextLine();
        if (raw == null) {
            return "confirm";
        }
        String trimmed = raw.trim().toLowerCase();
        if (trimmed.isEmpty()) {
            return "confirm";
        }
        return switch (trimmed) {
            case "w", "a", "up", "left", "\u001b[a", "\u001b[d" -> "prev";
            case "s", "d", "down", "right", "\u001b[b", "\u001b[c" -> "next";
            case "enter" -> "confirm";
            default -> "noop";
        };
    }

    private String center(String text) {
        if (text.length() >= WIDTH) {
            return text;
        }
        int leftPad = (WIDTH - text.length()) / 2;
        return " ".repeat(leftPad) + text;
    }

    private String centerWithin(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int leftPad = (width - text.length()) / 2;
        int rightPad = width - text.length() - leftPad;
        return " ".repeat(leftPad) + text + " ".repeat(rightPad);
    }

    private String padRight(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return text + " ".repeat(width - text.length());
    }

    private void clearScreen() {
        if (useAnsiClear) {
            System.out.print("\033[H\033[2J");
        } else {
            System.out.print("\n".repeat(60));
        }
        System.out.flush();
    }
}
