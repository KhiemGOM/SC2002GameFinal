package sc2002.game.domain.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sc2002.game.domain.combat.Combatant;

public final class StatusTracker {
    private final List<StatusEffect> activeEffects = new ArrayList<>();

    public List<StatusEffect> activeEffects() {
        return List.copyOf(activeEffects);
    }

    public void add(StatusEffect effect, Combatant owner) {
        if (!effect.isStackable()) {
            activeEffects.removeIf(existing -> existing.key().equals(effect.key()));
        }
        activeEffects.add(effect);
        effect.onApply(owner);
    }

    public boolean blocksAction() {
        for (StatusEffect effect : activeEffects) {
            if (effect.blocksAction()) {
                return true;
            }
        }
        return false;
    }

    public int modifyIncomingDamage(Combatant owner, Combatant attacker, int damage) {
        int modified = damage;
        for (StatusEffect effect : activeEffects) {
            modified = effect.modifyIncomingDamage(owner, attacker, modified);
        }
        return modified;
    }

    public void onTurnStart(Combatant owner) {
        for (StatusEffect effect : activeEffects) {
            effect.onTurnStart(owner);
        }
    }

    public void onTurnEnd(Combatant owner) {
        for (StatusEffect effect : activeEffects) {
            effect.onTurnEnd(owner);
        }
    }

    public void onRoundEnd() {
        tickAndExpire();
    }

    private void tickAndExpire() {
        Iterator<StatusEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            effect.tick();
            if (effect.isExpired()) {
                iterator.remove();
            }
        }
    }
}
