package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.enums.PlayingCardSuits;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;

import java.util.LinkedList;

/**
 * This class represents basic card game logic
 */
public abstract class GameOfCards extends Game {

    /**
     * Members
     */
    private int mInitialNumOfPlayerCards;

    /**
     * Constructor: Creates a game of cards instance
     * @param gameId The game name (used as id)
     * @param description The description for the game
     * @param leaderboardId The id of the leaderboard in google play services
     * @param maxNumberOfPlayers Maximum number of players in match
     * @param initialNumOfPlayerCards Number of cards for each player
     */
    public GameOfCards(String gameId, String description, String leaderboardId,
                       int maxNumOfPlayers, int initialNumOfPlayerCards) {
        super(gameId, description, leaderboardId, maxNumOfPlayers);
        mInitialNumOfPlayerCards = initialNumOfPlayerCards;
    }

    /**
     * Generates a new shuffled deck of cards
     * @return New deck of cards
     */
    public DeckOfCards generateDeck( int numberOfJokers) {
        DeckOfCards deck = new DeckOfCards();

        // Create game deck without jokers
        for (PlayingCardSuits symbol : PlayingCardSuits.values()) {
            if (symbol != PlayingCardSuits.NONE) {
                for (PlayingCardRanks value : PlayingCardRanks.values()) {
                    if (value != PlayingCardRanks.JOKER) {
                        deck.addCardToTop(new PlayingCard(value, symbol));
                    }
                }
            }
        }

        // Add the jokers to the deck
        for (int i = 0; i < numberOfJokers; i++) {
            deck.addCardToTop(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        }

        deck.shuffle();

        return deck;
    }

    /**
     * Getter
     * @return Player's number of cards in beginning of game
     */
    public int getInitialNumOfPlayerCards() {
        return mInitialNumOfPlayerCards;
    }
}