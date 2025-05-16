package org.poppe.dominion.core;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.poppe.dominion.strategies.Pair;
import org.poppe.dominion.strategies.Strategy;

/**
 *
 * @author poppe
 */
public class Player {
    public class PlayerState {
        protected int currentActions = 1;
        protected int currentBuys = 1;
        protected int currentMoney = 0;
        // Placeholder for GameEngine to eventually store our score in
        protected int finalVP = 0;
        protected int turnsTaken = 0;
        protected GameResult gameResult;

        public PlayerState() {
        }

        public PlayerState(int currentActions, int currentBuys, int currentMoney) {
            this.currentActions = currentActions;
            this.currentBuys = currentBuys;
            this.currentMoney = currentMoney;
        }

        protected void reset() {
            currentActions = 1;
            currentBuys = 1;
            currentMoney = 0;
        }
    }

    protected PlayerState state;

    public int getCurrentMoney() {
        return state.currentMoney;
    }

    public final int id;
    public String name = "";
    public String toString(){
        return name;
    }
    protected CardStack deck;
    protected CardStack hand;

    public int handSize() {
        return hand.numLeft();
    }

    public int getTreasure() {
        return hand.getTotalTreasure();
    }

    public boolean hasCard(Card.Name name){
        for (int i=0; i<handSize(); ++i){
            if (hand.examine(i).getName() == name){
                return true;
            }
        }
        return false;
    }

    public boolean hasCard(Card.Type type){
        for (int i=0; i<handSize(); ++i){
            if (hand.examine(i).getTypes().contains(type)){
                return true;
            }
        }
        return false;
    }

    public Optional<Integer> selectCard(Card.Name name){
        for (int i=0; i<handSize(); ++i){
            if (hand.examine(i).getName() == name){
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public Set<Integer> selectAllCards(Card.Name name){
        Set<Integer> cards = new HashSet<>();
        for (int i=0; i<handSize(); ++i){
            if (hand.examine(i).getName() == name){
                cards.add(i);
            }
        }
        return cards;
    }

    public Set<Integer> selectAllCards(Card.Type type){
        Set<Integer> cards = new HashSet<>();
        for (int i=0; i<handSize(); ++i){
            if (hand.examine(i).getTypes().contains(type)){
                cards.add(i);
            }
        }
        return cards;
    }

    public Optional<Integer> selectCard(Card.Type type){
        for (int i=0; i<handSize(); ++i){
            if (hand.examine(i).getTypes().contains(type)){
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public Card.Name getCardInHandName(int index) {
        return hand.examine(index).getName();
    }

    public List<Card.Type> getCardInHandType(int index) {
        return hand.examine(index).getTypes();
    }

    protected CardStack playArea;
    protected CardStack discardPile;
    protected Strategy strategy = null;

    protected Player(int id) {
        this.id = id;
    }
    protected void initalize(Tableau tableau){
        this.deck = new CardStack();
        for (int i=0; i<7; ++i)
        {
            deck.gain(tableau.getStack(Card.Name.COPPER).draw());
        }
        for (int i=0; i<3; ++i)
        {
            deck.gain(tableau.getStack(Card.Name.ESTATE).draw());
        }
        this.deck.shuffle();
        this.hand = new CardStack();
        this.playArea = new CardStack();
        this.discardPile = new CardStack();
        this.state = new PlayerState();
        this.strategy.reset();
    }

    protected void setStrategy(Strategy strategy) {
        this.strategy = strategy;
        this.name = String.format("%s_%d",strategy.name,this.id);
        this.strategy.setPlayer(this);
    }
    // Draws a single card from deck to hand
    protected void drawToHand() {
        drawToHand(1);
    }
    // Draws numCards from deck to hand
    protected void drawToHand(int numCards) {
        for (int i = 0; i < numCards; i++) {
            var optionalCard = this.drawFromDeck();
            if (optionalCard.isPresent()) {
                hand.gain(optionalCard.get());
            } else {
                // NO MORE CARDS TO DRAW!!!
                return;
            }
        }
    }

    protected Optional<Card> playActionCard() {
        // Tell Strategy to do its thing
        var cardIdx = strategy.pickAnActionCard();
        // If the strategy identified a card to play, return that, else return no card
        if (cardIdx.isPresent()) {
            return Optional.of(hand.pullCard(cardIdx.get()));
        } else {
            return Optional.empty();
        }
    }

    protected Optional<Card> playTreasureCard() {
        // Tell Strategy to do its thing
        var cardIdx = strategy.pickATreasureCard();
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
    
    protected Optional<Card> drawFromDeck() {
        // Need a special method here to bring in discard if deck is empty
        if (deck.numLeft() < 1) {
            deck.gain(discardPile);
            deck.shuffle();
        }
        // Sometimes we will have drawn every card we have so I guess we're done drawing cards!
        if (deck.numLeft() < 1) {
            return Optional.empty();
        }
        return Optional.of(deck.draw());
    }

    protected void doCleanup() {
        // Kick everything leftover from hand/play area to discard
        discardPile.gain(hand);
        discardPile.gain(playArea);
        // Draw 5 cards from the deck and put them in our hand
        drawToHand(5);
        // Need to reset all the counters for our buys/money/etc
        state.reset();
    }

    protected CardStack respondToCellar(){
        var indicesToDiscard = strategy.chooseDiscardsForCellar();
        // Need to reverse sort the indices so we can remove them from the hand
        indicesToDiscard.sort(Comparator.reverseOrder());
        CardStack cardsToDiscard = new CardStack();
        for (var i : indicesToDiscard){
            cardsToDiscard.gain(hand.pullCard(i));
        }
        return cardsToDiscard;
    }


    protected Pair<Optional<Card>,Optional<Card.Name>> respondToMine(Tableau tableau){
        var indexCardPair = strategy.executeMine(tableau);
        Optional<Card> c = Optional.empty();
        Optional<Card.Name> cn = indexCardPair.getSecond();
        if (indexCardPair.getFirst().isPresent()) {
            c = Optional.of(hand.pullCard(indexCardPair.getFirst().get()));
        }
        else {
            // If no card to trash, then there's no card to get from tableau. Reset just in case strategy goofed
            cn = Optional.empty();
        }
        return Pair.of(c,cn);
    }
}
