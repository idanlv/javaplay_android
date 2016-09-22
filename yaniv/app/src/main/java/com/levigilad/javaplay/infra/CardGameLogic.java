package com.levigilad.javaplay.infra;

import com.levigilad.javaplay.infra.entities.CardsDeck;
import com.levigilad.javaplay.infra.entities.GameCard;
import com.levigilad.javaplay.infra.enums.GameCardSymbols;
import com.levigilad.javaplay.infra.enums.GameCardValues;

import java.util.LinkedList;

/**
 * Created by User on 22/09/2016.
 */
public abstract class CardGameLogic extends GameLogic {
    /**
     * Generates a new shuffled deck of cards
     * @return New deck of cards
     */
    public CardsDeck generateDeck() {
        CardsDeck deck = new CardsDeck();

        // Create game deck
        for (GameCardValues value : GameCardValues.values()) {
            if (value != GameCardValues.JOKER) {
                for (GameCardSymbols symbol : GameCardSymbols.values()) {
                    deck.addCard(new GameCard(value, symbol));
                }
            } else {
                deck.addCard(new GameCard(value, GameCardSymbols.NONE));
                deck.addCard(new GameCard(value, GameCardSymbols.NONE));
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
    public LinkedList<CardsDeck> generateEmptyDecks(int numberOfDecks) {
        LinkedList<CardsDeck> decks = new LinkedList<>();


        for (int i = 0; i < numberOfDecks; i++) {
            decks.add(new CardsDeck());
        }

        return decks;
    }
}
