package org.poppe.dominion.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.poppe.dominion.strategies.BigMoney;
import org.poppe.dominion.strategies.BigMoney_Plus;
import org.poppe.dominion.strategies.Strategy;
import org.poppe.dominion.strategies.VillageSmithy;

/**
 *
 * @author poppep
 */
public class Dominion {
    public static final boolean DO_PRINTING = false;

    private enum Strategies {
        BIG_MONEY,
        BIG_MONEY2,
        CELLAR,
        MINE,
        SMITHY,
        VILLAGE_IDIOT,
        VILLAGE_SMITHY,
        WITCH
    }

    private static Strategy StrategyFactory(Player p, Strategies s) {
        switch (s) {
            case BIG_MONEY2:
                return new BigMoney(p);
            case CELLAR:
                return new BigMoney_Plus.Builder(p).withCellars(1).build();
            case MINE:
                return new BigMoney_Plus.Builder(p).withMines(1).build();
            case SMITHY:
                return new BigMoney_Plus.Builder(p).withSmithies(2).build();
            case VILLAGE_IDIOT:
                return new BigMoney_Plus.Builder(p).withVillages(10).build();
            case VILLAGE_SMITHY:
                return new VillageSmithy(p, 2, 0);
            case WITCH:
                return new BigMoney_Plus.Builder(p).withWitches(2).build();
            default:
                return new BigMoney(p);
        }

    }

    private static Player constructPlayer(int id, Strategies strat) {
        Player p = new Player(id);
        Strategy s = StrategyFactory(p, strat);
        p.setStrategy(s);
        return p;
    }

    public static void main(String[] args) {
        int numGamesToPlay = 10000;
        int totalTurnsTaken = 0;
        ArrayList<Strategies> strategiesToUse = new ArrayList<>();
        strategiesToUse.add(Strategies.BIG_MONEY);
        strategiesToUse.add(Strategies.MINE);
        strategiesToUse.add(Strategies.SMITHY);

        HashMap<Strategies, Integer> numWinsPerPlayer = new HashMap<>();
        HashMap<Strategies, Integer> numTiesPerPlayer = new HashMap<>();
        for (var s : strategiesToUse) {
            numWinsPerPlayer.put(s, 0);
            numTiesPerPlayer.put(s, 0);
        }

        for (int gameNum = 0; gameNum < numGamesToPlay; ++gameNum) {
            // Each game, shuffle the strategies to eliminate bias for who goes first
            Collections.rotate(strategiesToUse, 1);
            // Make one of each type of player we know about, and let 'em face off against
            // each other
            var players = new ArrayList<Player>();
            var playerScores = new ArrayList<Integer>();
            Map<Player, Strategies> playerStrats = new HashMap<>();
            for (int i = 0; i < strategiesToUse.size(); ++i) {
                var player = constructPlayer(i, strategiesToUse.get(i));
                players.add(player);
                playerStrats.put(player, strategiesToUse.get(i));
            }
            // Now make the GameEngine
            var gameEngine = new GameEngine(players);
            gameEngine.playGame();

            // Figure out scores and who won
            for (var p : players) {
                playerScores.add(p.state.finalVP);
                totalTurnsTaken += p.state.turnsTaken;
            }
            List<Player> highScoringPlayers = new ArrayList<>();
            for (var p : players) {
                if (p.state.finalVP == Collections.max(playerScores)) {
                    highScoringPlayers.add(p);
                }
            }
            if (highScoringPlayers.size() == 1) {
                // Somebody won outright, yay!
                var winningStrat = playerStrats.get(highScoringPlayers.get(0));
                // Increment number of wings for that strategy
                numWinsPerPlayer.merge(winningStrat, 1, Integer::sum);
            } else {
                // Look at number of turns to break tie
                List<Integer> numTurns = new ArrayList<>();
                for (var p : highScoringPlayers) {
                    numTurns.add(p.state.turnsTaken);
                }
                // Resolve ties if we can
                List<Player> fewestTurnPlayers = new ArrayList<>();
                for (var p : highScoringPlayers) {
                    if (p.state.turnsTaken == Collections.min(numTurns)) {
                        fewestTurnPlayers.add(p);
                    }
                }
                if (fewestTurnPlayers.size() == 1) {
                    // Only one player with a high score had the minimum number of turns, so he wins
                    // outright
                    var winningStrat = playerStrats.get(fewestTurnPlayers.get(0));
                    // Increment number of wins for that strategy
                    numWinsPerPlayer.merge(winningStrat, 1, Integer::sum);
                } else {
                    // Iterate through players at max score and the appropriate number of turns, and
                    // register ties
                    for (var p : fewestTurnPlayers) {
                        if (p.state.turnsTaken == Collections.min(numTurns)) {
                            // Increment number of ties for that strategy
                            numTiesPerPlayer.merge(playerStrats.get(p), 1, Integer::sum);
                        }
                    }
                }
            }

            // Print what the final score was
            players.forEach((player) -> {
                if (DO_PRINTING) {
                    System.out.println(String.format("%s (Player #%d) had a score of %d",
                            player.strategy.name, player.id, player.state.finalVP));
                }
            });
        }
        // Turn winning numbers into %
        HashMap<Strategies, Float> winningPercentages = new HashMap<>();
        for (HashMap.Entry<Strategies, Integer> entry : numWinsPerPlayer.entrySet()) {
            float wins = entry.getValue();
            winningPercentages.put(entry.getKey(), wins / numGamesToPlay * 100);
        }
        HashMap<Strategies, Float> tyingPercentages = new HashMap<>();
        for (HashMap.Entry<Strategies, Integer> entry : numTiesPerPlayer.entrySet()) {
            float ties = entry.getValue();
            tyingPercentages.put(entry.getKey(), ties / numGamesToPlay * 100);
        }
        System.out.println(String.format("Winning %%: %s", winningPercentages));
        System.out.println(String.format("Tying %%: %s", tyingPercentages));
        System.out.println(String.format("Average number of turns per game: %f",
                ((float) totalTurnsTaken) / numGamesToPlay / strategiesToUse.size()));
    }
}
