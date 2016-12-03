package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.entities.GameOfCards;
import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;
import com.levigilad.javaplay.infra.enums.PlayingCardState;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class represents Yaniv game logic
 */
public class YanivGame extends GameOfCards {

    /**
     * Constant
     */
    public static final int INITIAL_DEFAULT_CARD_COUNT = 5;
    private static final int MAX_DEFAULT_PLAYERS = 4;
    private static final int MAX_DEFAULT_YANIV_CARD_SCORE = 7;
    private static final int MIN_SEQUENCE_LENGTH = 3;
    private static final int MIN_DISCARDED_CARDS = 1;
    private static final int MIN_DUPLICATES_LENGTH = 2;
    private static final String YANIV = "Yaniv";
    private static final String DESCRIPTION = "Description for yaniv";
    private static final String LEADERBOARD_ID = "CgkIyuG_9qMQEAIQCA";
    private static final int DEFAULT_NUMBER_OF_DECKS = 2;
    private static final int DEFAULT_NUMBER_OF_JOKERS = 2;
    private static final int INITIAL_AVAILABLE_DISCARDED_DECK_SIZE = 1;


    /**
     * Empty constructor
     */
    public YanivGame() {
        this(MAX_DEFAULT_PLAYERS, INITIAL_DEFAULT_CARD_COUNT);
    }

    /**
     * Constructor
     * @param maxNumberOfPlayers number of players in game
     * @param numberOfStartingCards number of starting cards for player
     */
    public YanivGame(int maxNumberOfPlayers, int numberOfStartingCards) {
        super(YANIV, DESCRIPTION, LEADERBOARD_ID, maxNumberOfPlayers, numberOfStartingCards);
    }

    /**
     * Checks if player's deck is available for Yaniv
     * @param deck player's deck
     * @return True or False
     */
    public static boolean canYaniv(DeckOfCards deck) {
        return canYaniv(calculateDeckScore(deck));
    }

    /**
     * Checks if player's score is available for Yaniv
     * @param playerScore Score
     * @return True or False
     */
    public static boolean canYaniv(int playerScore) {
        return playerScore <= MAX_DEFAULT_YANIV_CARD_SCORE;
    }

    /**
     *  Check's if you won when you declare yaniv
     * @param playersID as the declarer ID of the player
     * @param playersHand as the declarer deck of cards
     * @param allPlayersHands all players deck's
     * @return the winner ID as a String
     */
    public static String declareYanivWinner(String playersID, DeckOfCards playersHand,
                                       HashMap<String, DeckOfCards> allPlayersHands) {
        String winnerID = playersID;
        int myScore;

        myScore = YanivGame.calculateDeckScore(playersHand);

        for (String player : allPlayersHands.keySet()) {
            // Check that its not me and if the score is lower or equals, I lose
            if(!player.equals(playersID) &&
                    (calculateDeckScore(allPlayersHands.get(player))) <= myScore) {
                winnerID = player;
            }
        }

        return winnerID;
    }

    /**
     * Calculates the score of a given deck
     * @param deck player's deck
     * @return Score
     */
    public static int calculateDeckScore(DeckOfCards deck) {
        int cardsTotalValue = 0;

        Iterator<PlayingCard> iterator = deck.iterator();

        while (iterator.hasNext()) {
            PlayingCard card = iterator.next();
            cardsTotalValue += getYanivCardValue(card);
        }

        return cardsTotalValue;
    }

    /**
     * Checks if user made a valid tryDiscard operation
     * @param discardCards cards
     * @return True or False
     */
    private static boolean isCardsDiscardValid(DeckOfCards discardCards) {
        sortDeck(discardCards);
        return ((discardCards.size() == MIN_DISCARDED_CARDS)
                || isSequence(discardCards)
                || isDuplicates(discardCards));
    }

