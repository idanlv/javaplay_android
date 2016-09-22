package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.Turn;
import com.levigilad.javaplay.infra.entities.CardsDeck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by User on 21/09/2016.
 */
public class YanivTurn extends Turn {
    private CardsDeck _hiddenDeck = new CardsDeck();
    private CardsDeck _discardedDeck = new CardsDeck();
    private LinkedList<int> _availableDiscardedCards = new LinkedList<>();
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

        for (int location : this._availableDiscardedCards) {
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
        this._hiddenDeck = new CardsDeck();
        this._hiddenDeck.fromJson(object.getJSONObject("hiddenDeck"));
        this._discardedDeck = new CardsDeck();
        this._discardedDeck.fromJson(object.getJSONObject("discardedDeck"));

        this._availableDiscardedCards = new LinkedList<>();
        JSONArray availableArray = object.getJSONArray("available");

        for (int i = 0; i < availableArray.length(); i++) {
            this._availableDiscardedCards.add(availableArray.getInt(i));
        }

        super.fromJson(object);
    }
}
