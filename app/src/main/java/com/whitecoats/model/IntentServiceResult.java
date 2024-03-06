package com.whitecoats.model;

import org.json.JSONObject;

public class IntentServiceResult {
    private String type;
    private JSONObject jsonObject;

    public IntentServiceResult(String mtype, JSONObject mjsonObject) {
        type = mtype;
        jsonObject = mjsonObject;
    }
    public String getType(){
        return type;
    }
    public JSONObject getJsonObject(){
        return jsonObject;
    }
}
