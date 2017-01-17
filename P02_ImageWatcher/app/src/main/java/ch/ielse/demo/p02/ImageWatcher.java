package ch.ielse.demo.p02;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * QQ 517309507
 */
public class ImageWatcher extends FrameLayout implements GestureDetector.OnGestureListener, ViewPager.OnPageChangeListener {
    private static final int SINGLE_TAP_UP_CONFIRMED = 1;
    private final Handler mHandler;

    static final float MIN_SCALE = 0.5f;
    static final float MAX_SCALE = 3.8f;
    static int MAX_TRANSLATE_X;
    static int MAX_TRANSLATE_Y;

    private static final int TOUCH_MODE_NONE = 0; // 无状态
    private static final int TOUCH_MODE_DOWN = 1; // 按下
    private static final int TOUCH_MODE_DRAG = 2; // 单点拖拽
    private static final int TOUCH_MODE_EXIT = 3; // 退出动作
    private static final int TOUCH_MODE_SLIDE = 4; // 页面滑动
    private static final int TOUCH_MODE_SCALE_ROTATE = 5; // 缩放旋转
    private static final int TOUCH_MODE_LOCK = 6; // 锁定
    private static final int TOUCH_MODE_AUTO_FLING = 7; // 动画中

    private TextView tCurrentIdx;
    private int mStatusBarHeight;
    private ImageView iSource;
    private ImageView iOrigin;

    private int mWidth, mHeight;
    private int mBackgroundColor;

    private int mTouchMode;
    private float mTouchSlop;

    private float mFingersDistance;
    private double mFingersAngle; // 相对于[ 东] point0作为起点;point1作为终点
    private float mFingersCenterX;
    private float mFingersCenterY;

    private ValueAnimator animTransitions;
    private boolean isInTransitionsAnimation;
    private final GestureDetector mGestureDetector;

    float mExitScalingRef; // 触摸退出进度

    private ImagePagerAdapter adapter;
    private ViewPager vPager;
    // private Bitmap mInitBitmap;
    private List<ImageView> mImageGroupList;
    private List<String> mUrlList;
    private int initPosition;

