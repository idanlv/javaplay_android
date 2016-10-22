package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.enums.PlayingCardSuits;
import com.levigilad.javaplay.infra.enums.PlayingCardRanks;

import java.util.LinkedList;

/**
 * This class represents basic card game logic
 */
public abstract class GameOfCards extends Game {

    private int mInitialNumOfPlayerCards;

    /**
     * Constructor
     * @param gameId
     * @param description
     * @param leaderboardId
     * @param maxNumOfPlayers
     * @param initialNumOfPlayerCards
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
    public DeckOfCards generateDeck(int numberOfDecks, int numberOfJokers) {
        DeckOfCards deck = new DeckOfCards();

        // Generate n numbers of decks
        for (int i = 0; i < numberOfDecks; i++) {
            // Create a single game deck
            for (PlayingCardSuits symbol : PlayingCardSuits.values()) {
                if (symbol == PlayingCardSuits.NONE) {
                    continue;
                }
                for (PlayingCardRanks value : PlayingCardRanks.values()) {
                    if (value == PlayingCardRanks.JOKER) {
                        continue;
                    }
                    deck.addCardToTop(new PlayingCard(value,symbol));
                }
            }
        }

        // Add the jokers
        for (int i = 0; i < numberOfJokers; i++) {
            deck.addCardToTop(new PlayingCard(PlayingCardRanks.JOKER, PlayingCardSuits.NONE));
        }

        deck.shuffle();

        return deck;
    }

    /**
     * Generates a list of empty decks
     * @param numberOfDecks Number of decks to generate
     * @return List of decks
     */
    // TODO: check if needed
    public LinkedList<DeckOfCards> generateEmptyDecks(int numberOfDecks) {
        LinkedList<DeckOfCards> decks = new LinkedList<>();


        for (int i = 0; i < numberOfDecks; i++) {
            decks.add(new DeckOfCards());
        }

        return decks;
    }

    /**
     * Getter
     * @return Player's number of cards in beginning of game
     */
    public int getInitialNumOfPlayerCards() {
        return mInitialNumOfPlayerCards;
    }
}