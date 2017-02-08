package ch.ielse.demo.p01;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by LY on 2017/2/6.
 */

public class ImageCropper extends FrameLayout implements GestureDetector.OnGestureListener, View.OnClickListener {
    private static final int TOUCH_MODE_NONE = 0; // 无状态
    private static final int TOUCH_MODE_DOWN = 1; // 按下
    private static final int TOUCH_MODE_DRAG = 2; // 单点拖拽
    private static final int TOUCH_MODE_SCALE = 5; // 缩放
    private static final int TOUCH_MODE_AUTO_FLING = 7; // 动画中

    private View iBack, iSubmit;
    private ImageView iSource;
    private MaskView vMask;
    private final GestureDetector mGestureDetector;


    private float initMinScale;
    private int mTouchMode = TOUCH_MODE_NONE;
    Bitmap bmpSource;

    public ImageCropper(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mGestureDetector = new GestureDetector(context, this);
        LayoutInflater.from(context).inflate(R.layout.layout_image_cropper, this);

        iBack = findViewById(R.id.i_back);
        iBack.setOnClickListener(this);
        iSubmit = findViewById(R.id.i_submit);
        iSubmit.setOnClickListener(this);

        iSource = (ImageView) findViewById(R.id.i_source);
        vMask = (MaskView) findViewById(R.id.v_mask);


    }

