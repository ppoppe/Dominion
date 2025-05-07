package org.poppe.dominion.strategies;

import java.util.Optional;

import org.poppe.dominion.core.Card;
import org.poppe.dominion.core.GameEngine;
import org.poppe.dominion.core.Tableau;

/**
 *
 * @author poppe
 */
public class BigMoney extends Strategy {
    public BigMoney() {
    }

    @Override
    public Optional<Integer> pickAnActionCard(GameEngine engine) {
        // We aren't going to own any action cards, so there's no need to ever specify
        // one to play
        return Optional.of(-1);
    }

    @Override
    public Optional<Integer> pickATreasureCard(GameEngine engine) {
        // We will play down every treasure card we have
        for (int i = 0; i< player.handSize(); i++)
        {
            if (player.getCardInHandType(i).contains(Card.Type.TREASURE)){
                // Found a treasure, so let's play it!
                return Optional.of(i);
            }
        }
        // If we got down to here, that means we have no treasure in our hand anymore
        return Optional.empty();
    }

    @Override
    public Optional<Card.Name> pickACardToBuy(Tableau tableau) {
        // We will only ever have one buy, so we don't need to consider a case where we
        // split money between two cards

        // Quick summary of priority:
        // colony, platinum, province, gold, duchy (maybe), silver

        switch (Math.max(player.getCurrentMoney(), 11)) {
            case 11: {
                var cardName = Card.Name.COLONY;
                if (stillInSupply(tableau, cardName)) {
                    return Optional.of(cardName);
                }
                // fallthrough
            }
            case 9, 10: {
                var cardName = Card.Name.PLATINUM;
                if (stillInSupply(tableau, cardName)) {
                    return Optional.of(cardName);
                }
                // fallthrough
            }
            case 8: {
                var cardName = Card.Name.PROVINCE;
                if (stillInSupply(tableau, cardName)) {
                    return Optional.of(cardName);
                }
                // fallthrough
            }
            case 6, 7: {
                var cardName = Card.Name.GOLD;
                if (stillInSupply(tableau, cardName)) {
                    return Optional.of(cardName);
                }
                // fallthrough
            }
            case 5: {
                var cardName = Card.Name.DUCHY;
                // If provinces are almost out, or colonies (assuming we have that pile present)
                // are almost out
                boolean nearEndgame = tableau.numLeft(Card.Name.PROVINCE) < 5;
                var colony = Card.Name.COLONY;
                nearEndgame = nearEndgame || (tableau.hasCardPile(colony) && tableau.numLeft(colony) < 5);
                if (stillInSupply(tableau, cardName) && nearEndgame) {
                    return Optional.of(cardName);
                }
                // fallthrough
            }
            case 3, 4: {
                var cardName = Card.Name.SILVER;
                if (stillInSupply(tableau, cardName)) {
                    return Optional.of(cardName);
                }
                // fallthrough
            }
            default:
                return Optional.empty();
        }
    }

    // helper functions
    private boolean stillInSupply(Tableau tableau, Card.Name name) {
        return tableau.hasCardPile(name) && tableau.numLeft(name) > 0;
    }
}
