//package ch.ielse.demo.p01.s;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
//import ch.ielse.demo.p01.R;
//import ch.ielse.demo.p01.Utils;
//
///**
// * Created by LY on 2017/2/6.
// */
//
//public class CropView extends View implements GestureDetector.OnGestureListener {
//    private final Paint mPaint = new Paint();
//
//    private static final int TOUCH_MODE_NONE = 0; // 无状态
//    private static final int TOUCH_MODE_DOWN = 1; // 按下
//    private static final int TOUCH_MODE_DRAG = 2; // 单点拖拽
//    private static final int TOUCH_MODE_SCALE = 5; // 缩放
//    private static final int TOUCH_MODE_AUTO_FLING = 7; // 动画中
//
//    Matrix mMatrix = new Matrix();
//
//    private Bitmap bmpMask;
//    private Bitmap bmpSource;
//
//    public CropView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        mGestureDetector = new GestureDetector(context, this);
//    }
//
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mWidth = w;
//        mHeight = h;
//        mCropSize = (int) (Math.min(w, h) * 0.70f);
////        mCropLeft = (mWidth - mCropSize) / 2;
////        mCropTop = (mHeight - mCropSize) / 2;
//        mCropCenterX = mWidth / 2;
//        mCropCenterY = mHeight / 2;
//
//        bmpMask = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
//        Canvas maskCanvas = new Canvas(bmpMask);
//        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        maskCanvas.drawColor(0x44000000);
//        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        maskCanvas.drawCircle(mCropCenterX, mCropCenterY, mCropSize / 2, maskPaint);
//        maskPaint.setXfermode(null);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), R.mipmap.avatar, options);
//
//        options.inSampleSize = Utils.calculateInSampleSize(options, mWidth, mHeight);
//        Log.e("TTT", "AAA  options.inSampleSize " + options.inSampleSize);
//        options.inJustDecodeBounds = false;
//        bmpSource = BitmapFactory.decodeResource(getResources(), R.mipmap.avatar, options);
//
//        if (bmpSource.getWidth() >= bmpSource.getHeight() && bmpSource.getHeight() < mCropSize) {
//            int valueWidth = (int) (bmpSource.getWidth() * (mCropSize * 1f / bmpSource.getHeight()));
//            bmpSource = Bitmap.createScaledBitmap(bmpSource, valueWidth, mCropSize, true);
//        } else if (bmpSource.getWidth() < bmpSource.getHeight() && bmpSource.getWidth() < mCropSize) {
//            int valueHeight = (int) (bmpSource.getHeight() * (mCropSize * 1f / bmpSource.getWidth()));
//            bmpSource = Bitmap.createScaledBitmap(bmpSource, mCropSize, valueHeight, true);
//        }
//        mSourceLeft = (mWidth - bmpSource.getWidth()) / 2;
//        mSourceTop = (mHeight - bmpSource.getHeight()) / 2;
//    }
//
//    float mSourceTop, mSourceLeft;
//
//    private final GestureDetector mGestureDetector;
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.save();
//        canvas.translate(mSourceLeft, mSourceTop);
//        canvas.drawBitmap(bmpSource, mMatrix, mPaint);
//        canvas.restore();
//
//        mPaint.setColor(0xFFFAEC0E);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(3);
//        canvas.drawCircle(mCropCenterX, mCropCenterY, mCropSize / 2, mPaint);
////        canvas.drawBitmap(bmpMask, 0, 0, mPaint);
//
//        String a = "cScale " + cScale;
//        mPaint.setTextSize(60);
//        canvas.drawText(a, 40,80,mPaint);
//    }
//
//    private int mTouchMode = TOUCH_MODE_NONE;
//
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (a != null) a.cancel();
//
//        final int action = event.getAction() & MotionEvent.ACTION_MASK;
//        switch (action) {
//            case MotionEvent.ACTION_UP:
//                onUp(event);
//                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                if (mTouchMode != TOUCH_MODE_SCALE) {
//                    mLastFingersDistance = 0;
//                    mLastFingersCenterX = 0;
//                    mLastFingersCenterY = 0;
//                }
//                mTouchMode = TOUCH_MODE_SCALE;
//
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//
//                if (event.getPointerCount() - 1 < 1 + 1) {
//                    mTouchMode = TOUCH_MODE_DRAG;
//                }
//                break;
//        }
//        return mGestureDetector.onTouchEvent(event);
//    }
//
//
//    private int mWidth;
//    private int mHeight;
//    private int mCropSize;
//    private int mCropCenterX, mCropCenterY;
//    private float cScale = 1;
//    private float mCurrentTranslateX;
//    private float mCurrentTranslateY;
//
//    private float mLastFingersDistance;
//    private float mLastFingersCenterX;
//    private float mLastFingersCenterY;
//
//    public void onUp(MotionEvent e) {
//        float endX = mCurrentTranslateX, endY = mCurrentTranslateY;
//
//        final int edgeX = (bmpSource.getWidth() - mCropSize) / 2;
//        if (mCurrentTranslateX > edgeX) endX = edgeX;
//        else if (mCurrentTranslateX < -edgeX) endX = -edgeX;
//
//        final int edgeY = (bmpSource.getHeight() - mCropSize) / 2;
//        if (mCurrentTranslateY > edgeY) endY = edgeY;
//        else if (mCurrentTranslateY < -edgeY) endY = -edgeY;
//
//        playAnim();
//    }
//
//    ValueAnimator a;
//
//    private void playAnim() {
//        final float startX = mCurrentTranslateX;
//        final float startY = mCurrentTranslateY;
//
//
//        float endScale = -1;
//        if (cScale < 1) {
//            endScale = 1;
//        } else if (cScale > MAX_SCALE) {
//            endScale = MAX_SCALE;
//        }
//        final float mStartScale = cScale;
//        final float mEndScale = endScale;
//
//        if (a != null) a.cancel();
//        a = ValueAnimator.ofFloat(0, 1);
//        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            float lastValue = 0;
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (float) animation.getAnimatedValue();
//                if (mEndScale > 0) {
//                    float changedScale = (mEndScale - mStartScale) * (value - lastValue);
//                    lastValue = value;
//                    cScale += changedScale;
//                    mMatrix.postScale(1 + changedScale, 1 + changedScale, bmpSource.getWidth() / 2, bmpSource.getHeight() / 2);
//                }
//                invalidate();
//            }
//        });
//        a.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (mStartScale < mEndScale) {
//                    float changedScale = 1 - cScale;
//                    cScale += changedScale;
//                    mMatrix.setScale(1 , 1 , bmpSource.getWidth() / 2, bmpSource.getHeight() / 2);
//                    invalidate();
//                }
//            }
//        });
//        a.start();
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        final float moveX = e2.getX() - e1.getX();
//        final float moveY = e2.getY() - e1.getY();
//
//        if (mTouchMode == TOUCH_MODE_DOWN) {
//            mTouchMode = TOUCH_MODE_DRAG;
//        }
//
//        if (mTouchMode == TOUCH_MODE_DRAG) {
//            handleDragGesture(distanceX, distanceY);
//        } else if (mTouchMode == TOUCH_MODE_SCALE) {
//            handleScaleGesture(e2);
//        }
//
//        invalidate();
//        return false;
//    }
//
//    static final float MIN_SCALE = 0.5f;
//    static final float MAX_SCALE = 3.8f;
//
//    private void handleDragGesture(float distanceX, float distanceY) {
//        mCurrentTranslateX += -distanceX;
//        mCurrentTranslateY += -distanceY;
//        mMatrix.postTranslate(-distanceX, -distanceY);
//    }
//
//    private void handleScaleGesture(MotionEvent e2) {
//        final float deltaX = e2.getX(1) - e2.getX(0);
//        final float deltaY = e2.getY(1) - e2.getY(0);
//        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
//        if (mLastFingersDistance == 0) mLastFingersDistance = distance;
//
//        float changedScale = (distance - mLastFingersDistance) / (mWidth * 0.8f);
//        cScale += changedScale;
//        if (cScale < MIN_SCALE) cScale = MIN_SCALE;
//        else if (cScale > MAX_SCALE) cScale = MAX_SCALE;
//        else
//            mMatrix.postScale(1 + changedScale, 1 + changedScale, bmpSource.getWidth() / 2, bmpSource.getHeight() / 2);
//
//        mLastFingersDistance = distance;
//
//        float centerX = (e2.getX(1) + e2.getX(0)) / 2;
//        float centerY = (e2.getY(1) + e2.getY(0)) / 2;
//        if (mLastFingersCenterX == 0 && mLastFingersCenterY == 0) {
//            mLastFingersCenterX = centerX;
//            mLastFingersCenterY = centerY;
//        }
//
//        float changedCenterX = centerX - mLastFingersCenterX;
//        mCurrentTranslateX += changedCenterX;
//        float changedCenterY = centerY - mLastFingersCenterY;
//        mCurrentTranslateY += changedCenterY;
//
//        mMatrix.postTranslate(changedCenterX, changedCenterY);
//
//        mLastFingersCenterX = centerX;
//        mLastFingersCenterY = centerY;
//    }
//
//    @Override
//    public boolean onDown(MotionEvent e) {
//        mTouchMode = TOUCH_MODE_DOWN;
//        return true;
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//    }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//    }
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        return false;
//    }
//}
