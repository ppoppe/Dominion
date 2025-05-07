package org.poppe.dominion.strategies;

import java.util.Optional;

import org.poppe.dominion.core.Card;
import org.poppe.dominion.core.GameEngine;
import org.poppe.dominion.core.Player;
import org.poppe.dominion.core.Tableau;

/**
 *
 * @author poppe
 */
public abstract class Strategy {

    protected Player player;

    public Strategy() {
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // Decide what actions to play
    // Examine Player's hand to see what choices need to be made, then inform the game engine of your choice
    // Return an index to the card in the Players hand we want to play down
    public abstract Optional<Integer> pickAnActionCard(GameEngine engine);
    
    public abstract Optional<Integer> pickATreasureCard(GameEngine engine);

    public abstract Optional<Card.Name> pickACardToBuy(Tableau tableau);
}
