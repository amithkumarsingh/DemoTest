package com.whitecoats.clinicplus.utils;


import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSpinner;

public class CustomSpinner extends AppCompatSpinner {
    private int lastPosition = 0;

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        boolean sameSelected = lastPosition == getSelectedItemPosition();
        OnItemSelectedListener onItemSelectedListener = getOnItemSelectedListener();
        if (sameSelected && onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
        lastPosition = position;
    }
}

