package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.enums.PlayingCardSuits;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;

/**
 * This abstract class represents basic card game logic
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
     * @param maxNumOfPlayers Maximum number of players in match
     * @param initialNumOfPlayerCards Number of cards for each player
     */
    public GameOfCards(String gameId, String description, String leaderboardId,
                       int maxNumOfPlayers, int initialNumOfPlayerCards) {
        super(gameId, description, leaderboardId, maxNumOfPlayers);
        mInitialNumOfPlayerCards = initialNumOfPlayerCards;
    }

    /**
     * Generates a new shuffled deck of cards
     * @param numberOfDecks number of decks to make a shuffle deck from
     * @param numberOfJokers number of jokers to add to the deck
     * @return New deck of cards
     */
    public static DeckOfCards generateDeck(int numberOfDecks, int numberOfJokers) {
        DeckOfCards deck = new DeckOfCards();

        // Generate n numbers of decks
        for (int i = 0; i < numberOfDecks; i++) {
            // Create a single game deck
            for (PlayingCardSuits symbol : PlayingCardSuits.values()) {
                if (symbol == PlayingCardSuits.NONE) {
                    // Skip Suit
                    continue;
                }
                for (PlayingCardRanks value : PlayingCardRanks.values()) {
                    if (value == PlayingCardRanks.JOKER) {
                        // Skip Joker
                        continue;
                    }
                    deck.addCardToTop(new PlayingCard(value,symbol));
                }
            }
        }

        // Add jokers to the deck
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