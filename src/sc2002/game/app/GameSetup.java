package sc2002.game.app;

import java.util.List;
import sc2002.game.domain.combat.player.PlayerCharacter;
import sc2002.game.domain.items.Item;
import sc2002.game.level.LevelConfig;

public record GameSetup(LevelConfig level, PlayerCharacter player, List<Item> items) {
}
