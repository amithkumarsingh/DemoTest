package com.whitecoats.clinicplus.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

public class TextImageClass extends androidx.appcompat.widget.AppCompatTextView {

    public TextImageClass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextImageClass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addImage(String imgStr, int imgID, int imgWidth, int imgHeight) {
        SpannableStringBuilder ss = new SpannableStringBuilder(this.getText());

        Drawable drawable = ContextCompat.getDrawable(this.getContext(), imgID);
        drawable.mutate();
        drawable.setBounds(0, 0, imgWidth, imgHeight);
        int start = this.getText().toString().indexOf(imgStr);
        ss.setSpan(new VerticalImageSpan(drawable), start, start + imgStr.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        this.setText(ss, BufferType.SPANNABLE);
    }


}
