package com.levigilad.javaplay.infra;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 21/09/2016.
 */
public interface IJsonSerializable {
    JSONObject toJson() throws JSONException;
    void fromJson(JSONObject object) throws JSONException;
}
