package ch.ielse.view.refresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import ch.ielse.demo.p04.R;
import ch.ielse.demo.p04.utils.Logger;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;


public class WaterRefreshHeader extends View implements PtrUIHandler {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();
    private final Path mPath = new Path();
    private static final String VALUE_SUCCESS = "刷新成功";
    private final float[] mTextWidths = new float[VALUE_SUCCESS.length()];

    private static final int STATE_NONE = 0;
    private static final int STATE_DRAGGED = 1;
    private static final int STATE_DRAGGED_RELEASE = 2;
    private static final int STATE_LOADING = 3;
    private static final int STATE_RESULT = 4;
    private int mState = STATE_NONE;

    private final int mCircleWaterRadius;
    private final int mMinDragRadius;
    private final int mSpacing;
    private final int mCircleWaterHeightWithSpacing;

    private int mOffsetToKeepHeaderWhileLoading;
    private int mHeight;
    private int mWidth;
    private int mCenterX;
    private int initialWaterPosY;
    private int mWaterCurrentPosY;
    private float animationPercent;
    private float mCircleWaterCriticalPercent;
    private Bitmap bmpRefreshArrow;
    private Bitmap bmpLoading;
    private Bitmap bmpSuccessTip;
    private float successTextSize;

    private Animation mDragReleaseAnimation;
    private Animation mLoadingAnimation;
    private Animation mResultAnimation;

    public WaterRefreshHeader(Context context) {
        this(context, null);
    }

