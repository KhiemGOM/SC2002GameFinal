package sc2002.game.domain.combat.enemy;

import java.util.List;
import sc2002.game.domain.combat.AbstractCombatant;
import sc2002.game.domain.combat.CombatRole;
import sc2002.game.domain.stats.Stats;

public abstract class Enemy extends AbstractCombatant {
    protected Enemy(String id, String name, Stats stats) {
        super(id, name, CombatRole.ENEMY, stats);
    }

    public String displayName() {
        int ordinal = parseOrdinalFromId(id());
        if (ordinal <= 0) {
            return name();
        }
        String label = name() + " " + toAlphabetLabel(ordinal);
        if (id().startsWith("backup-")) {
            return label + " (Backup)";
        }
        return label;
    }

    private int parseOrdinalFromId(String rawId) {
        int lastDash = rawId.lastIndexOf('-');
        if (lastDash < 0 || lastDash == rawId.length() - 1) {
            return -1;
        }
        try {
            return Integer.parseInt(rawId.substring(lastDash + 1));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String toAlphabetLabel(int value) {
        StringBuilder builder = new StringBuilder();
        int n = value;
        while (n > 0) {
            n--;
            builder.insert(0, (char) ('A' + (n % 26)));
            n /= 26;
        }
        return builder.toString();
    }

    public abstract List<String> asciiArt();
}
