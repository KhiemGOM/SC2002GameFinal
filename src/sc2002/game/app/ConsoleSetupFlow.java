package sc2002.game.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;
import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.combat.player.Warrior;
import sc2002.game.domain.combat.player.Wizard;
import sc2002.game.domain.items.Item;
import sc2002.game.domain.items.PotionItem;
import sc2002.game.domain.items.PowerStoneItem;
import sc2002.game.domain.items.SmokeBombItem;
import sc2002.game.level.EnemyType;
import sc2002.game.level.LevelConfig;
import sc2002.game.level.LevelFactory;
import sc2002.game.level.SpawnWave;

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
                List.of("START", "QUIT"),
                null
        );
        if (startChoice == 1) {
            clearScreen();
            System.out.println(center("Goodbye."));
            return null;
        }
        return promptSetup();
    }

    private GameSetup promptSetup() {
        List<String> playerOptions = List.of("Warrior", "Wizard");
        List<String> playerDetails = List.of(
                "HP 260 | ATK 40 | DEF 20 | SPD 30\nSkill: Shield Bash - Strike an enemy for normal damage + apply Stun (2 turns).",
                "HP 200 | ATK 50 | DEF 10 | SPD 20\nSkill: Arcane Blast - Strike all enemies for normal damage. Gain ATK +10 permanently for each enemy killed."
        );
        int playerChoice = promptMenu("CHOOSE PLAYER", "W/S move, Enter confirm", playerOptions, playerDetails);

        List<LevelConfig> levelConfigs = List.of(LevelFactory.easy(), LevelFactory.medium(), LevelFactory.hard());
        List<String> levelOptions = List.of("Easy", "Medium", "Hard");
        List<String> levelDetails = levelConfigs.stream().map(this::levelDetail).toList();
        int levelChoice = promptMenu("CHOOSE LEVEL", "W/S move, Enter confirm", levelOptions, levelDetails);

        List<String> itemOptions = List.of("Potion", "Power Stone", "Smoke Bomb");
        List<String> itemDetails = List.of(
                "Heals 100 HP, up to your max HP.",
                "Triggers your Special Skill once without changing cooldown.",
                "Enemy attacks deal 0 damage for current and next turn."
        );
        List<Supplier<Item>> itemFactories = List.of(PotionItem::new, PowerStoneItem::new, SmokeBombItem::new);

        List<Item> items = new ArrayList<>();
        int firstItem = promptMenu("CHOOSE ITEM 1/2", "Duplicates allowed", itemOptions, itemDetails);
        items.add(itemFactories.get(firstItem).get());

        int secondItem = promptMenu("CHOOSE ITEM 2/2", "Duplicates allowed", itemOptions, itemDetails);
        items.add(itemFactories.get(secondItem).get());

        PlayerCharacter player = playerChoice == 0 ? new Warrior("player-1") : new Wizard("player-1");
        LevelConfig level = levelConfigs.get(levelChoice);

        clearScreen();
        return new GameSetup(level, player, List.copyOf(items));
    }

    private String levelDetail(LevelConfig level) {
        StringBuilder sb = new StringBuilder();
        sb.append("Wave 1:  ").append(waveToString(level.initialWave()));
        if (level.backupWave() != null) {
            sb.append("\nWave 2:  ").append(waveToString(level.backupWave()));
        }
        return sb.toString();
    }

    private String waveToString(SpawnWave wave) {
        List<String> parts = new ArrayList<>();
        Map<EnemyType, Integer> entries = wave.entries();
        for (EnemyType type : EnemyType.values()) {
            Integer count = entries.get(type);
            if (count != null) {
                String name = type.name().charAt(0) + type.name().substring(1).toLowerCase();
                parts.add(name + " x" + count);
            }
        }
        return String.join("  ", parts);
    }

    private int promptMenu(String title, String subtitle, List<String> options, List<String> details) {
        int selected = 0;
        while (true) {
            String detail = (details != null) ? details.get(selected) : null;
            drawMenu(title, subtitle, options, selected, detail);
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

    private void drawMenu(String title, String subtitle, List<String> options, int selected, String detail) {
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

        if (detail != null) {
            System.out.println("|" + "-".repeat(WIDTH - 2) + "|");
            for (String dline : detail.split("\n")) {
                for (String wrapped : wrapLine("  " + dline, WIDTH - 4)) {
                    System.out.println("| " + padRight(wrapped, WIDTH - 4) + " |");
                }
            }
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

    private List<String> wrapLine(String text, int width) {
        if (text.length() <= width) {
            return List.of(text);
        }
        List<String> lines = new ArrayList<>();
        String remaining = text;
        while (remaining.length() > width) {
            int cut = remaining.lastIndexOf(' ', width);
            if (cut <= 0) {
                cut = width;
            }
            lines.add(remaining.substring(0, cut));
            remaining = "  " + remaining.substring(cut).trim();
        }
        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }
        return lines;
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
