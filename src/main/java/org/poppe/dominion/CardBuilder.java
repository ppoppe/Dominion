package org.poppe.dominion;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author poppep
 */
// This will be a singleton class
public class CardBuilder {
    private static CardBuilder cardBuilderInstance;
    private int cardUniqueId = 0;

    public static CardBuilder getInstance() {
        if (cardBuilderInstance == null) {
            cardBuilderInstance = new CardBuilder();
        }
        return cardBuilderInstance;
    }

    private CardBuilder() {
        // Build the complex card types
        action_attack.add(Card.Category.ACTION);
        action_attack.add(Card.Category.ATTACK);
    };

    // Shortcut to make an ArrayList containing Treasure
    private final ArrayList<Card.Category> treasure = new ArrayList<>(Collections.singletonList(Card.Category.TREASURE));
    // Shortcut to make an ArrayList containing Victory
    private final ArrayList<Card.Category> victory = new ArrayList<>(Collections.singletonList(Card.Category.VICTORY));
    // Shortcut to make an ArrayList containing Action
    private final ArrayList<Card.Category> action = new ArrayList<>(Collections.singletonList(Card.Category.ACTION));
    // Attack Cards
    private final ArrayList<Card.Category> action_attack = new ArrayList<>();

    // "Universal" Make variants
    public Card makeCard(Card.Type type) {
        cardUniqueId++;
        return switch (type) {
            case COPPER -> makeCopper(cardUniqueId);
            case SILVER -> makeSilver(cardUniqueId);
            case GOLD -> makeGold(cardUniqueId);
            case PLATINUM -> makePlatinum(cardUniqueId);
            case ESTATE -> makeEstate(cardUniqueId);
            case DUCHY -> makeDuchy(cardUniqueId);
            case PROVINCE -> makeProvince(cardUniqueId);
            case COLONY -> makeColony(cardUniqueId);
            case SMITHY -> makeSmithy(cardUniqueId);
            case VILLAGER -> makeVillager(cardUniqueId);
            case WITCH -> makeWitch(cardUniqueId);
            default -> null;
        };
    }
    public CardStack makeCards(Card.Type type, int num){
        CardStack cards = new CardStack();
        for (int i = 0; i < num; i++) {
            cards.AddToTop(makeCard(type));
        }
        return cards;
    }



    // Treasure Cards
    private Card makeCopper(int id) {
        return new Card.Builder(id, Card.Type.COPPER, treasure, "Copper", 0)
                .withExtraTreasure(1).build();
    }

    private Card makeSilver(int id) {
        return new Card.Builder(id, Card.Type.SILVER, treasure, "Silver", 3)
                .withExtraTreasure(2).build();
    }

    private Card makeGold(int id) {
        return new Card.Builder(id, Card.Type.GOLD, treasure, "Gold", 6)
                .withExtraTreasure(3).build();
    }

    private Card makePlatinum(int id) {
        return new Card.Builder(id, Card.Type.PLATINUM, treasure, "Platinum", 9)
                .withExtraTreasure(5).build();
    }


    // VP Cards
    private Card makeEstate(int id) {
        return new Card.Builder(id, Card.Type.ESTATE, victory, "Estate", 2)
                .withExtraVP(1).build();
    }

    private Card makeDuchy(int id) {
        return new Card.Builder(id, Card.Type.DUCHY, victory, "Duchy", 5)
                .withExtraVP(3).build();
    }

    private Card makeProvince(int id) {
        return new Card.Builder(id, Card.Type.PROVINCE, victory, "Province", 8)
                .withExtraVP(6).build();
    }

    private Card makeColony(int id) {
        return new Card.Builder(id, Card.Type.COLONY, victory, "Colony", 11)
                .withExtraVP(10).build();
    }


    // Action Cards
    private Card makeSmithy(int id) {
        return new Card.Builder(id, Card.Type.SMITHY, action, "Smithy", 4)
                .withExtraCards(3).build();
    }

    private Card makeVillager(int id) {
        return new Card.Builder(id, Card.Type.VILLAGER, action, "Villager", 2)
                .withExtraActions(2).withExtraCards(1).build();
    }


    private Card makeWitch(int id) {
        return new Card.Builder(id, Card.Type.WITCH, action_attack, "Witch", 5)
                .withExtraCards(2).build();
    }
}
