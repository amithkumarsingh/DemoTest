package com.whitecoats.clinicplus.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.whitecoats.clinicplus.R;

public class VoiceEMRHelpBottomSheet extends BottomSheetDialogFragment {

    private Context context;

    public VoiceEMRHelpBottomSheet(Context context) {
        this.context = context;
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        final View contentView = View.inflate(getContext(), R.layout.voice_emr_help, null);
        dialog.setContentView(contentView);
    }

}
