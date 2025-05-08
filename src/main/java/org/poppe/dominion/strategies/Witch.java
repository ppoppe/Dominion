package org.poppe.dominion.strategies;

import java.util.Optional;

import org.poppe.dominion.core.Card;
import org.poppe.dominion.core.Card.Name;
import org.poppe.dominion.core.Player;
import org.poppe.dominion.core.Tableau;

/**
 *
 * @author poppe
 */
// We will mostly want to do what big money does, with a couple minor exceptions
public class Witch extends BigMoney {
    private int numWitchesDesired;
    private int numWitchesPurchased = 0;

    public Witch(Player player, int numWitchesDesired) {
        super(player);
        this.numWitchesDesired = numWitchesDesired;
        this.name = "Witch";
    }

    @Override
    public Optional<Integer> pickAnActionCard() {
        // Play a witch if we have one
        // Can use convenince function player has for this purpose
        return player.selectCard(Card.Name.WITCH);
    }

    @Override
    public Optional<Name> pickACardToBuy_5(Tableau tableau) {
        if (numWitchesPurchased < numWitchesDesired && tableau.numLeft(Card.Name.WITCH) > 0) {
            ++numWitchesPurchased;
            return Optional.of(Card.Name.WITCH);
        }
        // If we don't want a witch, punt to BigMoney's implementation
        return super.pickACardToBuy_5(tableau);
    }

}
