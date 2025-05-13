package org.poppe.dominion.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.poppe.dominion.strategies.BigMoney;
import org.poppe.dominion.strategies.BigMoney_Plus;
import org.poppe.dominion.strategies.Strategy;

/**
 *
 * @author poppep
 */
public class Dominion {
    public static final boolean DO_PRINTING = false;

    public static void main(String[] args) {
        int numGamesToPlay = 1;
        int totalTurnsTaken = 0;

        // Make some strategies we can use below if we want to
        Strategy bigMoney = new BigMoney();
        Strategy bigMoneyWitch = new BigMoney_Plus.Builder().withWitches(2).build();
        Strategy bigMoneySmithy = new BigMoney_Plus.Builder().withSmithies(2).build();
        Strategy bigMoneyCellar = new BigMoney_Plus.Builder().withCellars(2).build();

        List<Player> players = new ArrayList<Player>();
        int id = 0;
        players.add(new Player(id));
        players.get(id).setStrategy(bigMoney);
        ++id;
        players.add(new Player(id));
        players.get(id).setStrategy(bigMoneyWitch);
        ++id;
        // players.add(new Player(id));
        // players.get(id).setStrategy(bigMoneySmithy);
        // ++id;
        // players.add(new Player(id));
        // players.get(id).setStrategy(bigMoneyCellar);
        // ++id;

        //  PlayerID, Wins/Ties
        Map<Integer, Integer> numWinsPerPlayer = new HashMap<>();
        Map<Integer, Integer> numTiesPerPlayer = new HashMap<>();
        Map<Integer, Player> idToPlayer = new HashMap<>();
        for (var p : players) {
            numWinsPerPlayer.put(p.id, 0);
            numTiesPerPlayer.put(p.id, 0);
            idToPlayer.put(p.id, p);
        }

        for (int gameNum = 0; gameNum < numGamesToPlay; ++gameNum) {
            // Each game, shuffle the strategies to eliminate bias for who goes first
            Collections.rotate(players, 1);

            // Now make the GameEngine
            var gameEngine = new GameEngine(players);
            gameEngine.playGame();

            // Print what the final score was
            players.forEach((player) -> {
                if (DO_PRINTING) {
                    System.out.println(String.format("%s (Player #%d) had a score of %d",
                            player.strategy.name, player.id, player.state.finalVP));
                }
            });

            for (var p : players) {
                if (p.state.gameResult == GameResult.WIN) {
                    // Merge here will increment the value at the key p
                    numWinsPerPlayer.merge(p.id, 1, Integer::sum);
                    System.out.println(p.toString());
                } else if (p.state.gameResult == GameResult.TIE) {
                    // Merge here will increment the value at the key p
                    numTiesPerPlayer.merge(p.id, 1, Integer::sum);
                }
                totalTurnsTaken += p.state.turnsTaken;
            }

        }
        // Turn winning numbers into %
        HashMap<Player, Float> winningPercentages = new HashMap<>();
        for (HashMap.Entry<Integer, Integer> entry : numWinsPerPlayer.entrySet()) {
            float wins = entry.getValue();
            Player p = idToPlayer.get(entry.getKey());
            winningPercentages.put(p, wins / numGamesToPlay * 100);
        }
        HashMap<Player, Float> tyingPercentages = new HashMap<>();
        for (HashMap.Entry<Integer, Integer> entry : numTiesPerPlayer.entrySet()) {
            float ties = entry.getValue();
            Player p = idToPlayer.get(entry.getKey());
            tyingPercentages.put(p, ties / numGamesToPlay * 100);
        }
        System.out.println(String.format("Winning %%: %s", winningPercentages));
        System.out.println(String.format("Tying %%: %s", tyingPercentages));
        System.out.println(String.format("Average number of turns per game: %f",
                ((float) totalTurnsTaken) / numGamesToPlay / players.size()));
    }
}
