package ch.ielse.demo.p02;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
 * QQ 517309507
 */
public class ImageWatcher extends FrameLayout implements GestureDetector.OnGestureListener {

    private int mStatusBarHeight;
    private FrameLayout.LayoutParams lpSource =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    private final ImageView iSource;
    private ImageView iOrigin;

    private int mWidth, mHeight;
    private int mBackgroundColor;

    private ValueAnimator animTransitions;
    private final GestureDetector gestureDetector;

    private boolean isInTransitionsAnimation;


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


    public void show(ImageView iOriginImageView) {
        iOrigin = iOriginImageView;
        if (iOrigin == null) return;

        iOrigin.setDrawingCacheEnabled(true);
        Bitmap bitmap = iOrigin.getDrawingCache();
        if (bitmap == null) return;

        Bitmap mirror = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        iOrigin.setDrawingCacheEnabled(false);

        iSource.setImageBitmap(mirror);

        iOrigin.setVisibility(View.INVISIBLE);
        setVisibility(View.VISIBLE);
        int[] location = new int[2];
        iOrigin.getLocationOnScreen(location);

        iSource.setTranslationX(location[0]);
        int locationYOfFullScreen = location[1];
        locationYOfFullScreen -= mStatusBarHeight;

        iSource.setTranslationY(locationYOfFullScreen);

        lpSource.width = iOrigin.getWidth();
        lpSource.height = iOrigin.getHeight();
        iSource.requestLayout();
        final ViewState viewStateOrigin = ViewState.write(iSource, ViewState.STATE_ORIGIN);
        viewStateOrigin.width = lpSource.width;
        viewStateOrigin.height = lpSource.height;

        final int sourceDefaultWidth, sourceDefaultHeight, sourceDefaultTranslateX, sourceDefaultTranslateY;

        if (iOrigin.getWidth() * 1f / iOrigin.getHeight() > mWidth * 1f / mHeight) {
            sourceDefaultWidth = mWidth;
            sourceDefaultHeight = (int) (sourceDefaultWidth * 1f / iOrigin.getWidth() * iOrigin.getHeight());
            sourceDefaultTranslateX = 0;
            sourceDefaultTranslateY = (mHeight - sourceDefaultHeight) / 2;
            iSource.setTag(R.id.image_orientation, "horizontal");
        } else {
            sourceDefaultHeight = mHeight;
            sourceDefaultWidth = (int) (sourceDefaultHeight * 1f / iOrigin.getHeight() * iOrigin.getWidth());
            sourceDefaultTranslateY = 0;
            sourceDefaultTranslateX = (mWidth - sourceDefaultWidth) / 2;
            iSource.setTag(R.id.image_orientation, "vertical");
        }

        // 此事件执行时间段内 禁止 触摸
        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;
        animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(300);
        animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float) animation.getAnimatedValue();
                lpSource.width = (int) (viewStateOrigin.width + (sourceDefaultWidth - viewStateOrigin.width) * p);
                lpSource.height = (int) (viewStateOrigin.height + (sourceDefaultHeight - viewStateOrigin.height) * p);
                iSource.requestLayout();
                iSource.setTranslationX(viewStateOrigin.translationX + (sourceDefaultTranslateX - viewStateOrigin.translationX) * p);
                iSource.setTranslationY(viewStateOrigin.translationY + (sourceDefaultTranslateY - viewStateOrigin.translationY) * p);
                setBackgroundColor(colorEvaluator.evaluate(p, 0x00000000, 0xFF000000));
            }
        });
        animTransitions.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewState.write(iSource, ViewState.STATE_DEFAULT);
            }
        });
        animTransitions.addListener(mAnimTransitionStateListener);
        animTransitions.start();
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
        if (isInTransitionsAnimation) return true;

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                onUp(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.e("TTT", "退出双手状态? ACTION_POINTER_UP");
                mFingersDistance = 0;
                mFingersAngle = 0;
                mFingersCenterX = 0;
                mFingersCenterY = 0;
                ViewState.write(iSource, ViewState.STATE_TOUCH_SCALE_ROTATE);
                mTouchMode = TOUCH_MODE_LOCK;
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }


    private static final int TOUCH_MODE_NONE = 0;
    private static final int TOUCH_MODE_DRAG = 1;
    private static final int TOUCH_MODE_EXIT = 2;
    private static final int TOUCH_MODE_SCALE_ROTATE = 4;
    private static final int TOUCH_MODE_LOCK = 8;
    private static final int TOUCH_MODE_AUTO_FLING = 9;

    private int mTouchMode;
    private float mTouchSclop;

    private float mFingersDistance;
    private double mFingersAngle; // 相对于[东] point0作为起点;point1作为终点
    private float mFingersCenterX;
    private float mFingersCenterY;
    boolean hasCheckExitGesture;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        Log.e("TTT", "e1.getX" + e1.getX() + "###e2.getX" + e2.getX());
        Log.e("TTT", "AAA e1.trany" + iSource.getTranslationY());
        if (mTouchMode == TOUCH_MODE_AUTO_FLING) return false;

        if (mTouchMode == TOUCH_MODE_NONE) {
            final float moveX = e2.getX() - e1.getX();
            final float moveY = e2.getY() - e1.getY();
            if (Math.abs(moveX) > mTouchSclop || Math.abs(moveY) > mTouchSclop) {
                ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
                ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);

                if (viewStateCurrent.scaleY > viewStateDefault.scaleY || viewStateCurrent.scaleX > viewStateDefault.scaleX) {
                    if (mTouchMode != TOUCH_MODE_DRAG) {
                        Log.e("TTT", "记录View状态 至 ->  TOUCH_MODE_DRAG");
                        ViewState.write(iSource, ViewState.STATE_DRAG);
                    }
                    mTouchMode = TOUCH_MODE_DRAG;
                } else if (moveY > mTouchSclop && moveY > moveX * 2) {
                    mTouchMode = TOUCH_MODE_EXIT;

                    // some time to view pager
                }
            }
        }

        if (e2.getPointerCount() >= 2) {
            if (mTouchMode != TOUCH_MODE_SCALE_ROTATE) {
                Log.e("TTT", "记录View状态 至 ->  TOUCH_MODE_SCALE_ROTATE");
                ViewState.write(iSource, ViewState.STATE_TOUCH_SCALE_ROTATE);
            }
            mTouchMode = TOUCH_MODE_SCALE_ROTATE;
        } else if (e2.getPointerCount() == 1) {
//            if (mTouchMode != TOUCH_MODE_EXIT) {
//                mTouchMode = TOUCH_MODE_DRAG;
//            }
        }

        // Log.e("TTT", "onScroll e1.getY():" + e1.getY() + "#e2.getY():" + e2.getY() + "#mTouchDownY:" + mTouchDownY);

        if (mTouchMode == TOUCH_MODE_SCALE_ROTATE) {
            handleScaleRotateGesture(e2);
        } else if (mTouchMode == TOUCH_MODE_EXIT) {
            handleExitGesture(e2, e1);
        } else if (mTouchMode == TOUCH_MODE_DRAG) {
            handleDragGesture(e2, e1);
        }

