package sc2002.game.domain.status;

import sc2002.game.domain.combat.Combatant;

public interface StatusEffect {
    String key();

    String displayName();

    boolean blocksAction();

    int modifyIncomingDamage(Combatant owner, Combatant attacker, int currentDamage);

    void onApply(Combatant owner);

    void onTurnStart(Combatant owner);

    void onTurnEnd(Combatant owner);

    void tick();

    boolean isExpired();

    default boolean isStackable() {
        return false;
    }
}
