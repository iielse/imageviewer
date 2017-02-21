//package ch.ielse.view.refresh;
//
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.ColorFilter;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PixelFormat;
//import android.graphics.drawable.Animatable;
//import android.graphics.drawable.Drawable;
//import android.util.DisplayMetrics;
//import android.util.TypedValue;
//import android.view.animation.Animation;
//import android.view.animation.Interpolator;
//import android.view.animation.LinearInterpolator;
//import android.view.animation.Transformation;
//
//import ch.ielse.demo.p04.R;
//import ch.ielse.demo.p04.utils.Logger;
//
//public class WaterRefreshView extends   Drawable implements Drawable.Callback, Animatable  {
//    @Override
//    public void invalidateDrawable(Drawable who) {
//
//    }
//
//    @Override
//    public void scheduleDrawable(Drawable who, Runnable what, long when) {
//
//    }
//
//    @Override
//    public void unscheduleDrawable(Drawable who, Runnable what) {
//
//    }
//    // private int mFixedTransY;
//
//    static class Circle {
//        float x;//圆x坐标
//        float y;//圆y坐标
//        float radius;//圆半径
//    }
//
//    private Circle mWater;
//    private Circle mWaterByDrag;
//
//    private Paint mPaint;
//    private Path mPath;
//    private float mDefaultCircleRadius;//圆半径最大值
//    private float mMinCircleRaidus;//圆半径最小值
//    private Bitmap arrowBitmap;//箭头
//    private final static int BACK_ANIM_DURATION = 180;
//    private final static float STROKE_WIDTH = 0;//边线宽度
//
//    private Bitmap loadingBitmap;
//    private float mCircleSpacing;
//
//    static final int STATE_ORIGIN = 1;
//    static final int STATE_SHAPE_CHANGING = 2;
//    static final int STATE_SHAPE_RESTORE = 3;
//    static final int STATE_LOADING = 4;
//
//    private int mState = STATE_ORIGIN;
//
//    public WaterRefreshView(Context context) {
//        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
//
////        parent.setTotalDragDistance((int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 98, mDisplayMetrics) + 0.5f));
//
//        setupAnimation();
//
//        mWater = new Circle();
//        mWaterByDrag = new Circle();
//        mPath = new Path();
//        mPaint = new Paint();
//        mPaint.setColor(Color.GRAY);
//        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        // mPaint.setStrokeWidth(STROKE_WIDTH);
//        arrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.refresh_arrow);
//
//        loadingBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.common_loading_tiny1);
//
//
//
//        mCircleSpacing = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, mDisplayMetrics) + 0.5f);
//
//        mDefaultCircleRadius = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, mDisplayMetrics) + 0.5f);
//        mMinCircleRaidus = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mDisplayMetrics) + 0.5f);
//
//        mWater.radius = mDefaultCircleRadius;
//        mWaterByDrag.radius = mDefaultCircleRadius;
//   //     mWater.x = (parent.getWidth() / 2f);
//        mWater.y = mDefaultCircleRadius;
//        mWaterByDrag.x = mWater.x;
//        mWaterByDrag.y = mWater.y;
//
//
//     //   Logger.e("initial parent width " + parent.getWidth());
//
//        int defaultTop = (int) (mWater.radius * 2 + mCircleSpacing);
//      //  mFixedTransY = -defaultTop;
//        mStartChangedYPoint = (int) (defaultTop + mCircleSpacing);
//    }
//
//    float aaa;
//    float mRealStartChangedStartPercent;
//    float mPercentSPoint; // 高度定住在形状变化起点时刻的  refreshView 所提供的百分比; 一般仅赋值一次
//    private int mStartChangedYPoint;// 高度定住在形状变化起点时刻的Y轴值; 一般仅赋值一次
//    int mOff = 0; // 所有的drag造作的位移总和结果
//
//    @Override
//    public void setPercent(float percent, boolean invalidate) {
//        if (percent > 1) {
//     //       mRefreshLayout.setRefreshing(true, true);
//        } else {
//            if (mOff >= mStartChangedYPoint && mPercentSPoint == 0) {
//                // 找到形变起点
//                mPercentSPoint = (percent / mOff) * mStartChangedYPoint;
//            }
//
//            if (mPercentSPoint == 0 || percent <= mPercentSPoint) {
//                mState = STATE_ORIGIN;
//                //  invalidateSelf();
//            } else {
//                mState = STATE_SHAPE_CHANGING;
//                // 形变修正后的p进度
//                float mDragPercent = (percent - mPercentSPoint) / (1f - mPercentSPoint);
//
//                float waterRadius = mDefaultCircleRadius * (1 - 0.25f * mDragPercent);
//                float dragRadius = mDefaultCircleRadius - (mDefaultCircleRadius - mMinCircleRaidus) * mDragPercent;
//                float dragOffsetOfWater = mOff - mStartChangedYPoint + (waterRadius - dragRadius);
//                mWater.radius = waterRadius;
//                mWaterByDrag.radius = dragRadius;
//                mWaterByDrag.y = mWater.y + dragOffsetOfWater;
//            }
//        }
//    }
//
//
//    @Override
//    public void offsetTopAndBottom(int offset) {
//        if (mState == STATE_ORIGIN) {
//     //       mFixedTransY += offset;
//        }
//        mOff += offset;
//        invalidateSelf();
//    }
//
//    public void resetOriginals() {
//        setPercent(0, false);
//
//        int defaultTop = (int) (mWater.radius * 2 + mCircleSpacing);
//      //  mFixedTransY = -defaultTop;
//    }
//
//    @Override
//    public void draw(Canvas canvas) {
//        Logger.e("draw state " + mState);
//        final int saveCount = canvas.save();
//
//
//        if (mState == STATE_ORIGIN) {
//     //       canvas.translate(0, mFixedTransY);
//            canvas.drawCircle(mWater.x, mWater.y, mWater.radius, mPaint);
//            canvas.drawBitmap(arrowBitmap, mWater.x, mWater.y, mPaint);
//        } else if (mState == STATE_SHAPE_CHANGING) {
//     //       canvas.translate(0, mFixedTransY);
//            makeBezierPath();
//            canvas.drawPath(mPath, mPaint);
//            canvas.drawCircle(mWater.x, mWater.y, mWater.radius, mPaint);
//            canvas.drawCircle(mWaterByDrag.x, mWaterByDrag.y, mWaterByDrag.radius, mPaint);
//
//            //    RectF bitmapArea = new RectF(mWater.x-0.5f*mWater.radius,mWater.y-0.5f*mWater.radius,mWater.x+ 0.5f*mWater.radius,mWater.y+0.5f*mWater.radius);
//            // scale 画布 代替上面代码
//            canvas.drawBitmap(arrowBitmap, mWater.x, mWater.y, mPaint);
//        } else if (mState == STATE_SHAPE_RESTORE) {
//
//     //       canvas.translate(0, mFixedTransY);
//
//            // mPaint.setAlpha((int)(255 * mDragPercent));
//            makeBezierPath();
//            canvas.drawPath(mPath, mPaint);
//            canvas.drawCircle(mWater.x, mWater.y, mWater.radius, mPaint);
//            canvas.drawCircle(mWaterByDrag.x, mWaterByDrag.y, mWaterByDrag.radius, mPaint);
//
//            //    RectF bitmapArea = new RectF(mWater.x-0.5f*mWater.radius,mWater.y-0.5f*mWater.radius,mWater.x+ 0.5f*mWater.radius,mWater.y+0.5f*mWater.radius);
//            // scale 画布 代替上面代码
//            canvas.drawBitmap(arrowBitmap, mWater.x, mWater.y, mPaint);
//
//        } else if (mState == STATE_LOADING) {
//            canvas.rotate(aaa);
//            canvas.drawBitmap(loadingBitmap, 0, 0, mPaint);
//        }
//        canvas.restoreToCount(saveCount);
//    }
//
//
//    @Override
//    public boolean isRunning() {
//        return false;
//    }
//
//
//    @Override
//    public void start() {
//        //  isRefreshing = true;
//        mAnimationShapeTransition.reset();
//        mAnimationLoading.reset();
//
////        mRefreshLayout.startAnimation(mAnimationShapeTransition);
//
//    }
//
//    @Override
//    public void stop() {
//        //  isRefreshing = false;
////        mRefreshLayout.clearAnimation();
//
//        resetOriginals();
//    }
//
//
//    /**
//     * 创建回弹动画
//     * 上圆半径减速恢复至最大半径
//     * 下圆半径减速恢复至最大半径
//     * 圆心距减速从最大值减到0(下圆Y从当前位置移动到上圆Y)。
//     *
//     * @return
//     */
//    Animation mAnimationShapeTransition, mAnimationLoading;
//
//    public void setupAnimation() {
//
//        mAnimationShapeTransition = new Animation() {
//            @Override
//            public void applyTransformation(final float interpolatedTime, Transformation t) {
//
//                float mDragPercent = interpolatedTime;
//
//                float waterRadius = mDefaultCircleRadius * (0.75f - 0.1f * mDragPercent);
//                float dragRadius = mMinCircleRaidus * (1 - 0.1f * mDragPercent);
//                float dragOffsetOfWater = (mOff - mStartChangedYPoint) * 0.5f * (1 - mDragPercent);
//                mWater.radius = waterRadius;
//                mWaterByDrag.radius = dragRadius;
//                mWaterByDrag.y = mWater.y + dragOffsetOfWater;
//                Logger.e("refreshLoadingUI " +  mWaterByDrag.y);
//
//                invalidateSelf();
//            }
//        };
//        mAnimationShapeTransition.setDuration(300);
//        mAnimationShapeTransition.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                mState = STATE_SHAPE_RESTORE;
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                //mRefreshLayout.startAnimation(mAnimationLoading);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//
//        mAnimationLoading = new Animation() {
//            @Override
//            public void applyTransformation(float interpolatedTime, Transformation t) {
//                Logger.e("loadingGO " + interpolatedTime);
//                aaa = interpolatedTime * 360;
//                invalidateSelf();
//            }
//        };
//        mAnimationLoading.setRepeatCount(Animation.INFINITE);
//        mAnimationLoading.setRepeatMode(Animation.RESTART);
//        mAnimationLoading.setInterpolator(LINEAR_INTERPOLATOR);
//        mAnimationLoading.setDuration(1000);
//        mAnimationLoading.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                mState = STATE_LOADING;
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//    }
//
//    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
//
//
//    private void makeBezierPath() {
//        mPath.reset();
//        //获取两圆的两个切线形成的四个切点  获得两个圆切线与圆心连线的夹角
//        double angle = Math.asin((mWater.radius - mWaterByDrag.radius) / (mWaterByDrag.y - mWater.y));
//        float top_x1 = (float) (mWater.x - mWater.radius * Math.cos(angle));
//        float top_y1 = (float) (mWater.y + mWater.radius * Math.sin(angle));
//
//        float top_x2 = (float) (mWater.x + mWater.radius * Math.cos(angle));
//        float top_y2 = top_y1;
//
//        float bottom_x1 = (float) (mWaterByDrag.x - mWaterByDrag.radius * Math.cos(angle));
//        float bottom_y1 = (float) (mWaterByDrag.y + mWaterByDrag.radius * Math.sin(angle));
//
//        float bottom_x2 = (float) (mWaterByDrag.x + mWaterByDrag.radius * Math.cos(angle));
//        float bottom_y2 = bottom_y1;
//
//        mPath.moveTo(mWater.x, mWater.y);
//        mPath.lineTo(top_x1, top_y1);
//        mPath.quadTo((mWaterByDrag.x - mWaterByDrag.radius), (mWaterByDrag.y + mWater.y) / 2, bottom_x1, bottom_y1);
//        mPath.lineTo(bottom_x2, bottom_y2);
//        mPath.quadTo((mWaterByDrag.x + mWaterByDrag.radius), (mWaterByDrag.y + top_y2) / 2, top_x2, top_y2);
//
//        mPath.close();
//    }
//
//    @Override
//    public void setAlpha(int alpha) {
//    }
//
//    @Override
//    public void setColorFilter(ColorFilter colorFilter) {
//    }
//
//    @Override
//    public int getOpacity() {
//        return PixelFormat.TRANSLUCENT;
//    }
//
//}
