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
public class VillageIdiot extends BigMoney {
    public VillageIdiot(Player player) {
        super(player);
        name = "VillageIdiot";
    }

    @Override
    public Optional<Integer> pickAnActionCard() {
        // Play all villages
        var village = player.selectCard(Card.Name.VILLAGE);
        if (village.isPresent())
            return village;

        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_3(Tableau tableau) {
        // BUY ALL THEM VILLAGES
        if (tableau.numLeft(Card.Name.VILLAGE) > 0) {
            return Optional.of(Card.Name.VILLAGE);
        }
        return super.pickACardToBuy_3(tableau);
    }
}