    public WaterRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        final DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        mSpacing = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, mDisplayMetrics) + 0.5f);
        mCircleWaterRadius = (int) ((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 37.125f, mDisplayMetrics) + 0.5f) / 2);
        mMinDragRadius = (int) ((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8.4375f, mDisplayMetrics) + 0.5f) / 2);
        successTextSize = (int) ((TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 21f, mDisplayMetrics) + 0.5f) / 2);
        mCircleWaterHeightWithSpacing = mCircleWaterRadius * 2 + mSpacing * 2;

        bmpLoading = BitmapFactory.decodeResource(context.getResources(), R.mipmap.refresh_loading);
        bmpSuccessTip = BitmapFactory.decodeResource(context.getResources(), R.mipmap.refresh_success);
        bmpRefreshArrow = BitmapFactory.decodeResource(context.getResources(), R.mipmap.refresh_arrow);

        setupAnimations();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final DisplayMetrics mDisplayMetrics = getContext().getResources().getDisplayMetrics();
        setMeasuredDimension(widthMeasureSpec, mHeight =
                (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 98, mDisplayMetrics) + 0.5f));
        mOffsetToKeepHeaderWhileLoading = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mDisplayMetrics) + 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        mCenterX = mWidth / 2;
        initialWaterPosY = mHeight - mCircleWaterHeightWithSpacing;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mState == STATE_DRAGGED) {
            final float currentPercent = animationPercent;
            final float p;
            if (currentPercent < mCircleWaterCriticalPercent || mCircleWaterCriticalPercent == 0) {
                p = 0;
            } else if (currentPercent > 1) {
                p = 1;
            } else {
                p = (currentPercent - mCircleWaterCriticalPercent) / (1 - mCircleWaterCriticalPercent);
            }
            mPaint.setColor(0xFFB1B1B1);
            final int y1 = (mCircleWaterHeightWithSpacing / 2) + mWaterCurrentPosY;
            final int radius1 = (int) (mCircleWaterRadius * (1 - 0.4f * p)); // circle radius after scale
            final int dragRadiusChangedValue = (int) ((mCircleWaterRadius - mMinDragRadius) * p);
            final int y2 = (mCircleWaterHeightWithSpacing / 2) + initialWaterPosY + dragRadiusChangedValue;
            final int radius2 = mCircleWaterRadius - dragRadiusChangedValue; // bottom radius after drag
            if (y2 > y1) {
                calculateBezierPath(mPath, mCenterX, y1, radius1, mCenterX, y2, radius2);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mPath, mPaint);
                mPaint.setColor(0xFF888888);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(mPath, mPaint);

                canvas.save();
                canvas.scale(1 - p * 0.4f, 1 - p * 0.4f, mCenterX, y1);
                canvas.drawBitmap(bmpRefreshArrow, mCenterX - bmpRefreshArrow.getWidth() / 2, y1 - bmpRefreshArrow.getHeight() / 2, mPaint);
                canvas.restore();
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mCenterX, y1, radius1, mPaint);
                mPaint.setColor(0xFF888888);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(mCenterX, y1, radius1, mPaint);

                canvas.drawBitmap(bmpRefreshArrow, mCenterX - bmpRefreshArrow.getWidth() / 2, y1 - bmpRefreshArrow.getHeight() / 2, mPaint);
            }
        } else if (mState == STATE_DRAGGED_RELEASE) {
            final float p = animationPercent;
            mPaint.setColor(Color.argb((int) (255 * (1 - p)), 177, 177, 177));
            final int y1 = (mCircleWaterHeightWithSpacing / 2) + mWaterCurrentPosY;
            final int radius1 = (int) (mCircleWaterRadius * (0.6f - 0.1f * p));
            final int y2 = (mCircleWaterHeightWithSpacing / 2) + (int) (initialWaterPosY * (1 - p * 0.6f)) + (mCircleWaterRadius - mMinDragRadius);
            final int radius2 = mCircleWaterRadius - (mCircleWaterRadius - mMinDragRadius);
            calculateBezierPath(mPath, mCenterX, y1, radius1, mCenterX, y2, radius2);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mPath, mPaint);
            mPaint.setColor(Color.argb((int) (255 * (1 - p)), 136, 136, 136));
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mPath, mPaint);
        } else if (mState == STATE_LOADING) {
            final float p = animationPercent;// [0 , 1]
            mPaint.setAlpha(255);
            final int angle = (int) (p * 120) / 10 * 30; // do not change
            canvas.save();
            int loadingCenterY = mHeight - mOffsetToKeepHeaderWhileLoading / 2 - adjustLoadingOffsetY;
            canvas.rotate(angle, mCenterX, loadingCenterY);

            canvas.drawBitmap(bmpLoading, mCenterX - bmpLoading.getWidth() / 2, loadingCenterY - bmpLoading.getHeight() / 2, mPaint);
            canvas.restore();
        } else if (mState == STATE_RESULT) {
            final float p = animationPercent * 3 > 1 ? 1 : animationPercent * 3;

            final float tipTextWidth = calculateTextTotalWidth(VALUE_SUCCESS, mPaint);
            mPaint.setTextSize(successTextSize);
            mPaint.setColor(Color.argb((int) (255 * p), 128, 128, 128));
            final int tipImgWidthWithSpacing = mSpacing + bmpSuccessTip.getWidth();
            canvas.drawText(VALUE_SUCCESS, mCenterX - tipTextWidth / 2 + tipImgWidthWithSpacing / 2, mHeight - mOffsetToKeepHeaderWhileLoading / 2 + successTextSize / 2, mPaint);
            canvas.drawBitmap(bmpSuccessTip, mCenterX - tipTextWidth / 2 - tipImgWidthWithSpacing / 2,
                    mHeight - mOffsetToKeepHeaderWhileLoading / 2 - bmpSuccessTip.getHeight() * 0.36f, mPaint);
        }

    }


    private float calculateTextTotalWidth(String text, Paint paint) {
        paint.getTextWidths(text, mTextWidths);
        float textTotalWidth = 0;
        for (float f : mTextWidths) {
            textTotalWidth += f;
        }
        return textTotalWidth;
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        Logger.e("AAA onUIPositionChange STATE_NONE");
        mState = STATE_NONE;

        clearAnimation();
        mCircleWaterCriticalPercent = 0;
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        frame.setRatioOfHeaderHeightToRefresh(1);
        frame.setOffsetToKeepHeaderWhileLoading(mOffsetToKeepHeaderWhileLoading);
        Logger.e("AAA onUIRefreshPrepare STATE_DRAGGED");
        mState = STATE_DRAGGED;
    }

    @Override
    public void onUIRefreshBegin(final PtrFrameLayout frame) {
        Logger.e("AAA onUIRefreshBegin STATE_LOADING");
        if (mState != STATE_LOADING) {
            mState = STATE_LOADING;
            startAnimation(mLoadingAnimation);
        }
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        Logger.e("AAA onUIRefreshComplete");
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        ensureCriticalValueOfShapeChange();

        setTranslationY(1f * (ptrIndicator.getCurrentPercent() > 1 ? -getTop() : 0));

        if (mState == STATE_DRAGGED && ptrIndicator.getCurrentPercent() >= 1) {
            Logger.e("AAA onUIPositionChange STATE_DRAGGED_RELEASE");
            mState = STATE_DRAGGED_RELEASE;
            startAnimation(mDragReleaseAnimation);
        }

        if (mState == STATE_DRAGGED) {
            animationPercent = ptrIndicator.getCurrentPercent();
            mWaterCurrentPosY = initialWaterPosY;
            if (ptrIndicator.getCurrentPosY() > mCircleWaterHeightWithSpacing) {
                mWaterCurrentPosY = initialWaterPosY - (ptrIndicator.getCurrentPosY() - mCircleWaterHeightWithSpacing);
                if (mWaterCurrentPosY < 0) mWaterCurrentPosY = 0;
            }

            invalidate();
        } else if (mState == STATE_DRAGGED_RELEASE || mState == STATE_LOADING) {
            adjustLoadingOffsetY = mHeight - mCircleWaterHeightWithSpacing / 2 - (mOffsetToKeepHeaderWhileLoading - bmpLoading.getHeight()) / 2
                    - mSpacing - (int) (mCircleWaterRadius * 0.4f);
            if (ptrIndicator.getCurrentPercent() <= mCircleWaterCriticalPercent) {
                adjustLoadingOffsetY = 0;
            } else if (ptrIndicator.getCurrentPercent() < 1 && ptrIndicator.getCurrentPercent() > mCircleWaterCriticalPercent) {
                adjustLoadingOffsetY *= (ptrIndicator.getCurrentPercent() - mCircleWaterCriticalPercent) / (1 - mCircleWaterCriticalPercent);
            }
        }

        Logger.e("AAA s [" + mState + "] adjustLoadingOffsetY " + adjustLoadingOffsetY + "##ptrIndicator.getCurrentPercent()" + ptrIndicator.getCurrentPercent() + "##mCircleWaterCriticalPercent " + mCircleWaterCriticalPercent);
    }

    int adjustLoadingOffsetY;

    private void ensureCriticalValueOfShapeChange() {
        if (mCircleWaterCriticalPercent == 0) {
            mCircleWaterCriticalPercent = (1f * mHeight - mOffsetToKeepHeaderWhileLoading) / mHeight;
        }
    }

    private void calculateBezierPath(Path path, int x1, int y1, int radius1, int x2, int y2, int radius2) {
        path.reset();
        path.moveTo(x1 - radius1, y1);
        rectF.set(x1 - radius1, y1 - radius1, x1 + radius1, y1 + radius1);
        path.arcTo(rectF, 180, 180);
        path.quadTo(x2 + radius2, (y1 + y2) / 2, x2 + radius2, y2);
        rectF.set(x2 - radius2, y2 - radius2, x2 + radius2, y2 + radius2);
        path.arcTo(rectF, 0, 180);
        path.quadTo(x2 - radius2, (y1 + y2) / 2, x1 - radius1, y1);
    }

    private void setupAnimations() {
        final Animation animation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                animationPercent = interpolatedTime;
                invalidate();
            }
        };
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mState != STATE_LOADING) {
                    mState = STATE_LOADING;
                    startAnimation(mLoadingAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        animation.setDuration(350);
        mDragReleaseAnimation = animation;

        final Animation animationLoading = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                animationPercent = interpolatedTime;
                invalidate();
            }
        };
        animationLoading.setRepeatCount(Animation.INFINITE);
        animationLoading.setRepeatMode(Animation.RESTART);
        animationLoading.setInterpolator(LINEAR_INTERPOLATOR);
        animationLoading.setDuration(1200);
        mLoadingAnimation = animationLoading;

        final Animation animationResult = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                animationPercent = interpolatedTime;
                invalidate();
            }
        };
        animationResult.setDuration(1000);
        mResultAnimation = animationResult;
    }

    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();

    public void refreshComplete(boolean result, final PtrFrameLayout frame) {
        if (!result) {
            frame.refreshComplete();
        } else if (mState != STATE_RESULT) {
            mState = STATE_RESULT;
            mResultAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    frame.refreshComplete();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            startAnimation(mResultAnimation);
        }
    }
}
