package org.poppe.dominion.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.poppe.dominion.core.Dominion.DO_PRINTING;

/**
 *
 * @author poppe
 */
public class GameEngine {

    private final ArrayList<Player> players;
    public int turn = 0;
    public Tableau tableau;

    public GameEngine(List<Player> players) {
        // Build the tableau before we do anything else
        this.tableau = new Tableau.Builder(players.size())
                .withCard(Card.Name.CELLAR)
                .withCard(Card.Name.MINE)
                .withCard(Card.Name.SMITHY)
                .withCard(Card.Name.VILLAGE)
                .withCard(Card.Name.WITCH)
                .build();

        this.players = new ArrayList<>(players);

        // Before the game officially starts, initialize players so they make decks,
        // then tell all the players to CleanUp so they draw a starting hand
        this.players.forEach((player) -> {
            player.initalize(tableau);
            player.doCleanup();
        });
    }

    public void playGame() {
        // For now, just end games when PROVINCE pile is done.
        boolean keepGoing = true;
        while (keepGoing) {
            ++turn;
            log("Turn %d:", turn);
            // Iterate through players and let each take their turn
            for (var player : players) {
                takeTurn(player);
                ++player.state.turnsTaken;
                if (tableau.numLeft(Card.Name.PROVINCE) < 1) {
                    keepGoing = false;
                    break;
                }
            }
        }
        // Now that game is over, score each player and do some printing
        players.forEach((player) -> {
            // logHand(player);
            // logDeck(player);
            // logDiscard(player);
            score(player);
        });
        determineWinner(players);
    }