    public void show() {
        int res = R.mipmap.avatar;

        final int mWidth = getWidth();
        final int mHeight = getHeight();
        if (mWidth * mHeight == 0) return;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), res, options);
        options.inSampleSize = Utils.calculateInSampleSize(options, mWidth, mHeight);
        options.inJustDecodeBounds = false;
        bmpSource = BitmapFactory.decodeResource(getResources(), res, options);
        Log.e("TTT", "AAA  bmpSource width" + bmpSource.getWidth());

        if (1f * bmpSource.getWidth() / bmpSource.getHeight() > 1f * iSource.getWidth() / iSource.getHeight()) {
            Log.e("TTT", "AAA  横图");
            int bitmapHeightAfterFitCenter = (int) (1f * bmpSource.getHeight() * iSource.getWidth() / bmpSource.getWidth());
            bmpSource = Bitmap.createScaledBitmap(bmpSource, iSource.getWidth(), bitmapHeightAfterFitCenter, true);

            float initMinScaleX = 1f * vMask.getCropWidth() / iSource.getWidth();
            float initMinScaleY = 1f * vMask.getCropHeight() / bitmapHeightAfterFitCenter;
            initMinScale = Math.max(initMinScaleX, initMinScaleY);
        } else {
            Log.e("TTT", "AAA  竖图");
            int bitmapWidthAfterFitCenter = (int) (1f * bmpSource.getWidth() * iSource.getHeight() / bmpSource.getHeight());
            bmpSource = Bitmap.createScaledBitmap(bmpSource, bitmapWidthAfterFitCenter, iSource.getHeight(), true);

            float initMinScaleX = 1f * vMask.getCropWidth() / bitmapWidthAfterFitCenter;
            float initMinScaleY = 1f * vMask.getCropHeight() / iSource.getHeight();
            initMinScale = Math.max(initMinScaleX, initMinScaleY);

            Log.e("TTT", "AAA  initMinScale " + initMinScale + "##initMinScaleX " + initMinScaleX + "##initMinScaleY " + initMinScaleY + "##vMask.getCropWidth()" + vMask.getCropWidth()
                    + "###bitmapWidthAfterFitCenter " + bitmapWidthAfterFitCenter);
            Log.e("TTT", "AAA  mWidth" + mWidth + "##iSourceWidth:" + iSource.getWidth());
        }

        if (initMinScale > 1) {
            iSource.setScaleX(initMinScale);
            iSource.setScaleY(initMinScale);
        }
        iSource.setImageBitmap(bmpSource);
    }

    @Override
    public void onClick(View v) {
        if (v == iSubmit) {
            float x = (bmpSource.getWidth() * iSource.getScaleX() - vMask.getCropWidth()) / 2f - iSource.getTranslationX();
            float width =  vMask.getCropWidth() * iSource.getScaleX();
            float y = (bmpSource.getHeight() * iSource.getScaleY() - vMask.getCropHeight()) / 2f - iSource.getTranslationY();
            float height =  vMask.getCropHeight() * iSource.getScaleY();
            Log.e("TTT" , "AAA x " + x + "##width " + width + "##y " + y + "##height " + height);
            Log.e("TTT" , "AAA bmpSource w " + bmpSource.getWidth() + " ## h " + bmpSource.getHeight());
            Toast.makeText(v.getContext().getApplicationContext() ,"doing.." , Toast.LENGTH_SHORT).show();
            Bitmap clip = Bitmap.createBitmap(bmpSource, (int)x, (int)y ,(int)width , (int)(height));
            ImageView vClipped = new ImageView(v.getContext());
            vClipped.setImageBitmap(clip);
            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setView(vClipped).create();
            dialog.show();

        } else if (v == iBack) {
            ((Activity) v.getContext()).onBackPressed();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (a != null) a.cancel();

        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_UP:
                onUp(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mTouchMode != TOUCH_MODE_SCALE) {
                    mLastFingersDistance = 0;
                    mLastFingersCenterX = 0;
                    mLastFingersCenterY = 0;
                    mDownScale = iSource.getScaleX();
                    mDownTranslateX = iSource.getTranslationX();
                    mDownTranslateY = iSource.getTranslationY();
                }
                mTouchMode = TOUCH_MODE_SCALE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() - 1 < 1 + 1) {
                    mTouchMode = TOUCH_MODE_DRAG;
                }
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    private float mDownScale;
    private float mDownTranslateX;
    private float mDownTranslateY;
    private float mLastFingersDistance;
    private float mLastFingersCenterX;
    private float mLastFingersCenterY;

    ValueAnimator a;

    public void onUp(MotionEvent e) {
        final float startScale = iSource.getScaleX();
        final float endScale;
        if (startScale < initMinScale) endScale = initMinScale;
        else endScale = startScale;

        final float edgeTranslateX = (bmpSource.getWidth() * endScale - vMask.getCropWidth()) / 2;
        final float startTranslateX = iSource.getTranslationX();
        final float endTranslateX;
        if (startTranslateX > edgeTranslateX) endTranslateX = edgeTranslateX;
        else if (startTranslateX < -edgeTranslateX) endTranslateX = -edgeTranslateX;
        else endTranslateX = startTranslateX;

        final float edgeTranslateY = (bmpSource.getHeight() * endScale - vMask.getCropHeight()) / 2;
        final float startTranslateY = iSource.getTranslationY();
        final float endTranslateY;
        if (startTranslateY > edgeTranslateY) endTranslateY = edgeTranslateY;
        else if (startTranslateY < -edgeTranslateY) endTranslateY = -edgeTranslateY;
        else endTranslateY = startTranslateY;

        if (endScale == startScale && endTranslateY == startTranslateY && endTranslateX == startTranslateX)
            return;

        if (a != null) a.cancel();
        a = ValueAnimator.ofFloat(0, 1);
        a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                iSource.setScaleX(startScale - (startScale - endScale) * value);
                iSource.setScaleY(startScale - (startScale - endScale) * value);
                iSource.setTranslationX(startTranslateX - (startTranslateX - endTranslateX) * value);
                iSource.setTranslationY(startTranslateY - (startTranslateY - endTranslateY) * value);
            }
        });
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTouchMode = TOUCH_MODE_AUTO_FLING;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mTouchMode = TOUCH_MODE_NONE;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTouchMode = TOUCH_MODE_NONE;
            }
        });
        a.start();
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mTouchMode == TOUCH_MODE_DOWN) {
            mTouchMode = TOUCH_MODE_DRAG;
        }
        if (mTouchMode == TOUCH_MODE_DRAG) {
            iSource.setTranslationX(iSource.getTranslationX() - distanceX);
            iSource.setTranslationY(iSource.getTranslationY() - distanceY);
        } else if (mTouchMode == TOUCH_MODE_SCALE) {
            final float deltaX = e2.getX(1) - e2.getX(0);
            final float deltaY = e2.getY(1) - e2.getY(0);
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (mLastFingersDistance == 0) mLastFingersDistance = distance;

            float changedScale = (distance - mLastFingersDistance) / (getWidth() * 0.8f);
            float changedScaleValue = mDownScale + changedScale;

            if (changedScaleValue < initMinScale / 2) changedScaleValue = initMinScale / 2;
            iSource.setScaleX(changedScaleValue);
            iSource.setScaleY(changedScaleValue);

            float centerX = (e2.getX(1) + e2.getX(0)) / 2;
            float centerY = (e2.getY(1) + e2.getY(0)) / 2;
            if (mLastFingersCenterX == 0 && mLastFingersCenterY == 0) {
                mLastFingersCenterX = centerX;
                mLastFingersCenterY = centerY;
            }

            float changedCenterX = centerX - mLastFingersCenterX;
            iSource.setTranslationX(mDownTranslateX + changedCenterX);
            float changedCenterY = centerY - mLastFingersCenterY;
            iSource.setTranslationY(mDownTranslateY + changedCenterY);
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mTouchMode = TOUCH_MODE_DOWN;
        mDownScale = iSource.getScaleX();
        mDownTranslateX = iSource.getTranslationX();
        mDownTranslateY = iSource.getTranslationY();
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
