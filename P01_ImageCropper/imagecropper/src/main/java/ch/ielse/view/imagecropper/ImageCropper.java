package ch.ielse.view.imagecropper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ImageCropper extends FrameLayout implements GestureDetector.OnGestureListener, View.OnClickListener {

    private boolean isMultiTouch = false;
    private View iBack;
    private View iSubmit;
    private ImageView iSource;
    private OverlayView vOverlay;
    private int mOutputWidth;
    private int mOutputHeight;
    private String mTag;
    private float mLastScale;
    private float mLastTranslateX;
    private float mLastTranslateY;
    private float mLastFingersDistance;
    private float mLastFingersCenterX;
    private float mLastFingersCenterY;
    private final GestureDetector mGestureDetector;
    private Callback mCallback;
    private ViewPropertyAnimator animRestore;
    private float initMinScale;
    private Bitmap bmpSource;

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
        vOverlay = (OverlayView) findViewById(R.id.v_overlay);
        setBackgroundColor(Color.BLACK);
        setVisibility(View.INVISIBLE);
    }

    /**
     * 裁剪图片 (输出图片目前只有输出比例是按照入参outputWidth和outputHeight宽高比例获得的，尺寸大小并没有，按照指定的尺寸大小图片可能极度失真) <br>
     * 见{@link ImageCropper#onClick(View)} output = mOutputWidth * mOutputHeight != 0 ? clip.createScaledBitmap(clip, mOutputWidth, mOutputHeight, true) <br>
     * createScaledBitmap 可能失真 55555555<br>
     *
     * @param sourceFilePath  原图片路径
     * @param outputWidth     输出宽度
     * @param outputHeight    输出高度
     * @param isCircleOverlay 遮罩蒙板是否为圆形，为圆形的条件时在isCircleOverlay为true的同时，outputWidth等于outputHeight才行
     * @param tag             若同一界面有多处裁剪功能，对此传递一个tag标志避免混淆
     */
    public void crop(String sourceFilePath, int outputWidth, int outputHeight, boolean isCircleOverlay, String tag) {
        final int mWidth = getWidth();
        final int mHeight = getHeight();
        if (mWidth * mHeight == 0) return;
        setVisibility(View.VISIBLE);
        mTag = tag;
        mOutputWidth = outputWidth;
        mOutputHeight = outputHeight;

        vOverlay.reset(mOutputWidth, mOutputHeight, isCircleOverlay);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourceFilePath, options);
        Log.e("TTT", "AAA  BitmapFactory.decodeFile w " + options.outWidth + "#h " + options.outHeight);
        options.inSampleSize = Utils.calculateInSampleSize(options, mWidth, mHeight);
        options.inJustDecodeBounds = false;
        bmpSource = BitmapFactory.decodeFile(sourceFilePath, options);
        Log.e("TTT", "AAA  bmpSource width" + bmpSource.getWidth());

        if (1f * bmpSource.getWidth() / bmpSource.getHeight() > 1f * iSource.getWidth() / iSource.getHeight()) {
            Log.e("TTT", "AAA  横图");
            int bitmapHeightAfterFitCenter = (int) (1f * bmpSource.getHeight() * iSource.getWidth() / bmpSource.getWidth());
            bmpSource = Bitmap.createScaledBitmap(bmpSource, iSource.getWidth(), bitmapHeightAfterFitCenter, true);
            float initMinScaleX = 1f * vOverlay.getOverlayWidth() / iSource.getWidth();
            float initMinScaleY = 1f * vOverlay.getOverlayHeight() / bitmapHeightAfterFitCenter;
            initMinScale = Math.max(initMinScaleX, initMinScaleY);
        } else {
            Log.e("TTT", "AAA  竖图");
            int bitmapWidthAfterFitCenter = (int) (1f * bmpSource.getWidth() * iSource.getHeight() / bmpSource.getHeight());
            bmpSource = Bitmap.createScaledBitmap(bmpSource, bitmapWidthAfterFitCenter, iSource.getHeight(), true);

            float initMinScaleX = 1f * vOverlay.getOverlayWidth() / bitmapWidthAfterFitCenter;
            float initMinScaleY = 1f * vOverlay.getOverlayHeight() / iSource.getHeight();
            initMinScale = Math.max(initMinScaleX, initMinScaleY);
            Log.e("TTT", "AAA  initMinScale " + initMinScale + "##initMinScaleX " + initMinScaleX + "##initMinScaleY " + initMinScaleY + "##vOverlay.getOverlayWidth()" + vOverlay.getOverlayWidth()
                    + "###bitmapWidthAfterFitCenter " + bitmapWidthAfterFitCenter);
            Log.e("TTT", "AAA  mWidth" + mWidth + "##iSourceWidth:" + iSource.getWidth());
        }

        final float defaultScale = initMinScale > 1 ? initMinScale : 1;
        iSource.setTranslationX(0);
        iSource.setTranslationY(0);
        iSource.setScaleX(defaultScale);
        iSource.setScaleY(defaultScale);
        iSource.setImageBitmap(bmpSource);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_UP:
                final float startScale = iSource.getScaleX();
                final float endScale;
                if (startScale < initMinScale) endScale = initMinScale;
                else endScale = startScale;

                final float edgeTranslateX = (bmpSource.getWidth() * endScale - vOverlay.getOverlayWidth()) / 2;
                final float startTranslateX = iSource.getTranslationX();
                final float endTranslateX;
                if (startTranslateX > edgeTranslateX) endTranslateX = edgeTranslateX;
                else if (startTranslateX < -edgeTranslateX) endTranslateX = -edgeTranslateX;
                else endTranslateX = startTranslateX;

                final float edgeTranslateY = (bmpSource.getHeight() * endScale - vOverlay.getOverlayHeight()) / 2;
                final float startTranslateY = iSource.getTranslationY();
                final float endTranslateY;
                if (startTranslateY > edgeTranslateY) endTranslateY = edgeTranslateY;
                else if (startTranslateY < -edgeTranslateY) endTranslateY = -edgeTranslateY;
                else endTranslateY = startTranslateY;

                if (endScale == startScale && endTranslateY == startTranslateY && endTranslateX == startTranslateX)
                    break;

                if (animRestore != null) animRestore.cancel();
                animRestore = iSource.animate().scaleX(endScale).scaleY(endScale).translationX(endTranslateX)
                        .translationY(endTranslateY);
                animRestore.start();

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!isMultiTouch) {
                    isMultiTouch = true;
                    mLastFingersDistance = 0;
                    mLastFingersCenterX = 0;
                    mLastFingersCenterY = 0;
                    mLastScale = iSource.getScaleX();
                    mLastTranslateX = iSource.getTranslationX();
                    mLastTranslateY = iSource.getTranslationY();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() - 1 < 1 + 1) {
                    isMultiTouch = false;
                }
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        mLastScale = iSource.getScaleX();
        mLastTranslateX = iSource.getTranslationX();
        mLastTranslateY = iSource.getTranslationY();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isMultiTouch) {
            iSource.setTranslationX(iSource.getTranslationX() - distanceX);
            iSource.setTranslationY(iSource.getTranslationY() - distanceY);
        } else {
            final float deltaX = e2.getX(1) - e2.getX(0);
            final float deltaY = e2.getY(1) - e2.getY(0);
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (mLastFingersDistance == 0) mLastFingersDistance = distance;

            float changedScale = (distance - mLastFingersDistance) / (getWidth() * 0.8f);
            float changedScaleValue = mLastScale + changedScale;

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
            iSource.setTranslationX(mLastTranslateX + changedCenterX);
            float changedCenterY = centerY - mLastFingersCenterY;
            iSource.setTranslationY(mLastTranslateY + changedCenterY);
        }
        return false;
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

    @Override
    public void onClick(View v) {
        if (animRestore != null) animRestore.cancel();

        if (v == iSubmit) {
            final float x = ((bmpSource.getWidth() * iSource.getScaleX() - vOverlay.getOverlayWidth()) / 2 - iSource.getTranslationX()) / iSource.getScaleX();
            final float width = vOverlay.getOverlayWidth() / iSource.getScaleX();
            final float y = ((bmpSource.getHeight() * iSource.getScaleY() - vOverlay.getOverlayHeight()) / 2 - iSource.getTranslationY()) / iSource.getScaleY();
            final float height = vOverlay.getOverlayHeight() / iSource.getScaleY();
            Log.e("TTT", "AAA x " + x + "##width " + width + "##y " + y + "##height " + height);
            Log.e("TTT", "AAA bmpSource w " + bmpSource.getWidth() + " ## h " + bmpSource.getHeight());
            Bitmap clip = Bitmap.createBitmap(bmpSource, (int) x, (int) y, (int) width, (int) (height));
            Bitmap output = clip;
            // output = mOutputWidth * mOutputHeight != 0 ? clip.createScaledBitmap(clip, mOutputWidth, mOutputHeight, true) :
            if (mCallback != null) {
                mCallback.onPictureCropOut(output, mTag);
                animate().alpha(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setVisibility(View.GONE);
                        setAlpha(1f);
                    }
                }).start();
            }
        } else if (v == iBack) {
            ((Activity) v.getContext()).onBackPressed();
        }
    }

    public interface Callback {
        void onPictureCropOut(Bitmap bitmap, String tag);
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }
}
