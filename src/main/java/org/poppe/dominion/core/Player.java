package org.poppe.dominion.core;

import java.util.List;
import java.util.Optional;

import org.poppe.dominion.strategies.Strategy;

/**
 *
 * @author poppe
 */
public class Player {
    public class PlayerState {
        protected int currentActions;
        protected int currentBuys;
        protected int currentMoney;

        public PlayerState() {
            this.reset();
        }

        public PlayerState(int currentActions, int currentBuys, int currentMoney) {
            this.currentActions = currentActions;
            this.currentBuys = currentBuys;
            this.currentMoney = currentMoney;
        }

        protected void reset() {
            currentActions = 1;
            currentBuys = 0;
            currentMoney = 0;
        }
    }

    protected PlayerState state;

    public int getCurrentMoney() {
        return state.currentMoney;
    }

    public final int id;
    public final String name;
    public final GameEngine engine;
    protected final CardStack deck;
    protected final CardStack hand;

    public int handSize() {
        return hand.numLeft();
    }

    public Card.Name getCardInHandName(int index) {
        return hand.pullCard(index).getName();
    }

    public List<Card.Type> getCardInHandType(int index) {
        return hand.pullCard(index).getTypes();
    }

    protected final CardStack playArea;
    protected final CardStack discardPile;
    protected final Strategy strategy = null;

    protected Player(GameEngine engine, int id, String name, CardStack startingCards) {
        this.engine = engine;
        this.id = id;
        this.name = name;
        this.deck = new CardStack();
        while (startingCards.numLeft() > 0) {
            this.deck.gain(startingCards.draw());
        }
        this.hand = new CardStack();
        this.playArea = new CardStack();
        this.discardPile = new CardStack();
        this.state = new PlayerState();
    }

    protected void initialize(Strategy strategy) {
        // Give the strategy a reference to me so it can access my functions to get at
        // the stacks
        this.strategy.setPlayer(this);
        // Call cleanup to get hand ready to go
        doCleanup();
    }

    protected void drawToHand() {
        drawToHand(1);
    }

    protected void drawToHand(int numCards){
        for (int i=0; i< numCards; i++){
            hand.gain(this.drawFromDeck());
        }
    }

    protected Optional<Card> playActionCard() {
        // Tell Strategy to do its thing
        var cardIdx = strategy.pickAnActionCard(this.engine);
        // If the strategy identified a card to play, return that, else return no card
        if (cardIdx.isPresent()) {
            return Optional.of(hand.pullCard(cardIdx.get()));
        } else {
            return Optional.empty();
        }
    }

    protected Optional<Card> playTreasureCard() {
        // Tell Strategy to do its thing
        var cardIdx = strategy.pickATreasureCard(this.engine);
        // If the strategy identified a card to play, return that, else return no card
        if (cardIdx.isPresent()) {
            return Optional.of(hand.pullCard(cardIdx.get()));
        } else {
            return Optional.empty();
        }
    }

    protected Optional<Card.Name> buyCard(Tableau tableau) {
        // Tell Strategy to do its thing
        var cardType = strategy.pickACardToBuy(tableau);
        // If the strategy identified a card to play, return that, else return no card
        if (cardType.isPresent()) {
            return Optional.of(cardType.get());
        } else {
            return Optional.empty();
        }
    }

    protected Card drawFromDeck() {
        // Need a special method here to bring in discard if deck is empty
        if (deck.numLeft() > 0) {
            // TODO Test if this consumes discardPile
            deck.gain(this.discardPile);
            deck.shuffle();
        }
        return deck.draw();
    }

    protected void doCleanup() {
        // Kick everything leftover from hand/play area to discard
        discardPile.gain(this.hand); // TODO does this consume hand?
        discardPile.gain(this.playArea);
        // Draw 5 cards from the deck and put them in our hand
        drawToHand(5);
        // Need to reset all the counters for our buys/money/etc
        state.reset();
    }
}
