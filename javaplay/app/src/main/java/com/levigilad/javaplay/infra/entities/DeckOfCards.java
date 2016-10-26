package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.enums.PlayingCardRanks;
import com.levigilad.javaplay.infra.enums.PlayingCardSuits;
import com.levigilad.javaplay.infra.interfaces.IJsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * This class represents a deck of cards
 */
public class DeckOfCards implements IJsonSerializable {
    /**
     * Constants
     */
    private static final String DECK = "deck";
    private static final String CARDS_SEPERATOR = ",";

    /**
     * Members
     */
    private LinkedList<PlayingCard> mCards;

    /**
     * Constructor: Creates an empty deck
     */
    public DeckOfCards() {
        mCards = new LinkedList<>();
    }

    /**
     * Constructor: Creates a deck with cards
     * @param cards initial deck cards
     */
    public DeckOfCards(LinkedList<PlayingCard> cards) {
        mCards = new LinkedList<>();

        for (PlayingCard card : cards) {
            mCards.add(new PlayingCard(card));
        }
    }

    /**
     * Copy constructor
     * @param deck of cards
     */
    public DeckOfCards(DeckOfCards deck) {
        this(deck.getCards());
    }

    /**
     * Removes a specific card from deck
     * @param card card to remove
     * @throws IllegalArgumentException If card does not exist in deck
     */
    public void removeCard(PlayingCard card) {
        if (!mCards.remove(card)) {
            throw new IllegalArgumentException("Card does not exists in deck");
        }
    }

    /**
     * Removes a specific card from deck by index
     * @param index index of card to remove
     * @throws IndexOutOfBoundsException If index not in bounds
     */
    public void removeCardByIndex(int index){
        mCards.remove(index);
    }

    /**
     * Clear all playing cards in the deck
     */
    public void clear() {
        mCards.clear();
    }

    /**
     * Combine two decks
     * @param other deck of cards to add all his cards
     */
    public void addAll(DeckOfCards other) {
        this.mCards.addAll(other.mCards);
    }

    /**
     * Replace a deck of card with another
     * @param other deck of card to replace with
     */
    public void replace(DeckOfCards other) {
        this.clear();
        this.addAll(other);
    }

    /**
     * Get playing card by index
     * @param index of the card in the deck
     * @return PlayingCard by index
     * @throws IndexOutOfBoundsException If index not in bounds
     */
    public PlayingCard get(int index) {
        return mCards.get(index);
    }

    /**
     * Returns deck size
     * @return deck size
     */
    public int size() {
        return mCards.size();
    }

    /**
     * Adds a card to top of deck
     * @param card card to add
     */
    public void addCardToTop(PlayingCard card) {
        mCards.addFirst(card);
    }

    /**
     * Adds a card to bottom of deck
     * @param card card to add
     */
    public void addCardToBottom(PlayingCard card) {
        mCards.addLast(card);
    }

    /**
     * Returns the card at top of the deck without removing it
     * @return card at top of the deck, or null if the deck is empty
     */
    public PlayingCard peek() {
        return mCards.peek();
    }

    /**
     * Returns the last playing card in the deck
     * @return card at the bottom of the deck
     */
    public PlayingCard getLast(){
        return mCards.getLast();
    }

    /**
     * Generates a json representation of the deck
     * @return Deck in Json format
     * @throws JSONException if the json was created incorrectly
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        final JSONArray cardsArray = new JSONArray();

        for (PlayingCard card : this.mCards) {
            cardsArray.put(card.toJson());
        }

        jsonObject.put(DECK, cardsArray);

        return jsonObject;
    }

    /**
     * Load deck from given json
     * @param jsonObject Deck in Json format
     * @throws JSONException If json object is not read correctly
     */
    @Override
    public void fromJson(JSONObject jsonObject) throws JSONException {
        // Clear object from old data
        this.mCards.clear();

        JSONArray cardsArray = jsonObject.getJSONArray(DECK);

        for (int i = 0; i < cardsArray.length(); i++) {
            PlayingCard card = new PlayingCard();
            card.fromJson((JSONObject) cardsArray.get(i));

            this.mCards.addLast(card);
        }
    }

    /**
     * Shuffles cards in deck
     */
    public void shuffle() {
        Collections.shuffle(this.mCards);
    }

    /**
     * Retrieves cards iterator
     * @return Iterator
     */
    public Iterator<PlayingCard> iterator() {
        return this.mCards.iterator();
    }

    /**
     * Generates a String representation of the deck
     * @return String representation of the deck
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (PlayingCard card : mCards) {
            builder.append(card.toString());
            builder.append(CARDS_SEPERATOR);
        }

        String str = builder.toString();

        // Chop last char if not empty
        if (str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

    /**
     * Playing card in a linked list
     * @return PlayingCards in a LinkedList
     */
    public LinkedList<PlayingCard> getCards() {
        return new LinkedList<>(this.mCards);
    }

    /**
     * Sorts the current deck
     */
    public void sort() {
        if (mCards.size() == 0) {
            return;
        }

        Collections.sort(mCards);

        // Get the joker count and move pass them
        Iterator<PlayingCard> iterator = mCards.iterator();

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
                    sortedDeck.addAll(jokersDeck.drawCard());
                    previousValue++;
                }

                sortedDeck.addCardToBottom(playingCard);
            }
        }

        while (jokersDeck.size() > 0) {
            sortedDeck.addCardToTop(jokersDeck.drawCard().get(0));
            jokersDeck.removeCardByIndex(0);
        }

        this.replace(sortedDeck);
    }

    /**
     * Draws cards from deck according to requested amount
     * @param numberOfCards
     * @return
     */
    public DeckOfCards drawCards(int numberOfCards) {
        DeckOfCards cards = new DeckOfCards();

        if (numberOfCards > this.size()) {
            throw new RuntimeException("Deck has less than requested amount of cards");
        }

        for (int i = 0; i < numberOfCards; i++) {
            cards.addCardToTop(mCards.pop());
        }

        return cards;
    }

    /**
     * Removes deck in deck if they exists in given deck
     * @param deck The deck to remove
     */
    public void removeAll(DeckOfCards deck) {
        mCards.removeAll(deck.getCards());
    }

    public DeckOfCards drawCard() {
        return this.drawCards(1);
    }
}