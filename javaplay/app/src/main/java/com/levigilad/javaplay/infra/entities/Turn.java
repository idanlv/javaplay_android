package com.levigilad.javaplay.infra.entities;

import com.levigilad.javaplay.infra.interfaces.IJsonSerializable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * This class represents a basic turn
 */
public abstract class Turn implements IJsonSerializable {
    private static final String TAG = "Turn";

    private int mTurnCounter;
    private String mGameId;

    public Turn(String gameId) {
        mGameId = gameId;
        mTurnCounter = 1;
    }

    /**
     * Retrieve Json representation of object
     * @return Json
     * @throws JSONException
     */
    public JSONObject toJson() throws JSONException {
        JSONObject retVal = new JSONObject();
        retVal.put("turnCounter", this.mTurnCounter);
        retVal.put("game_id", this.mGameId);

        return retVal;
    }

    /**
     * Update data according to Json value
     * @param object turn data
     * @throws JSONException
     */
    public void fromJson(JSONObject object) throws JSONException {
        this.mTurnCounter = object.getInt("turnCounter");
        this.mGameId = object.getString("game_id");
    }

    /**
     * This method retrieves turn data as byte array
     * @return Turn data in Byte Array
     */
    public byte[] export() throws JSONException {
        String st = toJson().toString();

        return st.getBytes(Charset.forName("UTF-8"));
    }

    /**
     * Updates turn according to given data
     * @param data Updated turn data
     * @throws JSONException
     */
    public void update(byte[] data) throws JSONException {
        JSONObject turnData = new JSONObject(new String(data));

        fromJson(turnData);
    }
}
