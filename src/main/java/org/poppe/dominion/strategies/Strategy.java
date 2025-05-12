package org.poppe.dominion.strategies;

import java.util.ArrayList;
import java.util.Optional;

import org.poppe.dominion.core.Card;
import org.poppe.dominion.core.Player;
import org.poppe.dominion.core.Tableau;

/**
 *
 * @author poppe
 */
public abstract class Strategy {

    protected Player player;
    public String name;

    public Strategy() {
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // Decide what actions to play
    // Examine Player's hand to see what choices need to be made, then inform the game engine of your choice
    // Return an index to the card in the Players hand we want to play down
    public abstract Optional<Integer> pickAnActionCard();
    
    public abstract Optional<Integer> pickATreasureCard();

    /**
     * Function will pick what cards from the hands should be discarded while playing Cellar
     * @return Array of Integers representing indices in the hand to be discarded
     */
    public abstract ArrayList<Integer> chooseDiscardsForCellar();
    /**
     * Function will pick a treasure card to trash for an upgrade
     * @return Index of card to trash, if any
     */
    public abstract Pair<Optional<Integer>,Optional<Card.Name>> executeMine(Tableau tableau);

    public Optional<Card.Name> pickACardToBuy(Tableau tableau) {
        Optional<Card.Name> card = Optional.empty();
        switch (Math.min(player.getCurrentMoney(), 11)) {
            case 11:
                card = pickACardToBuy_11(tableau);
                if (card.isPresent())
                    break;
            case 10:
                card = pickACardToBuy_10(tableau);
                if (card.isPresent())
                    break;
            case 9:
                card = pickACardToBuy_9(tableau);
                if (card.isPresent())
                    break;
            case 8:
                card = pickACardToBuy_8(tableau);
                if (card.isPresent())
                    break;
            case 7:
                card = pickACardToBuy_7(tableau);
                if (card.isPresent())
                    break;
            case 6:
                card = pickACardToBuy_6(tableau);
                if (card.isPresent())
                    break;
            case 5:
                card = pickACardToBuy_5(tableau);
                if (card.isPresent())
                    break;
            case 4:
                card = pickACardToBuy_4(tableau);
                if (card.isPresent())
                    break;
            case 3:
                card = pickACardToBuy_3(tableau);
                if (card.isPresent())
                    break;
            case 2:
                card = pickACardToBuy_2(tableau);
                if (card.isPresent())
                    break;
            case 1:
                card = pickACardToBuy_1(tableau);
                if (card.isPresent())
                    break;
            case 0:
                card = pickACardToBuy_0(tableau);
                if (card.isPresent())
                    break;
            default:
        }
        return card;
    };

    // Bunch of methods to override how to choose for a given amount of money
    public abstract Optional<Card.Name> pickACardToBuy_11(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_10(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_9(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_8(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_7(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_6(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_5(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_4(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_3(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_2(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_1(Tableau tableau);
    public abstract Optional<Card.Name> pickACardToBuy_0(Tableau tableau);
}
