package org.poppe.dominion.core;

import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author poppep
 */

// We will interpret "last" on the list as being the top of the stack, and
// "first" to be the bottom
public class CardStack extends LinkedList<Card> {

    public CardStack() {
    }

    // Alias for removeLast, i.e. from the top
    final protected Card draw() {
        return this.removeLast();
    }

    // Alias for addLast, i.e. on to top
    final protected void gain(Card card) {
        this.addLast(card);
    }

    final protected void shuffle() {
        Collections.shuffle(this);
    }

    final protected Card pullCard(int index) {
        if (index < 0 && index >= this.size()) {
            // That index is not in range for this stack
            throw new IllegalStateException("Index is out of range for this CardStack");
        } else {
            return this.remove(index);
        }
    }

    // How many of a given type exist in this card stack
    final public int getCount(Card.Type type) {
        int count = 0;
        for (var card : this) {
            if (card.getType() == type) {
                count++;
            }
        }
        return count;
    }

    // Total treasure value of all cards in the stack (only really considers simple
    // cards like Copper/Silver/etc)
    final public int getTotalTreasure() {
        int totalTreasure = 0;
        for (var card : this) {
            totalTreasure += card.getExtraTreasure();
        }
        return totalTreasure;
    }
}
