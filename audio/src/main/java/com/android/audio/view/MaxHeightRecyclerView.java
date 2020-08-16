package com.android.audio.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.audio.R;


/**
 * Created by xuzhb on 2020/6/30
 * Desc:支持maxHeight的RecyclerView
 */
public class MaxHeightRecyclerView extends RecyclerView {

    private int maxHeight;

    public MaxHeightRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public MaxHeightRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaxHeightRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecyclerView);
        maxHeight = ta.getLayoutDimension(R.styleable.MaxHeightRecyclerView_max_height, 0);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (maxHeight > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, heightSpec);
    }
}
