package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.IJsonSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by User on 21/09/2016.
 */
public class CardDeck implements IJsonSerializable {
    private LinkedList<GameCard> _cards;

    /**
     * Consructor
     * Creates an empty deck
     */
    public CardDeck() {
        _cards = new LinkedList<>();
    }

    /**
     * Constructor
     * Creates a deck with cards
     * @param cards initial deck cards
     */
    public CardDeck(LinkedList<GameCard> cards) {
        this._cards = cards;
    }

    /**
     * Removes a specific card from deck
     * @param card card to remove
     */
    public void removeCard(GameCard card) {
        if (!_cards.remove(card)) {
            throw new IllegalArgumentException("Card does not exists in deck");
        }
    }

    /**
     * Adds a card into deck
     * @param card card to add
     */
    public void addCard(GameCard card) {
        _cards.addFirst(card);
    }

    /**
     * Returns the card at top of the deck without removing it
     * @return card at top of the deck
     */
    public GameCard peek() {
        return _cards.peek();
    }

    /**
     * Returns the card at the top of the deck and removes it
     * @return card at top of the deck
     */
    public GameCard pop() {
        return _cards.pop();
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();

        JSONArray cardsArray = new JSONArray();

        for (GameCard card : this._cards) {
            cardsArray.put(card.toJson());
        }

        object.put("deck", cardsArray);

        return object;
    }

    @Override
    public void fromJson(JSONObject object) throws JSONException {
        JSONArray cardsArray = object.getJSONArray("deck");

        this._cards.clear();

        for (int i = 0; i < cardsArray.length(); i++) {
            GameCard card = new GameCard();
            card.fromJson((JSONObject) cardsArray.get(i));

            this._cards.addLast(card);
        }
    }
}
