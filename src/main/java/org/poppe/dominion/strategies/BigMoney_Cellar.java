package org.poppe.dominion.strategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.poppe.dominion.core.Card;
import org.poppe.dominion.core.Card.Name;
import org.poppe.dominion.core.Player;
import org.poppe.dominion.core.Tableau;

/**
 *
 * @author poppe
 */
public class BigMoney_Cellar extends BigMoney {
    private int numCellarsDesired;
    private int numCellarsPurchased;
    public BigMoney_Cellar(Player player, int numDesiredCellars)
    {
        super(player);
        this.name = "Big Money + Cellar";
        this.numCellarsDesired = numDesiredCellars;
    }
    public BigMoney_Cellar(Player player) {
        this(player, 1);
    }

    @Override
    public Optional<Integer> pickAnActionCard() {
        // If we have a cellar, we should play it, even if we wind up decided later not to discard anything
        var cellar = player.selectCard(Card.Name.CELLAR);
        if (cellar.isPresent()) {
            return cellar;
        }
        return super.pickAnActionCard();
    }

    @Override
    public Optional<Name> pickACardToBuy_2(Tableau tableau) {
        if (numCellarsPurchased < numCellarsDesired && tableau.numLeft(Card.Name.CELLAR) > 0){
            ++numCellarsPurchased;
            return Optional.of(Card.Name.CELLAR);
        }
        return Optional.empty();
    }

    @Override
    public ArrayList<Integer> chooseDiscardsForCellar() {
        // Use a Set so we only grab unique indices, in case logic below selects the same card twice
        Set<Integer> toBeDiscarded = new HashSet<>();
        // Grab all the VP cards (will also grab curse cards)
        if (player.hasCard(Card.Type.VICTORY))
        {
            toBeDiscarded.addAll(player.selectAllCards(Card.Type.VICTORY));
        }
        // If we currently only have 7 money, it would be worth discarding a copper for a chance at a silver
        // Similar logic applies if we have 5 (for a chance at getting gold)
        int currentTreasure = player.getTreasure();
        if (currentTreasure == 7 || currentTreasure == 5)
        {
            if (player.hasCard(Card.Name.COPPER))
            {
                // Don't have to protect .get() on optional because we checked above to make
                // sure we would get one when we asked
                toBeDiscarded.add(player.selectCard(Card.Name.COPPER).get());
            }
            // If we have an extra cellar and we didn't pick any cards above to discard, might as well get rid of it too
            if (player.hasCard(Card.Name.CELLAR) && toBeDiscarded.size() == 0)
            {
                // Don't have to protect .get() on optional because we checked above to make
                // sure we would get one when we asked
                toBeDiscarded.add(player.selectCard(Card.Name.CELLAR).get());
            }
        }
        return new ArrayList<>(toBeDiscarded);
    }
}
