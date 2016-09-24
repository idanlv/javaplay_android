package com.levigilad.javaplay.infra;

import com.levigilad.javaplay.infra.entities.DeckOfCards;
import com.levigilad.javaplay.infra.entities.GameCard;
import com.levigilad.javaplay.infra.enums.GameCardSuits;
import com.levigilad.javaplay.infra.enums.GameCardRanks;

import java.util.LinkedList;

/**
 * This class represents basic card game logic
 */
public abstract class CardGame extends Game {

    private int _initialNumOfPlayerCards;

    /**
     * Constructor
     * @param maxNumOfPlayers
     * @param initialNumOfPlayerCards
     */
    public CardGame(int maxNumOfPlayers, int initialNumOfPlayerCards) {
        super(maxNumOfPlayers);
        _initialNumOfPlayerCards = initialNumOfPlayerCards;
    }

    /**
     * Generates a new shuffled deck of cards
     * @return New deck of cards
     */
    public DeckOfCards generateDeck() {
        DeckOfCards deck = new DeckOfCards();

        // Create game deck
        for (GameCardRanks value : GameCardRanks.values()) {
            if (value != GameCardRanks.JOKER) {
                for (GameCardSuits symbol : GameCardSuits.values()) {
                    deck.addCard(new GameCard(value, symbol));
                }
            } else {
                deck.addCard(new GameCard(value, GameCardSuits.NONE));
                deck.addCard(new GameCard(value, GameCardSuits.NONE));
            }
        }

        deck.shuffle();

        return deck;
    }

    /**
     * Generates a list of empty decks
     * @param numberOfDecks Number of decks to generate
     * @return List of decks
     */
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