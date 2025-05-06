package org.poppe.dominion.strategies;

import org.poppe.dominion.core.GameEngine;
import org.poppe.dominion.core.Player;

/**
 *
 * @author poppe
 */
public abstract class Strategy {

    private Player player;

    public Strategy() {
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // Decide what actions to play
    // Examine player's hand to see what choices need to be made, then inform the game engine of your choice
    public abstract void decideActions(GameEngine engine);

    // Decide what cards to buy
    public abstract void decideBuys(GameEngine engine);

}
