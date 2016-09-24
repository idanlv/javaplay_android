package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.GameOfCards;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.GameCard;
import com.levigilad.javaplay.infra.enums.GameCardRanks;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class represents Yaniv game logic
 */
public class YanivGame extends GameOfCards {
    private static final int INITIAL_DEFAULT_CARD_COUNT = 5;
    private static final int MAX_DEFAULT_PLAYERS = 4;
    private final int MAX_DEFAULT_YANIV_CARD_SCORE = 7;
    private final int MIN_SEQUENCE_LENGTH = 3;
    private final int MIN_DISCARDED_CARDS = 1;
    private final int MIN_DUPLICATES_LENGTH = 2;

    /**
     * Constructor
     */
    public YanivGame(int numberOfPlayers, int numberOfStartingCards) {
        super(numberOfPlayers, numberOfStartingCards);
    }

    /**
     * Checks if player's deck is available for Yaniv
     * @param deck player's deck
     * @return True or False
     */
    public boolean canYaniv(DeckOfCards deck) {
        return canYaniv(calculateDeckScore(deck));
    }

    /**
     * Checks if player's score is available for Yaniv
     * @param playerScore Score
     * @return True or False
     */
    private boolean canYaniv(int playerScore) {
        return playerScore <= MAX_DEFAULT_YANIV_CARD_SCORE;
    }

    /**
     * Checks if player's deck is available for Assaf
     * @param deck player's deck
     * @param otherPlayerScore Score of the player who declared Yaniv
     * @return True or False
     */
    public boolean isAssaf(DeckOfCards deck, int otherPlayerScore) {
        int score = calculateDeckScore(deck);

        return (canYaniv(score) && (score <= otherPlayerScore));
    }

    /**
     * Calculates the score of given deck
     * @param deck player's deck
     * @return Score
     */
    public int calculateDeckScore(DeckOfCards deck) {
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
        GameCardRanks value = cards.peek().getRank();

        if (cards.size() < MIN_DUPLICATES_LENGTH) {
            return false;
        }

        for (GameCard card : cards) {
            if (value != card.getRank()) {
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
            int currentValue = card.getRank().getNumericValue();
            // First value in sequence
            if (previousValue == -1) {
                previousValue = currentValue;
            }
            // You can place a joker inside your sequence and it will act as the next number in
            // series
            else if ((currentValue == GameCardRanks.JOKER.getNumericValue()) ||
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
        switch (card.getRank()) {
            case TEN:
            case JACK:
            case QUEEN:
            case KING:
                return 10;
            default:
                return card.getRank().getNumericValue();
        }
    }

    @Override
    public String getDisplayName() {
        return "Yaniv";
    }
}
