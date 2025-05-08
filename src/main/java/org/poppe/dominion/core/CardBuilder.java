package org.poppe.dominion.core;

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
        action_attack.add(Card.Type.ACTION);
        action_attack.add(Card.Type.ATTACK);
    };

    // Shortcut to make an ArrayList containing Treasure
    private final ArrayList<Card.Type> treasure = new ArrayList<>(Collections.singletonList(Card.Type.TREASURE));
    // Shortcut to make an ArrayList containing Victory
    private final ArrayList<Card.Type> victory = new ArrayList<>(Collections.singletonList(Card.Type.VICTORY));
    // Shortcut to make an ArrayList containing Action
    private final ArrayList<Card.Type> action = new ArrayList<>(Collections.singletonList(Card.Type.ACTION));
    // Attack Cards
    private final ArrayList<Card.Type> action_attack = new ArrayList<>();

    // "Universal" Make variants
    public Card makeCard(Card.Name type) {
        cardUniqueId++;
        return switch (type) {
            case COPPER -> makeCopper(cardUniqueId);
            case SILVER -> makeSilver(cardUniqueId);
            case GOLD -> makeGold(cardUniqueId);
            case PLATINUM -> makePlatinum(cardUniqueId);
            case ESTATE -> makeEstate(cardUniqueId);
            case CURSE -> makeCurse(cardUniqueId);
            case DUCHY -> makeDuchy(cardUniqueId);
            case PROVINCE -> makeProvince(cardUniqueId);
            case COLONY -> makeColony(cardUniqueId);
            case CELLAR -> makeCellar(cardUniqueId);
            case SMITHY -> makeSmithy(cardUniqueId);
            case VILLAGE -> makeVillage(cardUniqueId);
            case WITCH -> makeWitch(cardUniqueId);
            default -> null;
        };
    }

    public CardStack makeCards(Card.Name type, int num) {
        CardStack cards = new CardStack();
        for (int i = 0; i < num; i++) {
            cards.gain(makeCard(type));
        }
        return cards;
    }

    // Treasure Cards
    private Card makeCopper(int id) {
        return new Card.Builder(id, Card.Name.COPPER, treasure, "Copper", 0)
                .withExtraTreasure(1).build();
    }

    private Card makeSilver(int id) {
        return new Card.Builder(id, Card.Name.SILVER, treasure, "Silver", 3)
                .withExtraTreasure(2).build();
    }

    private Card makeGold(int id) {
        return new Card.Builder(id, Card.Name.GOLD, treasure, "Gold", 6)
                .withExtraTreasure(3).build();
    }

    private Card makePlatinum(int id) {
        return new Card.Builder(id, Card.Name.PLATINUM, treasure, "Platinum", 9)
                .withExtraTreasure(5).build();
    }

    // VP Cards
    private Card makeCurse(int id) {
        return new Card.Builder(id, Card.Name.CURSE, victory, "Curse", 0)
                .withExtraVP(-1).build();
    }

    private Card makeEstate(int id) {
        return new Card.Builder(id, Card.Name.ESTATE, victory, "Estate", 2)
                .withExtraVP(1).build();
    }

    private Card makeDuchy(int id) {
        return new Card.Builder(id, Card.Name.DUCHY, victory, "Duchy", 5)
                .withExtraVP(3).build();
    }

    private Card makeProvince(int id) {
        return new Card.Builder(id, Card.Name.PROVINCE, victory, "Province", 8)
                .withExtraVP(6).build();
    }

    private Card makeColony(int id) {
        return new Card.Builder(id, Card.Name.COLONY, victory, "Colony", 11)
                .withExtraVP(10).build();
    }

    // Action Cards
    private Card makeCellar(int id) {
        return new Card.Builder(id, Card.Name.CELLAR, action, "Cellar", 2)
                .withExtraActions(1).build();
    }

    private Card makeSmithy(int id) {
        return new Card.Builder(id, Card.Name.SMITHY, action, "Smithy", 4)
                .withExtraCards(3).build();
    }

    private Card makeVillage(int id) {
        return new Card.Builder(id, Card.Name.VILLAGE, action, "Village", 3)
                .withExtraActions(2).withExtraCards(1).build();
    }

    private Card makeWitch(int id) {
        return new Card.Builder(id, Card.Name.WITCH, action_attack, "Witch", 5)
                .withExtraCards(2).build();
    }
}
