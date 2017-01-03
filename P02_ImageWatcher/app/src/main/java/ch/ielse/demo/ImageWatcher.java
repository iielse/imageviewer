package ch.ielse.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by LY on 2017/1/3.
 */

public class ImageWatcher extends FrameLayout implements View.OnClickListener {

    private FrameLayout.LayoutParams lpSource =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    private final ImageView iSource;
    private int mOriginWidth;
    private int mOriginHeight;
    private int mDefaultWidth;
    private int mDefaultHeight;
    private int mDefaultTranslateX;
    private int mDefaultTranslateY;
    private int mOriginTranslateX;
    private int mOriginTranslateY;

    private ValueAnimator animShowEnter;

    public ImageWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        iSource = new ImageView(context);
        iSource.setBackgroundColor(0xFFFFFFFF);
        iSource.setOnClickListener(this);
        addView(iSource, lpSource);
        setVisibility(View.INVISIBLE);
        setBackgroundColor(0x99000000);
    }

    public void show(ImageView iOrigin) {
        iOrigin.setDrawingCacheEnabled(true);
        Bitmap bitmap = iOrigin.getDrawingCache();
        if (bitmap == null) return;

        Bitmap mirror = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        iOrigin.setDrawingCacheEnabled(false);

        iSource.setImageBitmap(mirror);

        setVisibility(View.VISIBLE);
        int[] location = new int[2];
        iOrigin.getLocationOnScreen(location);
        mOriginWidth = iOrigin.getWidth();
        mOriginHeight = iOrigin.getHeight();
        mOriginTranslateX = location[0];
        mOriginTranslateY = location[1];

        lpSource.width = mOriginWidth;
        lpSource.height = mOriginHeight;
        iSource.setTranslationX(mOriginTranslateX);
        iSource.setTranslationY(mOriginTranslateY);
        iSource.requestLayout();

        calcDefaultConfigValue();


        if (animShowEnter != null) animShowEnter.cancel();
        animShowEnter = null;
        animShowEnter = ValueAnimator.ofFloat(0, 1).setDuration(400);
        animShowEnter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animShowEnter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float) animation.getAnimatedValue();
                lpSource.width = (int) (mOriginWidth + (mDefaultWidth - mOriginWidth) * p);
                Log.e("TTT", "animation update: [p]" + p + "###" + (int) (mOriginWidth + (mDefaultWidth - mOriginWidth) * p));
                lpSource.height = (int) (mOriginHeight + (mDefaultHeight - mOriginHeight) * p);
                iSource.requestLayout();
                iSource.setTranslationX(mOriginTranslateX + (mDefaultTranslateX - mOriginTranslateX) * p);
                iSource.setTranslationY(mOriginTranslateY + (mDefaultTranslateY - mOriginTranslateY) * p);
            }
        });
        animShowEnter.start();
    }

    @Override
    public void onClick(View v) {
        if (v == iSource) {
            setVisibility(View.GONE);
        }
    }

    private void calcDefaultConfigValue() {
        final int mWidth = getWidth();
        final int mHeight = getHeight();
        if (mOriginWidth * 1f / mOriginHeight > mWidth * 1f / mHeight) {
            // hor
            mDefaultWidth = mWidth;
            mDefaultHeight = (int) (mDefaultWidth * 1f / mOriginWidth * mOriginHeight);
            mDefaultTranslateX = 0;
            mDefaultTranslateY = (mHeight - mDefaultHeight) / 2;
        } else {
            mDefaultHeight = mHeight;
            mDefaultWidth = (int) (mDefaultHeight * 1f / mOriginHeight * mOriginWidth);
            mDefaultTranslateY = 0;
            mDefaultTranslateX = (mWidth - mDefaultWidth) / 2;
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animShowEnter != null) animShowEnter.cancel();
        animShowEnter = null;
    }
}
