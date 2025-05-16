package org.poppe.dominion.strategies;

import java.util.ArrayList;
import java.util.Optional;

import org.poppe.dominion.core.Card;
import org.poppe.dominion.core.Card.Name;
import org.poppe.dominion.core.Tableau;

/**
 *
 * @author poppe
 */
public class BigMoney extends Strategy {
    public BigMoney() {
        this.name = "Big Money";
    }

    public void reset() { // nothing to do here for BigMoney
    }

    @Override
    public Optional<Integer> pickAnActionCard() {
        // We aren't going to own any action cards, so there's no need to ever specify
        // one to play
        return Optional.empty();
    }

    @Override
    public Optional<Integer> pickATreasureCard() {
        // We will play down every treasure card we have
        // Can use convenince function player has for this purpose
        return player.selectCard(Card.Type.TREASURE);
    }

    // All the buy implementations
    @Override
    public Optional<Name> pickACardToBuy_11(Tableau tableau) {
        if (tableau.numLeft(Card.Name.COLONY) > 0) {
            return Optional.of(Card.Name.COLONY);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_10(Tableau tableau) {
        return (pickACardToBuy_9(tableau));
    }

    @Override
    public Optional<Name> pickACardToBuy_9(Tableau tableau) {
        if (tableau.numLeft(Card.Name.PLATINUM) > 0) {
            return Optional.of(Card.Name.PLATINUM);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_8(Tableau tableau) {
        if (tableau.numLeft(Card.Name.PROVINCE) > 0) {
            return Optional.of(Card.Name.PROVINCE);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_7(Tableau tableau) {
        return (pickACardToBuy_6(tableau));
    }

    @Override
    public Optional<Name> pickACardToBuy_6(Tableau tableau) {
        if (tableau.numLeft(Card.Name.GOLD) > 0) {
            return Optional.of(Card.Name.GOLD);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_5(Tableau tableau) {
        // If provinces are almost out, or colonies (assuming we have that pile present)
        // are almost out
        boolean nearEndgame = tableau.numLeft(Card.Name.PROVINCE) < 5;
        nearEndgame = nearEndgame || (tableau.hasCardPile(Card.Name.COLONY) && tableau.numLeft(Card.Name.COLONY) < 5);
        if (tableau.numLeft(Card.Name.DUCHY) > 0 && nearEndgame) {
            return Optional.of(Card.Name.DUCHY);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_4(Tableau tableau) {
        return (pickACardToBuy_3(tableau));
    }

    @Override
    public Optional<Name> pickACardToBuy_3(Tableau tableau) {
        if (tableau.numLeft(Card.Name.SILVER) > 0) {
            return Optional.of(Card.Name.SILVER);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_2(Tableau tableau) {
        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_1(Tableau tableau) {
        return Optional.empty();
    }

    @Override
    public Optional<Name> pickACardToBuy_0(Tableau tableau) {
        return Optional.empty();
    }

    @Override
    public ArrayList<Integer> chooseDiscardsForCellar() {
        // Big Money isn't going to have a Cellar, so our default action for us and
        // anyone extending us who also doesn't want to deal with cellars is to do
        // nothing
        return new ArrayList<>();
    }

    @Override
    public Pair<Optional<Integer>,Optional<Card.Name>> executeMine(Tableau tableau) {
        // Big Money isn't going to have a Mine, so our default action for us and
        // anyone extending us who also doesn't want to deal with Mines is to do
        // nothing
        Optional<Integer> i = Optional.empty();
        Optional<Card.Name> cn = Optional.empty();
        return Pair.of(i, cn);
    }

    
}
