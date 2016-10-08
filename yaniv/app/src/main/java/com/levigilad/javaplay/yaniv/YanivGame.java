package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.GameOfCards;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.GameCardRanks;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents Yaniv game logic
 */
public class YanivGame extends GameOfCards {
    
    // Consts
    private static final int INITIAL_DEFAULT_CARD_COUNT = 5;
    private static final int MAX_DEFAULT_PLAYERS = 4;
    private final int MAX_DEFAULT_YANIV_CARD_SCORE = 7;
    private final int MIN_SEQUENCE_LENGTH = 3;
    private final int MIN_DISCARDED_CARDS = 1;
    private final int MIN_DUPLICATES_LENGTH = 2;

    /**
     * Empty constructor
     */
    public YanivGame() {
        this(MAX_DEFAULT_PLAYERS, INITIAL_DEFAULT_CARD_COUNT);
    }

    /**
     * Constructor
     * @param numberOfPlayers number of players in game
     * @param numberOfStartingCards number of starting cards for player
     */
    public YanivGame(int numberOfPlayers, int numberOfStartingCards) {
        super(R.string.yaniv_game_id, R.string.yaniv_description, R.string.yaniv_leaderboard_id,
                numberOfPlayers, numberOfStartingCards);
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

        Iterator<PlayingCard> iterator = deck.iterator();

        while (iterator.hasNext()) {
            PlayingCard card = iterator.next();
            cardsTotalValue += getCardValue(card);
        }

        return cardsTotalValue;
    }

    /**
     * Checks if user made a valid discard operation
     * @param discardCards cards
     * @return True or False
     */
    public boolean isCardsDiscardValid(List<PlayingCard> discardCards) {
        return ((discardCards.size() == MIN_DISCARDED_CARDS)
                || isSequence(discardCards)
                || isDuplicates(discardCards));
    }

    /**
     * Checks if this cards have the same value
     * @param cards cards
     * @return True or False
     */
    private boolean isDuplicates(List<PlayingCard> cards) {
        GameCardRanks value = cards.get(0).getRank();

        if (cards.size() < MIN_DUPLICATES_LENGTH) {
            return false;
        }

        for (PlayingCard card : cards) {
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
    private boolean isSequence(List<PlayingCard> cardSeries) {
        int previousValue = -1;

        if (cardSeries.size() < MIN_SEQUENCE_LENGTH) {
            return false;
        }

        Collections.sort(cardSeries, new Comparator<PlayingCard>() {
            @Override
            public int compare(PlayingCard o1, PlayingCard o2) {
                return o1.compareTo(o2);
            }
        });

        for (PlayingCard card : cardSeries) {
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
           ; }
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
    private int getCardValue(PlayingCard card) {
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
}
