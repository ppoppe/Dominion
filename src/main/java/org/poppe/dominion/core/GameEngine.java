package org.poppe.dominion.core;

import java.util.HashMap;

/**
 *
 * @author poppe
 */
public class GameEngine {
    private HashMap<Integer, Player> players;
    public Tableau tableau;

    public GameEngine(HashMap<Integer, Player> players) {
        // Build the tableau before we do anything else
        tableau = new Tableau.Builder(2)
                .withCard(Card.Name.SMITHY)
                .withCard(Card.Name.VILLAGE)
                .withCard(Card.Name.WITCH)
                .build();

        this.players = new HashMap<>(players);

        // Before the game officially starts, give all the players a starting deck,
        // then tell all the players to CleanUp so they
        // draw a starting hand
        players.forEach((_, player) -> {
            player.deck.gain(initializeHand());
            player.doCleanup();
        });
    }

    public void playGame(){
        // For now, just end games when PROVINCE pile is done.
        while (tableau.numLeft(Card.Name.PROVINCE) > 0) {
            // Iterate through players and let each take their turn
            players.forEach((_, player) -> {
                takeTurn(player);
            });
        }
        // Now that game is over, score each player
        players.forEach((_, player) -> {
            score(player);
        });
    }

    // Draw requisite copper and estate cards from Tableau to feed to player
    // Use unprotected .get() since we know for a fact there's plenty of cards there
    // for now
    private CardStack initializeHand() {
        CardStack cs = new CardStack();
        for (int i = 0; i < 7; i++) {
            cs.gain(tableau.pullCard(Card.Name.COPPER).get());
        }
        for (int i = 0; i < 3; i++) {
            cs.gain(tableau.pullCard(Card.Name.ESTATE).get());
        }
        return cs;
    }

    private void takeTurn(Player player) {
        // Do the action phase
        while (player.state.currentActions > 0) {
            // Ask the player for a card to play
            var card = player.playActionCard();
            if (card.isPresent()) {
                if (!card.get().getTypes().contains(Card.Type.ACTION)) {
                    throw new IllegalStateException(String.format(
                            "Player %d was asked for an action card and returned %s, which is not an action card",
                            player.id, card.get().getName().toString()));
                }
                // First thing to do is note that the player has executed an action card and
                // thus has one less action
                --player.state.currentActions;
                // Now go and do the card
                executeCard(player, card.get());
            }
            // If they didn't pass me a card, then we can break out of the while loop and go
            // to the next phase
            break;
        }
        // Because in later variants, players actually have to lay down money "in
        // order",
        var card = player.playTreasureCard();
        while (card.isPresent()) {
            if (!card.get().getTypes().contains(Card.Type.TREASURE)) {
                throw new IllegalStateException(String.format(
                        "Player %d was asked for an treasure card and returned %s, which is not a treasure card",
                        player.id, card.get().getName().toString()));
            }
            // Now go and do the card
            executeCard(player, card.get());
            // Ask the player for another treasure
            card = player.playTreasureCard();
        }
        // Done laying down treasure now, let the player execute some buys
        while (player.state.currentBuys > 0) {
            // Pass the player a view into the current tableau
            var cardName = player.buyCard(tableau);
            if (cardName.isPresent()) {
                var name = cardName.get();
                // Double check the player can afford this, or else he's cheating!
                if (tableau.numLeft(name) == 0) {
                    throw new IllegalStateException(String.format(
                            "Player %d asked to buy %s, but that pile is empty",
                            player.id, name.toString()));
                }
                // Look at the top card of that pile so we can see how much it costs
                int cost = tableau.getStack(name).peek().getCost();
                if (cost > player.state.currentMoney) {
                    throw new IllegalStateException(String.format(
                            "Player %d asked to buy %s, but lacks funds to do so (has %d, but card costs %d)",
                            player.id, name.toString(), player.state.currentMoney, cost));
                }
                // Okeyday then, let's mechanize that purchase
                var purchasedCard = tableau.pullCard(name).get(); // returns optional, but we know it'll be there
                player.state.currentMoney -= purchasedCard.getCost();
                --player.state.currentBuys;
                player.discardPile.gain(purchasedCard);
            }
        }
        // Cleanup time!
        player.doCleanup();
    }

    private void executeCard(Player player, Card card) {
        // Handle all the "easy" cards here, and then call other helpers to deal with
        // thornier cards
        switch (card.getName()) {
            case CURSE, ESTATE, COLONY, PROVINCE, DUCHY:
                break;
            case COPPER, SILVER, GOLD, PLATINUM:
                player.state.currentMoney += card.getExtraTreasure();
                break;
            case SMITHY:
                // Tell the player to draw 4 more cards
                player.drawToHand(card.getExtraCards());
                break;
            case VILLAGE:
                // Draw a card, add two actions
                player.drawToHand(card.getExtraCards());
                player.state.currentActions += card.getExtraActions();
                break;
            case WITCH:
                // Draw two cards
                player.drawToHand(card.getExtraCards());
                // all players but this one have to draw a curse, in player order (in case we
                // run out)
                int nextPlayerId = player.id + 1;
                if (nextPlayerId >= players.size()) {
                    nextPlayerId = 0;
                }
                while (nextPlayerId != player.id && tableau.numLeft(Card.Name.CURSE) > 0) {
                    // pullCard will return an Optional, but we verified it exists above so we're ok
                    players.get(nextPlayerId).deck.gain(tableau.pullCard(Card.Name.CURSE).get());
                    ++nextPlayerId;
                    if (nextPlayerId >= players.size()) {
                        nextPlayerId = 0;
                    }
                }
                break;
            default:
                break;

        }

    }

    private void score(Player player) {
        // We'll eventually need to know how to score more complicated cards, but for
        // now, just assume
        // face value VP is all we are dealing with
        int vp = 0;
        // Player will have a deck/hand/discard at this point of the game
        // TODO: Might eventually also have stuff on islands/duration/etc
        vp += score(player.deck);
        vp += score(player.hand);
        vp += score(player.discardPile);
        player.state.finalVP = vp;
    }

    private int score(CardStack stack) {
        int vp = 0;
        while (stack.numLeft() > 0) {
            vp += stack.draw().getExtraVP();
        }
        return vp;
    }
}
