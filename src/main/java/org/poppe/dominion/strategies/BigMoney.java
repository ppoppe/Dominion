package org.poppe.dominion.strategies;

import java.util.Map;
import java.util.Optional;

import org.poppe.dominion.core.Card;
import org.poppe.dominion.core.Card.Type;
import org.poppe.dominion.core.CardStack;
import org.poppe.dominion.core.GameEngine;

/**
 *
 * @author poppe
 */
public class BigMoney extends Strategy{
    public BigMoney(){}

    @Override
    public Optional<Integer> pickAnActionCard(GameEngine engine) {
        // We aren't going to own any action cards, so there's no need to ever specify one to play
        return Optional.of(-1);
    }

    @Override
    public Optional<Integer> pickATreasureCard(GameEngine engine) {
        // We will play down every treasure card we have
        var hand = this.player.seeHand();
        for (int i = 0; i< hand.size(); i++)
        {
            if (hand.)
        }
    }

    @Override
    public Optional<Type> pickACardToBuy(Map<Type, CardStack> tableau) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pickACardToBuy'");
    }




}
