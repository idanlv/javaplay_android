package com.levigilad.javaplay.infra;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * Created by User on 21/09/2016.
 */
public abstract class BaseTurn {
    private static final String TAG = "BaseTurn";
    private int _turnCounter;

    public BaseTurn() {
        _turnCounter = 1;
    }

    /**
     * This method retrieves a json representation of turn data
     * @return Turn data in JSON format
     */
    protected abstract JSONObject dataToJson() throws JSONException;

    /**
     * This method retrieves turn data as byte array
     * @return Turn data in Byte Array
     */
    public byte[] turnToByteArray() {
        JSONObject retVal = new JSONObject();

        try {
            retVal.put("data", dataToJson());
            retVal.put("turnCounter", this._turnCounter);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        String st = retVal.toString();

        return st.getBytes(Charset.forName("UTF-8"));
    }
}
