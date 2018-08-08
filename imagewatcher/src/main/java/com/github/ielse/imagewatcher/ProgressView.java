package com.github.ielse.imagewatcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import java.util.ArrayList;

/**
 * PtrFrameLayout 源码微调
 */
public class ProgressView extends View {

    private AnimationDrawable mDrawable;
    private float mScale = 1f;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(new MaterialProgressDrawable(getContext(), this));
    }

    private void initView(AnimationDrawable d) {
        mDrawable = d;
        mDrawable.setAlpha(255);
        mDrawable.setCallback(this);
    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        if (dr == mDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        super.scheduleDrawable(who, what, when);

    }

    public void start() {
        mDrawable.start();
    }

    public void stop() {
        mDrawable.stop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int size = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();
        Rect rect = mDrawable.getBounds();
        int l = getPaddingLeft() + (getMeasuredWidth() - mDrawable.getIntrinsicWidth()) / 2;
        canvas.translate(l, getPaddingTop());
        canvas.scale(mScale, mScale, rect.exactCenterX(), rect.exactCenterY());
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    static class MaterialProgressDrawable extends AnimationDrawable {
        private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
        private static final Interpolator END_CURVE_INTERPOLATOR = new EndCurveInterpolator();
        private static final Interpolator START_CURVE_INTERPOLATOR = new StartCurveInterpolator();
        private static final Interpolator EASE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
        // Maps to ProgressBar default style
        private static final int CIRCLE_DIAMETER = 30;
        private static final float CENTER_RADIUS = 8f; //should add up to 10 when + stroke_width
        private static final float STROKE_WIDTH = 2f;
        /**
         * The duration of a single progress spin in milliseconds.
         */
        private static final int ANIMATION_DURATION = 1000 * 80 / 60;
        /**
         * The number of points in the progress "star".
         */
        private static final float NUM_POINTS = 5f;
        private static final float MAX_PROGRESS_ARC = .8f;
        private final int[] COLORS = new int[]{
                0xFFFFFFFF,
                0xFFFFFFFF,
                0xFFFFFFFF,
                0xFFFFFFFF
        };
        /**
         * The list of animators operating on this drawable.
         */
        private final ArrayList<Animation> mAnimators = new ArrayList<Animation>();
        /**
         * The indicator ring, used to manage animation state.
         */
        private final Ring mRing;
        private final Callback mCallback = new Callback() {
            @Override
            public void invalidateDrawable(Drawable d) {
                invalidateSelf();
            }

            @Override
            public void scheduleDrawable(Drawable d, Runnable what, long when) {
                scheduleSelf(what, when);
            }

            @Override
            public void unscheduleDrawable(Drawable d, Runnable what) {
                unscheduleSelf(what);
            }
        };
        /**
         * Canvas rotation in degrees.
         */
        private float mRotation;
        private Resources mResources;
        private View mParent;
        private Animation mAnimation;
        private float mRotationCount;
        private double mWidth;
        private double mHeight;
        private Animation mFinishAnimation;

        public MaterialProgressDrawable(Context context, View parent) {
            mParent = parent;
            mResources = context.getResources();
            mRing = new Ring(mCallback);
            mRing.setColors(COLORS);
            setSizeParameters(CIRCLE_DIAMETER, CIRCLE_DIAMETER, CENTER_RADIUS, STROKE_WIDTH);
            setupAnimators();
        }

        private void setSizeParameters(double progressCircleWidth, double progressCircleHeight,
                                       double centerRadius, double strokeWidth) {
            final Ring ring = mRing;
            final DisplayMetrics metrics = mResources.getDisplayMetrics();
            final float screenDensity = metrics.density;
            mWidth = progressCircleWidth * screenDensity;
            mHeight = progressCircleHeight * screenDensity;
            ring.setStrokeWidth((float) strokeWidth * screenDensity);
            ring.setCenterRadius(centerRadius * screenDensity);
            ring.setColorIndex(0);
            ring.setInsets((int) mWidth, (int) mHeight);
        }

        @Override
        public int getIntrinsicHeight() {
            return (int) mHeight;
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) mWidth;
        }

        @Override
        public void draw(Canvas c) {

            final Rect bounds = getBounds();
            final int saveCount = c.save();
            c.rotate(mRotation, bounds.exactCenterX(), bounds.exactCenterY());
            mRing.draw(c, bounds);
            c.restoreToCount(saveCount);
        }

        public int getAlpha() {
            return mRing.getAlpha();
        }

        @Override
        public void setAlpha(int alpha) {
            mRing.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            mRing.setColorFilter(colorFilter);
        }


        void setRotation(float rotation) {
            mRotation = rotation;
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public boolean isRunning() {
            final ArrayList<Animation> animators = mAnimators;
            final int N = animators.size();
            for (int i = 0; i < N; i++) {
                final Animation animator = animators.get(i);
                if (animator.hasStarted() && !animator.hasEnded()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            mAnimation.reset();
            mRing.storeOriginals();
            // Already showing some part of the ring
            if (mRing.getEndTrim() != mRing.getStartTrim()) {
                mParent.startAnimation(mFinishAnimation);
            } else {
                mRing.setColorIndex(0);
                mRing.resetOriginals();
                mParent.startAnimation(mAnimation);
            }
        }

        @Override
        public void stop() {
            mParent.clearAnimation();
            setRotation(0);
            mRing.setShowArrow(false);
            mRing.setColorIndex(0);
            mRing.resetOriginals();
        }

        private void setupAnimators() {
            final Ring ring = mRing;
            final Animation finishRingAnimation = new Animation() {
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    // shrink back down and complete a full rotation before starting other circles
                    // Rotation goes between [0..1].
                    float targetRotation = (float) (Math.floor(ring.getStartingRotation()
                            / MAX_PROGRESS_ARC) + 1f);
                    final float startTrim = ring.getStartingStartTrim()
                            + (ring.getStartingEndTrim() - ring.getStartingStartTrim())
                            * interpolatedTime;
                    ring.setStartTrim(startTrim);
                    final float rotation = ring.getStartingRotation()
                            + ((targetRotation - ring.getStartingRotation()) * interpolatedTime);
                    ring.setRotation(rotation);
                    ring.setArrowScale(1 - interpolatedTime);
                }
            };
            finishRingAnimation.setInterpolator(EASE_INTERPOLATOR);
            finishRingAnimation.setDuration(ANIMATION_DURATION / 2);
            finishRingAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ring.goToNextColor();
                    ring.storeOriginals();
                    ring.setShowArrow(false);
                    mParent.startAnimation(mAnimation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            final Animation animation = new Animation() {
                @Override
                public void applyTransformation(float interpolatedTime, Transformation t) {
                    // The minProgressArc is calculated from 0 to create an angle that
                    // matches the stroke width.
                    final float minProgressArc = (float) Math.toRadians(ring.getStrokeWidth()
                            / (2 * Math.PI * ring.getCenterRadius()));
                    final float startingEndTrim = ring.getStartingEndTrim();
                    final float startingTrim = ring.getStartingStartTrim();
                    final float startingRotation = ring.getStartingRotation();
                    // Offset the minProgressArc to where the endTrim is located.
                    final float minArc = MAX_PROGRESS_ARC - minProgressArc;
                    final float endTrim = startingEndTrim
                            + (minArc * START_CURVE_INTERPOLATOR.getInterpolation(interpolatedTime));
                    ring.setEndTrim(endTrim);
                    final float startTrim = startingTrim
                            + (MAX_PROGRESS_ARC * END_CURVE_INTERPOLATOR
                            .getInterpolation(interpolatedTime));
                    ring.setStartTrim(startTrim);
                    final float rotation = startingRotation + (0.25f * interpolatedTime);
                    ring.setRotation(rotation);
                    float groupRotation = ((720.0f / NUM_POINTS) * interpolatedTime)
                            + (720.0f * (mRotationCount / NUM_POINTS));
                    setRotation(groupRotation);
                }
            };
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.RESTART);
            animation.setInterpolator(LINEAR_INTERPOLATOR);
            animation.setDuration(ANIMATION_DURATION);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mRotationCount = 0;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // do nothing
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    ring.storeOriginals();
                    ring.goToNextColor();
                    ring.setStartTrim(ring.getEndTrim());
                    mRotationCount = (mRotationCount + 1) % (NUM_POINTS);
                }
            });
            mFinishAnimation = finishRingAnimation;
            mAnimation = animation;
        }

        private static class Ring {
            private final RectF mTempBounds = new RectF();
            private final Paint mArcPaint = new Paint();
            private final Paint mArrowPaint = new Paint();
            private final Callback mRingCallback;
            private final Paint mCirclePaint = new Paint();
            private float mStartTrim = 0.0f;
            private float mEndTrim = 0.0f;
            private float mRotation = 0.0f;
            private float mStrokeWidth = 5.0f;
            private float mStrokeInset = 2.5f;
            private int[] mColors;
            // mColorIndex represents the offset into the available mColors that the
            // progress circle should currently display. As the progress circle is
            // animating, the mColorIndex moves by one to the next available color.
            private int mColorIndex;
            private float mStartingStartTrim;
            private float mStartingEndTrim;
            private float mStartingRotation;
            private boolean mShowArrow;
            private float mArrowScale;
            private double mRingCenterRadius;
            private int mAlpha;

            public Ring(Callback callback) {
                mRingCallback = callback;
                mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
                mArcPaint.setAntiAlias(true);
                mArcPaint.setStyle(Paint.Style.STROKE);
                mArrowPaint.setStyle(Paint.Style.FILL);
                mArrowPaint.setAntiAlias(true);
                mCirclePaint.setAntiAlias(true);
            }


            /**
             * Draw the progress spinner
             */
            public void draw(Canvas c, Rect bounds) {

                final RectF arcBounds = mTempBounds;
                arcBounds.set(bounds);
                arcBounds.inset(mStrokeInset, mStrokeInset);
                final float startAngle = (mStartTrim + mRotation) * 360;
                final float endAngle = (mEndTrim + mRotation) * 360;
                float sweepAngle = endAngle - startAngle;
                mArcPaint.setColor(mColors[mColorIndex]);
                mArcPaint.setAlpha(mAlpha);
                c.drawArc(arcBounds, startAngle, sweepAngle, false, mArcPaint);
            }


            /**
             * Set the colors the progress spinner alternates between.
             *
             * @param colors Array of integers describing the colors. Must be non-<code>null</code>.
             */
            public void setColors(int[] colors) {
                mColors = colors;
                // if colors are reset, make sure to reset the color index as well
                setColorIndex(0);
            }

            /**
             * @param index Index into the color array of the color to display in
             *              the progress spinner.
             */
            public void setColorIndex(int index) {
                mColorIndex = index;
            }

            /**
             * Proceed to the next available ring color. This will automatically
             * wrap back to the beginning of colors.
             */
            public void goToNextColor() {
                mColorIndex = (mColorIndex + 1) % (mColors.length);
            }

            public void setColorFilter(ColorFilter filter) {
                mArcPaint.setColorFilter(filter);
                invalidateSelf();
            }

            /**
             * @return Current alpha of the progress spinner and arrowhead.
             */
            public int getAlpha() {
                return mAlpha;
            }

            /**
             * @param alpha Set the alpha of the progress spinner and associated arrowhead.
             */
            public void setAlpha(int alpha) {
                mAlpha = alpha;
            }


            public float getStrokeWidth() {
                return mStrokeWidth;
            }

            /**
             * @param strokeWidth Set the stroke width of the progress spinner in pixels.
             */
            public void setStrokeWidth(float strokeWidth) {
                mStrokeWidth = strokeWidth;
                mArcPaint.setStrokeWidth(strokeWidth);
                invalidateSelf();
            }


            public float getStartTrim() {
                return mStartTrim;
            }


            public void setStartTrim(float startTrim) {
                mStartTrim = startTrim;
                invalidateSelf();
            }

            public float getStartingStartTrim() {
                return mStartingStartTrim;
            }

            public float getStartingEndTrim() {
                return mStartingEndTrim;
            }


            public float getEndTrim() {
                return mEndTrim;
            }


            public void setEndTrim(float endTrim) {
                mEndTrim = endTrim;
                invalidateSelf();
            }

            public void setRotation(float rotation) {
                mRotation = rotation;
                invalidateSelf();
            }

            public void setInsets(int width, int height) {
                final float minEdge = (float) Math.min(width, height);
                float insets;
                if (mRingCenterRadius <= 0 || minEdge < 0) {
                    insets = (float) Math.ceil(mStrokeWidth / 2.0f);
                } else {
                    insets = (float) (minEdge / 2.0f - mRingCenterRadius);
                }
                mStrokeInset = insets;
            }


            public float getInsets() {
                return mStrokeInset;
            }

            public double getCenterRadius() {
                return mRingCenterRadius;
            }

            /**
             * @param centerRadius Inner radius in px of the circle the progress
             *                     spinner arc traces.
             */
            public void setCenterRadius(double centerRadius) {
                mRingCenterRadius = centerRadius;
            }

            /**
             * @param show Set to true to show the arrow head on the progress spinner.
             */
            public void setShowArrow(boolean show) {
                if (mShowArrow != show) {
                    mShowArrow = show;
                    invalidateSelf();
                }
            }

            /**
             * @param scale Set the scale of the arrowhead for the spinner.
             */
            public void setArrowScale(float scale) {
                if (scale != mArrowScale) {
                    mArrowScale = scale;
                    invalidateSelf();
                }
            }

            /**
             * @return The amount the progress spinner is currently rotated, between [0..1].
             */
            public float getStartingRotation() {
                return mStartingRotation;
            }

            /**
             * If the start / end trim are offset to begin with, store them so that
             * animation starts from that offset.
             */
            public void storeOriginals() {
                mStartingStartTrim = mStartTrim;
                mStartingEndTrim = mEndTrim;
                mStartingRotation = mRotation;
            }

            /**
             * Reset the progress spinner to default rotation, start and end angles.
             */
            public void resetOriginals() {
                mStartingStartTrim = 0;
                mStartingEndTrim = 0;
                mStartingRotation = 0;
                setStartTrim(0);
                setEndTrim(0);
                setRotation(0);
            }

            private void invalidateSelf() {
                mRingCallback.invalidateDrawable(null);
            }
        }

        /**
         * Squishes the interpolation curve into the second half of the animation.
         */
        private static class EndCurveInterpolator extends AccelerateDecelerateInterpolator {
            @Override
            public float getInterpolation(float input) {
                return super.getInterpolation(Math.max(0, (input - 0.5f) * 2.0f));
            }
        }

        /**
         * Squishes the interpolation curve into the first half of the animation.
         */
        private static class StartCurveInterpolator extends AccelerateDecelerateInterpolator {
            @Override
            public float getInterpolation(float input) {
                return super.getInterpolation(Math.min(1, input * 2.0f));
            }
        }
    }
}
