package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.Turn;
import com.levigilad.javaplay.infra.CardGameLogic;
import com.levigilad.javaplay.infra.entities.CardsDeck;
import com.levigilad.javaplay.infra.entities.GameCard;
import com.levigilad.javaplay.infra.enums.GameCardValues;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class represents Yaniv game logic
 */
public class YanivGameLogic extends CardGameLogic {
    private final int INITIAL_CARD_COUNT = 5;
    private final int MIN_SEQUENCE_LENGTH = 3;
    private final int MIN_DISCARDED_CARDS = 1;
    private final int MIN_DUPLICATES_LENGTH = 2;
    private final int MAX_YANIV_CARD_SCORE = 7;

    /**
     * Checks if player's deck is available for Yaniv
     * @param deck player's deck
     * @return True or False
     */
    public boolean isYaniv(CardsDeck deck) {
        return isYaniv(calculateDeckScore(deck));
    }

    /**
     * Checks if player's score is available for Yaniv
     * @param playerScore Score
     * @return True or False
     */
    private boolean isYaniv(int playerScore) {
        return playerScore <= MAX_YANIV_CARD_SCORE;
    }

    /**
     * Checks if player's deck is available for Assaf
     * @param deck player's deck
     * @param otherPlayerScore Score of the player who declared Yaniv
     * @return True or False
     */
    public boolean isAssaf(CardsDeck deck, int otherPlayerScore) {
        int score = calculateDeckScore(deck);

        return (isYaniv(score) && (score <= otherPlayerScore));
    }

    /**
     * Calculates the score of given deck
     * @param deck player's deck
     * @return Score
     */
    public int calculateDeckScore(CardsDeck deck) {
        int cardsTotalValue = 0;

        Iterator<GameCard> iterator = deck.iterator();

        while (iterator.hasNext()) {
            GameCard card = iterator.next();
            cardsTotalValue += getCardValue(card);
        }

        return cardsTotalValue;
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

        if (cardSeries.size() < MIN_SEQUENCE_LENGTH) {
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
