package com.levigilad.javaplay.infra;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This interface provides Json parsing functions
 */
public interface IJsonSerializable {
    /**
     * Provides Json representation of an object
     * @return
     * @throws JSONException
     */
    JSONObject toJson() throws JSONException;

    /**
     * Initialize object according to Json representation
     * @param object
     * @throws JSONException
     */
    void fromJson(JSONObject object) throws JSONException;
}
