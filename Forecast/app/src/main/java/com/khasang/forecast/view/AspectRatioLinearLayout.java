package com.khasang.forecast.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.khasang.forecast.R;

public class AspectRatioLinearLayout extends LinearLayout {

    private static final float DEFAULT_ASPECT_RATIO = 1.73f;
    private final float aspectRatio;

    public AspectRatioLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioLinearLayout);
        aspectRatio = typedArray.getFloat(R.styleable.AspectRatioLinearLayout_aspect_ratio, DEFAULT_ASPECT_RATIO);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newWidth;
        int newHeight;

        newWidth = getMeasuredWidth();
        newHeight = (int) (newWidth / aspectRatio);
        setMeasuredDimension(newWidth, newHeight);
    }
}
