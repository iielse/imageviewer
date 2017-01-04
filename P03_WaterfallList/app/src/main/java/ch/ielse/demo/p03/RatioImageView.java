package ch.ielse.demo.p03;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RatioImageView extends ImageView {
    private int mWidthRatio;
    private int mHeightRatio;


    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRatio(int widthRatio, int heightRatio) {
        mWidthRatio = widthRatio;
        mHeightRatio = heightRatio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mWidthRatio != 0 && mHeightRatio != 0) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightValue = (int) (widthSize * 1f / mHeightRatio * mWidthRatio);
            setMeasuredDimension(widthSize, heightValue);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
