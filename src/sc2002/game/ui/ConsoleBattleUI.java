package sc2002.game.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sc2002.game.domain.combat.Combatant;
import sc2002.game.domain.combat.enemy.Enemy;
import sc2002.game.domain.items.Item;
import sc2002.game.domain.status.StatusEffect;
import sc2002.game.domain.status.TimedStatusEffect;
import sc2002.game.engine.BattleState;

public final class ConsoleBattleUI implements BattleUI {
    private static final int WIDTH = 78;
    private static final String HLINE = "-".repeat(WIDTH);
    private final Scanner scanner;
    private final boolean useAnsiClear;
    private BattleState currentState;
    private int currentRound = 1;
    private List<String> turnOrder = List.of();
    private final List<String> roundEvents = new ArrayList<>();

    public ConsoleBattleUI() {
        this(new Scanner(System.in), true);
    }

    public ConsoleBattleUI(boolean useAnsiClear) {
        this(new Scanner(System.in), useAnsiClear);
    }

    public ConsoleBattleUI(Scanner scanner, boolean useAnsiClear) {
        this.scanner = scanner;
        this.useAnsiClear = useAnsiClear;
    }

    @Override
    public void showBattleInitialized(BattleState state) {
        currentState = state;
        currentRound = state.roundNumber();
    }

    @Override
    public void showRoundStart(BattleState state, List<Combatant> orderedTurnList) {
        maybeShowRoundSummary();
        currentState = state;
        currentRound = state.roundNumber();
        turnOrder = orderedTurnList.stream().map(Combatant::name).toList();
    }

    @Override
    public void showBattleState(BattleState state) {
        currentState = state;
        maybeShowRoundSummary();
    }

    @Override
    public int promptPlayerAction(BattleState state, List<String> actionLabels) {
        currentState = state;
        List<String> options = actionLabels;
        int selected = 0;
        while (true) {
            render(options, selected, 2, null, List.of("WASD to move. Enter to confirm."), false, true);
            String cmd = readNavCommand();
            if (cmd.equals("confirm")) {
                return selected + 1;
            }
            selected = moveInGrid(selected, options.size(), 2, cmd);
        }
    }

    @Override
    public Enemy promptEnemyTarget(List<Enemy> aliveEnemies) {
        if (aliveEnemies.isEmpty()) {
            return null;
        }
        int selected = 0;
        while (true) {
            List<String> enemyOptions = aliveEnemies.stream()
                    .map(e -> e.displayName() + " HP " + e.currentHp() + "/" + e.baseStats().maxHp())
                    .toList();
            List<String> options = new ArrayList<>(enemyOptions);
            options.add("[Back]");
            render(options, selected, 2, null, List.of("Select target. WASD to move, Enter to confirm, B to back."), false, true);
            String cmd = readNavCommand();
            if (cmd.equals("back")) {
                return null;
            }
            if (cmd.equals("confirm")) {
                if (selected == aliveEnemies.size()) {
                    return null;
                }
                return aliveEnemies.get(selected);
            }
            selected = moveInGrid(selected, options.size(), 2, cmd);
        }
    }

    @Override
    public Item promptItemChoice(List<Item> items) {
        if (items.isEmpty()) {
            render(null, -1, 0, null, List.of("No usable items left."), false, true);
            return null;
        }
        int selected = 0;
        while (true) {
            List<String> itemOptions = items.stream().map(Item::name).toList();
            List<String> options = new ArrayList<>(itemOptions);
            options.add("[Back]");
            String selectedDetail;
            if (selected < items.size()) {
                selectedDetail = "Info: " + itemDescription(items.get(selected));
            } else {
                selectedDetail = null;
            }
            render(options, selected, 0, selectedDetail, List.of("Select item. WASD to move, Enter to confirm, B to back."), false, true);
            String cmd = readNavCommand();
            if (cmd.equals("back")) {
                return null;
            }
            if (cmd.equals("confirm")) {
                if (selected == items.size()) {
                    return null;
                }
                return items.get(selected);
            }
            selected = moveInVerticalList(selected, options.size(), cmd);
        }
    }

    @Override
    public PostBattleOption showBattleEnded(boolean playerWon, BattleState state) {
        maybeShowRoundSummary();
        currentState = state;
        return showEndScreen(playerWon, state);
    }

    @Override
    public void showInfo(String message) {
        roundEvents.add(message);
    }

