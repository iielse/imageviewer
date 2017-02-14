package ch.ielse.view.imagewatcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
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
 * <p>
 * 图片查看器，为各位追求用户体验的daLao提供更优质的服务<br/>
 * <b>它能够</b><br/>
 * 1、点击图片时以一种无缝顺畅的动画切换到图片查看的界面，同样以一种无缝顺畅的动画退出图片查看界面
 * 2、支持多图查看，快速翻页，双击放大，单击退出，双手缩放旋转图片
 * 3、下拽退出查看图片的操作，以及效果是本View的最大卖点(仿微信)
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
    private static final int TOUCH_MODE_LOCK = 6; // 缩放旋转锁定
    private static final int TOUCH_MODE_AUTO_FLING = 7; // 动画中

    private TextView tCurrentIdx;
    private ImageView iSource;
    private ImageView iOrigin;

    private int mErrorImageRes = R.mipmap.error_picture;
    private int mStatusBarHeight;
    private int mWidth, mHeight;
    private int mBackgroundColor = 0x00000000;
    private int mTouchMode = TOUCH_MODE_NONE;
    private float mTouchSlop;

    private float mFingersDistance;
    private double mFingersAngle; // 相对于[东] point0作为起点;point1作为终点
    private float mFingersCenterX;
    private float mFingersCenterY;
    private float mExitScalingRef; // 触摸退出进度

    private ValueAnimator animBackground;
    private ValueAnimator animImageTransform;
    private boolean isInTransformAnimation;
    private final GestureDetector mGestureDetector;

    private OnPictureLongPressListener mPictureLongPressListener;
    private ImagePagerAdapter adapter;
    private ViewPager vPager;
    private List<ImageView> mImageGroupList;
    private List<String> mUrlList;
    private int initPosition;
    private int mPagerPositionOffsetPixels;

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

    /**
     * @param i              被点击的ImageView
     * @param imageGroupList 被点击的ImageView的所在列表，加载图片时会提前展示列表中已经下载完成的thumb图片
     * @param urlList        被加载的图片url列表，数量必须大于等于 imageGroupList.size。 且顺序应当和imageGroupList保持一致
     */
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

        if (animImageTransform != null) animImageTransform.cancel();
        animImageTransform = null;

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
        if (isInTransformAnimation) return true;

        ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);

        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_UP:
                onUp(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (vsDefault != null && (mTouchMode != TOUCH_MODE_SLIDE) || mPagerPositionOffsetPixels == 0) {
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
                if (vsDefault != null && mTouchMode != TOUCH_MODE_SLIDE) {
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
            if (Math.abs(moveX) > mTouchSlop || Math.abs(moveY) > mTouchSlop) {
                ViewState vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
                ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);

                if (vsDefault == null) {
                    // 没有vsDefault标志的View说明图标正在下载中。转化为Slide手势，可以进行viewpager的翻页滑动
                    mTouchMode = TOUCH_MODE_SLIDE;
                } else if (vsCurrent.scaleY > vsDefault.scaleY || vsCurrent.scaleX > vsDefault.scaleX) {
                    // 图片当前为放大状态。宽或高超出了屏幕尺寸
                    if (mTouchMode != TOUCH_MODE_DRAG) {
                        ViewState.write(iSource, ViewState.STATE_DRAG);
                    }
                    // 转化为Drag手势，可以对图片进行拖拽操作
                    mTouchMode = TOUCH_MODE_DRAG;
                    String imageOrientation = (String) iSource.getTag(R.id.image_orientation);
                    if ("horizontal".equals(imageOrientation)) {
                        float translateXEdge = vsDefault.width * (vsCurrent.scaleX - 1) / 2;
                        if (vsCurrent.translationX >= translateXEdge && moveX > 0) {
                            // 图片位于边界，且仍然尝试向边界外拽动。。转化为Slide手势，可以进行viewpager的翻页滑动
                            mTouchMode = TOUCH_MODE_SLIDE;
                        } else if (vsCurrent.translationX <= -translateXEdge && moveX < 0) {
                            // 同上
                            mTouchMode = TOUCH_MODE_SLIDE;
                        }
                    } else if ("vertical".equals(imageOrientation)) {
                        if (vsDefault.width * vsCurrent.scaleX <= mWidth) {
                            // 同上
                            mTouchMode = TOUCH_MODE_SLIDE;
                        } else {
                            float translateXRightEdge = vsDefault.width * vsCurrent.scaleX / 2 - vsDefault.width / 2;
                            float translateXLeftEdge = mWidth - vsDefault.width * vsCurrent.scaleX / 2 - vsDefault.width / 2;
                            if (vsCurrent.translationX >= translateXRightEdge && moveX > 0) {
                                // 同上
                                mTouchMode = TOUCH_MODE_SLIDE;
                            } else if (vsCurrent.translationX <= translateXLeftEdge && moveX < 0) {
                                // 同上
                                mTouchMode = TOUCH_MODE_SLIDE;
                            }
                        }
                    }
                } else if (Math.abs(moveX) < mTouchSlop && moveY > mTouchSlop * 3) {
                    // 单手垂直下拉。转化为Exit手势，可以在下拉过程中看到原始界面;
                    mTouchMode = TOUCH_MODE_EXIT;
                } else if (Math.abs(moveX) > mTouchSlop) {
                    // 左右滑动。转化为Slide手势，可以进行viewpager的翻页滑动
                    mTouchMode = TOUCH_MODE_SLIDE;
                }
            }
        }

        if (mTouchMode == TOUCH_MODE_SLIDE) {
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

    /**
     * 处理单击的手指事件
     */
    public boolean onSingleTapConfirmed() {
        if (iSource == null) return false;
        ViewState vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
        ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (vsDefault == null || (vsCurrent.scaleY <= vsDefault.scaleY && vsCurrent.scaleX <= vsDefault.scaleX)) {
            mExitScalingRef = 0;
        } else {
            mExitScalingRef = 1;
        }
        handleExitTouchResult();
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        boolean hadTapMessage = mHandler.hasMessages(SINGLE_TAP_UP_CONFIRMED);
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
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (mPictureLongPressListener != null) {
            mPictureLongPressListener.onPictureLongPress(iSource, mUrlList.get(vPager.getCurrentItem()), vPager.getCurrentItem());
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    /**
     * 处理响应退出图片查看
     */
    private void handleExitGesture(MotionEvent e2, MotionEvent e1) {
        if (iSource == null) return;
        ViewState vsTouchDown = ViewState.read(iSource, ViewState.STATE_TOUCH_DOWN);
        if (vsTouchDown == null) return;

        mExitScalingRef = 1;
        final float moveY = e2.getY() - e1.getY();
        final float moveX = e2.getX() - e1.getX();
        if (moveY > 0) {
            mExitScalingRef -= moveY / getHeight();
        }
        if (mExitScalingRef < MIN_SCALE) mExitScalingRef = MIN_SCALE;

        iSource.setTranslationX(vsTouchDown.translationX + moveX);
        iSource.setTranslationY(vsTouchDown.translationY + moveY);
        iSource.setScaleX(vsTouchDown.scaleX * mExitScalingRef);
        iSource.setScaleY(vsTouchDown.scaleY * mExitScalingRef);
        setBackgroundColor(mColorEvaluator.evaluate(mExitScalingRef, 0x00000000, 0xFF000000));
    }

    /**
     * 处理响应双手拖拽缩放旋转
     */
    private void handleScaleRotateGesture(MotionEvent e2) {
        if (iSource == null) return;
        final ViewState vsTouchScaleRotate = ViewState.read(iSource, ViewState.STATE_TOUCH_SCALE_ROTATE);
        if (vsTouchScaleRotate == null) return;

        final float deltaX = e2.getX(1) - e2.getX(0);
        final float deltaY = e2.getY(1) - e2.getY(0);
        double angle = Math.toDegrees(Math.atan(deltaX / deltaY));
        if (deltaY < 0) angle = angle + 180;
        if (mFingersAngle == 0) mFingersAngle = angle;

        float changedAngle = (float) (mFingersAngle - angle);
        float changedAngleValue = (vsTouchScaleRotate.rotation + changedAngle) % 360;
        if (changedAngleValue > 180) {
            changedAngleValue = changedAngleValue - 360;
        } else if (changedAngleValue < -180) {
            changedAngleValue = changedAngleValue + 360;
        }
        iSource.setRotation(changedAngleValue);

        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (mFingersDistance == 0) mFingersDistance = distance;

        float changedScale = (mFingersDistance - distance) / (mWidth * 0.8f);
        float scaleResultX = vsTouchScaleRotate.scaleX - changedScale;
        if (scaleResultX < MIN_SCALE) scaleResultX = MIN_SCALE;
        else if (scaleResultX > MAX_SCALE) scaleResultX = MAX_SCALE;
        iSource.setScaleX(scaleResultX);
        float scaleResultY = vsTouchScaleRotate.scaleY - changedScale;
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
        float changedCenterXValue = vsTouchScaleRotate.translationX - changedCenterX;
        if (changedCenterXValue > MAX_TRANSLATE_X) changedCenterXValue = MAX_TRANSLATE_X;
        else if (changedCenterXValue < -MAX_TRANSLATE_X) changedCenterXValue = -MAX_TRANSLATE_X;
        iSource.setTranslationX(changedCenterXValue);

        float changedCenterY = mFingersCenterY - centerY;
        float changedCenterYValue = vsTouchScaleRotate.translationY - changedCenterY;
        if (changedCenterYValue > MAX_TRANSLATE_Y) changedCenterYValue = MAX_TRANSLATE_Y;
        else if (changedCenterYValue < -MAX_TRANSLATE_Y) changedCenterYValue = -MAX_TRANSLATE_Y;
        iSource.setTranslationY(changedCenterYValue);
    }

    /**
     * 处理响应单手拖拽平移
     */
    private void handleDragGesture(MotionEvent e2, MotionEvent e1) {
        if (iSource == null) return;
        final float moveY = e2.getY() - e1.getY();
        final float moveX = e2.getX() - e1.getX();

        ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (vsDefault == null) return;
        ViewState vsTouchDrag = ViewState.read(iSource, ViewState.STATE_DRAG);
        if (vsTouchDrag == null) return;

        float translateXValue = vsTouchDrag.translationX + moveX * 1.6f;

        String imageOrientation = (String) iSource.getTag(R.id.image_orientation);
        if ("horizontal".equals(imageOrientation)) {
            float translateXEdge = vsDefault.width * (vsTouchDrag.scaleX - 1) / 2;
            if (translateXValue > translateXEdge) {
                translateXValue = translateXEdge + (translateXValue - translateXEdge) * 0.12f;
            } else if (translateXValue < -translateXEdge) {
                translateXValue = -translateXEdge + (translateXValue - (-translateXEdge)) * 0.12f;
            }
        } else if ("vertical".equals(imageOrientation)) {
            if (vsDefault.width * vsTouchDrag.scaleX <= mWidth) {
                mTouchMode = TOUCH_MODE_SLIDE;
            } else {
                float translateXRightEdge = vsDefault.width * vsTouchDrag.scaleX / 2 - vsDefault.width / 2;
                float translateXLeftEdge = mWidth - vsDefault.width * vsTouchDrag.scaleX / 2 - vsDefault.width / 2;

                if (translateXValue > translateXRightEdge) {
                    translateXValue = translateXRightEdge + (translateXValue - translateXRightEdge) * 0.12f;
                } else if (translateXValue < translateXLeftEdge) {
                    translateXValue = translateXLeftEdge + (translateXValue - translateXLeftEdge) * 0.12f;
                }
            }
        }
        iSource.setTranslationX(translateXValue);
        iSource.setTranslationY(vsTouchDrag.translationY + moveY * 1.6f);
    }

    /**
     * 处理结束下拉退出的手指事件，进行退出图片查看或者恢复到初始状态的收尾动画<br>
     * 还需要还原背景色
     */
    private void handleExitTouchResult() {
        if (iSource == null) return;

        if (mExitScalingRef > 0.9f) {
            ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
            if (vsDefault == null) return;
            animSourceViewStateTransform(iSource, vsDefault);
            animBackgroundTransform(0xFF000000);
        } else {
            ViewState vsOrigin = ViewState.read(iSource, ViewState.STATE_ORIGIN);
            if (vsOrigin == null) return;
            if (vsOrigin.alpha == 0)
                vsOrigin.translationX(iSource.getTranslationX()).translationY(iSource.getTranslationY());

            animSourceViewStateTransform(iSource, vsOrigin);
            animBackgroundTransform(0x00000000);

            ((FrameLayout) iSource.getParent()).getChildAt(2).animate().alpha(0).start();
        }
    }

    /**
     * 处理结束双击的手指事件，进行图片放大到指定大小或者恢复到初始大小的收尾动画
     */
    private void handleDoubleTapTouchResult() {
        if (iSource == null) return;
        ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (vsDefault == null) return;
        ViewState vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);

        if (vsCurrent.scaleY <= vsDefault.scaleY && vsCurrent.scaleX <= vsDefault.scaleX) {
            final float expectedScale = (MAX_SCALE - vsDefault.scaleX) * 0.4f + vsDefault.scaleX;
            animSourceViewStateTransform(iSource,
                    ViewState.write(iSource, ViewState.STATE_TEMP).scaleX(expectedScale).scaleY(expectedScale));
        } else {
            animSourceViewStateTransform(iSource, vsDefault);
        }
    }

    /**
     * 处理结束缩放旋转模式的手指事件，进行恢复到零旋转角度和大小收缩到正常范围以内的收尾动画<br>
     * 如果是从{@link ImageWatcher#TOUCH_MODE_EXIT}半路转化过来的事件 还需要还原背景色
     */
    private void handleScaleRotateTouchResult() {
        if (iSource == null) return;
        ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (vsDefault == null) return;
        ViewState vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);

        final float endScaleX, endScaleY;
        Log.e("TTT", "AAA  vsCurrent.scaleX :" + vsCurrent.scaleX + "###  vsDefault.scaleX:" + vsDefault.scaleX);
        endScaleX = vsCurrent.scaleX < vsDefault.scaleX ? vsDefault.scaleX : vsCurrent.scaleX;
        endScaleY = vsCurrent.scaleY < vsDefault.scaleY ? vsDefault.scaleY : vsCurrent.scaleY;

        ViewState vsTemp = ViewState.copy(vsDefault, ViewState.STATE_TEMP).scaleX(endScaleX).scaleY(endScaleY);
        iSource.setTag(ViewState.STATE_TEMP, vsTemp);
        animSourceViewStateTransform(iSource, vsTemp);
        animBackgroundTransform(0xFF000000);
    }

    /**
     * 处理结束拖拽模式的手指事件，进行超过边界则恢复到边界的收尾动画
     */
    private void handleDragTouchResult() {
        if (iSource == null) return;
        ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (vsDefault == null) return;
        ViewState vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);

        final float endTranslateX, endTranslateY;
        String imageOrientation = (String) iSource.getTag(R.id.image_orientation);
        if ("horizontal".equals(imageOrientation)) {
            float translateXEdge = vsDefault.width * (vsCurrent.scaleX - 1) / 2;
            if (vsCurrent.translationX > translateXEdge) endTranslateX = translateXEdge;
            else if (vsCurrent.translationX < -translateXEdge)
                endTranslateX = -translateXEdge;
            else endTranslateX = vsCurrent.translationX;

            if (vsDefault.height * vsCurrent.scaleY <= mHeight) {
                endTranslateY = vsDefault.translationY;
            } else {
                float translateYBottomEdge = vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2;
                float translateYTopEdge = mHeight - vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2;

                if (vsCurrent.translationY > translateYBottomEdge)
                    endTranslateY = translateYBottomEdge;
                else if (vsCurrent.translationY < translateYTopEdge)
                    endTranslateY = translateYTopEdge;
                else endTranslateY = vsCurrent.translationY;
            }
        } else if ("vertical".equals(imageOrientation)) {
            float translateYEdge = vsDefault.height * (vsCurrent.scaleY - 1) / 2;
            if (vsCurrent.translationY > translateYEdge) endTranslateY = translateYEdge;
            else if (vsCurrent.translationY < -translateYEdge)
                endTranslateY = -translateYEdge;
            else endTranslateY = vsCurrent.translationY;

            if (vsDefault.width * vsCurrent.scaleX <= mWidth) {
                endTranslateX = vsDefault.translationX;
            } else {
                float translateXRightEdge = vsDefault.width * vsCurrent.scaleX / 2 - vsDefault.width / 2;
                float translateXLeftEdge = mWidth - vsDefault.width * vsCurrent.scaleX / 2 - vsDefault.width / 2;

                if (vsCurrent.translationX > translateXRightEdge)
                    endTranslateX = translateXRightEdge;
                else if (vsCurrent.translationX < translateXLeftEdge)
                    endTranslateX = translateXLeftEdge;
                else endTranslateX = vsCurrent.translationX;
            }
        } else {
            return;
        }
        if (vsCurrent.translationX == endTranslateX && vsCurrent.translationY == endTranslateY) {
            return;// 如果没有变化跳过动画实行时间的触摸锁定
        }
        animSourceViewStateTransform(iSource,
                ViewState.write(iSource, ViewState.STATE_TEMP).translationX(endTranslateX).translationY(endTranslateY));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPagerPositionOffsetPixels = positionOffsetPixels;
    }

    /**
     * 每当ViewPager滑动到新的一页后，此方法会被触发<br/>
     * 此刻必不可少的需要同步更新顶部索引，还原前一项后一项的状态等
     */
    @Override
    public void onPageSelected(int position) {
        iSource = adapter.mImageSparseArray.get(position);
        if (iOrigin != null) {
            iOrigin.setVisibility(View.VISIBLE);
        }
        if (position < mImageGroupList.size()) {
            iOrigin = mImageGroupList.get(position);
            if (iOrigin.getDrawable() != null) iOrigin.setVisibility(View.INVISIBLE);
        }
        refreshCurrentIdx(position);

        ImageView mLast = adapter.mImageSparseArray.get(position - 1);
        if (ViewState.read(mLast, ViewState.STATE_DEFAULT) != null) {
            ViewState.restoreByAnim(mLast, ViewState.STATE_DEFAULT).create().start();
        }
        ImageView mNext = adapter.mImageSparseArray.get(position + 1);
        if (ViewState.read(mNext, ViewState.STATE_DEFAULT) != null) {
            ViewState.restoreByAnim(mNext, ViewState.STATE_DEFAULT).create().start();
        }
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
            ImageView errorView = new ImageView(container.getContext());
            errorView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            errorView.setImageResource(mErrorImageRes);
            itemView.addView(errorView);
            errorView.setVisibility(View.GONE);

            if (setDefaultDisplayConfigs(imageView, position, hasPlayBeginAnimation)) {
                hasPlayBeginAnimation = true;
            }
            return itemView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * 更新ViewPager中每项的当前状态，比如是否加载，比如是否加载失败
         *
         * @param position 当前项的位置
         * @param loading  是否显示加载中
         * @param error    是否显示加载失败
         */
        void notifyItemChangedState(int position, boolean loading, boolean error) {
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

                ImageView errorView = (ImageView) itemView.getChildAt(2);
                errorView.setAlpha(1f);
                errorView.setVisibility(error ? View.VISIBLE : View.GONE);
            }
        }

        private boolean setDefaultDisplayConfigs(final ImageView imageView, final int pos, boolean hasPlayBeginAnimation) {
            boolean isFindEnterImagePicture = false;

            ViewState.write(imageView, ViewState.STATE_ORIGIN).alpha(0).scaleXBy(1.5f).scaleYBy(1.5f);
            if (pos < mImageGroupList.size()) {
                ImageView originRef = mImageGroupList.get(pos);
                if (pos == initPosition && !hasPlayBeginAnimation) {
                    isFindEnterImagePicture = true;
                    iSource = imageView;
                    iOrigin = originRef;
                }

                int[] location = new int[2];
                originRef.getLocationOnScreen(location);
                imageView.setTranslationX(location[0]);
                int locationYOfFullScreen = location[1];
                locationYOfFullScreen -= mStatusBarHeight;
                imageView.setTranslationY(locationYOfFullScreen);
                imageView.getLayoutParams().width = originRef.getWidth();
                imageView.getLayoutParams().height = originRef.getHeight();

                ViewState.write(imageView, ViewState.STATE_ORIGIN).width(originRef.getWidth()).height(originRef.getHeight());

                Drawable bmpMirror = originRef.getDrawable();
                if (bmpMirror != null) {
                    int bmpMirrorWidth = bmpMirror.getBounds().width();
                    int bmpMirrorHeight = bmpMirror.getBounds().height();
                    ViewState vsThumb = ViewState.write(imageView, ViewState.STATE_THUMB).width(bmpMirrorWidth).height(bmpMirrorHeight)
                            .translationX((mWidth - bmpMirrorWidth) / 2).translationY((mHeight - bmpMirrorHeight) / 2);
                    imageView.setImageDrawable(bmpMirror);

                    if (isFindEnterImagePicture) {
                        animSourceViewStateTransform(imageView, vsThumb);
                    } else {
                        ViewState.restore(imageView, vsThumb.mTag);
                    }
                }
            }

            final boolean isPlayEnterAnimation = isFindEnterImagePicture;
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
                    notifyItemChangedState(pos, false, false);

                    ViewState vsDefault = ViewState.write(imageView, ViewState.STATE_DEFAULT).width(sourceDefaultWidth).height(sourceDefaultHeight)
                            .translationX(sourceDefaultTranslateX).translationY(sourceDefaultTranslateY);
                    if (isPlayEnterAnimation) {
                        animSourceViewStateTransform(imageView, vsDefault);
                    } else {
                        ViewState.restore(imageView, vsDefault.mTag);
                        imageView.setAlpha(0f);
                        imageView.animate().alpha(1).start();
                    }
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    notifyItemChangedState(pos, true, false);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    notifyItemChangedState(pos, false, imageView.getDrawable() == null);
                }
            });

            if (isPlayEnterAnimation) {
                iOrigin.setVisibility(View.INVISIBLE);
                animBackgroundTransform(0xFF000000);
            }
            return isPlayEnterAnimation;
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
        return mPagerPositionOffsetPixels == 0;
    }

    /**
     * 动画执行时加入这个监听器后会自动记录标记 {@link ImageWatcher#isInTransformAnimation} 的状态<br/>
     * isInTransformAnimation值为true的时候可以达到在动画执行时屏蔽触摸操作的目的
     */
    final AnimatorListenerAdapter mAnimTransitionStateListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationCancel(Animator animation) {
            isInTransformAnimation = false;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            isInTransformAnimation = true;
            mTouchMode = TOUCH_MODE_AUTO_FLING;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            isInTransformAnimation = false;
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

    public void setErrorImageRes(int resErrorImage) {
        mErrorImageRes = resErrorImage;
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
        if (animImageTransform != null) animImageTransform.cancel();
        animImageTransform = null;
        if (animBackground != null) animBackground.cancel();
        animBackground = null;
    }

    /**
     * 当界面处于图片查看状态需要在Activity中的{@link Activity#onBackPressed()}
     * 将事件传递给ImageWatcher优先处理<br/>
     * 1、当处于收尾动画执行状态时，消费返回键事件<br/>
     * 2、当图片处于放大状态时，执行图片缩放到原始大小的动画，消费返回键事件<br/>
     * 3、当图片处于原始状态时，退出图片查看，消费返回键事件<br/>
     * 4、其他情况，ImageWatcher并没有展示图片
     */
    public boolean handleBackPressed() {
        return isInTransformAnimation || (iSource != null && getVisibility() == View.VISIBLE && onSingleTapConfirmed());
    }

    /**
     * 将指定的ImageView形态(尺寸大小，缩放，旋转，平移，透明度)逐步转化到期望值
     */
    private void animSourceViewStateTransform(ImageView view, final ViewState vsResult) {
        if (view == null) return;
        if (animImageTransform != null) animImageTransform.cancel();

        animImageTransform = ViewState.restoreByAnim(view, vsResult.mTag).addListener(mAnimTransitionStateListener).create();

        if (animImageTransform != null) {
            if (vsResult.mTag == ViewState.STATE_ORIGIN) {
                animImageTransform.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 如果是退出查看操作，动画执行完后，原始被点击的ImageView恢复可见
                        if (iOrigin != null) iOrigin.setVisibility(View.VISIBLE);
                        setVisibility(View.GONE);
                    }
                });
            }
            animImageTransform.start();
        }
    }

    /**
     * 执行ImageWatcher自身的背景色渐变至期望值[colorResult]的动画
     */
    private void animBackgroundTransform(final int colorResult) {
        if (colorResult == mBackgroundColor) return;
        if (animBackground != null) animBackground.cancel();
        final int mCurrentBackgroundColor = mBackgroundColor;
        animBackground = ValueAnimator.ofFloat(0, 1).setDuration(300);
        animBackground.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float) animation.getAnimatedValue();
                setBackgroundColor(mColorEvaluator.evaluate(p, mCurrentBackgroundColor, colorResult));
            }
        });
        animBackground.start();
    }

    /**
     * 当前展示图片长按的回调
     */
    public interface OnPictureLongPressListener {
        /**
         * @param v   当前被按的ImageView
         * @param url 当前ImageView加载展示的图片url地址
         * @param pos 当前ImageView在展示组中的位置
         */
        void onPictureLongPress(ImageView v, String url, int pos);
    }

    public void setOnPictureLongPressListener(OnPictureLongPressListener listener) {
        mPictureLongPressListener = listener;
    }
}
