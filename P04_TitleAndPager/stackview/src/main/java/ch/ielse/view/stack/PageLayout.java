package ch.ielse.view.stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;



public class PageLayout extends FrameLayout {
    public interface Callback {
        void popCancel();

        void pop(boolean anim);

        void sliding(float p);
    }

    private List<Callback> callbacks = new ArrayList<>();

    public void addCallback(Callback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    private FrameLayout mPageChildWrap;
    private final Stack<FrameLayout> mChildContainers = new Stack<>();
//    private FrameLayout mCurrentPage;
//    private FrameLayout mLastPage;

    private FrameLayout layShadow;
    private View viewShadow;

    public PageLayout(Context context) {
        this(context, null);
    }

    public PageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        addView(mPageChildWrap = new FrameLayout(context));

        layShadow = new FrameLayout(context);
        addView(layShadow);
        viewShadow = new View(context);
        viewShadow.setBackgroundResource(R.drawable.shpe_stack_page_shadow);
        final float scale = context.getResources().getDisplayMetrics().density;
        int shadowWidth = (int) (28 * scale + 0.5f);
        LayoutParams lpShadow = new LayoutParams(shadowWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        lpShadow.gravity = Gravity.RIGHT;
        viewShadow.setLayoutParams(lpShadow);
        layShadow.addView(viewShadow);
        viewShadow.setAlpha(0f);
    }


    public void put(View view) {
        if (isAnim) return;

        if (!mChildContainers.isEmpty()) {
            final FrameLayout layLastWrap = mChildContainers.pop();
            mPageChildWrap.removeView(layLastWrap);
            layLastWrap.removeAllViewsInLayout();
        }

        final FrameLayout layCurrentWrap = new FrameLayout(getContext());
        layCurrentWrap.addView(view);
        mChildContainers.add(layCurrentWrap);
        mPageChildWrap.addView(layCurrentWrap);
    }

    public void push(View view, boolean anim) {
        if (mChildContainers.isEmpty()) {
            put(view);
            return;
        }

        if (isAnim) return;

        final FrameLayout layCurrentWrap = new FrameLayout(getContext());
        layCurrentWrap.addView(view);
        final FrameLayout layLastWrap = mChildContainers.peek();

        if (anim) {
            ValueAnimator animator = generatePageAnimator(0, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (Float) animation.getAnimatedValue();
                    layLastWrap.setTranslationX(-(mWidth / 2f) * p);
                    layCurrentWrap.setTranslationX(getWidth() * (1 - p));
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layLastWrap.setVisibility(View.GONE);
                }
            });
            animator.start();
            isAnim = true;
        } else {
            layLastWrap.setVisibility(View.GONE);
            layLastWrap.setTranslationX(-(mWidth / 2f));
        }
        mChildContainers.add(layCurrentWrap);
        mPageChildWrap.addView(layCurrentWrap);
    }

    public void pop(float progress, boolean anim) {
        if (mChildContainers.size() < 2) {
            return;
        }

        if (isAnim) return;

        final FrameLayout layCurrentWrap = mChildContainers.pop();
        final FrameLayout layLastWrap = mChildContainers.peek();

        layLastWrap.setVisibility(View.VISIBLE);

        if (anim) {
            ValueAnimator animator = generatePageAnimator(progress, 1);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float p = (Float) animation.getAnimatedValue();
                    layCurrentWrap.setTranslationX(mWidth * p);
                    layLastWrap.setTranslationX(-mWidth / 2 * (1 - p));

                    layShadow.setTranslationX(-mWidth + p * mWidth);
                    layShadow.setBackgroundColor(ce.evaluate(p, 0x33000000, 0x00000000));
                    viewShadow.setAlpha(p);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPageChildWrap.removeView(layCurrentWrap);
                    layCurrentWrap.removeAllViewsInLayout();


                }
            });
            animator.start();
        } else {
            mPageChildWrap.removeView(layCurrentWrap);
            layCurrentWrap.removeAllViewsInLayout();
            layLastWrap.setTranslationX(0);
            layLastWrap.setVisibility(View.VISIBLE);
        }
    }

    private void popCancel(float progress) {
        if (mChildContainers.size() < 2) {
            // Not enough item
            return;
        }
        final FrameLayout curr = mChildContainers.get(mChildContainers.size() - 1);
        final FrameLayout last = mChildContainers.get(mChildContainers.size() - 2);
        last.setVisibility(View.VISIBLE);

        ValueAnimator animator = generatePageAnimator(progress, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (Float) animation.getAnimatedValue();
                curr.setTranslationX(mWidth * progress);
                last.setTranslationX(-mWidth / 2 * (1 - progress));

                layShadow.setTranslationX(-mWidth + progress * mWidth);
                layShadow.setBackgroundColor(ce.evaluate(progress, 0x33000000, 0x00000000));
                viewShadow.setAlpha(progress);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                last.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mBackEdge = mWidth / 2;
    }

    private boolean isAnim;
    private int mWidth;
    private float mBackEdge;


    private ValueAnimator generatePageAnimator(float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end).setDuration(400);
        animator.addListener(mAnimStateChangedListener);
        return animator;
    }

    private AnimatorListenerAdapter mAnimStateChangedListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationCancel(Animator animation) {
            isAnim = false;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            isAnim = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            isAnim = false;
        }
    };

    private float tmpMoveProgress; // 0 ~ 1
    boolean mShouldSlidingPage;
    boolean mIsBeingDragged;
    float mInitialDownX;
    private float touchSlop;
    float downCurrPageTransX, downLastPageTransX;
    FrameLayout mCurrentWrap;
    FrameLayout mLastWrap;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || isAnim) {
            return false;
        }
        final int action = event.getAction() & android.view.MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged && !isOnlyOnePageOrEmpty()) {
                    mIsBeingDragged = true;
                    mInitialDownX = event.getRawX();

                    mCurrentWrap = mChildContainers.get(mChildContainers.size() - 1);
                    mLastWrap = mChildContainers.get(mChildContainers.size() - 2);

                    downCurrPageTransX = mCurrentWrap.getTranslationX();
                    downLastPageTransX = mLastWrap.getTranslationX();
                }

                if (mLastWrap != null && mLastWrap.getVisibility() != View.VISIBLE) {
                    mLastWrap.setVisibility(View.VISIBLE);
                }

                if (!mShouldSlidingPage) {
                    if (!isOnlyOnePageOrEmpty() && Math.abs(event.getRawX() - mInitialDownX) > touchSlop) {
                        mShouldSlidingPage = true;
                    }
                } else {
                    float currPageResult = downCurrPageTransX + event.getRawX() - mInitialDownX;
                    if (currPageResult < 0) {
                        currPageResult = 0;
                    }
                    mCurrentWrap.setTranslationX(currPageResult);

                    layShadow.setTranslationX(-mWidth + currPageResult);
                    layShadow.setBackgroundColor(ce.evaluate(currPageResult / mWidth, 0x33000000, 0x00000000));
                    viewShadow.setAlpha(1 - currPageResult / mWidth);

                    float lastResult = downLastPageTransX + (event.getRawX() - mInitialDownX) / 2;
                    if (lastResult > 0) {
                        lastResult = 0;
                    }
                    mLastWrap.setTranslationX(lastResult);
                    tmpMoveProgress = currPageResult / mWidth;

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isOnlyOnePageOrEmpty()) {
                    if (mShouldSlidingPage) {
                        if (event.getRawX() - mInitialDownX > mBackEdge) {
                            pop(tmpMoveProgress, true);
                            tmpMoveProgress = 1;
                        } else {
                            popCancel(tmpMoveProgress);
                            tmpMoveProgress = 0;
                        }
                    } else {
                        if (mLastWrap != null && mLastWrap.getVisibility() != View.GONE) {
                            mLastWrap.setVisibility(View.GONE);
                        }
                    }
                }
                mIsBeingDragged = false;
                mShouldSlidingPage = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    public boolean isOnlyOnePageOrEmpty() {
        return mChildContainers.size() <= 1;
    }

    private final TypeEvaluator<Integer> ce = new TypeEvaluator<Integer>() {
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
}
