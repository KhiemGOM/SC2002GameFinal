package sc2002.game.app;

import java.util.ArrayList;
import java.util.List;
import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.combat.player.Warrior;
import sc2002.game.domain.combat.player.Wizard;
import sc2002.game.domain.items.Item;
import sc2002.game.domain.items.PotionItem;
import sc2002.game.domain.items.PowerStoneItem;
import sc2002.game.domain.items.SmokeBombItem;
import sc2002.game.level.LevelConfig;

public record GameSetup(LevelConfig level, PlayerCharacter player, List<Item> items) {
    public GameSetup freshCopy() {
        return new GameSetup(level, freshPlayer(player), freshItems(items));
    }

    private static PlayerCharacter freshPlayer(PlayerCharacter template) {
        if (template instanceof Warrior) {
            return new Warrior(template.id());
        }
        if (template instanceof Wizard) {
            return new Wizard(template.id());
        }
        throw new IllegalArgumentException("Unsupported player type: " + template.getClass().getName());
    }

    private static List<Item> freshItems(List<Item> source) {
        List<Item> copy = new ArrayList<>();
        for (Item item : source) {
            copy.add(freshItem(item));
        }
        return List.copyOf(copy);
    }

    private static Item freshItem(Item item) {
        return switch (item.name()) {
            case "Potion" -> new PotionItem();
            case "Power Stone" -> new PowerStoneItem();
            case "Smoke Bomb" -> new SmokeBombItem();
            default -> throw new IllegalArgumentException("Unsupported item: " + item.name());
        };
    }
}
