package org.poppe.dominion.strategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.poppe.dominion.core.Card;
import org.poppe.dominion.core.Card.Name;
import org.poppe.dominion.core.Player;
import org.poppe.dominion.core.Tableau;

/**
 *
 * @author poppe
 */

 /**
  * Big Money + is Big Money, designed to add one extra card, although you can
  * add more than one extra.
  * It will play action cards in random orders, and extra cards will be Bought
  * in descending cost priority (alphabetical for same priced cards) so
  * specifying more than one type of card to buy will be hairy.
  * All specific card implementations will assume they are the only type of
  * "extra" card BM+ is using so they will NOT be optimal in general for mix/match scenario
  */
public class BigMoney_Plus extends BigMoney {
    private final int numCellarToBuy;
    private final int numMineToBuy;
    private final int numSmithyToBuy;
    private final int numVillageToBuy;
    private final int numWitchToBuy;
    private int numCellarBought = 0;
    private int numMineBought = 0;
    private int numSmithyBought = 0;
    private int numVillageBought = 0;
    private int numWitchBought = 0;
    private BigMoney_Plus(Builder b) {
        super(b.player);
        this.numCellarToBuy = b.numCellarToBuy;
        this.numMineToBuy = b.numMineToBuy;
        this.numSmithyToBuy = b.numSmithyToBuy;
        this.numVillageToBuy = b.numVillageToBuy;
        this.numWitchToBuy = b.numWitchToBuy;
        name = "Big Money+";
    }

    public static class Builder {
        private int numCellarToBuy = 0;
        private int numMineToBuy = 0;
        private int numSmithyToBuy = 0;
        private int numVillageToBuy = 0;
        private int numWitchToBuy = 0;
        private final Player player;
        public Builder(Player player){
            this.player = player;
        }
        public Builder withCellars(int numToBuy){
            this.numCellarToBuy = numToBuy;
            return this;
        }
        public Builder withMines(int numToBuy){
            this.numMineToBuy = numToBuy;
            return this;
        }
        public Builder withSmithies(int numToBuy){
            this.numSmithyToBuy = numToBuy;
            return this;
        }
        public Builder withVillages(int numToBuy){
            this.numVillageToBuy = numToBuy;
            return this;
        }
        public Builder withWitches(int numToBuy){
            this.numWitchToBuy = numToBuy;
            return this;
        }

        public BigMoney_Plus build() {
            return new BigMoney_Plus(this); // use private constructor and chain together whatever Builders were specified
        }
    }

    @Override
    public Optional<Integer> pickAnActionCard() {
        // Pick an action card and play it, who even knows what it might be :)
        if (player.hasCard(Card.Type.ACTION)) {
            return player.selectCard(Card.Type.ACTION);
        }
        return super.pickAnActionCard();
    }

    @Override
    public Optional<Name> pickACardToBuy_5(Tableau tableau) {
        if (numWitchBought < numWitchToBuy && tableau.numLeft(Card.Name.WITCH) > 0) {
            ++numWitchBought;
            return Optional.of(Card.Name.WITCH);
        }
        if (numMineBought < numMineToBuy && tableau.numLeft(Card.Name.MINE) > 0){
            ++numMineBought;
            return Optional.of(Card.Name.MINE);
        }
        return super.pickACardToBuy_5(tableau);
    }
    @Override
    public Optional<Name> pickACardToBuy_4(Tableau tableau) {
        if (numSmithyBought < numSmithyToBuy && tableau.numLeft(Card.Name.SMITHY) > 0){
            ++numSmithyBought;
            return Optional.of(Card.Name.SMITHY);
        }
        return super.pickACardToBuy_4(tableau);
    }
    @Override
    public Optional<Name> pickACardToBuy_3(Tableau tableau) {
        if (numVillageBought < numVillageToBuy && tableau.numLeft(Card.Name.VILLAGE) > 0){
            ++numVillageBought;
            return Optional.of(Card.Name.VILLAGE);
        }
        return super.pickACardToBuy_3(tableau);
    }
    @Override
    public Optional<Name> pickACardToBuy_2(Tableau tableau) {
        if (numCellarBought < numCellarToBuy && tableau.numLeft(Card.Name.CELLAR) > 0){
            ++numCellarBought;
            return Optional.of(Card.Name.CELLAR);
        }
        return super.pickACardToBuy_2(tableau);
    }

    @Override
    public ArrayList<Integer> chooseDiscardsForCellar() {
        // Use a Set so we only grab unique indices, in case logic below selects the same card twice
        Set<Integer> toBeDiscarded = new HashSet<>();
        // Grab all the VP cards (will also grab curse cards)
        if (player.hasCard(Card.Type.VICTORY))
        {
            toBeDiscarded.addAll(player.selectAllCards(Card.Type.VICTORY));
        }
        // If we currently only have 7 money, it would be worth discarding a copper for a chance at a silver
        // Similar logic applies if we have 5 (for a chance at getting gold)
        int currentTreasure = player.getTreasure();
        if (currentTreasure == 7 || currentTreasure == 5)
        {
            if (player.hasCard(Card.Name.COPPER))
            {
                // Don't have to protect .get() on optional because we checked above to make
                // sure we would get one when we asked
                toBeDiscarded.add(player.selectCard(Card.Name.COPPER).get());
            }
            // If we have an extra cellar and we didn't pick any cards above to discard, might as well get rid of it too
            if (player.hasCard(Card.Name.CELLAR) && toBeDiscarded.size() == 0)
            {
                // Don't have to protect .get() on optional because we checked above to make
                // sure we would get one when we asked
                toBeDiscarded.add(player.selectCard(Card.Name.CELLAR).get());
            }
        }
        return new ArrayList<>(toBeDiscarded);
    }

    @Override
    public Pair<Optional<Integer>,Optional<Card.Name>> executeMine(Tableau tableau) {
        // Really only understand copper -> silver and silver->gold by default. 
        Optional<Integer> i = Optional.empty();
        Optional<Card.Name> cn = Optional.empty();
        if (player.hasCard(Card.Name.SILVER) && tableau.numLeft(Card.Name.GOLD) > 0){
            i = player.selectCard(Card.Name.SILVER);
            cn = Optional.of(Card.Name.GOLD);
        }
        if (player.hasCard(Card.Name.COPPER) && tableau.numLeft(Card.Name.SILVER) > 0){
            i = player.selectCard(Card.Name.COPPER);
            cn = Optional.of(Card.Name.SILVER);
        }
        return Pair.of(i,cn);
    }

    
}
