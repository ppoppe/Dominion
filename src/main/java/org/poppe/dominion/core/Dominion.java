package org.poppe.dominion.core;

import java.util.HashMap;

import org.poppe.dominion.strategies.BigMoney;

/**
 *
 * @author poppep
 */
public class Dominion {

    public static void main(String[] args) {

        // For now, just make a couple players
        var players = new HashMap<Integer, Player>();
        Player p1 = new Player(0, "Player 1");
        BigMoney strategy1 = new BigMoney(p1);
        p1.setStrategy(strategy1);
        players.put(p1.id, p1);
        Player p2 = new Player(1, "Player 2");
        BigMoney strategy2 = new BigMoney(p2);
        p2.setStrategy(strategy2);
        players.put(p2.id, p2);

        // Now make the GameEngine
        var gameEngine = new GameEngine(players);
        gameEngine.playGame();

        // Print what the final score was
        players.forEach((playerId, player) -> {

            System.out.println(String.format("%s (Player #%d) had a score of %d",
                    player.strategy.name, playerId, player.state.finalVP));
        });
    }
}
