package com.levigilad.javaplay.yaniv;

import android.content.Context;

import com.levigilad.javaplay.R;
import com.levigilad.javaplay.infra.entities.GameOfCards;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;

import java.util.Iterator;

/**
 * This class represents Yaniv game logic
 */
public class YanivGame extends GameOfCards {
    
    // Consts
    public static final int INITIAL_DEFAULT_CARD_COUNT = 5;
    private static final int MAX_DEFAULT_PLAYERS = 4;
    private final int MAX_DEFAULT_YANIV_CARD_SCORE = 7;
    private final int MIN_SEQUENCE_LENGTH = 3;
    private final int MIN_DISCARDED_CARDS = 1;
    private final int MIN_DUPLICATES_LENGTH = 2;

    /**
     * Empty constructor
     */
    public YanivGame(Context context) {
        this(context, MAX_DEFAULT_PLAYERS, INITIAL_DEFAULT_CARD_COUNT);
    }

    /**
     * Constructor
     * @param numberOfPlayers number of players in game
     * @param numberOfStartingCards number of starting cards for player
     */
    public YanivGame(Context context, int numberOfPlayers, int numberOfStartingCards) {
        super(context.getString(R.string.yaniv_game_id),
                context.getString(R.string.yaniv_description),
                context.getString(R.string.yaniv_leaderboard_id),
                numberOfPlayers,
                numberOfStartingCards);
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
    public boolean canYaniv(int playerScore) {
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
            cardsTotalValue += getYanivCardValue(card);
        }

        return cardsTotalValue;
    }

    /**
     * Checks if user made a valid discard operation
     * @param discardCards cards
     * @return True or False
     */
    public boolean isCardsDiscardValid(DeckOfCards discardCards) {
        return ((discardCards.size() == MIN_DISCARDED_CARDS)
                || isSequence(discardCards)
                || isDuplicates(discardCards));
    }

    /**
     * Checks if this cards have the same value
     * @param cards cards
     * @return True or False
     */
    public boolean isDuplicates(DeckOfCards cards) {

        if (cards.size() < MIN_DUPLICATES_LENGTH) {
            return false;
        }

        PlayingCardRanks value = cards.get(0).getRank();

        Iterator<PlayingCard> it = cards.iterator();

        while (it.hasNext()) {
            if (value != it.next().getRank()) {
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
    public boolean isSequence(DeckOfCards cardSeries) {
        PlayingCardSuits cardsSuit;
        Iterator<PlayingCard> it;
        PlayingCard playingCard = null;
        int jokerCount = 0;
        int previousValue;
        PlayingCardSuits suit;


        if (cardSeries.size() < MIN_SEQUENCE_LENGTH) {
            return false;
        }

        cardSeries.sort();

        // Get the joker count and move pass them
        it = cardSeries.iterator();
        while (it.hasNext()) {
            playingCard = it.next();
            if (playingCard.getRank() == PlayingCardRanks.JOKER) {
                jokerCount++;
            } else {
                break;
            }
        }

        // Get current card values
        suit = playingCard.getSuit();
        previousValue = playingCard.getRank().getNumericValue();

        // Move on the remaining cards
        while (it.hasNext()) {
            playingCard = it.next();

            // if not same suit, return quit
            if (playingCard.getSuit() != suit) {
                return false;
            }

            // Joker handle
            while (playingCard.getRank().getNumericValue() > previousValue + 1 &&
                    jokerCount > 0) {
                jokerCount--;
                previousValue++;
            }

            // Check if this is the next expected card
            if (playingCard.getRank().getNumericValue() == previousValue + 1) {
                previousValue++;
            }

            // Current number does not continue the sequence
            else {
                return false;
            }
        }

        // All is OK
        return true;
    }

    /**
     * This method returns the numeric value of the card within game rules
     * @param card as PlayingCard
     * @return int value as Yaniv card value
     */
    public int getYanivCardValue(PlayingCard card) {
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
