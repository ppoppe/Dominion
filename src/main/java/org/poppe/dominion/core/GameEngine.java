package org.poppe.dominion.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author poppe
 */
public class GameEngine {
    private HashMap<Integer, Player> players;
    public Tableau tableau;

    public GameEngine() {
        this.players = new HashMap<>();
        // For now, just make a couple players
        Player p1 = new Player(this, 1, "Player 1", initializeHand());
        this.players.put(p1.id, p1);
        Player p2 = new Player(this, 2, "Player 2", initializeHand());
        this.players.put(p2.id, p2);
    }

    public Map<Card.Type, CardStack> getTableau() {
        return Collections.unmodifiableMap(tableau);
    }

    private CardStack initializeHand() {
        CardStack cs = new CardStack();
        for (int i = 0; i < 7; i++) {
            cs.gain(tableau.get(Card.Type.COPPER).draw());
        }
        for (int i = 0; i < 3; i++) {
            cs.gain(tableau.get(Card.Type.ESTATE).draw());
        }
        return cs;
    }
}
