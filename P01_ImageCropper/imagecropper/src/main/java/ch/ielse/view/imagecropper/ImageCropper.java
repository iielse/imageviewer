package ch.ielse.view.imagecropper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 该View用于对位图进行局部采集，之后输出新的位图
 */
public class ImageCropper extends FrameLayout implements GestureDetector.OnGestureListener, View.OnClickListener {

    private final View iBack;
    private final View iSubmit;
    private final ImageView iSource;
    private final OverlayView vOverlay;
    private final TextView tTitle;
    private float initMinScale;
    private int mOutputWidth;
    private int mOutputHeight;
    private String mTag;

    boolean isOutputFixedSize;
    private boolean isMultiTouch = false;
    private float mLastScale;
    private float mLastTranslateX;
    private float mLastTranslateY;
    private float mLastFingersDistance;
    private float mLastFingersCenterX;
    private float mLastFingersCenterY;
    private final GestureDetector mGestureDetector;

    private Callback mCallback;
    private ViewPropertyAnimator animRestore;
    private Bitmap bmpSource;

    public ImageCropper(Context context) {
        this(context, null);
    }

    public ImageCropper(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mGestureDetector = new GestureDetector(context, this);
        LayoutInflater.from(context).inflate(R.layout.layout_image_cropper, this);
        (iBack = findViewById(R.id.i_back)).setOnClickListener(this);
        (iSubmit = findViewById(R.id.i_submit)).setOnClickListener(this);
        tTitle = (TextView) findViewById(R.id.t_title);
        iSource = (ImageView) findViewById(R.id.i_source);
        vOverlay = (OverlayView) findViewById(R.id.v_overlay);
        setBackgroundColor(Color.BLACK);
        setVisibility(View.INVISIBLE);
    }

    /**
     * @param outputFixedSize 是否按照vImageCropper.crop()方法入参outputWidth和outputHeight来输出，false表示仅只按其比例输出，true为绝对尺寸输出
     */
    public void setOutputFixedSize(boolean outputFixedSize) {
        isOutputFixedSize = outputFixedSize;
    }

