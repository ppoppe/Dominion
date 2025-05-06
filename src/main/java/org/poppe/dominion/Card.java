package org.poppe.dominion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author poppep
 */
public final class Card {

    public enum Type {
        COPPER,
        SILVER,
        GOLD,
        PLATINUM,
        ESTATE,
        DUCHY,
        PROVINCE,
        COLONY,
        CURSE,
        SMITHY,
        VILLAGER,
        WITCH
    }

    public enum Category {
        ACTION,
        ATTACK,
        TREASURE,
        VICTORY
    }

    private final int id;
    private final Type type;
    private final ArrayList<Category> category;
    private final String name;
    private final int cost;
    private final int extraTreasure;
    private final int extraVP;
    private final int extraCards;
    private final int extraActions;
    private final int extraBuys;

    private Card(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.category = builder.category;
        this.name = builder.name;
        this.cost = builder.cost;
        this.extraTreasure = builder.extraTreasure;
        this.extraVP = builder.extraVP;
        this.extraCards = builder.extraCards;
        this.extraActions = builder.extraActions;
        this.extraBuys = builder.extraBuys;
    }

    // Getters

    public Type getType() {
        return type;
    }

    public List<Category> getCategory() {
        return Collections.unmodifiableList(category);
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getExtraTreasure() {
        return extraTreasure;
    }

    public int getExtraVP() {
        return extraVP;
    }

    public int getExtraCards() {
        return extraCards;
    }

    public int getExtraActions() {
        return extraActions;
    }

    public int getExtraBuys() {
        return extraBuys;
    }

    // Builder Pattern implementation
    public static class Builder {
        // Mandatory
        private final int id;
        private final Type type;
        private final ArrayList<Category> category;
        private final String name;
        private final int cost;

        // Optional
        private int extraTreasure = 0;
        private int extraVP = 0;
        private int extraCards = 0;
        private int extraActions = 0;
        private int extraBuys = 0;

        public Builder(int id, Type type, List<Category> category, String name, int cost) {
            // Basic validation on fields
            if (type == null) {
                throw new IllegalArgumentException("Card Type cannot be null.");
            }
            if (category == null || category.isEmpty()) {
                throw new IllegalArgumentException("Card Category cannot be null or empty.");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Card Name cannot be null or empty.");
            }
            if (cost < 0) {
                throw new IllegalArgumentException("Card Cost cannot be negative.");
            }
            this.id = id;
            this.type = type;
            this.category = new ArrayList<>(category);
            this.name = name;
            this.cost = cost;
        }

        public Builder withExtraTreasure(int extraTreasure) {
            if (extraTreasure < 1) {
                throw new IllegalArgumentException("Number of extra treasure cannot be less than 1.");
            }
            this.extraTreasure = extraTreasure;
            return this; // returns Builder instance so we can chain calls of them together
        }

        public Builder withExtraVP(int extraVP) {
            this.extraVP = extraVP;
            return this; // returns Builder instance so we can chain calls of them together
        }

        public Builder withExtraCards(int extraCards) {
            if (extraCards < 1) {
                throw new IllegalArgumentException("Number of extra cards cannot be less than 1.");
            }
            this.extraCards = extraCards;
            return this; // returns Builder instance so we can chain calls of them together
        }

        public Builder withExtraActions(int extraActions) {
            if (extraActions < 1) {
                throw new IllegalArgumentException("Number of extra actions cannot be less than 1.");
            }
            this.extraActions = extraActions;
            return this; // returns Builder instance so we can chain calls of them together
        }

        public Builder withExtraBuys(int extraBuys) {
            if (extraBuys < 1) {
                throw new IllegalArgumentException("Number of extra buys cannot be less than 1.");
            }
            this.extraBuys = extraBuys;
            return this; // returns Builder instance so we can chain calls of them together
        }

        // Actual build method
        public Card build() {
            return new Card(this); // use private constructor and chain together whatever Builders were specified
        }
    }

}
