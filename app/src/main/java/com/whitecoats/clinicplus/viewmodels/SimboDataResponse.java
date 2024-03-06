
package com.whitecoats.clinicplus.viewmodels;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SimboDataResponse {

    @SerializedName("sts.spk")
    @Expose
    private String stsSpk;
    @SerializedName("prs")
    @Expose
    private List<PrData> prs = null;

    public String getStsSpk() {
        return stsSpk;
    }

    public void setStsSpk(String stsSpk) {
        this.stsSpk = stsSpk;
    }

    public List<PrData> getPrs() {
        return prs;
    }

    public void setPrs(List<PrData> prs) {
        this.prs = prs;
    }

}
