package ch.ielse.demo.p02;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

public class SquareImageView extends ImageView {
    private float mMaxSize;

    public SquareImageView(Context context) {
        this(context, null);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareImageView);
        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        mMaxSize = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                a.getDimension(R.styleable.SquareImageView_maxSize, 216)
                , mDisplayMetrics) + 0.5f);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (mMaxSize > 0 && widthSize > mMaxSize) {
            widthSize = (int) mMaxSize;
        }
        setMeasuredDimension(widthSize, widthSize);
    }
}
