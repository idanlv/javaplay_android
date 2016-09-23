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
    private DeckOfCards _hiddenDeck = new DeckOfCards();
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

        gameData.put("initializeDone", this._initializeDone);
        gameData.put("hiddenDeck", this._hiddenDeck.toJson());
        gameData.put("discardedDeck", this._discardedDeck.toJson());

        JSONArray availableArray = new JSONArray();

        for (Integer location : this._availableDiscardedCards) {
            availableArray.put(availableArray);
        }

        gameData.put("available", availableArray);

        return gameData;
    }

    /**
     * Update data according to Json value
     * @param object turn data
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        this._initializeDone = object.getBoolean("initializeDone");
        this._hiddenDeck = new DeckOfCards();
        this._hiddenDeck.fromJson(object.getJSONObject("hiddenDeck"));
        this._discardedDeck = new DeckOfCards();
        this._discardedDeck.fromJson(object.getJSONObject("discardedDeck"));

        this._availableDiscardedCards = new LinkedList<>();
        JSONArray availableArray = object.getJSONArray("available");

        for (int i = 0; i < availableArray.length(); i++) {
            this._availableDiscardedCards.add(availableArray.getInt(i));
        }

        super.fromJson(object);
    }
}