    public ImageWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new GestureHandler(this);
        mGestureDetector = new GestureDetector(context, this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        addView(vPager = new ViewPager(getContext()));
        vPager.addOnPageChangeListener(this);
        setVisibility(View.INVISIBLE);

        addView(tCurrentIdx = new TextView(context));
        tCurrentIdx.setTextColor(0xFFFFFFFF);
        tCurrentIdx.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        tCurrentIdx.setTranslationY(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, displayMetrics) + 0.5f);
    }

    public void show(ImageView i, List<ImageView> imageGroupList, final List<String> urlList) {
        if (i == null || imageGroupList == null || urlList == null || imageGroupList.size() < 1 ||
                urlList.size() < imageGroupList.size()) {
            String info = "i[" + i + "]";
            info += "#imageGroupList " + (imageGroupList == null ? "null" : "size : " + imageGroupList.size());
            info += "#urlList " + (urlList == null ? "null" : "size :" + urlList.size());
            throw new IllegalArgumentException("error params \n" + info);
        }
        initPosition = imageGroupList.indexOf(i);
        if (initPosition < 0) {
            throw new IllegalArgumentException("error params initPosition " + initPosition);
        }
        if (i.getDrawable() == null) return;

        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;

        mImageGroupList = imageGroupList;
        mUrlList = urlList;

        iOrigin = null;
        iSource = null;

        ImageWatcher.this.setVisibility(View.VISIBLE);
        vPager.setAdapter(adapter = new ImagePagerAdapter());
        vPager.setCurrentItem(initPosition);
        refreshCurrentIdx(initPosition);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (iSource == null) return true;
        if (isInTransitionsAnimation) return true;

        ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);

        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_UP:
                onUp(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (viewStateDefault != null && mTouchMode != TOUCH_MODE_SLIDE) {
                    if (mTouchMode != TOUCH_MODE_SCALE_ROTATE) {
                        mFingersDistance = 0;
                        mFingersAngle = 0;
                        mFingersCenterX = 0;
                        mFingersCenterY = 0;
                        ViewState.write(iSource, ViewState.STATE_TOUCH_SCALE_ROTATE);
                    }
                    mTouchMode = TOUCH_MODE_SCALE_ROTATE;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (viewStateDefault != null && mTouchMode != TOUCH_MODE_SLIDE) {
                    if (event.getPointerCount() - 1 < 1 + 1) {
                        mTouchMode = TOUCH_MODE_LOCK;
                    }
                }
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mTouchMode = TOUCH_MODE_DOWN;
        ViewState.write(iSource, ViewState.STATE_TOUCH_DOWN);
        vPager.onTouchEvent(e);
        return true;
    }

    public void onUp(MotionEvent e) {
        if (mTouchMode == TOUCH_MODE_EXIT) {
            handleExitTouchResult();
        } else if (mTouchMode == TOUCH_MODE_SCALE_ROTATE
                || mTouchMode == TOUCH_MODE_LOCK) {
            handleScaleRotateTouchResult();
        } else if (mTouchMode == TOUCH_MODE_DRAG) {
            handleDragTouchResult();
        }
        vPager.onTouchEvent(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        final float moveX = e2.getX() - e1.getX();
        final float moveY = e2.getY() - e1.getY();
        if (mTouchMode == TOUCH_MODE_DOWN) {
            Log.e("TTT", "AAA TOUCH_MODE_DOWN 开始鉴别手势");
            if (Math.abs(moveX) > mTouchSlop || Math.abs(moveY) > mTouchSlop) {
                ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
                ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);

                if (viewStateDefault == null) {
                    Log.e("TTT", "AAA 没有Default标志，图片正在下载");
                    mTouchMode = TOUCH_MODE_SLIDE;
                } else if (viewStateCurrent.scaleY > viewStateDefault.scaleY || viewStateCurrent.scaleX > viewStateDefault.scaleX) {
                    // 图片为放大状态，宽或高超出了屏幕尺寸
                    if (mTouchMode != TOUCH_MODE_DRAG) {
                        ViewState.write(iSource, ViewState.STATE_DRAG);
                    }
                    Log.e("TTT", "AAA 图片为放大状态，宽或高超出了屏幕尺寸 记录View状态 至 ->TOUCH_MODE_DRAG");
                    mTouchMode = TOUCH_MODE_DRAG;

                    String imageOrientation = (String) iSource.getTag(R.id.image_orientation);
                    if ("horizontal".equals(imageOrientation)) {
                        float translateXEdge = viewStateDefault.width * (viewStateCurrent.scaleX - 1) / 2;
                        if (viewStateCurrent.translationX >= translateXEdge && moveX > 0) {
                            mTouchMode = TOUCH_MODE_SLIDE;
                        } else if (viewStateCurrent.translationX <= -translateXEdge && moveX < 0) {
                            mTouchMode = TOUCH_MODE_SLIDE;
                        }
                    } else if ("vertical".equals(imageOrientation)) {
                        if (viewStateDefault.width * viewStateCurrent.scaleX <= mWidth) {
                            mTouchMode = TOUCH_MODE_SLIDE;
                        } else {
                            float translateXRightEdge = viewStateDefault.width * viewStateCurrent.scaleX / 2 - viewStateDefault.width / 2;
                            float translateXLeftEdge = mWidth - viewStateDefault.width * viewStateCurrent.scaleX / 2 - viewStateDefault.width / 2;
                            if (viewStateCurrent.translationX >= translateXRightEdge && moveX > 0) {
                                mTouchMode = TOUCH_MODE_SLIDE;
                            } else if (viewStateCurrent.translationX <= translateXLeftEdge && moveX < 0) {
                                mTouchMode = TOUCH_MODE_SLIDE;
                            }
                        }
                    }
                } else if (Math.abs(moveX) < mTouchSlop && moveY > mTouchSlop * 3) {
                    // 图片为原始状态，并且尝试单手垂直下拉
                    Log.e("TTT", "AAA 记录View状态 至 ->  TOUCH_MODE_EXIT");
                    mTouchMode = TOUCH_MODE_EXIT;
                } else if (Math.abs(moveX) > mTouchSlop) {
                    // 左右滑动
                    Log.e("TTT", "AAA 记录View状态 至 ->  TOUCH_MODE_SLIDE");
                    mTouchMode = TOUCH_MODE_SLIDE;
                }
            }
        }

        if (mTouchMode == TOUCH_MODE_SLIDE) {
            Log.e("TTT", "AAA onScroll 处理 TOUCH_MODE_SLIDE distanceX:" + distanceX);
            vPager.onTouchEvent(e2);
        } else if (mTouchMode == TOUCH_MODE_SCALE_ROTATE) {
            handleScaleRotateGesture(e2);
        } else if (mTouchMode == TOUCH_MODE_EXIT) {
            handleExitGesture(e2, e1);
        } else if (mTouchMode == TOUCH_MODE_DRAG) {
            handleDragGesture(e2, e1);
        }
        return false;
    }

    public boolean onSingleTapConfirmed() {
        mHandler.removeMessages(SINGLE_TAP_UP_CONFIRMED);
        Log.e("TTT", "AAA onSingleTapConfirmed");
        ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
        ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (viewStateDefault == null || (viewStateCurrent.scaleY <= viewStateDefault.scaleY && viewStateCurrent.scaleX <= viewStateDefault.scaleX)) {
            mExitScalingRef = 0;
            handleExitTouchResult();
        } else {
            handleDoubleTapTouchResult();
        }
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        boolean hadTapMessage = mHandler.hasMessages(SINGLE_TAP_UP_CONFIRMED);
        Log.e("TTT", "AAA onSingleTapUp hasTapMessage " + hadTapMessage);
        if (hadTapMessage) {
            mHandler.removeMessages(SINGLE_TAP_UP_CONFIRMED);
            handleDoubleTapTouchResult();
            return true;
        } else {
            mHandler.sendEmptyMessageDelayed(SINGLE_TAP_UP_CONFIRMED, 350);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
//        Log.e("TTT", "AAA onShowPress");
    }

    @Override
    public void onLongPress(MotionEvent e) {
//        Log.e("TTT", "AAA onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        Log.e("TTT", "AAA onFling : velocityX" + velocityX);
        return false;
    }


    /**
     * 单手退出图片查看模式[移动中触摸事件处理]
     */
    private void handleExitGesture(MotionEvent e2, MotionEvent e1) {
        mExitScalingRef = 1;
        final float moveY = e2.getY() - e1.getY();
        final float moveX = e2.getX() - e1.getX();
        if (moveY > 0) {
            mExitScalingRef -= moveY / getHeight();
        }
        if (mExitScalingRef < MIN_SCALE) mExitScalingRef = MIN_SCALE;

        ViewState viewStateTouchDown = ViewState.read(iSource, ViewState.STATE_TOUCH_DOWN);
        if (viewStateTouchDown == null) return;

        iSource.setTranslationX(viewStateTouchDown.translationX + moveX);
        iSource.setTranslationY(viewStateTouchDown.translationY + moveY);
        iSource.setScaleX(viewStateTouchDown.scaleX * mExitScalingRef);
        iSource.setScaleY(viewStateTouchDown.scaleY * mExitScalingRef);
        setBackgroundColor(mColorEvaluator.evaluate(mExitScalingRef, 0x00000000, 0xFF000000));
    }

    /**
     * 双手拖拽缩放旋转[移动中触摸事件处理]
     */
    private void handleScaleRotateGesture(MotionEvent e2) {
        final ViewState viewStateTouchScaleRotate = ViewState.read(iSource, ViewState.STATE_TOUCH_SCALE_ROTATE);
        if (viewStateTouchScaleRotate == null) return;

        final float deltaX = e2.getX(1) - e2.getX(0);
        final float deltaY = e2.getY(1) - e2.getY(0);
        double angle = Math.toDegrees(Math.atan(deltaX / deltaY));
        if (deltaY < 0) angle = angle + 180;
        if (mFingersAngle == 0) mFingersAngle = angle;

        float changedAngle = (float) (mFingersAngle - angle);
        float changedAngleValue = (viewStateTouchScaleRotate.rotation + changedAngle) % 360;
        if (changedAngleValue > 180) {
            changedAngleValue = changedAngleValue - 360;
        } else if (changedAngleValue < -180) {
            changedAngleValue = changedAngleValue + 360;
        }
        iSource.setRotation(changedAngleValue);

        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (mFingersDistance == 0) mFingersDistance = distance;

        float changedScale = (mFingersDistance - distance) / (mWidth * 0.8f);
        float scaleResultX = viewStateTouchScaleRotate.scaleX - changedScale;
        if (scaleResultX < MIN_SCALE) scaleResultX = MIN_SCALE;
        else if (scaleResultX > MAX_SCALE) scaleResultX = MAX_SCALE;
        iSource.setScaleX(scaleResultX);
        float scaleResultY = viewStateTouchScaleRotate.scaleY - changedScale;
        if (scaleResultY < MIN_SCALE) scaleResultY = MIN_SCALE;
        else if (scaleResultY > MAX_SCALE) scaleResultY = MAX_SCALE;
        iSource.setScaleY(scaleResultY);

        float centerX = (e2.getX(1) + e2.getX(0)) / 2;
        float centerY = (e2.getY(1) + e2.getY(0)) / 2;
        if (mFingersCenterX == 0 && mFingersCenterY == 0) {
            mFingersCenterX = centerX;
            mFingersCenterY = centerY;
        }
        float changedCenterX = mFingersCenterX - centerX;
        float changedCenterXValue = viewStateTouchScaleRotate.translationX - changedCenterX;
        if (changedCenterXValue > MAX_TRANSLATE_X) changedCenterXValue = MAX_TRANSLATE_X;
        else if (changedCenterXValue < -MAX_TRANSLATE_X) changedCenterXValue = -MAX_TRANSLATE_X;
        iSource.setTranslationX(changedCenterXValue);

        float changedCenterY = mFingersCenterY - centerY;
        float changedCenterYValue = viewStateTouchScaleRotate.translationY - changedCenterY;
        if (changedCenterYValue > MAX_TRANSLATE_Y) changedCenterYValue = MAX_TRANSLATE_Y;
        else if (changedCenterYValue < -MAX_TRANSLATE_Y) changedCenterYValue = -MAX_TRANSLATE_Y;
        iSource.setTranslationY(changedCenterYValue);
    }

    /**
     * 单手拖拽平移[移动中触摸事件处理]
     */
    private void handleDragGesture(MotionEvent e2, MotionEvent e1) {
        final float moveY = e2.getY() - e1.getY();
        final float moveX = e2.getX() - e1.getX();
        final ViewState viewStateTouchDrag = ViewState.read(iSource, ViewState.STATE_DRAG);
        if (viewStateTouchDrag == null) return;
        final ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (viewStateDefault == null) return;

        float translateXValue = viewStateTouchDrag.translationX + moveX * 1.6f;

        String imageOrientation = (String) iSource.getTag(R.id.image_orientation);
        if ("horizontal".equals(imageOrientation)) {
            float translateXEdge = viewStateDefault.width * (viewStateTouchDrag.scaleX - 1) / 2;
            if (translateXValue > translateXEdge) {
                translateXValue = translateXEdge + (translateXValue - translateXEdge) * 0.12f;
            } else if (translateXValue < -translateXEdge) {
                translateXValue = -translateXEdge + (translateXValue - (-translateXEdge)) * 0.12f;
            }
        } else if ("vertical".equals(imageOrientation)) {
            if (viewStateDefault.width * viewStateTouchDrag.scaleX <= mWidth) {
                mTouchMode = TOUCH_MODE_SLIDE;
            } else {
                float translateXRightEdge = viewStateDefault.width * viewStateTouchDrag.scaleX / 2 - viewStateDefault.width / 2;
                float translateXLeftEdge = mWidth - viewStateDefault.width * viewStateTouchDrag.scaleX / 2 - viewStateDefault.width / 2;

                if (translateXValue > translateXRightEdge) {
                    translateXValue = translateXRightEdge + (translateXValue - translateXRightEdge) * 0.12f;
                } else if (translateXValue < translateXLeftEdge) {
                    translateXValue = translateXLeftEdge + (translateXValue - translateXLeftEdge) * 0.12f;
                }
            }
        }
        iSource.setTranslationX(translateXValue);
        iSource.setTranslationY(viewStateTouchDrag.translationY + moveY * 1.6f);
    }

    /**
     * 退出模式的手指事件收尾动画
     */
    private void handleExitTouchResult() {
        if (mExitScalingRef > 0.9f) {
            // 还原
            final int startBackgroundColor = mBackgroundColor;
            final int endBackgroundColor = 0xFF000000;
            final ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
            final ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
            if (viewStateDefault == null) return;

            if (animTransitions != null) animTransitions.cancel();
            animTransitions = null;
            animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
            animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (float) animation.getAnimatedValue();
                    iSource.setTranslationX(viewStateCurrent.translationX + (viewStateDefault.translationX - viewStateCurrent.translationX) * p);
                    iSource.setTranslationY(viewStateCurrent.translationY + (viewStateDefault.translationY - viewStateCurrent.translationY) * p);
                    iSource.setScaleX(viewStateCurrent.scaleX + (viewStateDefault.scaleX - viewStateCurrent.scaleX) * p);
                    iSource.setScaleY(viewStateCurrent.scaleY + (viewStateDefault.scaleY - viewStateCurrent.scaleY) * p);
                    iSource.setRotation((viewStateCurrent.rotation + (viewStateDefault.rotation - viewStateCurrent.rotation) * p) % 360);
                    setBackgroundColor(mColorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
                }
            });
            animTransitions.addListener(mAnimTransitionStateListener);
            animTransitions.start();
        } else {
            // 退出
            final ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
            final ViewState viewStateOrigin;
            ViewState sourceOriginState = ViewState.read(iSource, ViewState.STATE_ORIGIN);
            if (sourceOriginState != null) {
                viewStateOrigin = sourceOriginState;
            } else {
                // fade out
                viewStateOrigin = ViewState.write(iSource, ViewState.STATE_ORIGIN);
                viewStateOrigin.alpha = 0;
                viewStateOrigin.scaleX *= 1.5f;
                viewStateOrigin.scaleY *= 1.5f;
            }
            final int startBackgroundColor = mBackgroundColor;
            final int endBackgroundColor = 0x00000000;

            if (animTransitions != null) animTransitions.cancel();
            animTransitions = null;
            animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
            animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (float) animation.getAnimatedValue();

                    iSource.getLayoutParams().width = (int) (viewStateCurrent.width + (viewStateOrigin.width - viewStateCurrent.width) * p);
                    iSource.getLayoutParams().height = (int) (viewStateCurrent.height + (viewStateOrigin.height - viewStateCurrent.height) * p);
                    iSource.requestLayout();

                    iSource.setTranslationX(viewStateCurrent.translationX + (viewStateOrigin.translationX - viewStateCurrent.translationX) * p);
                    iSource.setTranslationY(viewStateCurrent.translationY + (viewStateOrigin.translationY - viewStateCurrent.translationY) * p);
                    iSource.setScaleX(viewStateCurrent.scaleX + (viewStateOrigin.scaleX - viewStateCurrent.scaleX) * p);
                    iSource.setScaleY(viewStateCurrent.scaleY + (viewStateOrigin.scaleY - viewStateCurrent.scaleY) * p);
                    iSource.setRotation((viewStateCurrent.rotation + (viewStateOrigin.rotation - viewStateCurrent.rotation) * p) % 360);
                    iSource.setAlpha(viewStateCurrent.alpha + (viewStateOrigin.alpha - viewStateCurrent.alpha) * p);

                    setBackgroundColor(mColorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
                }
            });
            animTransitions.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    iOrigin.setVisibility(View.VISIBLE);
                    setVisibility(View.GONE);
                }
            });
            animTransitions.addListener(mAnimTransitionStateListener);
            animTransitions.start();
        }
    }

    /**
     * 退出模式的手指事件收尾动画
     */
    private void handleDoubleTapTouchResult() {
        final ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
        final ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (viewStateDefault == null) return;

        if (viewStateCurrent.scaleY <= viewStateDefault.scaleY && viewStateCurrent.scaleX <= viewStateDefault.scaleX) {
            // 放大
            final int startBackgroundColor = mBackgroundColor;
            final int endBackgroundColor = 0xFF000000;
            final float expectedScale = (MAX_SCALE - viewStateDefault.scaleX) * 0.4f + viewStateDefault.scaleX;

            if (animTransitions != null) animTransitions.cancel();
            animTransitions = null;
            animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
            animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (float) animation.getAnimatedValue();
                    iSource.setScaleX(viewStateCurrent.scaleX + (expectedScale - viewStateCurrent.scaleX) * p);
                    iSource.setScaleY(viewStateCurrent.scaleY + (expectedScale - viewStateCurrent.scaleY) * p);
                    setBackgroundColor(mColorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
                }
            });
            animTransitions.addListener(mAnimTransitionStateListener);
            animTransitions.start();
        } else {
            // 还原
            final int startBackgroundColor = mBackgroundColor;
            final int endBackgroundColor = 0xFF000000;

            if (animTransitions != null) animTransitions.cancel();
            animTransitions = null;
            animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
            animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (float) animation.getAnimatedValue();

                    iSource.setTranslationX(viewStateCurrent.translationX + (viewStateDefault.translationX - viewStateCurrent.translationX) * p);
                    iSource.setTranslationY(viewStateCurrent.translationY + (viewStateDefault.translationY - viewStateCurrent.translationY) * p);
                    iSource.setScaleX(viewStateCurrent.scaleX + (viewStateDefault.scaleX - viewStateCurrent.scaleX) * p);
                    iSource.setScaleY(viewStateCurrent.scaleY + (viewStateDefault.scaleY - viewStateCurrent.scaleY) * p);
                    iSource.setRotation((viewStateCurrent.rotation + (viewStateDefault.rotation - viewStateCurrent.rotation) * p) % 360);
                    setBackgroundColor(mColorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
                }
            });
            animTransitions.addListener(mAnimTransitionStateListener);
            animTransitions.start();
        }
    }

    /**
     * 缩放旋转模式的手指事件收尾动画
     */
    private void handleScaleRotateTouchResult() {
        // 缩放旋转恢复
        final int startBackgroundColor = mBackgroundColor;
        final int endBackgroundColor = 0xFF000000;
        final ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
        final ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (viewStateDefault == null) return;

        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;
        animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
        animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float) animation.getAnimatedValue();
                iSource.setTranslationX(viewStateCurrent.translationX + (viewStateDefault.translationX - viewStateCurrent.translationX) * p);
                iSource.setTranslationY(viewStateCurrent.translationY + (viewStateDefault.translationY - viewStateCurrent.translationY) * p);
                if (viewStateCurrent.scaleX < viewStateDefault.scaleX) {
                    iSource.setScaleX(viewStateCurrent.scaleX + (viewStateDefault.scaleX - viewStateCurrent.scaleX) * p);
                }
                if (viewStateCurrent.scaleY < viewStateDefault.scaleY) {
                    iSource.setScaleY(viewStateCurrent.scaleY + (viewStateDefault.scaleY - viewStateCurrent.scaleY) * p);
                }

                iSource.setRotation((viewStateCurrent.rotation + (viewStateDefault.rotation - viewStateCurrent.rotation) * p) % 360);
                setBackgroundColor(mColorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
            }
        });
        animTransitions.addListener(mAnimTransitionStateListener);
        animTransitions.start();
    }

    /**
     * 拖拽模式的手指事件收尾动画
     */
    private void handleDragTouchResult() {
        // 拖拽恢复
        final int startBackgroundColor = mBackgroundColor;
        final int endBackgroundColor = 0xFF000000;

        ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (viewStateDefault == null) return;
        final ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);

        final float endTranslateX, endTranslateY;
        String imageOrientation = (String) iSource.getTag(R.id.image_orientation);
        if ("horizontal".equals(imageOrientation)) {
            float translateXEdge = viewStateDefault.width * (viewStateCurrent.scaleX - 1) / 2;
            if (viewStateCurrent.translationX > translateXEdge) endTranslateX = translateXEdge;
            else if (viewStateCurrent.translationX < -translateXEdge)
                endTranslateX = -translateXEdge;
            else endTranslateX = viewStateCurrent.translationX;

            if (viewStateDefault.height * viewStateCurrent.scaleY <= mHeight) {
                endTranslateY = viewStateDefault.translationY;
            } else {
                float translateYBottomEdge = viewStateDefault.height * viewStateCurrent.scaleY / 2 - viewStateDefault.height / 2;
                float translateYTopEdge = mHeight - viewStateDefault.height * viewStateCurrent.scaleY / 2 - viewStateDefault.height / 2;

                if (viewStateCurrent.translationY > translateYBottomEdge)
                    endTranslateY = translateYBottomEdge;
                else if (viewStateCurrent.translationY < translateYTopEdge)
                    endTranslateY = translateYTopEdge;
                else endTranslateY = viewStateCurrent.translationY;
            }
        } else if ("vertical".equals(imageOrientation)) {
            float translateYEdge = viewStateDefault.height * (viewStateCurrent.scaleY - 1) / 2;
            if (viewStateCurrent.translationY > translateYEdge) endTranslateY = translateYEdge;
            else if (viewStateCurrent.translationY < -translateYEdge)
                endTranslateY = -translateYEdge;
            else endTranslateY = viewStateCurrent.translationY;

            if (viewStateDefault.width * viewStateCurrent.scaleX <= mWidth) {
                endTranslateX = viewStateDefault.translationX;
            } else {
                float translateXRightEdge = viewStateDefault.width * viewStateCurrent.scaleX / 2 - viewStateDefault.width / 2;
                float translateXLeftEdge = mWidth - viewStateDefault.width * viewStateCurrent.scaleX / 2 - viewStateDefault.width / 2;

                if (viewStateCurrent.translationX > translateXRightEdge)
                    endTranslateX = translateXRightEdge;
                else if (viewStateCurrent.translationX < translateXLeftEdge)
                    endTranslateX = translateXLeftEdge;
                else endTranslateX = viewStateCurrent.translationX;
            }
        } else {
            return;
        }

        if (viewStateCurrent.translationX == endTranslateX && viewStateCurrent.translationY == endTranslateY) {
            return;// 动画跳过
        }

        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;
        animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
        animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float) animation.getAnimatedValue();

                iSource.setTranslationX(viewStateCurrent.translationX + (endTranslateX - viewStateCurrent.translationX) * p);
                iSource.setTranslationY(viewStateCurrent.translationY + (endTranslateY - viewStateCurrent.translationY) * p);

                setBackgroundColor(mColorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
            }
        });
        animTransitions.addListener(mAnimTransitionStateListener);
        animTransitions.start();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        iSource = adapter.mImageSparseArray.get(position);
        if (iOrigin != null) {
            iOrigin.setVisibility(View.VISIBLE);
        }
        if (position < mImageGroupList.size()) {
            iOrigin = mImageGroupList.get(position);
            iOrigin.setVisibility(View.INVISIBLE);
        }
        refreshCurrentIdx(position);

        ImageView mLast = adapter.mImageSparseArray.get(position - 1);
        if (mLast != null) ViewState.restore(mLast, ViewState.STATE_DEFAULT);
        ImageView mNext = adapter.mImageSparseArray.get(position + 1);
        if (mNext != null) ViewState.restore(mNext, ViewState.STATE_DEFAULT);
    }


    @Override
    public void onPageScrollStateChanged(int state) {
    }

    class ImagePagerAdapter extends PagerAdapter {
        private final FrameLayout.LayoutParams lpCenter = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        private final SparseArray<ImageView> mImageSparseArray = new SparseArray<>();
        private boolean hasPlayBeginAnimation;

        @Override
        public int getCount() {
            return mUrlList != null ? mUrlList.size() : 0;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            mImageSparseArray.remove(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FrameLayout itemView = new FrameLayout(container.getContext());
            container.addView(itemView);
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemView.addView(imageView);
            mImageSparseArray.put(position, imageView);

            MaterialProgressView loadView = new MaterialProgressView(container.getContext());
            lpCenter.gravity = Gravity.CENTER;
            loadView.setLayoutParams(lpCenter);
            itemView.addView(loadView);

            if (setDefaultDisplayConfigs(imageView, position, hasPlayBeginAnimation)) {
                hasPlayBeginAnimation = true;
            }
            return itemView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        void notifyItemChangeLoadingState(int position, boolean loading) {
            ImageView imageView = mImageSparseArray.get(position);
            if (imageView != null) {
                FrameLayout itemView = (FrameLayout) imageView.getParent();
                MaterialProgressView loadView = (MaterialProgressView) itemView.getChildAt(1);
                if (loading) {
                    loadView.setVisibility(View.VISIBLE);
                    loadView.start();
                } else {
                    loadView.stop();
                    loadView.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean setDefaultDisplayConfigs(final ImageView imageView, final int pos, boolean hasPlayBeginAnimation) {
        boolean isFindEnterImagePicture = false;

        ViewState viewStateOrigin = null;
        Drawable bmpMirror = null;
        if (pos < mImageGroupList.size()) {
            ImageView originRef = mImageGroupList.get(pos);
            int[] location = new int[2];
            originRef.getLocationOnScreen(location);
            imageView.setTranslationX(location[0]);
            int locationYOfFullScreen = location[1];
            locationYOfFullScreen -= mStatusBarHeight;
            imageView.setTranslationY(locationYOfFullScreen);
            viewStateOrigin = ViewState.write(imageView, ViewState.STATE_ORIGIN);
            viewStateOrigin.width = originRef.getWidth();
            viewStateOrigin.height = originRef.getHeight();
            imageView.getLayoutParams().width = originRef.getWidth();
            imageView.getLayoutParams().height = originRef.getHeight();

            bmpMirror = originRef.getDrawable();
            if (pos == initPosition && !hasPlayBeginAnimation) {
                isFindEnterImagePicture = true;
                iSource = imageView;
                iOrigin = originRef;
            }
        }

        final boolean mPlayEnterAnimation = isFindEnterImagePicture;
        final ViewState mViewStateOrigin = viewStateOrigin;
        if (bmpMirror != null) {
            final int sourceTmpWidth, sourceTmpHeight, sourceTmpTranslateX, sourceTmpTranslateY;
            sourceTmpWidth = bmpMirror.getBounds().width();
            sourceTmpHeight = bmpMirror.getBounds().height();
            sourceTmpTranslateX = (mWidth - sourceTmpWidth) / 2;
            sourceTmpTranslateY = (mHeight - sourceTmpHeight) / 2;
            imageView.setImageDrawable(bmpMirror);

            if (mPlayEnterAnimation) {
                if (animTransitions != null) animTransitions.cancel();
                animTransitions = null;
                animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
                animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float p = (float) animation.getAnimatedValue();
                        imageView.getLayoutParams().width = (int) (mViewStateOrigin.width + (sourceTmpWidth - mViewStateOrigin.width) * p);
                        imageView.getLayoutParams().height = (int) (mViewStateOrigin.height + (sourceTmpHeight - mViewStateOrigin.height) * p);
                        imageView.requestLayout();
                        imageView.setTranslationX(mViewStateOrigin.translationX + (sourceTmpTranslateX - mViewStateOrigin.translationX) * p);
                        imageView.setTranslationY(mViewStateOrigin.translationY + (sourceTmpTranslateY - mViewStateOrigin.translationY) * p);
                    }
                });
                animTransitions.addListener(mAnimTransitionStateListener);
                animTransitions.start();
            } else {
                imageView.getLayoutParams().width = sourceTmpWidth;
                imageView.getLayoutParams().height = sourceTmpHeight;
                imageView.requestLayout();
                imageView.setTranslationX(sourceTmpTranslateX);
                imageView.setTranslationY(sourceTmpTranslateY);
            }
        }
        // loadHighDefinitionPicture
        Glide.with(imageView.getContext()).load(mUrlList.get(pos)).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                final int sourceDefaultWidth, sourceDefaultHeight, sourceDefaultTranslateX, sourceDefaultTranslateY;
                int resourceImageWidth = resource.getWidth();
                int resourceImageHeight = resource.getHeight();
                if (resourceImageWidth * 1f / resourceImageHeight > mWidth * 1f / mHeight) {
                    sourceDefaultWidth = mWidth;
                    sourceDefaultHeight = (int) (sourceDefaultWidth * 1f / resourceImageWidth * resourceImageHeight);
                    sourceDefaultTranslateX = 0;
                    sourceDefaultTranslateY = (mHeight - sourceDefaultHeight) / 2;
                    imageView.setTag(R.id.image_orientation, "horizontal");
                } else {
                    sourceDefaultHeight = mHeight;
                    sourceDefaultWidth = (int) (sourceDefaultHeight * 1f / resourceImageHeight * resourceImageWidth);
                    sourceDefaultTranslateY = 0;
                    sourceDefaultTranslateX = (mWidth - sourceDefaultWidth) / 2;
                    imageView.setTag(R.id.image_orientation, "vertical");
                }
                imageView.setImageBitmap(resource);
                adapter.notifyItemChangeLoadingState(pos, false);

                final ViewState viewStateOrigin = ViewState.read(imageView, ViewState.STATE_ORIGIN);
                if (mPlayEnterAnimation && viewStateOrigin != null) {
                    final ViewState viewStateCurrent = ViewState.write(imageView, ViewState.STATE_CURRENT);
                    if (viewStateCurrent.width == 0 && viewStateCurrent.height == 0) {
                        viewStateCurrent.width = viewStateOrigin.width;
                        viewStateCurrent.height = viewStateOrigin.height;
                    }
                    if (animTransitions != null) animTransitions.cancel();
                    animTransitions = null;
                    animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
                    animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float p = (float) animation.getAnimatedValue();
                            imageView.getLayoutParams().width = (int) (viewStateCurrent.width + (sourceDefaultWidth - viewStateCurrent.width) * p);
                            imageView.getLayoutParams().height = (int) (viewStateCurrent.height + (sourceDefaultHeight - viewStateCurrent.height) * p);
                            imageView.requestLayout();
                            imageView.setTranslationX(viewStateCurrent.translationX + (sourceDefaultTranslateX - viewStateCurrent.translationX) * p);
                            imageView.setTranslationY(viewStateCurrent.translationY + (sourceDefaultTranslateY - viewStateCurrent.translationY) * p);
                        }
                    });
                    animTransitions.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ViewState.write(imageView, ViewState.STATE_DEFAULT);
                        }
                    });
                    animTransitions.addListener(mAnimTransitionStateListener);
                    animTransitions.start();
                } else {
                    imageView.getLayoutParams().width = sourceDefaultWidth;
                    imageView.getLayoutParams().height = sourceDefaultHeight;
                    imageView.requestLayout();
                    imageView.setTranslationX(sourceDefaultTranslateX);
                    imageView.setTranslationY(sourceDefaultTranslateY);
                    ViewState viewStateDefault = ViewState.write(imageView, ViewState.STATE_DEFAULT);
                    viewStateDefault.width = sourceDefaultWidth;
                    viewStateDefault.height = sourceDefaultHeight;
                    imageView.setAlpha(0f);
                    imageView.animate().alpha(1).start();
                }
            }

            @Override
            public void onLoadStarted(Drawable placeholder) {
                adapter.notifyItemChangeLoadingState(pos, true);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                adapter.notifyItemChangeLoadingState(pos, false);
            }
        });


        if (mPlayEnterAnimation) {
            ValueAnimator animBackground = ValueAnimator.ofFloat(0, 1).setDuration(300);
            animBackground.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (float) animation.getAnimatedValue();
                    setBackgroundColor(mColorEvaluator.evaluate(p, 0x00000000, 0xFF000000));
                }
            });
            animBackground.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    iOrigin.setVisibility(View.INVISIBLE);
                }
            });
            animBackground.start();
        }
        return mPlayEnterAnimation;
    }

    static class ViewState {
        static final int STATE_ORIGIN = R.id.state_origin;
        static final int STATE_DEFAULT = R.id.state_default;
        static final int STATE_CURRENT = R.id.state_current;
        static final int STATE_DRAG = R.id.state_touch_drag;
        static final int STATE_TOUCH_DOWN = R.id.state_touch_down;
        static final int STATE_TOUCH_SCALE_ROTATE = R.id.state_touch_scale_rotate;

        int width;
        int height;
        float translationX;
        float translationY;
        float scaleX;
        float scaleY;
        float rotation;
        float alpha;

        static ViewState write(View view, int tag) {
            if (view == null) return null;

            ViewState viewState = read(view, tag);
            if (viewState == null) view.setTag(tag, viewState = new ViewState());

            viewState.width = view.getWidth();
            viewState.height = view.getHeight();
            viewState.translationX = view.getTranslationX();
            viewState.translationY = view.getTranslationY();
            viewState.scaleX = view.getScaleX();
            viewState.scaleY = view.getScaleY();
            viewState.rotation = view.getRotation();
            viewState.alpha = view.getAlpha();
            return viewState;
        }

        static ViewState read(View view, int tag) {
            return view.getTag(tag) != null ? (ViewState) view.getTag(tag) : null;
        }

        static void restore(View view, int tag) {
            ViewState viewState = read(view, tag);
            if (viewState != null) {
                view.animate().translationX(viewState.translationX).translationY(viewState.translationY)
                        .scaleX(viewState.scaleX).scaleY(viewState.scaleY).rotation(viewState.rotation)
                        .alpha(viewState.alpha).start();
            }
        }
    }

    private static class GestureHandler extends Handler {
        WeakReference<ImageWatcher> mRef;

        GestureHandler(ImageWatcher ref) {
            mRef = new WeakReference<>(ref);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mRef.get() != null) {
                ImageWatcher holder = mRef.get();
                switch (msg.what) {
                    case SINGLE_TAP_UP_CONFIRMED:
                        holder.onSingleTapConfirmed();
                        break;
                    default:
                        throw new RuntimeException("Unknown message " + msg); //never
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    final AnimatorListenerAdapter mAnimTransitionStateListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationCancel(Animator animation) {
            isInTransitionsAnimation = false;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            isInTransitionsAnimation = true;
            mTouchMode = TOUCH_MODE_AUTO_FLING;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            isInTransitionsAnimation = false;
        }
    };

    final TypeEvaluator<Integer> mColorEvaluator = new TypeEvaluator<Integer>() {
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

    public void setTranslucentStatus(int statusBarHeight) {
        mStatusBarHeight = statusBarHeight;
    }

    private void refreshCurrentIdx(int position) {
        if (mUrlList.size() > 1) {
            tCurrentIdx.setVisibility(View.VISIBLE);
            tCurrentIdx.setText((position + 1) + " / " + mUrlList.size());
        } else {
            tCurrentIdx.setVisibility(View.GONE);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        super.setBackgroundColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        MAX_TRANSLATE_X = mWidth / 2;
        MAX_TRANSLATE_Y = mHeight / 2;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;
    }

    public boolean handleBackPressed() {
        if (isInTransitionsAnimation) return true;

        if (getVisibility() == View.VISIBLE) {
            ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
            ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);

            if (viewStateDefault == null || (viewStateCurrent.scaleY <= viewStateDefault.scaleY && viewStateCurrent.scaleX <= viewStateDefault.scaleX)) {
                mExitScalingRef = 0;
                handleExitTouchResult();
            } else {
                handleDoubleTapTouchResult();
            }
            return true;
        }
        return false;
    }
}
