package com.whitecoats.clinicplus.models;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.whitecoats.clinicplus.R;

public class PrescriptionViewItemHolder extends RecyclerView.ViewHolder {

    private TextView carTitleText = null;
//    private LinearLayout imageTileOneLayout;

    private ImageView prescriptionImageView = null;

    public PrescriptionViewItemHolder(View itemView) {
        super(itemView);

        if (itemView != null) {
            prescriptionImageView = (ImageView) itemView.findViewById(R.id.imageTileOne);
//            imageTileOneLayout = (LinearLayout) itemView.findViewById(R.id.imageTileOneLayout);
        }
    }


    public ImageView getPrescriptionImageView() {
        return prescriptionImageView;
    }
}