    public void setTranslucentStatus(int statusBarHeight) {
        findViewById(R.id.v_fit).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight));
    }


    private void cropInternal(String sourceFilePath, int outputWidth, int outputHeight, boolean isCircleOverlay, String tag) {
        final int mWidth = getWidth();
        final int mHeight = getWidth();
        if (mWidth * mHeight == 0) return;

        setVisibility(View.VISIBLE);
        mTag = tag;

        mOutputWidth = outputWidth;
        mOutputHeight = outputHeight;

        vOverlay.reset(mOutputWidth, mOutputHeight, isCircleOverlay);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourceFilePath, options);
        options.inSampleSize = Utils.calculateInSampleSize(options, mWidth, mHeight);
        options.inJustDecodeBounds = false;
        bmpSource = BitmapFactory.decodeFile(sourceFilePath, options);

        if (1f * bmpSource.getWidth() / bmpSource.getHeight() > 1f * iSource.getWidth() / iSource.getHeight()) {
            int bitmapHeightAfterFitCenter = (int) (1f * bmpSource.getHeight() * iSource.getWidth() / bmpSource.getWidth());
            bmpSource = Bitmap.createScaledBitmap(bmpSource, iSource.getWidth(), bitmapHeightAfterFitCenter, true);
            float initMinScaleX = 1f * vOverlay.getOverlayWidth() / iSource.getWidth();
            float initMinScaleY = 1f * vOverlay.getOverlayHeight() / bitmapHeightAfterFitCenter;
            initMinScale = Math.max(initMinScaleX, initMinScaleY);
        } else {
            int bitmapWidthAfterFitCenter = (int) (1f * bmpSource.getWidth() * iSource.getHeight() / bmpSource.getHeight());
            bmpSource = Bitmap.createScaledBitmap(bmpSource, bitmapWidthAfterFitCenter, iSource.getHeight(), true);
            float initMinScaleX = 1f * vOverlay.getOverlayWidth() / bitmapWidthAfterFitCenter;
            float initMinScaleY = 1f * vOverlay.getOverlayHeight() / iSource.getHeight();
            initMinScale = Math.max(initMinScaleX, initMinScaleY);
        }

        final float defaultScale = initMinScale > 1 ? initMinScale : 1;
        iSource.setTranslationX(0);
        iSource.setTranslationY(0);
        iSource.setScaleX(defaultScale);
        iSource.setScaleY(defaultScale);
        iSource.setImageBitmap(bmpSource);
    }

    /**
     * 裁剪图片(输出的图片默认只有比例是按照入参outputWidth和outputHeight获得的，尺寸大小并没有按照指定的尺寸大小。<br>
     * 如果要求大小也为入参请调用{@link ImageCropper#setOutputFixedSize(boolean)} 设置值为true) <br>
     * 逻辑见{@link ImageCropper#onClick(View)} output = mOutputWidth * mOutputHeight != 0 ? Bitmap.createScaledBitmap(clip, mOutputWidth, mOutputHeight, true) : clip <br>
     * ps: createScaledBitmap (小图变大图会造成内容的失真) 55555555<br>
     *
     * @param sourceFilePath  原图片路径
     * @param outputWidth     输出宽度
     * @param outputHeight    输出高度
     * @param isCircleOverlay 遮罩蒙板是否为圆形，为圆形的条件时在isCircleOverlay为true的同时，outputWidth等于outputHeight才行
     * @param tag             若同一界面有多处裁剪功能，对此传递一个tag标志避免混淆
     */
    public void crop(final String sourceFilePath, final int outputWidth, final int outputHeight, final boolean isCircleOverlay, final String tag) {
        post(new Runnable() {
            @Override
            public void run() {
                cropInternal(sourceFilePath, outputWidth, outputHeight, isCircleOverlay, tag);
            }
        });
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
            if (mCallback != null) {
                final float x = ((bmpSource.getWidth() * iSource.getScaleX() - vOverlay.getOverlayWidth()) / 2 - iSource.getTranslationX()) / iSource.getScaleX();
                final float width = vOverlay.getOverlayWidth() / iSource.getScaleX();
                final float y = ((bmpSource.getHeight() * iSource.getScaleY() - vOverlay.getOverlayHeight()) / 2 - iSource.getTranslationY()) / iSource.getScaleY();
                final float height = vOverlay.getOverlayHeight() / iSource.getScaleY();
                Bitmap clip = Bitmap.createBitmap(bmpSource, (int) x, (int) y, (int) width, (int) (height));
                Bitmap output = (isOutputFixedSize && mOutputWidth * mOutputHeight != 0) ? Bitmap.createScaledBitmap(clip, mOutputWidth, mOutputHeight, true) : clip;

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


    public static class Helper {
        private static final int VIEW_IMAGE_CROPPER_ID = R.id.view_image_cropper;
        private final ViewGroup activityDecorView;
        private final ImageCropper mImageCropper;

        private Helper(Activity activity) {
            mImageCropper = new ImageCropper(activity);
            mImageCropper.setId(VIEW_IMAGE_CROPPER_ID);
            activityDecorView = (ViewGroup) activity.getWindow().getDecorView();
        }

        public static Helper with(Activity activity) {
            return new Helper(activity);
        }


        public Helper setCallback(Callback cb) {
            mImageCropper.mCallback = cb;
            return this;
        }

        public Helper setOutputFixedSize(boolean isOutputFixedSize) {
            mImageCropper.isOutputFixedSize = isOutputFixedSize;
            return this;
        }

        public Helper setTitle(String title) {
            mImageCropper.tTitle.setText(title);
            return this;
        }

        public Helper setTranslucentStatusHeight(int translucentStatusHeight) {
            mImageCropper.setTranslucentStatus(translucentStatusHeight);
            return this;
        }

        public ImageCropper create() {
            removeExistingOverlayInView(activityDecorView);
            activityDecorView.addView(mImageCropper);
            return mImageCropper;
        }

        void removeExistingOverlayInView(ViewGroup parent) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child.getId() == VIEW_IMAGE_CROPPER_ID) {
                    parent.removeView(child);
                }
                if (child instanceof ViewGroup) {
                    removeExistingOverlayInView((ViewGroup) child);
                }
            }
        }
    }
}
