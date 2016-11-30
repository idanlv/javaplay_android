package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.interfaces.IJsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * This class represents a basic turn
 */
public abstract class Turn implements IJsonSerializable {
    /**
     * Constants
     */
    private static final String TURN_COUNTER = "turn_counter";
    private static final String GAME_ID = "game_id";
    private static final String JSON_ENCODING = "UTF-8";

    /**
     * Members
     */
    private int mTurnCounter;
    private String mGameId;

    /**
     * Constructor: Creates an initial Turn object
     * @param gameId The game id
     */
    public Turn(String gameId) {
        mGameId = gameId;
        mTurnCounter = 0;
    }

    /**
     * Retrieve Json representation of object
     * @return Json
     * @throws JSONException If the json wasn't created correctly
     */
    public JSONObject toJson() throws JSONException {
        JSONObject retVal = new JSONObject();
        retVal.put(TURN_COUNTER, this.mTurnCounter);
        retVal.put(GAME_ID, this.mGameId);

        return retVal;
    }

    /**
     * Update data according to Json value
     * @param jsonObject turn data
     * @throws JSONException If the json wasn't read correctly
     */
    public void fromJson(JSONObject jsonObject) throws JSONException {
        this.mTurnCounter = jsonObject.getInt(TURN_COUNTER);
        this.mGameId = jsonObject.getString(GAME_ID);
    }

    /**
     * This method retrieves turn data as byte array
     * @return Turn data in Byte Array
     */
    public byte[] export() throws JSONException {
        String st = toJson().toString();

        return st.getBytes(Charset.forName(JSON_ENCODING));
    }

    /**
     * Updates turn according to given data
     * @param data Updated turn data
     * @throws JSONException If data isn't a valid json object
     */
    public void update(byte[] data) throws JSONException {
        JSONObject turnData = new JSONObject(new String(data));

        fromJson(turnData);
    }

    /**
     * Increases the turn counter
     */
    public void increaseTurnCounter() {
        mTurnCounter++;
    }
}