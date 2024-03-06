package com.whitecoats.clinicplus;

import org.json.JSONArray;

import java.io.Serializable;

public class JSonArrayParser implements Serializable {
    public JSONArray obj;

    public JSonArrayParser(JSONArray obj) {
        this.obj = obj;
    }

    public JSONArray getObj() {
        return obj;
    }
}
