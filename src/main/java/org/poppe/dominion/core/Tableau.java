package org.poppe.dominion.core;

import java.util.HashMap;
import java.util.Optional;

/**
 *
 * @author poppep
 */

// Tableau is a fancy HashMap of CardStacks, arranged by Type
public class Tableau {
    private final HashMap<Card.Name, CardStack> supplyPiles;

    protected final CardStack trashPile = new CardStack();

    private Tableau(Builder builder) {
        supplyPiles = builder.supplyPiles;
    }

    public boolean hasCardPile(Card.Name name) {
        return supplyPiles.containsKey(name);
    }

    public int numLeft(Card.Name name) {
        if (hasCardPile(name)) {
            return supplyPiles.get(name).numLeft();
        } else {
            return 0;
        }
    }

    protected Optional<Card> pullCard(Card.Name name) {
        if (hasCardPile(name) && numLeft(name) > 0) {
            return Optional.of(supplyPiles.get(name).draw());
        }
        return Optional.empty();
    }

    /** Getter method to grab a particular stack of cards, mostly for the GameEngine to use */
    protected CardStack getStack(Card.Name name) {
        if (!hasCardPile(name)) {
            throw new IllegalStateException(
                    String.format("Someone asked to get the stack of %s cards, but that doesn't exist",
                            name.toString()));
        }
        return supplyPiles.get(name);
    }

    public static class Builder {
        private final int treasurePileSize = 100;
        private final int numPlayers;
        // Mandatory
        private HashMap<Card.Name, CardStack> supplyPiles = new HashMap<>();
        // Optionals

        public Builder(int numPlayers) {
            this.numPlayers = numPlayers;
            CardBuilder cb = CardBuilder.getInstance();
            if (numPlayers < 2 || numPlayers > 6) {
                throw new IllegalArgumentException("Number of players must be between 2 and 6.");
            }
            // Build all the cards you have to have to play a game
            Card.Name x = Card.Name.COPPER;// need extra coppers for building starting hands later
            this.supplyPiles.put(x, cb.makeCards(x, treasurePileSize + numPlayers * 7));
            x = Card.Name.SILVER;
            this.supplyPiles.put(x, cb.makeCards(x, treasurePileSize));
            x = Card.Name.GOLD;
            this.supplyPiles.put(x, cb.makeCards(x, treasurePileSize));
            x = Card.Name.ESTATE; // need extra estates for building starting hands later
            this.supplyPiles.put(x, cb.makeCards(x, numVP(numPlayers) + numPlayers * 3));
            x = Card.Name.DUCHY;
            this.supplyPiles.put(x, cb.makeCards(x, numVP(numPlayers)));
            x = Card.Name.PROVINCE;
            this.supplyPiles.put(x, cb.makeCards(x, numProvinces(numPlayers)));
            x = Card.Name.CURSE;
            this.supplyPiles.put(x, cb.makeCards(x, numCurses(numPlayers)));
        }

        public Builder withCard(Card.Name type) {
            int numCards = 10;
            var card = CardBuilder.getInstance().makeCard(type);
            var cardTypes = card.getTypes();
            if (cardTypes.size() == 1 && cardTypes.contains(Card.Type.VICTORY)) {
                // Pure victory card
                numCards = numVP(this.numPlayers);
            } else if (cardTypes.size() == 1 && cardTypes.contains((Card.Type.TREASURE))) {
                // Pure treasure card
                numCards = treasurePileSize;
            }
            // -1 because already have one to add to the stack
            var cs = CardBuilder.getInstance().makeCards(type, numCards - 1);
            // Stick that first card we made on the bottom of the stack
            cs.gainToBottom(card);
            this.supplyPiles.put(type, cs);
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
            return (numPlayers - 1) * 10;
        }
    }
}
