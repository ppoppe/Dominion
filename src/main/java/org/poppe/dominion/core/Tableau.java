package org.poppe.dominion.core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author poppep
 */

// Tableau is a fancy HashMap of CardStacks, arranged by Type
public class Tableau extends HashMap<Card.Type, CardStack> {
    private Tableau(Builder builder) {
        super(builder.stacks);
    }

    public static class Builder {
        private final int treasure_size = 100;
        private final int numPlayers;
        // Mandatory
        private Map<Card.Type, CardStack> stacks = new HashMap<>();
        // Optional

        public Builder(int numPlayers) {
            this.numPlayers = numPlayers;
            CardBuilder cb = CardBuilder.getInstance();
            if (numPlayers < 2 || numPlayers > 6) {
                throw new IllegalArgumentException("Number of players must be between 2 and 6.");
            }
            // Build all the cards you have to have to play a game
            Card.Type x = Card.Type.COPPER;// need extra coppers for building starting hands later
            this.stacks.put(x, cb.makeCards(x, treasure_size + numPlayers * 7));
            x = Card.Type.SILVER;
            this.stacks.put(x, cb.makeCards(x, treasure_size));
            x = Card.Type.GOLD;
            this.stacks.put(x, cb.makeCards(x, treasure_size));
            x = Card.Type.ESTATE; // need extra estates for building starting hands later
            this.stacks.put(x, cb.makeCards(x, numVP(numPlayers) + numPlayers * 3)); 
            x = Card.Type.DUCHY;
            this.stacks.put(x, cb.makeCards(x, numVP(numPlayers)));
            x = Card.Type.PROVINCE;
            this.stacks.put(x, cb.makeCards(x, numProvinces(numPlayers)));
            x = Card.Type.CURSE;
            this.stacks.put(x, cb.makeCards(x, numCurses(numPlayers)));
        }

        public Builder withCard(Card.Type type)
        {
            int numCards = 10;
            var card = CardBuilder.getInstance().makeCard(type);
            if (card.getCategory().contains(Card.Category.TREASURE)){
                numCards = numVP(this.numPlayers);
            }
            // -1 because already have one to add to the stack
            var cs = CardBuilder.getInstance().makeCards(type, numCards - 1);
            // Stick that first card we made on the bottom of the stack
            cs.addFirst(card);
            this.stacks.put(type, cs);
            return this; // returns Builder instance so we can chain calls of them together
        }

        public Tableau build() {
            return new Tableau(this);
        }

        private int numVP(int numPlayers) {
            int numVP = 0;
            switch (numPlayers) {
                case 2 -> numVP = 8;
                case 3, 4, 5, 6 -> numVP = 12;
                // No idea how you'd ever get here but catch just in case
                default -> throw new IllegalArgumentException("Number of players must be between 2 and 6.");
            }
            return numVP;
        }

        private int numProvinces(int numPlayers) {
            int numProvinces = 0;
            switch (numPlayers) {
                case 2 -> numProvinces = 8;
                case 3, 4 -> numProvinces = 12;
                case 5 -> numProvinces = 15;
                case 6 -> numProvinces = 18;
                // No idea how you'd ever get here but catch just in case
                default -> throw new IllegalArgumentException("Number of players must be between 2 and 6.");
            }
            return numProvinces;
        }

        private int numCurses(int numPlayers) {
            return (numPlayers - 1)*10;
        }
    }

}
