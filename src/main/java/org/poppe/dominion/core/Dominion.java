package org.poppe.dominion.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.poppe.dominion.strategies.BigMoney;
import org.poppe.dominion.strategies.BigMoney_Cellar;
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
        BIG_MONEY2,
        BIG_MONEY_CELLAR,
        VILLAGE_IDIOT,
        VILLAGE_SMITHY,
        WITCH
    }

    private static Strategy StrategyFactory(Player p, Strategies s) {
        switch (s) {
            case BIG_MONEY2:
                return new BigMoney(p);
            case BIG_MONEY_CELLAR:
                return new BigMoney_Cellar(p,2);
            case VILLAGE_IDIOT:
                return new VillageIdiot(p);
            case VILLAGE_SMITHY:
                return new VillageSmithy(p, 2, 0);
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
        int numGamesToPlay = 10000;
        int totalTurnsTaken = 0;
        ArrayList<Strategies> strategiesToUse = new ArrayList<>();
        strategiesToUse.add(Strategies.BIG_MONEY);
        strategiesToUse.add(Strategies.BIG_MONEY_CELLAR);
        strategiesToUse.add(Strategies.WITCH);
 
        HashMap<Strategies, Integer> numWinsPerPlayer = new HashMap<>();
        HashMap<Strategies, Integer> numTiesPerPlayer = new HashMap<>();
        for (var s : strategiesToUse){
            numWinsPerPlayer.put(s, 0);
            numTiesPerPlayer.put(s, 0);
        }

        for(int gameNum = 0; gameNum < numGamesToPlay; ++gameNum) {
            // Each game, shuffle the strategies to eliminate bias for who goes first
            Collections.rotate(strategiesToUse,1);
            // Make one of each type of player we know about, and let 'em face off against
            // each other
            var players = new ArrayList<Player>();
            for (int i = 0; i < strategiesToUse.size(); ++i) {
                var player = constructPlayer(i, strategiesToUse.get(i));
                players.add(player);
            }
            // Now make the GameEngine
            var gameEngine = new GameEngine(players);
            gameEngine.playGame();

            var scores = new ArrayList<Integer>();
            for (var p : players) {
                scores.add(p.state.finalVP);
                totalTurnsTaken += p.state.turnsTaken;
            }

            var maxScore = Collections.max(scores);
            var playersAtMax = Collections.frequency(scores, maxScore);
            if (playersAtMax == 1) {
                var winningIndex = scores.indexOf(maxScore);
                numWinsPerPlayer.merge(strategiesToUse.get(winningIndex), 1, Integer::sum);
            } else {
                // Find indices of all players who got the tying score
                List<Integer> tyingIndices = new ArrayList<>();
                List<Integer> numTurns = new ArrayList<>();
                for (int i = 0; i < scores.size(); ++i){
                    if (scores.get(i) == maxScore)
                    {
                        tyingIndices.add(i);
                        numTurns.add(players.get(i).state.turnsTaken);
                    }
                }
                // Resolve ties if we can
                int minNumTurns = Collections.min(numTurns);
                int numAtMinTurns = Collections.frequency(numTurns, minNumTurns);
                if (numAtMinTurns == 1)
                {
                    // Only one player with a high score had the minimum number of turns, so he wins outright
                    var winningIndex = tyingIndices.get(numTurns.indexOf(minNumTurns));
                    numWinsPerPlayer.merge(strategiesToUse.get(winningIndex), 1, Integer::sum);
                }
                else
                {
                    // Iterate through players at max score and the appropriate number of turns, and register ties
                    for (int i = 0; i < tyingIndices.size(); ++i){
                        if (numTurns.get(i) == minNumTurns){
                            int ind = tyingIndices.get(i);
                            numTiesPerPlayer.merge(strategiesToUse.get(ind), 1, Integer::sum);
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
        HashMap<Strategies,Float> winningPercentages = new HashMap<>();
        for (HashMap.Entry<Strategies,Integer> entry : numWinsPerPlayer.entrySet()){
            float wins = entry.getValue();
            winningPercentages.put(entry.getKey(), wins / numGamesToPlay * 100);
        }
        HashMap<Strategies,Float> tyingPercentages = new HashMap<>();
        for (HashMap.Entry<Strategies,Integer> entry : numTiesPerPlayer.entrySet()){
            float ties = entry.getValue();
            tyingPercentages.put(entry.getKey(), ties / numGamesToPlay * 100);
        }
        System.out.println(String.format("Winning %%: %s",winningPercentages));
        System.out.println(String.format("Tying %%: %s",tyingPercentages));
        System.out.println(String.format("Average number of turns per game: %f",((float)totalTurnsTaken) / numGamesToPlay));
    }
}