    private void render(
            List<String> options,
            int selected,
            int optionGridCols,
            String selectedOptionDetail,
            List<String> messages,
            boolean showInventoryLine,
            boolean decorateMessages
    ) {
        clearScreen();
        System.out.println(HLINE);
        System.out.println(center("SC2002 BATTLE ARENA  |  ROUND " + currentRound));
        System.out.println(HLINE);
        if (!turnOrder.isEmpty()) {
            System.out.println("Turn Order: " + String.join(" > ", turnOrder));
        }
        System.out.println();

        renderEnemies();
        System.out.println();
        renderPlayerSprite();
        System.out.println();
        renderHud(options, selected, optionGridCols, selectedOptionDetail, messages, showInventoryLine, decorateMessages);
    }

    private void renderEnemies() {
        if (currentState == null || currentState.aliveEnemies().isEmpty()) {
            System.out.println("No enemies on field.");
            return;
        }

        List<Enemy> enemies = currentState.aliveEnemies();
        int cardWidth = 24;
        int cardsPerRow = 3;
        int cardLines = 6;

        for (int start = 0; start < enemies.size(); start += cardsPerRow) {
            int end = Math.min(start + cardsPerRow, enemies.size());
            List<Enemy> row = enemies.subList(start, end);

            List<List<String>> cards = row.stream()
                    .map(enemy -> enemyCardLines(enemy, cardWidth))
                    .toList();

            for (int line = 0; line < cardLines; line++) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < cards.size(); i++) {
                    if (i > 0) {
                        builder.append("  ");
                    }
                    builder.append(cards.get(i).get(line));
                }
                System.out.println(builder);
            }
            System.out.println();
        }
    }

    private void renderHud(
            List<String> options,
            int selected,
            int optionGridCols,
            String selectedOptionDetail,
            List<String> messages,
            boolean showInventoryLine,
            boolean decorateMessages
    ) {
        if (currentState == null) {
            return;
        }
        var p = currentState.player();

        String top = "+" + "-".repeat(WIDTH - 2) + "+";
        System.out.println(top);
        System.out.println("| " + padRight(p.name() + "  " + hpInline(p.currentHp(), p.baseStats().maxHp()), WIDTH - 4) + " |");
        System.out.println("| " + padRight("ATK " + p.effectiveAttack() + "   DEF " + p.effectiveDefense() + "   SPD " + p.speed(), WIDTH - 4) + " |");
        List<String> playerStatuses = formatStatusEntries(p);
        if (!playerStatuses.isEmpty()) {
            String statusLabel = "Status: ";
            String statusIndent = " ".repeat(statusLabel.length());
            for (int i = 0; i < playerStatuses.size(); i++) {
                String prefix = (i == 0) ? statusLabel : statusIndent;
                System.out.println("| " + padRight(prefix + playerStatuses.get(i), WIDTH - 4) + " |");
            }
        }
        System.out.println("|" + "-".repeat(WIDTH - 2) + "|");

        if (options != null && !options.isEmpty()) {
            if (optionGridCols >= 2) {
                int cols = optionGridCols;
                int rows = (options.size() + cols - 1) / cols;
                int innerWidth = WIDTH - 4;
                int baseColWidth = innerWidth / cols;
                for (int row = 0; row < rows; row++) {
                    StringBuilder line = new StringBuilder();
                    for (int col = 0; col < cols; col++) {
                        int index = row * cols + col;
                        int colWidth = (col == cols - 1)
                                ? innerWidth - (baseColWidth * (cols - 1))
                                : baseColWidth;
                        String cell = pick(options, selected, index);
                        line.append(padRight(cell, colWidth));
                    }
                    System.out.println("| " + line + " |");
                }
            } else {
                for (int i = 0; i < options.size(); i++) {
                    String line = (i == selected ? "> " : "  ") + options.get(i);
                    List<String> wrapped = wrapLine(line, WIDTH - 4);
                    for (String part : wrapped) {
                        System.out.println("| " + padRight(part, WIDTH - 4) + " |");
                    }
                    if (i == selected && selectedOptionDetail != null && !selectedOptionDetail.isBlank()) {
                        List<String> detailWrapped = wrapLine("   " + selectedOptionDetail, WIDTH - 4);
                        for (String detail : detailWrapped) {
                            System.out.println("| " + padRight(detail, WIDTH - 4) + " |");
                        }
                    }
                }
            }
        } else if (showInventoryLine) {
            List<String> wrapped = wrapLine("Items: " + currentState.playerItems().stream().map(Item::name).toList(), WIDTH - 4);
            for (String part : wrapped) {
                System.out.println("| " + padRight(part, WIDTH - 4) + " |");
            }
        }

        for (String message : messages) {
            String line = decorateMessages ? "-> " + message : message;
            List<String> wrapped = wrapLine(line, WIDTH - 4);
            for (int i = 0; i < wrapped.size(); i++) {
                String segment = wrapped.get(i);
                if (decorateMessages && i > 0 && segment.startsWith("-> ")) {
                    segment = "   " + segment.substring(3);
                }
                System.out.println("| " + padRight(segment, WIDTH - 4) + " |");
            }
        }

        System.out.println(top);
    }

    private void renderPlayerSprite() {
        if (currentState == null) {
            return;
        }
        List<String> sprite = playerAscii();
        String label = currentState.player().name();
        int indent = 4;
        for (String line : sprite) {
            System.out.println(" ".repeat(indent) + line);
        }
        System.out.println(" ".repeat(indent) + label);
    }

    private String pick(List<String> options, int selected, int index) {
        if (index < 0 || index >= options.size()) {
            return "";
        }
        String label = options.get(index);
        return (selected == index ? "> " : "  ") + label;
    }

    private int moveInVerticalList(int selected, int size, String cmd) {
        return switch (cmd) {
            case "up", "left" -> (selected - 1 + size) % size;
            case "down", "right" -> (selected + 1) % size;
            default -> selected;
        };
    }

    private int moveInGrid(int selected, int size, int cols, String cmd) {
        if (size <= 0 || cols <= 0) {
            return selected;
        }
        int rows = (size + cols - 1) / cols;
        int row = selected / cols;
        int col = selected % cols;

        int targetRow = row;
        int targetCol = col;
        switch (cmd) {
            case "up" -> targetRow = Math.max(0, row - 1);
            case "down" -> targetRow = Math.min(rows - 1, row + 1);
            case "left" -> targetCol = Math.max(0, col - 1);
            case "right" -> targetCol = Math.min(cols - 1, col + 1);
            default -> {
                return selected;
            }
        }

        int target = targetRow * cols + targetCol;
        if (target < size) {
            return target;
        }

        int rowStart = targetRow * cols;
        int rowEnd = Math.min(size - 1, rowStart + cols - 1);
        if (rowStart > rowEnd) {
            return selected;
        }
        return switch (cmd) {
            case "right" -> rowEnd;
            case "left" -> rowStart;
            case "up", "down" -> rowEnd;
            default -> selected;
        };
    }

    private String readNavCommand() {
        if (!scanner.hasNextLine()) {
            System.out.println("\nConsole input closed. Exiting game.");
            System.exit(0);
        }
        String raw = scanner.nextLine();
        if (raw == null) {
            return "confirm";
        }
        String trimmed = raw.trim().toLowerCase();
        if (trimmed.isEmpty()) {
            return "confirm";
        }
        return switch (trimmed) {
            case "w", "up", "\u001b[a" -> "up";
            case "s", "down", "\u001b[b" -> "down";
            case "a", "left", "\u001b[d" -> "left";
            case "d", "right", "\u001b[c" -> "right";
            case "enter" -> "confirm";
            case "b", "back", "q" -> "back";
            default -> "noop";
        };
    }

    private String hpInline(int hp, int maxHp) {
        return "HP " + hp + "/" + maxHp + " " + bar(hp, maxHp, 10);
    }

    private String itemDescription(Item item) {
        return switch (item.name()) {
            case "Potion" -> "Heals 100 HP, up to your max HP.";
            case "Smoke Bomb" -> "Enemy attacks deal 0 damage for current and next turn.";
            case "Power Stone" -> "Triggers your Special Skill once without changing cooldown.";
            default -> "No additional information.";
        };
    }

    private List<String> enemyCardLines(Enemy enemy, int width) {
        int hp = enemy.currentHp();
        int max = enemy.baseStats().maxHp();
        String hpBar = "HP " + bar(hp, max, 12);
        String hpNum = "HP " + hp + "/" + max;
        List<String> statuses = formatStatusEntries(enemy);
        String statusLine = statuses.isEmpty() ? "" : "Status: " + String.join(", ", statuses);

        List<String> art = enemyAscii(enemy);
        return List.of(
                padRight(hpBar, width),
                padRight(hpNum, width),
                padRight(art.get(0), width),
                padRight(art.get(1), width),
                padRight(enemy.displayName(), width),
                padRight(statusLine, width)
        );
    }

    private List<String> enemyAscii(Enemy enemy) {
        return enemy.asciiArt();
    }

    private List<String> playerAscii() {
        return currentState.player().asciiArt();
    }

    private String bar(int value, int max, int width) {
        if (max <= 0) {
            return "[" + "-".repeat(width) + "]";
        }
        int filled = Math.max(0, Math.min(width, (int) Math.round(value / (double) max * width)));
        return "[" + "#".repeat(filled) + "-".repeat(width - filled) + "]";
    }

    private String center(String text) {
        if (text.length() >= WIDTH) {
            return text;
        }
        int leftPad = (WIDTH - text.length()) / 2;
        return " ".repeat(leftPad) + text;
    }

    private String padRight(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return text + " ".repeat(width - text.length());
    }

    private String padLeft(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return " ".repeat(width - text.length()) + text;
    }

    private void clearScreen() {
        if (useAnsiClear) {
            System.out.print("\033[H\033[2J");
        } else {
            System.out.print("\n".repeat(80));
        }
        System.out.flush();
    }

    private void showRoundSummaryAndWait() {
        List<String> summaryLines = new ArrayList<>();
        summaryLines.add("TURN " + currentRound + " SUMMARY");
        summaryLines.add("");
        if (roundEvents.isEmpty()) {
            summaryLines.add("No actions recorded.");
        } else {
            int index = 1;
            for (String event : roundEvents) {
                summaryLines.add(index + ". " + event);
                index++;
            }
        }
        summaryLines.add(padLeft("Press Enter to continue...", WIDTH - 4));
        render(null, -1, 0, null, summaryLines, false, false);

        if (!scanner.hasNextLine()) {
            System.exit(0);
        }
        scanner.nextLine();

        roundEvents.clear();
    }

    private void maybeShowRoundSummary() {
        if (!roundEvents.isEmpty()) {
            showRoundSummaryAndWait();
        }
    }

    private PostBattleOption showEndScreen(boolean playerWon, BattleState state) {
        int selected = 0;
        List<String> options = List.of("Replay Same Settings", "Start New Game", "Exit");
        while (true) {
            clearScreen();
            String top = "=".repeat(WIDTH);
            System.out.println(top);
            System.out.println(center("SC2002 BATTLE RESULT"));
            System.out.println(top);
            System.out.println();

            List<String> art = playerWon ? victoryAscii() : defeatAscii();
            for (String line : art) {
                System.out.println(center(line));
            }
            System.out.println();

            String result = playerWon ? "YOU WIN" : "YOU LOSE";
            System.out.println(center(result));
            if (playerWon) {
                System.out.println(center("Congratulations, you have defeated all your enemies."));
                System.out.println(center("Remaining HP: " + state.player().currentHp() + "/" + state.player().baseStats().maxHp()));
                System.out.println(center("Total Rounds: " + state.roundNumber()));
            } else {
                System.out.println(center("Defeated. Don't give up, try again!"));
                System.out.println(center("Enemies Remaining: " + state.aliveEnemies().size()));
                System.out.println(center("Total Rounds Survived: " + state.roundNumber()));
            }
            System.out.println(center("Level: " + state.levelConfig().difficulty()));
            System.out.println();
            System.out.println(center("Choose what to do next:"));
            for (int i = 0; i < options.size(); i++) {
                String prefix = (i == selected) ? "> " : "  ";
                System.out.println(center(prefix + options.get(i)));
            }
            System.out.println();
            System.out.println(center("W/S to move, Enter to confirm"));

            String cmd = readNavCommand();
            if ("confirm".equals(cmd)) {
                return switch (selected) {
                    case 0 -> PostBattleOption.REPLAY_SAME_SETTINGS;
                    case 1 -> PostBattleOption.START_NEW_GAME;
                    default -> PostBattleOption.EXIT;
                };
            }
            selected = moveInVerticalList(selected, options.size(), cmd);
        }
    }

    private List<String> victoryAscii() {
        return List.of(
                "__        _____ _   _ ",
                "\\ \\      / /_ _| \\ | |",
                " \\ \\ /\\ / / | ||  \\| |",
                "  \\ V  V /  | || |\\  |",
                "   \\_/\\_/  |___|_| \\_|"
        );
    }

    private List<String> defeatAscii() {
        return List.of(
                " _     ___  ____  _____ ",
                "| |   / _ \\/ ___|| ____|",
                "| |  | | | \\___ \\|  _|  ",
                "| |__| |_| |___) | |___ ",
                "|_____\\___/|____/|_____|"
        );
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
            remaining = remaining.substring(cut).trim();
        }
        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }
        return lines;
    }

    private List<String> formatStatusEntries(Combatant combatant) {
        List<StatusEffect> effects = combatant.statuses().activeEffects();
        if (effects.isEmpty()) {
            return List.of();
        }
        return effects.stream().map(this::formatStatus).toList();
    }

    private String formatStatus(StatusEffect effect) {
        String base = effect.displayName();
        if (effect instanceof TimedStatusEffect timed) {
            if (timed.remainingTurns() == TimedStatusEffect.INFINITE_DURATION) {
                return base + " (Infinite)";
            }
            int turns = timed.remainingTurns();
            return base + " (" + turns + (turns == 1 ? " turn" : " turns") + ")";
        }
        return base;
    }
}
