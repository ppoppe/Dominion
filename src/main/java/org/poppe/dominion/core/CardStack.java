package org.poppe.dominion.core;

import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author poppep
 */

// We will interpret "last" on the list as being the top of the stack, and
// "first" to be the bottom
public class CardStack {
    protected LinkedList<Card> stack;

    public CardStack() {
        stack = new LinkedList<>();
    }

    // Alias for removeLast, i.e. from the top
    final protected Card draw() {
        return stack.removeLast();
    }

    /** Alias for addLast, i.e. on to top **/
    final protected void gain(Card card) {
        stack.addLast(card);
    }

    /** Alias for addFirst, i.e. on bottom of stack */
    final protected void gainToBottom(Card card) {
        stack.addFirst(card);
    }

    // Alias for addLast, i.e. on to top
    final protected void gain(CardStack stack) {
        while (stack.numLeft() > 0) {
            this.gain(stack.draw());
        }
    }

    final protected Card peek() {
        return stack.peek();
    }

    final protected int numLeft() {
        return stack.size();
    }

    final protected void shuffle() {
        Collections.shuffle(stack);
    }

    final protected Card pullCard(int index) {
        if (index < 0 && index >= stack.size()) {
            // That index is not in range for this stack
            throw new IllegalStateException("Index is out of range for this CardStack");
        } else {
            return stack.remove(index);
        }
    }

    // How many of a given card exist in this card stack
    final public int getCount(Card.Name name) {
        int count = 0;
        for (var card : stack) {
            if (card.getName() == name) {
                count++;
            }
        }
        return count;
    }

    // Total treasure value of all cards in the stack (only really considers simple
    // cards like Copper/Silver/etc)
    final public int getTotalTreasure() {
        int totalTreasure = 0;
        for (var card : stack) {
            totalTreasure += card.getExtraTreasure();
        }
        return totalTreasure;
    }
}
