package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.Turn;
import com.levigilad.javaplay.infra.entities.DeckOfCards;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * This class represents Yaniv game turn data
 */
public class YanivTurn extends Turn {
    // Consts
    public static final String INITIALIZE_DONE = "initializeDone";
    public static final String AVAILABLE_DECK = "availableDeck";
    public static final String DISCARDED_DECK = "discardedDeck";
    public static final String AVAILABLE_CARDS = "availableCards";

    // Members
    private DeckOfCards _availableDeck = new DeckOfCards();
    private DeckOfCards _discardedDeck = new DeckOfCards();
    private LinkedList<Integer> _availableDiscardedCards = new LinkedList<>();
    private boolean _initializeDone = false;

    /**
     * Retrieve Json representation of object
     * @return Json
     * @throws JSONException
     */
    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject gameData = super.toJson();

        gameData.put(INITIALIZE_DONE, this._initializeDone);
        gameData.put(AVAILABLE_DECK, this._availableDeck.toJson());
        gameData.put(DISCARDED_DECK, this._discardedDeck.toJson());

        JSONArray availableArray = new JSONArray();

        for (Integer location : this._availableDiscardedCards) {
            availableArray.put(availableArray);
        }

        gameData.put(AVAILABLE_CARDS, availableArray);

        return gameData;
    }

    /**
     * Update data according to Json value
     * @param object turn data
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        this._initializeDone = object.getBoolean(INITIALIZE_DONE);
        this._availableDeck = new DeckOfCards();
        this._availableDeck.fromJson(object.getJSONObject(AVAILABLE_DECK));
        this._discardedDeck = new DeckOfCards();
        this._discardedDeck.fromJson(object.getJSONObject(DISCARDED_DECK));

        this._availableDiscardedCards = new LinkedList<>();
        JSONArray availableArray = object.getJSONArray(AVAILABLE_CARDS);

        for (int i = 0; i < availableArray.length(); i++) {
            this._availableDiscardedCards.add(availableArray.getInt(i));
        }

        super.fromJson(object);
    }
}
