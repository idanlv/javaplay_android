package com.levigilad.javaplay.infra.entities;

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
    private LinkedList<PlayingCard> _cards;

    /**
     * Constructor: Creates an empty deck
     */
    public DeckOfCards() {
        _cards = new LinkedList<>();
    }

    /**
     * Constructor: Creates a deck with cards
     * @param cards initial deck cards
     */
    public DeckOfCards(LinkedList<PlayingCard> cards) {
        _cards = new LinkedList<>();

        for (PlayingCard card : cards) {
            _cards.add(new PlayingCard(card));
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
        if (!_cards.remove(card)) {
            throw new IllegalArgumentException("Card does not exists in deck");
        }
    }

    /**
     * Removes a specific card from deck by index
     * @param index index of card to remove
     * @throws IndexOutOfBoundsException If index not in bounds
     */
    public void removeCardByIndex(int index){
        _cards.remove(index);
    }

    /**
     * Clear all playing cards in the deck
     */
    public void clear() {
        _cards.clear();
    }


    /**
     * Get playing card by index
     * @param index of the card in the deck
     * @return PlayingCard by index
     * @throws IndexOutOfBoundsException If index not in bounds
     */
    public PlayingCard get(int index) {
        return _cards.get(index);
    }

    /**
     * Returns deck size
     * @return deck size
     */
    public int size() {
        return _cards.size();
    }

    /**
     * Adds a card to top of deck
     * @param card card to add
     */
    public void addCardToTop(PlayingCard card) {
        _cards.addFirst(card);
    }

    /**
     * Adds a card to bottom of deck
     * @param card card to add
     */
    public void addCardToBottom(PlayingCard card) {
        _cards.addLast(card);
    }

    /**
     * Returns the card at top of the deck without removing it
     * @return card at top of the deck, or null if the deck is empty
     */
    public PlayingCard peek() {
        return _cards.peek();
    }

    /**
     * Returns the card at the top of the deck and removes it
     * @return card at top of the deck
     * @throws NoSuchElementException if this deck is empty
     */
    public PlayingCard pop() {
        return _cards.pop();
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

        for (PlayingCard card : this._cards) {
            cardsArray.put(card.toJson());
        }

        jsonObject.put(DECK, cardsArray);

        return jsonObject;
    }

    /**
     * Load deck from given json
     * @param object Deck in Json format
     * @throws JSONException If json object is not read correctly
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        // Clear object from old data
        this._cards.clear();

        JSONArray cardsArray = object.getJSONArray(DECK);

        for (int i = 0; i < cardsArray.length(); i++) {
            PlayingCard card = new PlayingCard();
            card.fromJson((JSONObject) cardsArray.get(i));

            this._cards.addLast(card);
        }
    }

    /**
     * Shuffles cards in deck
     */
    public void shuffle() {
        Collections.shuffle(this._cards);
    }

    /**
     * Retrieves cards iterator
     * @return Iterator
     */
    public Iterator<PlayingCard> iterator() {
        return this._cards.iterator();
    }

    /**
     * Generates a String representation of the deck
     * @return String representation of the deck
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (PlayingCard card : _cards) {
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
        return new LinkedList<>(this._cards);
    }

    /**
     * Sorts the current deck
     */
    public void sort() {
        Collections.sort(_cards);
    }
}