    private void takeTurn(Player player) {
        logHand(player);
        // Do the action phase
        boolean keepGoing = true;
        while (keepGoing) {
            // Ask the player for a card to play
            var card = player.playActionCard();
            if (card.isPresent()) {
                if (!card.get().getTypes().contains(Card.Type.ACTION)) {
                    throw new IllegalStateException(String.format(
                            "Player %d was asked for an action card and returned %s, which is not an action card",
                            player.id, card.get().getName().toString()));
                }
                log("  %s plays a %s", player.name, card.get().toString());
                // Decrement actions for playing the card now
                --player.state.currentActions;
                // Now go and do the card
                executeCard(player, card.get());
            }
            // If they didn't pass me a card, then we can break out of the while loop and go
            // to the next phase
            else {
                keepGoing = false;
            }
            // If out of actions, then go to next state
            if (player.state.currentActions < 1) {
                keepGoing = false;
            }
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
        log("  %s has $%d", player.name, player.state.currentMoney);
        while (player.state.currentBuys > 0) {
            // Pass the player a view into the current tableau
            var cardName = player.buyCard(tableau);
            if (cardName.isPresent()) {
                var name = cardName.get();
                // Double check the player can afford this, or else he's cheating!
                if (tableau.numLeft(name) < 1) {
                    throw new IllegalStateException(String.format(
                            "Player %d asked to buy %s, but that pile is empty",
                            player.id, name.toString()));
                }
                // Look at a card in the pile so we can see how much it costs
                int cost = tableau.getStack(name).examine(0).getCost();
                if (cost > player.state.currentMoney) {
                    throw new IllegalStateException(String.format(
                            "Player %d asked to buy %s, but lacks funds to do so (has %d, but card costs %d)",
                            player.id, name.toString(), player.state.currentMoney, cost));
                }
                // Okeyday then, let's mechanize that purchase
                var purchasedCard = tableau.pullCard(name).get(); // returns optional, but we know it'll be there
                log("   %s buys a %s", player.name, purchasedCard.toString());
                player.state.currentMoney -= purchasedCard.getCost();
                --player.state.currentBuys;
                player.discardPile.gain(purchasedCard);
            } else {
                // Doesn't want to buy anything
                break;
            }
        }
        // Cleanup time!
        player.doCleanup();
    }

    private void executeCard(Player player, Card card) {
        // Handle all the "easy" cards here, and then call other helpers to deal with
        // thornier cards

        // Lay the card down
        // TODO Will eventually need to steer cards to other areas if they fit certain
        // criteria
        player.playArea.gain(card);
        switch (card.getName()) {
            case CURSE, ESTATE, COLONY, PROVINCE, DUCHY -> {
            }
            case COPPER, SILVER, GOLD, PLATINUM -> {
                player.state.currentMoney += card.getExtraTreasure();
            }
            case CELLAR -> {
                player.state.currentActions += card.getExtraActions();
                // Tell the player to discard cards so they can draw more
                var cellarCardsToDiscard = player.respondToCellar();
                if (DO_PRINTING) {
                    for (int i = 0; i < cellarCardsToDiscard.numLeft(); ++i) {
                        System.out.println(String.format("    Discarding: %s",
                                cellarCardsToDiscard.examine(i).toString()));
                    }
                }
                int numDiscarding = cellarCardsToDiscard.numLeft();
                // Move those cards to their discard pile
                player.discardPile.gain(cellarCardsToDiscard);
                player.drawToHand(numDiscarding);
            }
            case MINE -> {
                var mineTrashGain = player.respondToMine(tableau);
                if (mineTrashGain.getFirst().isPresent()) {
                    Card cardToTrash = mineTrashGain.getFirst().get();
                    // Better be a treasure
                    if (!cardToTrash.getTypes().contains(Card.Type.TREASURE)) {
                        throw new IllegalStateException(String.format(
                                "Player %d was asked for an treasure card and returned %s, which is not a treasure card",
                                player.id, cardToTrash.getName().toString()));
                    }
                    if (DO_PRINTING) {
                        System.out.println(
                                String.format("   %s trashes a %s", player.name, cardToTrash.toString()));
                    }
                    tableau.trashPile.gain(cardToTrash);
                    if (mineTrashGain.getSecond().isPresent()) {
                        Card.Name name = mineTrashGain.getSecond().get();
                        if (tableau.numLeft(name) < 1) {
                            throw new IllegalStateException(String.format(
                                    "Player %d asked to trade up to %s, but that pile is empty",
                                    player.id, name.toString()));
                        }
                        if (tableau.numLeft(name) < 1) {
                            throw new IllegalStateException(String.format(
                                    "Player %d asked to trash a treasure %s, but that pile is empty",
                                    player.id, name.toString()));
                        }
                        // Look at a card in the pile so we can see how much it costs
                        int cost = tableau.getStack(name).examine(0).getCost();
                        if (cost > cardToTrash.getCost() + 3) {
                            throw new IllegalStateException(String.format(
                                    "Player %d was asked to pick a card costing no more than 3 more than the card to trash (%s) but picked %s",
                                    player.id, cardToTrash.getName(), name));
                        }
                        var gainedCard = tableau.pullCard(name).get(); // returns optional, but we know it'll exist
                        if (DO_PRINTING) {
                            System.out.println(
                                    String.format("   %s buys a %s", player.name, gainedCard.toString()));
                        }
                        // Better be a treasure
                        if (!gainedCard.getTypes().contains(Card.Type.TREASURE)) {
                            throw new IllegalStateException(String.format(
                                    "Player %d was asked pick a treasure card and chose %s, which is not a treasure card",
                                    player.id, gainedCard.getName().toString()));
                        }
                        // Gain card to hand
                        player.hand.gain(gainedCard);
                    }
                }
            }
            case SMITHY -> {
                // Tell the player to draw 4 more cards
                player.drawToHand(card.getExtraCards());
            }
            case VILLAGE -> {
                // Draw a card, add two actions
                player.drawToHand(card.getExtraCards());
                player.state.currentActions += card.getExtraActions();
            }
            case WITCH -> {
                // Draw two cards
                player.drawToHand(card.getExtraCards());
                // all players but this one have to draw a curse, in player order (in case we
                // run out)
                for (var p : players){
                    if (p == player){
                        // This is the person playing the witch, they don't get curses
                        continue;
                    }
                    if (tableau.numLeft(Card.Name.CURSE) > 0){
                        p.deck.gain(tableau.pullCard(Card.Name.CURSE).get());
                    }
                }
            }
            default -> {
            }
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

    private void determineWinner(List<Player> players){

            List<Integer> playerScores = new ArrayList<Integer>();
            // Figure out scores and who won
            for (Player p : players) {
                playerScores.add(p.state.finalVP);
            }
            List<Player> highScoringPlayers = new ArrayList<>();
            int maxScore = Collections.max(playerScores);
            for (int i = players.size() - 1; i >= 0; --i) {
                Player p = players.get(i);
                if (p.state.finalVP == maxScore) {
                    highScoringPlayers.add(players.remove(i));
                } else {
                    // if you have less than the max score, you lose!
                    p.state.gameResult = GameResult.LOSS;
                }
            }
            if (highScoringPlayers.size() == 1) {
                // Somebody won outright, yay!
                // Increment number of wins for that strategy
                highScoringPlayers.get(0).state.gameResult = GameResult.WIN;
            } else {
                // Look at number of turns to break tie
                List<Integer> numTurns = new ArrayList<>();
                for (var p : highScoringPlayers) {
                    numTurns.add(p.state.turnsTaken);
                }
                // Resolve ties if we can
                List<Player> fewestTurnPlayers = new ArrayList<>();
                int minTurns = Collections.min(numTurns);
                for (int i = highScoringPlayers.size() - 1; i >= 0; --i) {
                    Player p = highScoringPlayers.get(i);
                    if (p.state.turnsTaken == minTurns) {
                        fewestTurnPlayers.add(highScoringPlayers.remove(i));
                    }
                    else {
                        p.state.gameResult = GameResult.LOSS;
                    }
                }
                if (fewestTurnPlayers.size() == 1) {
                    // Only one player with a high score had the minimum number of turns, so he wins
                    // outright
                    // Increment number of wins for that strategy
                    fewestTurnPlayers.get(0).state.gameResult = GameResult.WIN;
                } else {
                    // Iterate through players at max score and the appropriate number of turns, and
                    // register ties
                    for (var p : fewestTurnPlayers) {
                        p.state.gameResult = GameResult.TIE;
                    }
                }
            }
    }

    public void logHand(Player player)
    {
        if (!DO_PRINTING) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(player.name);
        sb.append(" has ");
        logStack(player.hand, sb);
        sb.append(" in their hand.");
        log(sb.toString());
    }
    public void logDeck(Player player)
    {
        if (!DO_PRINTING) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(player.name);
        sb.append(" has ");
        logStack(player.deck, sb);
        sb.append(" in their deck.");
        log(sb.toString());
    }
    public void logDiscard(Player player)
    {
        if (!DO_PRINTING) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(player.name);
        sb.append(" has ");
        logStack(player.discardPile, sb);
        sb.append(" in their discard.");
        log(sb.toString());
    }

    public void logStack(CardStack stack, StringBuilder sb)
    {
        for (int i = 0; i < stack.numLeft(); ++i)
        {
            sb.append(stack.examine(i).toString());
            sb.append(", ");
        }
    }

    public void log(String s, Object... args) {
        if (DO_PRINTING) {
            System.out.println(String.format(s, args));
        }
    }
}
