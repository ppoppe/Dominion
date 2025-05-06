package org.poppe.dominion.core;

import org.poppe.dominion.strategies.Strategy;

/**
 *
 * @author poppe
 */
public class Player {
    public final int id;
    public final String name;
    public final GameEngine engine;
    protected final CardStack deck;
    protected final CardStack hand;
    protected final CardStack playArea;
    protected final CardStack discardPile;
    protected final Strategy strategy = null;

    public Player(GameEngine engine, int id, String name, CardStack startingCards){
        this.engine = engine;
        this.id = id;
        this.name = name;
        this.deck = new CardStack();
        while (!startingCards.isEmpty()){
            this.deck.gain(startingCards.draw());
        }
        this.hand = new CardStack();
        this.playArea = new CardStack();
        this.discardPile  = new CardStack();
    }
    
    public void initialize(Strategy strategy){
        // Give the strategy a reference to me so it can access my functions to get at the stacks
        this.strategy.setPlayer(this);
        // Call cleanup to get hand ready to go
        this.doCleanup();
    }

    public void doActions(){
        // Tell Strategy to do its thing
        this.strategy.decideActions(this.engine);
    }

    public void doBuys(){
        this.strategy.decideBuys(this.engine);
    }

    public void doCleanup(){
        // Kick everything leftover from hand/play area to discard
        this.discardPile.addAll(this.hand);
        this.hand.clear();
        this.discardPile.addAll(this.playArea);
        this.playArea.clear();
        // Draw 5 cards from the deck and put them in our hand
        for (int i = 0; i < 5; i++){
            if (this.deck.isEmpty())
            {
                this.deck.addAll(this.discardPile);
                this.discardPile.clear();
                this.deck.shuffle();
            }
            this.hand.gain(this.deck.draw());
        }
    }

}
