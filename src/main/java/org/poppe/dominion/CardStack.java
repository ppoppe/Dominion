/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.poppe.dominion;

import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author poppep
 */
public class CardStack {
    private LinkedList<Card> cardStack;

    CardStack() {
    };
    CardStack() {
    };

    final public void AddToTop(Card card) {
        cardStack.addLast(card);
    }

    final public void AddToBottom(Card card) {
        cardStack.addFirst(card);
    }

    final public Card RemoveFromTop() {
        return cardStack.removeLast();
    }

    final public Card RemoveFromBottom() {
        return cardStack.removeFirst();
    }

    final public int Size() {
        return cardStack.size();
    }

    final public void Shuffle() {
        Collections.shuffle(cardStack);
    }

    final public Card GetCard(int index) {
        if (index < 0 && index >= cardStack.size()) {
            // That index is not in range for this stack
            throw new IllegalStateException("Index is out of range for this CardStack");
        } else {
            return cardStack.remove(index);
        }
    }

    // How many of a given type exist in this card stack
    final public int GetCount(Card.Type type) {
        int count = 0;
        for (var card : cardStack) {
            if (card.getType() == type) {
                count++;
            }
        }
        return count;
    }

    // Total treasure value of all cards in the stack (only really considers simple cards like Copper/Silver/etc)
    final public int GetTotalTreasure() {
        int totalTreasure = 0;
        for (var card : cardStack)
        {
            totalTreasure += card.getExtraTreasure();
        }
        return totalTreasure;
    }
}
