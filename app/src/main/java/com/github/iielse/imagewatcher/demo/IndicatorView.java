package com.github.iielse.imagewatcher.demo;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class IndicatorView extends LinearLayout {
    private final List<ImageView> dotList = new ArrayList<>();
    private final int size;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics d = new DisplayMetrics();
        ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(d);
        size = (int)(6 * d.density + 0.5);
        setPadding(size, 0, 0, 0);
    }

    public void reset(int count, int select, int normalRes, int selectRes) {
        if (count < 0) count = 0;
        if (select > count) select = count;

        dotList.clear();
        removeAllViewsInLayout();

        if (count > 1) {
            LayoutParams lpDot = new LayoutParams(size, size);
            lpDot.setMargins(0, 0, size, 0);
            for (int i = 0; i < count; i++) {
                ImageView vDot = new ImageView(getContext());
                vDot.setLayoutParams(lpDot);

                vDot.setImageResource(i == select ? selectRes : normalRes);

                dotList.add(vDot);
                addView(vDot);
            }
        }
    }

    public void select(int pos, int normalRes, int selectRes) {
        if (dotList.size() == 0) return;

        pos = pos % dotList.size();

        for (int i = 0; i < dotList.size(); i++) {
            final ImageView vDot = dotList.get(i);
            vDot.setImageResource(i == pos ? selectRes :
                    normalRes);
        }
    }
}
