package org.poppe.dominion.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.poppe.dominion.strategies.BigMoney;

/**
 *
 * @author poppe
 */
public class GameEngine {
    private HashMap<Integer, Player> players;
    public Tableau tableau;

    public GameEngine() {
        players = new HashMap<>();
        // For now, just make a couple players
        Player p1 = new Player(this, 0, "Player 1", initializeHand());
        BigMoney strategy1 = new BigMoney();
        p1.initialize(strategy1);
        players.put(p1.id, p1);
        Player p2 = new Player(this, 1, "Player 2", initializeHand());
        BigMoney strategy2 = new BigMoney();
        p2.initialize(strategy2);
        players.put(p2.id, p2);

        // Before the game officially starts, tell all the players to CleanUp so they
        // draw a starting hand
        players.forEach((_, player) -> {
            player.doCleanup();
        });

        // For now, just end games when PROVINCE pile is done.
        while (tableau.get(Card.Type.PROVINCE).size() > 0) {
            // Iterate through players and let each take their turn
            players.forEach((_, player) -> {
                takeTurn(player);
            });
        }
    }

    public Map<Card.Type, CardStack> getTableau() {
        return Collections.unmodifiableMap(tableau);
    }

    // Draw requisite copper and estate cards from Tableau to feed to player
    // Use unprotected .get() since we know for a fact there's plenty of cards there
    // for now
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

    private void takeTurn(Player player) {
        // Do the action phase
        while (player.state.currentActions > 0) {
            // Ask the player for a card to play
            var card = player.playActionCard();
            if (card.isPresent()) {
                if (!card.get().getCategories().contains(Card.Category.ACTION)) {
                    throw new IllegalStateException(String.format(
                            "Player %d was asked for an action card and returned %s, which is not an action card",
                            player.id, card.get().getType().toString()));
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
            if (!card.get().getCategories().contains(Card.Category.TREASURE)) {
                throw new IllegalStateException(String.format(
                        "Player %d was asked for an treasure card and returned %s, which is not a treasure card",
                        player.id, card.get().getType().toString()));
            }
            // Now go and do the card
            executeCard(player, card.get());
        }
        // Done laying down treasure now, let the player execute some buys
        while (player.state.currentBuys > 0) {
            // Pass the player a view into the current tableau
            var cardType = player.buyCard(tableau);
            if (cardType.isPresent()) {
                // Double check the player can afford this, or else he's cheating!
                var tableauStack = tableau.get(cardType.get());
                if (tableauStack.size() == 0) {
                    throw new IllegalStateException(String.format(
                            "Player %d asked to buy %s, but that pile is empty",
                            player.id, cardType.get().toString()));
                }
                int cost = tableauStack.peek().getCost();
                if (cost > player.state.currentMoney) {
                    throw new IllegalStateException(String.format(
                            "Player %d asked to buy %s, but lacks funds to do so (has %d, but card costs %d)",
                            player.id, cardType.get().toString(), player.state.currentMoney, cost));
                }
                // Okeyday then, let's mechanize that purchase
                Card purchasedCard = tableauStack.draw();
                player.state.currentMoney -= purchasedCard.getCost();
                --player.state.currentBuys;
            }
        }
        // Cleanup time!
        player.doCleanup();
    }

    private void executeCard(Player player, Card card) {
        // Handle all the "easy" cards here, and then call other helpers to deal with
        // thornier cards
        switch (card.getType()) {
            case COLONY:
                break;
            case COPPER:
                player.state.currentMoney += 1;
                break;
            case CURSE:
                break;
            case DUCHY:
                break;
            case ESTATE:
                break;
            case GOLD:
                player.state.currentMoney += 3;
                break;
            case PLATINUM:
                player.state.currentMoney += 5;
                break;
            case PROVINCE:
                break;
            case SILVER:
                player.state.currentMoney += 2;
                break;
            case SMITHY:
                // Tell the player to draw 4 more cards
                player.drawToHand(4);
                break;
            case VILLAGER:
                // Draw a card, add two actions
                player.drawToHand(1);
                ++player.state.currentActions;
                ++player.state.currentActions;
                break;
            case WITCH:
                // all players but this one have to draw a curse, in player order (in case we run out)
                int nextPlayerId = player.id + 1;
                if (nextPlayerId >= players.size()) {
                    nextPlayerId = 0;
                }
                var curseStack = tableau.get(Card.Type.CURSE);
                while (nextPlayerId != player.id && curseStack.size() > 0) {
                    players.get(nextPlayerId).deck.gain(curseStack.draw());
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
}
