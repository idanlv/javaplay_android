package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.Game;
import com.levigilad.javaplay.infra.entities.PlayingCard;
import com.levigilad.javaplay.infra.enums.GameCardSuits;
import com.levigilad.javaplay.infra.enums.GameCardRanks;

import java.util.LinkedList;

/**
 * This class represents basic card game logic
 */
public abstract class GameOfCards extends Game {

    private int _initialNumOfPlayerCards;

    /**
     * Constructor
     * @param maxNumOfPlayers
     * @param initialNumOfPlayerCards
     */
    public GameOfCards(String gameId, String description, String leaderboardId,
                       int maxNumOfPlayers, int initialNumOfPlayerCards) {
        super(gameId, description, leaderboardId, maxNumOfPlayers);
        _initialNumOfPlayerCards = initialNumOfPlayerCards;
    }

    /**
     * Generates a new shuffled deck of cards
     * @return New deck of cards
     */
    public DeckOfCards generateDeck( int numberOfJokers) {
        DeckOfCards deck = new DeckOfCards();

        // Create game deck
        for (GameCardSuits symbol : GameCardSuits.values()) {
            if (symbol == GameCardSuits.NONE) {
                continue;
            }
            for (GameCardRanks value : GameCardRanks.values()) {
                if (value == GameCardRanks.JOKER) {
                    continue;
                }
                deck.addCardToTop(new PlayingCard(value,symbol));
            }
        }

        // Add the jokers
        for (int i = 0; i < numberOfJokers; i++) {
            deck.addCardToTop(new PlayingCard(GameCardRanks.JOKER, GameCardSuits.NONE));
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
        return _initialNumOfPlayerCards;
    }
}