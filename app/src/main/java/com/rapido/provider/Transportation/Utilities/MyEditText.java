package com.rapido.provider.Transportation.Utilities;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;

public class MyEditText extends AppCompatEditText {
    public MyEditText(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/Montserrat-Medium.ttf", context);
        setTypeface(customFont);
    }
}