    /**
     * Checks if this cards have the same value
     * @param cards sorted deck of cards
     * @return True or False
     */
    public static boolean isDuplicates(DeckOfCards cards) {

        if (cards.size() < MIN_DUPLICATES_LENGTH) {
            return false;
        }

        Iterator<PlayingCard> it = cards.iterator();
        PlayingCardRanks rank = it.next().getRank();


        while (it.hasNext()) {
            PlayingCardRanks currentRank = it.next().getRank();
            if ((currentRank != PlayingCardRanks.JOKER) && (rank != currentRank)) {
                return false;
            }

        }

        return true;
    }

    /**
     * Checks if this card series is a valid sequence
     * @param cardSeries sorted deck of cards
     * @return True or False
     */
    public static boolean isSequence(DeckOfCards cardSeries) {
        PlayingCard playingCard = null;
        int previousValue;
        PlayingCardSuits suit;

        if (cardSeries.size() < MIN_SEQUENCE_LENGTH) {
            return false;
        }

        Iterator<PlayingCard> it = cardSeries.iterator();

        // Since we've checked for minimal size, next will certainly return a value
        playingCard = it.next();
        suit = playingCard.getSuit();
        previousValue = playingCard.getRank().getNumericValue();

        // Move on the remaining cards
        while (it.hasNext()) {
            playingCard = it.next();

            if (playingCard.getRank() == PlayingCardRanks.JOKER) {
                previousValue++;
            }
            // if not same suit, return quit
            else if (playingCard.getSuit() != suit) {
                return false;
            }
            // Check if this is the next expected card
            else if (playingCard.getRank().getNumericValue() == previousValue + 1) {
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
    public static int getYanivCardValue(PlayingCard card) {
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

    /**
     * Creates an initialized match object
     * @param participantIds Participants in match
     * @return Initialized YanivTurn object
     */
    public static YanivTurn initiateMatch(ArrayList<String> participantIds) {
        YanivTurn turn = new YanivTurn();

        DeckOfCards globalDeck = YanivGame.generateDeck();
        turn.setGlobalDeck(globalDeck);

        for(String participantId : participantIds) {
            DeckOfCards deck = globalDeck.drawCards(INITIAL_DEFAULT_CARD_COUNT);
            turn.addParticipantDeck(participantId, deck);
        }

        DeckOfCards availableDiscardedDeck =
                globalDeck.drawCards(INITIAL_AVAILABLE_DISCARDED_DECK_SIZE);
        turn.setAvailableDiscardedDeck(availableDiscardedDeck);

        return turn;
    }

    /**
     * Generates a new deck of cards with default number of decks and jokers
     * @return Initialized deck of cards
     */
    private static DeckOfCards generateDeck() {
        return generateDeck(DEFAULT_NUMBER_OF_DECKS, DEFAULT_NUMBER_OF_JOKERS);
    }

    /**
     * Tries to discard given cards from player's deck
     * @param participantId Participant identifier
     * @param turnData Current turn data object
     * @return True if discarded. Otherwise false.
     */
    public static boolean tryDiscard(String participantId, YanivTurn turnData) {
        if (turnData.hasTurnDiscardedDeck()) {
            throw new UnsupportedOperationException("Participant cannot discard twice in same turn");
        }

        DeckOfCards discardedDeck = new DeckOfCards();

        Iterator<PlayingCard> iterator = turnData.getPlayerHand(participantId).iterator();

        while (iterator.hasNext()) {
            PlayingCard card = iterator.next();
            if (card.isDiscarded()) {
                discardedDeck.addCardToBottom(card);
            }
        }

        if (isCardsDiscardValid(discardedDeck)) {
            turnData.getPlayerHand(participantId).removeAll(discardedDeck);
            turnData.setTurnDiscardedDeck(discardedDeck);
            return true;
        }

        return false;
    }

    /**
     * This method is called when player draws a card from global deck
     * @param participantId Participant identifier
     * @param turnData Current turn data object
     */
    public static void onDrawFromGlobalDeck(String participantId, YanivTurn turnData) {
        // No cards left in deck
        if (turnData.getGlobalCardDeck().size() == 0) {
            // Change status of cards to Available

            // Add discarded deck to global deck and shuffle cards
            turnData.getGlobalCardDeck().addAll(turnData.getDiscardedCards());
            turnData.getGlobalCardDeck().shuffle();

            // Empty discarded deck
            turnData.getDiscardedCards().clear();
        }

        turnData.getPlayerHand(participantId)
                .addCardToBottom(turnData.getGlobalCardDeck().drawFirstCard());

        updateAvailableDiscardedDeck(turnData);
    }

    /**
     * Updates available discarded deck according to participant's disacrded deck
     * @param turnData Turn data
     */
    static void updateAvailableDiscardedDeck(YanivTurn turnData) {
        turnData.getDiscardedCards().addAll(turnData.getAvailableDiscardedCards());
        turnData.getAvailableDiscardedCards().clear();

        DeckOfCards discardedCards = turnData.getTurnDiscardedDeck();
        turnData.setAvailableDiscardedDeck(findAvailableCards(discardedCards));
        turnData.getDiscardedCards().addAll(discardedCards);

        turnData.setTurnDiscardedDeck(null);
    }

    /**
     * Removes the available cards for next turn from given discarded deck
     * @param discardedDeck Current deck discarded by participant
     * @return Available cards
     */
    static DeckOfCards findAvailableCards(DeckOfCards discardedDeck) {
        DeckOfCards availableDeck = new DeckOfCards();

        availableDeck.addCardToBottom(discardedDeck.drawFirstCard());

        // Participant has discarded more than one card, therefore we need to add a second card
        // for available deck. This card must be the last.
        if (discardedDeck.size() > 0) {
            availableDeck.addCardToBottom(discardedDeck.drawLastCard());
        }

        return availableDeck;
    }

    /**
     * This method is called when player draws a card from available discarded deck
     * @param participantId Participant identifier
     * @param turnData Current turn data object
     * @param drawnCard card which was selected by player
     */
    public static void onDrawFromDiscardedDeck(String participantId, YanivTurn turnData,
                                               PlayingCard drawnCard) {
        turnData.getAvailableDiscardedCards().removeCard(drawnCard);
        turnData.getPlayerHand(participantId).addCardToBottom(drawnCard);
        drawnCard.setState(PlayingCardState.AVAILABLE);
        turnData.getDiscardedCards().addAll(turnData.getAvailableDiscardedCards());

        turnData.getAvailableDiscardedCards().clear();

        updateAvailableDiscardedDeck(turnData);
    }

    private static void sortDeck(DeckOfCards deck) {
        if (deck.size() == 0) {
            return;
        }

        deck.sort();

        // Get the joker count and move pass them
        Iterator<PlayingCard> iterator = deck.iterator();

        DeckOfCards sortedDeck = new DeckOfCards();
        DeckOfCards jokersDeck = new DeckOfCards();
        PlayingCard playingCard = null;

        int previousValue = -1;
        while (iterator.hasNext()) {
            playingCard = iterator.next();
            if (playingCard.getRank() == PlayingCardRanks.JOKER) {
                jokersDeck.addCardToBottom(playingCard);
            } else if (sortedDeck.size() == 0) {
                // Get current card values
                previousValue = playingCard.getRank().getNumericValue();
                sortedDeck.addCardToBottom(playingCard);
            } else {
                int currentValue = playingCard.getRank().getNumericValue();

                while ((currentValue > previousValue + 1) && (jokersDeck.size() > 0)) {
                    sortedDeck.addCardToBottom(jokersDeck.drawFirstCard());
                    previousValue++;
                }

                sortedDeck.addCardToBottom(playingCard);
            }
        }

        boolean top = true;

        // If first card is ace, add jokers at end of deck
        if (sortedDeck.get(0).getRank() == PlayingCardRanks.ACE) {
            top = false;
        }

        // Add remaining jokers in beginning of game
        while (jokersDeck.size() > 0) {
            if (top) {
                sortedDeck.addCardToTop(jokersDeck.drawFirstCard());
            } else {
                sortedDeck.addCardToBottom(jokersDeck.drawFirstCard());
            }

        }

        deck.replace(sortedDeck);
    }
}