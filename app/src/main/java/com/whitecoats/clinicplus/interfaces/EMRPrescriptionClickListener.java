package com.whitecoats.clinicplus.interfaces;

import android.view.View;

import com.whitecoats.clinicplus.models.EMRPrescriptionModel;

import org.json.JSONArray;

/**
 * Created by Mohammad suhail ahmed on 01-09-2020.
 */
public interface EMRPrescriptionClickListener {
    public void onItemClick(View v, EMRPrescriptionModel emrPrescriptionModel);
}
