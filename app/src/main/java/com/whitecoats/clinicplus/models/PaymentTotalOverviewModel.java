package com.whitecoats.clinicplus.models;

import org.json.JSONArray;
import org.json.JSONObject;

public class PaymentTotalOverviewModel {

    public String getCollectedPayment() {
        return collectedPayment;
    }

    public void setCollectedPayment(String collectedPayment) {
        this.collectedPayment = collectedPayment;
    }

    public String getAwaitingPayment() {
        return awaitingPayment;
    }

    public void setAwaitingPayment(String awaitingPayment) {
        this.awaitingPayment = awaitingPayment;
    }

    String collectedPayment,awaitingPayment;

}

