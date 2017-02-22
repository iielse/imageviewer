package ch.ielse.demo.p04.business.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import ch.ielse.demo.p04.R;


public class LevelView extends View {
    private final Paint mPaint = new Paint();

    private Bitmap bmpCrown;
    private Bitmap bmpSunlight;
    private Bitmap bmpMoon;
    private Bitmap bmpStar;
    private final int itemSize;

    private int mAdjustOffsetY;
    private int mLevel;
    private int mSpacing;

    public LevelView(Context context) {
        this(context, null);
    }

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        mSpacing = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, mDisplayMetrics) + 0.5f);

        bmpCrown = BitmapFactory.decodeResource(context.getResources(), R.mipmap.level_crown);
        bmpSunlight = BitmapFactory.decodeResource(context.getResources(), R.mipmap.level_sunlight);
        bmpMoon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.level_moon);
        bmpStar = BitmapFactory.decodeResource(context.getResources(), R.mipmap.level_star);

        if (bmpCrown.getWidth() != bmpCrown.getHeight()
                || bmpSunlight.getWidth() != bmpSunlight.getHeight()
                || bmpMoon.getWidth() != bmpMoon.getHeight()
                || bmpStar.getWidth() != bmpStar.getHeight()) {
            throw new IllegalArgumentException("bmp resources must be square");
        }
        if (bmpCrown.getWidth() != bmpSunlight.getWidth()
                || bmpCrown.getWidth() != bmpMoon.getWidth()
                || bmpCrown.getWidth() != bmpStar.getWidth()) {
            throw new IllegalArgumentException("bmp resources must be same size");
        }

        itemSize = bmpCrown.getWidth();

        setLevel(54);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int crown = mLevel / 64;
        final int sunlight = (mLevel - (crown * 64)) / 16;
        final int moon = (mLevel - (crown * 64) - (sunlight * 16)) / 4;
        final int star = mLevel - (crown * 64) - (sunlight * 16) - (moon * 4);

        canvas.save();
        for (int i = 0; i < crown; i++) {
            canvas.drawBitmap(bmpCrown, 0, mAdjustOffsetY, mPaint);
            canvas.translate(itemSize + mSpacing, 0);
        }
        for (int i = 0; i < sunlight; i++) {
            canvas.drawBitmap(bmpSunlight, 0, mAdjustOffsetY, mPaint);
            canvas.translate(itemSize + mSpacing, 0);
        }
        for (int i = 0; i < moon; i++) {
            canvas.drawBitmap(bmpMoon, 0, mAdjustOffsetY, mPaint);
            canvas.translate(itemSize + mSpacing, 0);
        }
        for (int i = 0; i < star; i++) {
            canvas.drawBitmap(bmpStar, 0, mAdjustOffsetY, mPaint);
            canvas.translate(itemSize + mSpacing, 0);
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int resultHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            resultHeight = heightSize;
        } else {
            int selfExpectedResultHeight = bmpCrown.getHeight() + getPaddingTop() + getPaddingBottom();
            resultHeight = selfExpectedResultHeight;
            if (heightMode == MeasureSpec.AT_MOST) {
                resultHeight = Math.min(resultHeight, heightSize);
            }
        }
        mAdjustOffsetY = (resultHeight - itemSize) / 2;
        if (mAdjustOffsetY < 0) mAdjustOffsetY = 0;

        setMeasuredDimension(widthSize, resultHeight);
    }

    public void setLevel(int level) {
        if (level > 256) level = 256;
        else if (level < 0) level = 0;
        mLevel = level;
        invalidate();
    }
}
