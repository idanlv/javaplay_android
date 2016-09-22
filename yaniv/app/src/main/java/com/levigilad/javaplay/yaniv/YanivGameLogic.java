package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.Turn;
import com.levigilad.javaplay.infra.CardGameLogic;
import com.levigilad.javaplay.infra.entities.CardsDeck;
import com.levigilad.javaplay.infra.entities.GameCard;
import com.levigilad.javaplay.infra.enums.GameCardValues;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by User on 22/09/2016.
 */
public class YanivGameLogic extends CardGameLogic {
    private final int INITIAL_CARD_COUNT = 5;
    private final int MINIMUM_SEQUENCE_LENGTH = 3;
    private final int MIN_DISCARDED_CARDS = 1;
    private final int MIN_DUPLICATES_LENGTH = 2;

    @Override
    public Turn playTurn(Turn currentTurnData) {
        return null;
    }

    /**
     * Checks if user made a valid discard operation
     * @param discardCards cards
     * @return True or False
     */
    public boolean isCardsDiscardValid(LinkedList<GameCard> discardCards) {
        return ((discardCards.size() == MIN_DISCARDED_CARDS)
                || isSequence(discardCards)
                || isDuplicates(discardCards));
    }

    /**
     * Checks if this cards have the same value
     * @param cards cards
     * @return True or False
     */
    private boolean isDuplicates(LinkedList<GameCard> cards) {
        GameCardValues value = cards.peek().getValue();

        if (cards.size() < MIN_DUPLICATES_LENGTH) {
            return false;
        }

        for (GameCard card : cards) {
            if (value != card.getValue()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if this card series is a valid sequence
     * @param cardSeries cards
     * @return True or False
     */
    private boolean isSequence(LinkedList<GameCard> cardSeries) {
        int previousValue = -1;

        if (cardSeries.size() < MINIMUM_SEQUENCE_LENGTH) {
            return false;
        }

        for (GameCard card : cardSeries) {
            int currentValue = card.getValue().getNumericValue();
            // First value in sequence
            if (previousValue == -1) {
                previousValue = currentValue;
            }
            // You can place a joker inside your sequence and it will act as the next number in
            // series
            else if ((currentValue == GameCardValues.JOKER.getNumericValue()) ||
                    (currentValue == previousValue + 1)) {
                previousValue++;
            }
            // Current number does not continue the sequence
            else {
                return false;
            }
        }

        return true;
    }

    /**
     * This method returns the numeric value of the card within game rules
     * @param card
     * @return
     */
    private int getCardValue(GameCard card) {
        switch (card.getValue()) {
            case TEN:
            case PRINCE:
            case QUEEN:
            case KING:
                return 10;
            default:
                return card.getValue().getNumericValue();
        }
    }
}
