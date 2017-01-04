package ch.ielse.demo.p02;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by LY on 2017/1/3.
 */

public class ImageWatcher extends FrameLayout implements GestureDetector.OnGestureListener {

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

    private ValueAnimator animTransitions;
    private final GestureDetector gestureDetector;

    public ImageWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);

        gestureDetector = new GestureDetector(context, this);
        mTouchSclop = ViewConfiguration.get(context).getScaledTouchSlop();

        iSource = new ImageView(context);
        iSource.setBackgroundColor(0xFFFFFFFF);
        iSource.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(iSource, lpSource);
        setVisibility(View.INVISIBLE);
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


        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;
        animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
        animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float) animation.getAnimatedValue();
                lpSource.width = (int) (mOriginWidth + (mDefaultWidth - mOriginWidth) * p);
                lpSource.height = (int) (mOriginHeight + (mDefaultHeight - mOriginHeight) * p);
                iSource.requestLayout();
                iSource.setTranslationX(mOriginTranslateX + (mDefaultTranslateX - mOriginTranslateX) * p);
                iSource.setTranslationY(mOriginTranslateY + (mDefaultTranslateY - mOriginTranslateY) * p);

                setBackgroundColor(colorEvaluator.evaluate(p, 0x00000000, 0xFF000000));
            }
        });
        animTransitions.start();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;

    }


    private final TypeEvaluator<Integer> colorEvaluator = new TypeEvaluator<Integer>() {
        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            int startColor = startValue;
            int endColor = endValue;

            int alpha = (int) (Color.alpha(startColor) + fraction * (Color.alpha(endColor) - Color.alpha(startColor)));
            int red = (int) (Color.red(startColor) + fraction * (Color.red(endColor) - Color.red(startColor)));
            int green = (int) (Color.green(startColor) + fraction * (Color.green(endColor) - Color.green(startColor)));
            int blue = (int) (Color.blue(startColor) + fraction * (Color.blue(endColor) - Color.blue(startColor)));

            return Color.argb(alpha, red, green, blue);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                onUp(event);
                break;
        }

        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.e("TTT", "onDown x:" + e.getX() + "#y:" + e.getY() + "#rawx:" + e.getRawX() + "#rawy:" + e.getRawY());
        resetTouchConfigs();
        mTouchDownX = e.getX();
        mTouchDownY = e.getY();
        return true;
    }

    public void onUp(MotionEvent e) {
        if (mTouchMode == TOUCH_MODE_EXIT) {
            handleExitTouchResult();
        }

        //mTouchMode = TOUCH_MODE_AUTO_FLING;
    }

    boolean hasCheckExitGesture;

    private void resetTouchConfigs() {
        mTouchMode = TOUCH_MODE_NONE;
        hasCheckExitGesture = false;
    }

    private static final int TOUCH_MODE_NONE = 0;
    private static final int TOUCH_MODE_EXIT = 2;
    private static final int TOUCH_MODE_AUTO_FLING = 9;

    private int mTouchMode;
    private float mTouchDownX, mTouchDownY;
    private float mTouchSclop;

    @Override
    public void onShowPress(MotionEvent e) {
        Log.e("TTT", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.e("TTT", "onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!hasCheckExitGesture && mTouchMode == TOUCH_MODE_NONE) {
            final float moveX = e2.getX() - mTouchDownX;
            final float moveY = e2.getY() - mTouchDownY;
//            Log.e("TTT", "onScroll e1.getY():" + e1.getY() + "#e2.getY():" + e2.getY() + "#mTouchDownY:" + mTouchDownY);
//            Log.e("TTT", "onScroll moveX:" + moveX + "##moveY:" + moveY);

            if (moveY > mTouchSclop && moveY > moveX * 2) {
                mTouchMode = TOUCH_MODE_EXIT;
            }
        }

        if (mTouchMode == TOUCH_MODE_EXIT) {
            handleExitGesture(e2);
        }

        Log.e("TTT", "onScroll distanceX:" + distanceX + "##distanceY:" + distanceY);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.e("TTT", "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e("TTT", "onFling");
        return false;
    }


    /**
     * 退出进度 0
     */
    private float mExitScalingRef;
    private float currScale;


    /**
     * 单手退出拖拽
     */
    private void handleExitGesture(MotionEvent e1) {
        mExitScalingRef = 1;
        final float moveY = e1.getY() - mTouchDownY;
        final float moveX = e1.getX() - mTouchDownX;
        if (moveY > 0) {
            mExitScalingRef -= moveY / getHeight();
        }
        if (mExitScalingRef < 0.3) mExitScalingRef = 0.3f;

        iSource.setTranslationX(mDefaultTranslateX + moveX);
        iSource.setTranslationY(mDefaultTranslateY + moveY);
        iSource.setScaleX(mExitScalingRef);
        iSource.setScaleY(mExitScalingRef);
        setBackgroundColor(colorEvaluator.evaluate(mExitScalingRef, 0x00000000, 0xFF000000));
    }

    @Override
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        super.setBackgroundColor(color);
    }

    private int mBackgroundColor;

    private void handleExitTouchResult() {
        if (mExitScalingRef > 0.9f) {
            // 恢复

        } else {
            // 退出
            final int startWidth = mDefaultWidth;
            final int endWidth = mOriginWidth;
            final int startHeight = mDefaultHeight;
            final int endHeight = mOriginHeight;
            final float startTranslateX = iSource.getTranslationX();
            final float endTranslateX = mOriginTranslateX;
            final float startTranslateY = iSource.getTranslationY();
            final float endTranslateY = mOriginTranslateY;
            final int startBackgroundColor = mBackgroundColor;
            final int endBackgroundColor = 0x00000000;
            final float startScaleX = iSource.getScaleX();
            final float endScaleX = 1;
            final float startScaleY = iSource.getScaleY();
            final float endScaleY = 1;

            if (animTransitions != null) animTransitions.cancel();
            animTransitions = null;
            animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(350);
            animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (float) animation.getAnimatedValue();

                    boolean sizeChanged = false;
                    if (startWidth != endWidth) {
                        lpSource.width = (int) (startWidth + (endWidth - startWidth) * p);
                        sizeChanged = true;
                    }
                    if (startHeight != endHeight) {
                        lpSource.height = (int) (startHeight + (endHeight - startHeight) * p);
                        sizeChanged = true;
                    }
                    if (sizeChanged) {
                        iSource.requestLayout();
                    }
                    if (startTranslateX != endTranslateX) {
                        iSource.setTranslationX(startTranslateX + (endTranslateX - startTranslateX) * p);
                    }
                    if (startTranslateY != endTranslateY) {
                        iSource.setTranslationY(startTranslateY + (endTranslateY - startTranslateY) * p);
                    }
                    if (startScaleX != endScaleX) {
                        iSource.setScaleX(startScaleX + (endScaleX - startScaleX) * p);
                    }
                    if (startScaleY != endScaleY) {
                        iSource.setScaleY(startScaleY + (endScaleY - startScaleY) * p);
                        Log.e("TTT", "scale endScaleY:" + endScaleY);
                    }

                    setBackgroundColor(colorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
                }
            });
            animTransitions.start();
        }
    }
}
