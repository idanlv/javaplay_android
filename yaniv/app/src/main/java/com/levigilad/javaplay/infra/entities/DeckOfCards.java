package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.interfaces.IJsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class represents a deck of cards
 */
public class DeckOfCards implements IJsonSerializable {
    // Consts
    public static final String DECK = "deck";

    // Members
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

    public DeckOfCards(DeckOfCards deck) {
        this(deck.getCards());
    }

    /**
     * Removes a specific card from deck
     * @param card card to remove
     */
    public void removeCard(PlayingCard card) {
        if (!_cards.remove(card)) {
            throw new IllegalArgumentException("Card does not exists in deck");
        }
    }

    /**
     * Adds a card into deck
     * @param card card to add
     */
    public void addCardToTop(PlayingCard card) {
        _cards.addFirst(card);
    }

    /**
     * Returns the card at top of the deck without removing it
     * @return card at top of the deck
     */
    public PlayingCard peek() {
        return _cards.peek();
    }

    /**
     * Returns the card at the top of the deck and removes it
     * @return card at top of the deck
     */
    public PlayingCard pop() {
        return _cards.pop();
    }

    /**
     * Generate a json representation of the deck
     * @return Deck in Json format
     * @throws JSONException
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        JSONArray cardsArray = new JSONArray();

        for (PlayingCard card : this._cards) {
            cardsArray.put(card.toJson());
        }

        jsonObject.put(DECK, cardsArray);

        return jsonObject;
    }

    /**
     * Load deck from given json
     * @param object Deck in Json format
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        JSONArray cardsArray = object.getJSONArray(DECK);

        this._cards.clear();

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
     * Retrieve cards iterator
     * @return Iterator
     */
    public Iterator<PlayingCard> iterator() {
        return this._cards.iterator();
    }


    public String toString() {
        String str = "";

        for (PlayingCard card : _cards) {
            str += "(" + card.getRank() + "," + card.getSuit() + "),";
        }

        // chop last char
        str.substring(0, str.length() -1);

        return str;
    }

    public LinkedList<PlayingCard> getCards() {
        return this._cards;
    }
}
