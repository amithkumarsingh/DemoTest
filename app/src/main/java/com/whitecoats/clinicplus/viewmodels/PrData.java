
package com.whitecoats.clinicplus.viewmodels;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrData {

    @SerializedName("intent_id")
    @Expose
    private Integer intentId;
    @SerializedName("intent")
    @Expose
    private String intent;
    @SerializedName("data")
    @Expose
    private List<DataObjects> data = null;
    @SerializedName("inv_intent_id")
    @Expose
    private Integer invIntentId;
    @SerializedName("inv_data_id")
    @Expose
    private List<String> invDataId = null;

    public Integer getIntentId() {
        return intentId;
    }

    public void setIntentId(Integer intentId) {
        this.intentId = intentId;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<DataObjects> getData() {
        return data;
    }

    public void setData(List<DataObjects> data) {
        this.data = data;
    }

    public Integer getInvIntentId() {
        return invIntentId;
    }

    public void setInvIntentId(Integer invIntentId) {
        this.invIntentId = invIntentId;
    }

    public List<String> getInvDataId() {
        return invDataId;
    }

    public void setInvDataId(List<String> invDataId) {
        this.invDataId = invDataId;
    }

}
