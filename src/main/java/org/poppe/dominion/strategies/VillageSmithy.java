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
public class VillageSmithy extends BigMoney {
    private int numSmithiesDesired;
    private int numVillagesDesired;
    private int numSmithiesPurchased = 0;
    private int numVillagesPurchased = 0;
    public VillageSmithy(Player player, int numSmithiesDesired, int numVillagesDesired) {
        super(player);
        name = "VillageSmithy";
        this.numSmithiesDesired = numSmithiesDesired;
        this.numVillagesDesired = numVillagesDesired;
    }

    @Override
    public Optional<Integer> pickAnActionCard() {
        // Play all villages, then all smithies
        var village = player.selectCard(Card.Name.VILLAGE);
        if (village.isPresent()) return village;
        // Take advantage of Optional behavior here:
        return player.selectCard(Card.Name.SMITHY);
    }

    @Override
    public Optional<Name> pickACardToBuy_4(Tableau tableau) {
        if (numSmithiesPurchased < numSmithiesDesired && tableau.numLeft(Card.Name.SMITHY) > 0) {
            ++numSmithiesPurchased;
            return Optional.of(Card.Name.SMITHY);
        }
        // If we don't want a smithy, punt to BigMoney's implementation
        return super.pickACardToBuy_4(tableau);
    }

    @Override
    public Optional<Name> pickACardToBuy_3(Tableau tableau) {
        if (numVillagesPurchased < numVillagesDesired && tableau.numLeft(Card.Name.VILLAGE) > 0) {
            ++numVillagesPurchased;
            return Optional.of(Card.Name.VILLAGE);
        }
        // If we don't want a village, punt to BigMoney's implementation
        return super.pickACardToBuy_3(tableau);
    }
}