//        Log.e("TTT", "onScroll distanceX:" + distanceX + "##distanceY:" + distanceY);
        return false;
    }


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
    public void onLongPress(MotionEvent e) {
        Log.e("TTT", "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e("TTT", "onFling");
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.e("TTT", "AAA onDown x:" + e.getX() + "#y:" + e.getY() + "#rawx:" + e.getRawX() + "#rawy:" + e.getRawY() + "#TransY:" + iSource.getTranslationY());
        // resetTouchConfigs
        mTouchMode = TOUCH_MODE_NONE;
        hasCheckExitGesture = false;

        mFingersDistance = 0;
        mFingersAngle = 0;
        mFingersCenterX = 0;
        mFingersCenterY = 0;

        ViewState.write(iSource, ViewState.STATE_TOUCH_DOWN);
        return true;
    }

    public void onUp(MotionEvent e) {
        Log.e("TTT", "onUp touchmode:" + mTouchMode);
        if (mTouchMode == TOUCH_MODE_EXIT) {
            handleExitTouchResult();
        } else if (mTouchMode == TOUCH_MODE_SCALE_ROTATE
                || mTouchMode == TOUCH_MODE_LOCK) {
            handleScaleRotateTouchResult();
        } else if (mTouchMode == TOUCH_MODE_DRAG) {
            handleDragTouchResult();
        }

        //mTouchMode = TOUCH_MODE_AUTO_FLING;
    }


    private void handleDragGesture(MotionEvent e2, MotionEvent e1) {
        final float moveY = e2.getY() - e1.getY();
        final float moveX = e2.getX() - e1.getX();

        ViewState viewStateTouchDrag = ViewState.read(iSource, ViewState.STATE_DRAG);
        iSource.setTranslationX(viewStateTouchDrag.translationX + moveX);
        iSource.setTranslationY(viewStateTouchDrag.translationY + moveY);
    }

    float mExitScalingRef; // 触摸退出进度

    /**
     * 单手退出拖拽[移动中触摸事件处理]
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
        iSource.setTranslationX(viewStateTouchDown.translationX + moveX);
        iSource.setTranslationY(viewStateTouchDown.translationY + moveY);
        iSource.setScaleX(viewStateTouchDown.scaleX * mExitScalingRef);
        iSource.setScaleY(viewStateTouchDown.scaleY * mExitScalingRef);
        setBackgroundColor(colorEvaluator.evaluate(mExitScalingRef, 0x00000000, 0xFF000000));
    }

    static final float MIN_SCALE = 0.5f;
    static final float MAX_SCALE = 3.8f;
    static int MAX_TRANSLATE_X;
    static int MAX_TRANSLATE_Y;

    /**
     * 双手拖拽缩放旋转[移动中触摸事件处理]
     */
    private void handleScaleRotateGesture(MotionEvent e2) {
        final ViewState viewStateTouchScaleRotate = ViewState.read(iSource, ViewState.STATE_TOUCH_SCALE_ROTATE);

        final float deltaX = e2.getX(1) - e2.getX(0);
        final float deltaY = e2.getY(1) - e2.getY(0);
        double angle = Math.toDegrees(Math.atan(deltaX / deltaY));
        if (deltaY < 0) angle = angle + 180;
        if (mFingersAngle == 0) mFingersAngle = angle;

        float changedAngle = (float) (mFingersAngle - angle);
        iSource.setRotation((viewStateTouchScaleRotate.rotation + changedAngle) % 360);

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
        if (changedCenterX > MAX_TRANSLATE_X) changedCenterX = MAX_TRANSLATE_X;
        else if (changedCenterX < -MAX_TRANSLATE_X) changedCenterX = -MAX_TRANSLATE_X;
        iSource.setTranslationX(viewStateTouchScaleRotate.translationX - changedCenterX);

        float changedCenterY = mFingersCenterY - centerY;
        if (changedCenterY > MAX_TRANSLATE_Y) changedCenterY = MAX_TRANSLATE_Y;
        else if (changedCenterY < -MAX_TRANSLATE_Y) changedCenterY = -MAX_TRANSLATE_Y;
        iSource.setTranslationY(viewStateTouchScaleRotate.translationY - changedCenterY);
    }


    private void handleExitTouchResult() {
        if (mExitScalingRef > 0.9f) {
            // 恢复
            final int startBackgroundColor = mBackgroundColor;
            final int endBackgroundColor = 0xFF000000;
            final ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
            final ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);

            if (animTransitions != null) animTransitions.cancel();
            animTransitions = null;
            animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(350);
            animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (float) animation.getAnimatedValue();
                    iSource.setTranslationX(viewStateCurrent.translationX + (viewStateDefault.translationX - viewStateCurrent.translationX) * p);
                    iSource.setTranslationY(viewStateCurrent.translationY + (viewStateDefault.translationY - viewStateCurrent.translationY) * p);
                    iSource.setScaleX(viewStateCurrent.scaleX + (viewStateDefault.scaleX - viewStateCurrent.scaleX) * p);
                    iSource.setScaleY(viewStateCurrent.scaleY + (viewStateDefault.scaleY - viewStateCurrent.scaleY) * p);
                    iSource.setRotation((viewStateCurrent.rotation + (viewStateDefault.rotation - viewStateCurrent.rotation) * p) % 360);
                    setBackgroundColor(colorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
                }
            });
            animTransitions.addListener(mAnimTransitionStateListener);
            animTransitions.start();
        } else {
            // 退出
            final ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
            final ViewState viewStateOrigin = ViewState.read(iSource, ViewState.STATE_ORIGIN);
            final int startBackgroundColor = mBackgroundColor;
            final int endBackgroundColor = 0x00000000;

            if (animTransitions != null) animTransitions.cancel();
            animTransitions = null;
            animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(350);
            animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (float) animation.getAnimatedValue();

                    lpSource.width = (int) (viewStateCurrent.width + (viewStateOrigin.width - viewStateCurrent.width) * p);
                    lpSource.height = (int) (viewStateCurrent.height + (viewStateOrigin.height - viewStateCurrent.height) * p);
                    iSource.requestLayout();

                    iSource.setTranslationX(viewStateCurrent.translationX + (viewStateOrigin.translationX - viewStateCurrent.translationX) * p);
                    iSource.setTranslationY(viewStateCurrent.translationY + (viewStateOrigin.translationY - viewStateCurrent.translationY) * p);
                    iSource.setScaleX(viewStateCurrent.scaleX + (viewStateOrigin.scaleX - viewStateCurrent.scaleX) * p);
                    iSource.setScaleY(viewStateCurrent.scaleY + (viewStateOrigin.scaleY - viewStateCurrent.scaleY) * p);
                    iSource.setRotation((viewStateCurrent.rotation + (viewStateOrigin.rotation - viewStateCurrent.rotation) * p) % 360);

                    setBackgroundColor(colorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
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


    private void handleScaleRotateTouchResult() {
        // 恢复
        final int startBackgroundColor = mBackgroundColor;
        final int endBackgroundColor = 0xFF000000;
        final ViewState viewStateCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
        final ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);

        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;
        animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(350);
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
                setBackgroundColor(colorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
            }
        });
        animTransitions.addListener(mAnimTransitionStateListener);
        animTransitions.start();
    }


    private void handleDragTouchResult() {
        // 恢复
        final int startBackgroundColor = mBackgroundColor;
        final int endBackgroundColor = 0xFF000000;

        ViewState viewStateDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
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

                if (viewStateCurrent.translationY > translateYBottomEdge) {
                    Log.e("TTT", "AAA 向上回弹");
                    endTranslateY = translateYBottomEdge;
                } else if (viewStateCurrent.translationY < translateYTopEdge) {
                    Log.e("TTT", "AAA 向下回弹");
                    endTranslateY = translateYTopEdge;
                } else {
                    Log.e("TTT", "AAA 内部消化");
                    endTranslateY = viewStateCurrent.translationY;
                }
            }

        } else if ("vertical".equals(imageOrientation)) {
            Log.i("TTT", "vertical vertical vertical vertical");
            endTranslateX = viewStateDefault.translationX;

            float translateYEdge = viewStateDefault.height * (viewStateCurrent.scaleY - 1) / 2;
            if (viewStateCurrent.translationY > translateYEdge) endTranslateY = translateYEdge;
            else if (viewStateCurrent.translationY < -translateYEdge)
                endTranslateY = -translateYEdge;
            else endTranslateY = viewStateCurrent.translationY;
        } else {
            return;
        }

        if (viewStateCurrent.translationX == endTranslateX && viewStateCurrent.translationY == endTranslateY) {
            return;// 350毫秒动画跳过
        }

        if (animTransitions != null) animTransitions.cancel();
        animTransitions = null;
        animTransitions = ValueAnimator.ofFloat(0, 1).setDuration(350);
        animTransitions.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float) animation.getAnimatedValue();

                iSource.setTranslationX(viewStateCurrent.translationX + (endTranslateX - viewStateCurrent.translationX) * p);
                iSource.setTranslationY(viewStateCurrent.translationY + (endTranslateY - viewStateCurrent.translationY) * p);

                setBackgroundColor(colorEvaluator.evaluate(p, startBackgroundColor, endBackgroundColor));
            }
        });
        animTransitions.addListener(mAnimTransitionStateListener);
        animTransitions.start();
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

        static ViewState write(View view, int tag) {
            ViewState viewState = read(view, tag);
            if (viewState == null) view.setTag(tag, viewState = new ViewState());

            viewState.width = view.getWidth();
            viewState.height = view.getHeight();
            viewState.translationX = view.getTranslationX();
            viewState.translationY = view.getTranslationY();
            viewState.scaleX = view.getScaleX();
            viewState.scaleY = view.getScaleY();
            viewState.rotation = view.getRotation();
            return viewState;
        }

        static ViewState read(View view, int tag) {
            return view.getTag(tag) != null ? (ViewState) view.getTag(tag) : null;
        }
    }

    private final AnimatorListenerAdapter mAnimTransitionStateListener = new AnimatorListenerAdapter() {
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

    public void setTranslucentStatus(int statusBarHeight) {
        mStatusBarHeight = statusBarHeight;
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
}
