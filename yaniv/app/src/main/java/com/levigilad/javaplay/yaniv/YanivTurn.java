package com.levigilad.javaplay.yaniv;

import com.levigilad.javaplay.infra.IJsonSerializable;
import com.levigilad.javaplay.infra.Turn;
import com.levigilad.javaplay.infra.entities.CardsDeck;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 21/09/2016.
 */
public class YanivTurn extends Turn {
    private CardsDeck _hiddenDeck = new CardsDeck();
    private CardsDeck _exposedDeck = new CardsDeck();
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
        gameData.put("exposedDeck", this._exposedDeck.toJson());

        return gameData;
    }

    /**
     * Update data according to Json value
     * @param object turn data
     * @throws JSONException
     */
    @Override
    public void fromJson(JSONObject object) throws JSONException {
        this._initializeDone = data.getBoolean("initializeDone");
        this._hiddenDeck = new CardsDeck();
        this._hiddenDeck.fromJson(data.getJSONObject("hiddenDeck"));
        this._exposedDeck = new CardsDeck();
        this._exposedDeck.fromJson(data.getJSONObject("exposedDeck"));

        super.fromJson(object);
    }
}
