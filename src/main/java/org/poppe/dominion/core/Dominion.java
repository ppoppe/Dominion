package org.poppe.dominion.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.poppe.dominion.strategies.BigMoney;
import org.poppe.dominion.strategies.Strategy;
import org.poppe.dominion.strategies.VillageIdiot;
import org.poppe.dominion.strategies.VillageSmithy;
import org.poppe.dominion.strategies.Witch;

/**
 *
 * @author poppep
 */
public class Dominion {
    public static final boolean DO_PRINTING = false;

    private enum Strategies{
        BIG_MONEY,
        //VILLAGE_IDIOT,
        VILLAGE_SMITHY,
        WITCH
    }

    private static Strategy StrategyFactory(Player p, Strategies s) {
        switch (s) {
            //case VILLAGE_IDIOT:
            //   return new VillageIdiot(p);
            case VILLAGE_SMITHY:
                return new VillageSmithy(p);
            case WITCH:
                return new Witch(p, 2);
            default:
                return new BigMoney(p);
        }

    }
    private static Player constructPlayer(int id, Strategies strat){
        Player p = new Player(id);
        Strategy s = StrategyFactory(p, strat);
        p.setStrategy(s);
        return p;
    }

    public static void main(String[] args) {
        int numPlayers = Strategies.values().length;
        HashMap<Strategies, Integer> numWinsPerPlayer = new HashMap<>(numPlayers);
        for (var s : Strategies.values()){
            numWinsPerPlayer.put(s, 0);
        }
        int numCleanlyWonGames = 0;
        ArrayList<Strategies> availableStrategies = new ArrayList<>(numPlayers);
        for (var strat : Strategies.values()) {
            availableStrategies.add(strat);
        }

        while (numCleanlyWonGames < 10000) {
            // Each game, shuffle the strategies to eliminate bias for who goes first
            var thisGameStrategies = new ArrayList<>(availableStrategies);
            Collections.shuffle(thisGameStrategies);
            // Make one of each type of player we know about, and let 'em face off against
            // each other
            var players = new ArrayList<Player>(numPlayers);
            for (int i = 0; i < thisGameStrategies.size(); ++i) {
                var player = constructPlayer(i, thisGameStrategies.get(i));
                players.add(player);
            }
            // Now make the GameEngine
            var gameEngine = new GameEngine(players);
            gameEngine.playGame();

            var scores = new ArrayList<Integer>(numPlayers);
            for (var p : players) {
                scores.add(p.state.finalVP);
            }

            var maxScore = Collections.max(scores);
            var playersAtMax = Collections.frequency(scores, maxScore);
            if (playersAtMax == 1) {
                ++numCleanlyWonGames;
                var maxIndex = scores.indexOf(maxScore);
                var winningStrategy = thisGameStrategies.get(maxIndex);
                numWinsPerPlayer.put(winningStrategy, numWinsPerPlayer.get(winningStrategy) + 1);
            } else {
                // ties get ignored
            }

            // Print what the final score was
            players.forEach((player) -> {
                if (DO_PRINTING) {
                    System.out.println(String.format("%s (Player #%d) had a score of %d",
                            player.strategy.name, player.id, player.state.finalVP));
                }
            });
        }
        System.out.println(String.format("Number of games won out of %d",numCleanlyWonGames));
        System.out.println(numWinsPerPlayer);
    }
}
