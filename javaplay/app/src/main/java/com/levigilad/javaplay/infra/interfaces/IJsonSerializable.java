package com.levigilad.javaplay.infra.interfaces;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This interface provides Json parsing functions
 */
public interface IJsonSerializable {
    /**
     * Provides Json representation of an object
     * @return A json representation of an object
     * @throws JSONException if the json wasn't created correctly
     */
    JSONObject toJson() throws JSONException;

    /**
     * Initialize object according to Json representation
     * @param jsonObject to convert
     * @throws JSONException if the json wasn't read correctly
     */
    void fromJson(JSONObject jsonObject) throws JSONException;
}
