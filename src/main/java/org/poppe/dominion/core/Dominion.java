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
        VILLAGE_IDIOT,
        VILLAGE_SMITHY,
        WITCH
    }

    private static Strategy StrategyFactory(Player p, Strategies s) {
        switch (s) {
            case VILLAGE_IDIOT:
              return new VillageIdiot(p);
            case VILLAGE_SMITHY:
                return new VillageSmithy(p,1,0);
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
        ArrayList<Strategies> strategiesToUse = new ArrayList<>();
        strategiesToUse.add(Strategies.BIG_MONEY);
        strategiesToUse.add(Strategies.VILLAGE_SMITHY);
        strategiesToUse.add(Strategies.WITCH);

        HashMap<Strategies, Integer> numWinsPerPlayer = new HashMap<>();
        for (var s : strategiesToUse){
            numWinsPerPlayer.put(s, 0);
        }
        int numCleanlyWonGames = 0;

        while (numCleanlyWonGames < 10000) {
            // Each game, shuffle the strategies to eliminate bias for who goes first
            var thisGameStrategies = new ArrayList<>(strategiesToUse);
            Collections.shuffle(thisGameStrategies);
            // Make one of each type of player we know about, and let 'em face off against
            // each other
            var players = new ArrayList<Player>();
            for (int i = 0; i < thisGameStrategies.size(); ++i) {
                var player = constructPlayer(i, thisGameStrategies.get(i));
                players.add(player);
            }
            // Now make the GameEngine
            var gameEngine = new GameEngine(players);
            gameEngine.playGame();

            var scores = new ArrayList<Integer>();
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
        // Turn winning numbers into %
        HashMap<Strategies,Float> winningPercentages = new HashMap<>();
        for (HashMap.Entry<Strategies,Integer> entry : numWinsPerPlayer.entrySet()){
            float wins = entry.getValue();
            winningPercentages.put(entry.getKey(), wins / numCleanlyWonGames * 100);
        }
        System.out.println(String.format("Number of games won out of %d",numCleanlyWonGames));
        System.out.println(String.format("Winning %%: %s",winningPercentages));
    }
}
