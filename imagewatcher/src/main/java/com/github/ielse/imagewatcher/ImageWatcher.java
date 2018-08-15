package com.github.ielse.imagewatcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

public class ImageWatcher extends FrameLayout implements GestureDetector.OnGestureListener, ViewPager.OnPageChangeListener {
    private static final int SINGLE_TAP_UP_CONFIRMED = 1;
    private static final int DATA_INITIAL = 2;
    public static final int STATE_ENTER_DISPLAYING = 3;
    public static final int STATE_EXIT_HIDING = 4;
    private final Handler mHandler;
    protected final float MIN_SCALE = 0.5f; // 显示中的图片最小缩小系数
    protected final float MAX_SCALE = 3.6f; // 显示中的图片最大放大系数
    protected float scaleSensitivity = 0.3f;// 缩放灵敏度 越小越灵敏
    protected float edgeResilience = 0.16f; // 边缘阻尼弹性 越小越难拉动

    protected static final int TOUCH_MODE_NONE = 0; // 无状态
    protected static final int TOUCH_MODE_DOWN = 1; // 按下
    protected static final int TOUCH_MODE_DRAG = 2; // 单点拖拽
    protected static final int TOUCH_MODE_SLIDE = 4; // 页面滑动
    protected static final int TOUCH_MODE_SCALE = 5; // 缩放
    protected static final int TOUCH_MODE_SCALE_LOCK = 6; // 缩放锁定
    protected static final int TOUCH_MODE_AUTO_FLING = 7; // 动画中
    protected static final int TOUCH_MODE_EXIT = 3; // 退出动作

    private ImageView iSource;

    protected int mErrorImageRes = R.mipmap.error_picture; // 图片加载失败站位图
    protected int mStatusBarHeight; // 状态栏高度
    private int mWidth, mHeight;
    private int mBackgroundColor = 0x00000000;
    private int mTouchMode = TOUCH_MODE_NONE;
    private final float mTouchSlop;

    private float mFingersDistance;
    private float mFingersCenterX;
    private float mFingersCenterY;
    private float mExitRef; // 触摸退出进度

    private ValueAnimator animFling;
    private ValueAnimator animBackground;
    private ValueAnimator animImageTransform;
    private boolean isInTransformAnimation;
    private final GestureDetector mGestureDetector;

    private boolean isInitLayout = false;
    protected ImageView initI;
    protected SparseArray<ImageView> initImageGroupList;
    protected List<Uri> initUrlList;

    private OnPictureLongPressListener pictureLongPressListener; // 图片长按回调
    private ImagePagerAdapter adapter;
    private final ViewPager vPager;
    protected SparseArray<ImageView> mImageGroupList; // 图片所在的ImageView控件集合，Int类型的Key对应position
    protected List<Uri> mUrlList; // 图片地址列表
    protected int initPosition;
    private int currentPosition;
    private int mPagerPositionOffsetPixels;
    private Loader loader;
    private OnStateChangedListener stateChangedListener;
    private IndexProvider indexProvider;
    private View idxView;
    private LoadingUIProvider loadingUIProvider;

    private boolean detachAffirmative; // dismiss detach parent
    private boolean detachedParent;

    public ImageWatcher(Context context) {
        this(context, null);
    }

    public ImageWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new RefHandler(this);
        mGestureDetector = new GestureDetector(context, this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        addView(vPager = new ViewPager(context));
        vPager.addOnPageChangeListener(this);
        setVisibility(View.INVISIBLE);

        setIndexProvider(new DefaultIndexProvider());
        setLoadingUIProvider(new DefaultLoadingUIProvider());
    }

    public void setLoader(Loader l) {
        loader = l;
    }

    public void setDetachAffirmative(boolean detachAffirmative) {
        this.detachAffirmative = detachAffirmative;
    }

    public void setIndexProvider(IndexProvider ip) {
        indexProvider = ip;
        if (indexProvider != null) {
            if (idxView != null) removeView(idxView);
            idxView = indexProvider.initialView(getContext());
            addView(idxView);
        }
    }

    public void setLoadingUIProvider(LoadingUIProvider lp) {
        loadingUIProvider = lp;
    }

    public void setOnStateChangedListener(OnStateChangedListener changedListener) {
        stateChangedListener = changedListener;
    }

    public void setOnPictureLongPressListener(OnPictureLongPressListener listener) {
        pictureLongPressListener = listener;
    }

    public interface Loader {
        void load(Context context, Uri uri, LoadCallback lc);
    }

    public interface LoadCallback {
        void onResourceReady(Drawable resource);

        void onLoadStarted(Drawable placeholder);

        void onLoadFailed(Drawable errorDrawable);
    }

    public interface IndexProvider {
        View initialView(Context context);

        void onPageChanged(ImageWatcher imageWatcher, int position, List<Uri> dataList);
    }

    public class DefaultIndexProvider implements IndexProvider {
        TextView tCurrentIdx;

        @Override
        public View initialView(Context context) {
            tCurrentIdx = new TextView(context);
            LayoutParams lpCurrentIdx = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lpCurrentIdx.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            tCurrentIdx.setLayoutParams(lpCurrentIdx);
            tCurrentIdx.setTextColor(0xFFFFFFFF);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float tCurrentIdxTransY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, displayMetrics) + 0.5f;
            tCurrentIdx.setTranslationY(tCurrentIdxTransY);
            return tCurrentIdx;
        }

        @Override
        public void onPageChanged(ImageWatcher imageWatcher, int position, List<Uri> dataList) {
            if (mUrlList.size() > 1) {
                tCurrentIdx.setVisibility(View.VISIBLE);
                final String idxInfo = (position + 1) + " / " + mUrlList.size();
                tCurrentIdx.setText(idxInfo);
            } else {
                tCurrentIdx.setVisibility(View.GONE);
            }
        }
    }


    public interface LoadingUIProvider {
        View initialView(ViewGroup parent);

        void start(View loadView);

        void stop(View loadView);
    }

    public class DefaultLoadingUIProvider implements LoadingUIProvider {
        final LayoutParams lpCenterInParent = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        @Override
        public View initialView(ViewGroup parent) {
            lpCenterInParent.gravity = Gravity.CENTER;
            ProgressView progressView = new ProgressView(parent.getContext());
            progressView.setLayoutParams(lpCenterInParent);
            return progressView;
        }

        @Override
        public void start(View loadView) {
            loadView.setVisibility(View.VISIBLE);
            ((ProgressView) loadView).start();
        }

        @Override
        public void stop(View loadView) {
            ((ProgressView) loadView).stop();
            loadView.setVisibility(View.GONE);
        }
    }

    public interface OnStateChangedListener {
        void onStateChangeUpdate(ImageWatcher imageWatcher, ImageView clicked, int position, Uri uri, float animatedValue, int actionTag);

        void onStateChanged(ImageWatcher imageWatcher, int position, Uri uri, int actionTag);
    }

    public interface OnPictureLongPressListener {
        /**
         * @param v   当前被按的ImageView
         * @param uri 当前ImageView加载展示的图片地址
         * @param pos 当前ImageView在展示组中的位置
         */
        void onPictureLongPress(ImageView v, Uri uri, int pos); // 当前展示图片长按的回调
    }


    /**
     * 调用show方法前，请先调用setLoader 给ImageWatcher提供加载图片的实现
     *
     * @param i              被点击的ImageView
     * @param imageGroupList 被点击的ImageView的所在列表，加载图片时会提前展示列表中已经下载完成的thumb图片
     * @param urlList        被加载的图片url列表，数量必须大于等于 imageGroupList.size。 且顺序应当和imageGroupList保持一致
     */
    public void show(ImageView i, SparseArray<ImageView> imageGroupList, final List<Uri> urlList) {
        if (loader == null) {
            throw new NullPointerException("please invoke `setLoader` first [loader == null]");
        }

        if (i == null || imageGroupList == null || urlList == null || imageGroupList.size() < 1 ||
                urlList.size() < imageGroupList.size()) {
            String info = "i[" + i + "]";
            info += "#imageGroupList " + (imageGroupList == null ? "null" : "size : " + imageGroupList.size());
            info += "#urlList " + (urlList == null ? "null" : "size :" + urlList.size());
            throw new IllegalArgumentException("error params \n" + info);
        }

        if (i.getDrawable() == null) return;

        if (!isInitLayout) {
            initI = i;
            initImageGroupList = imageGroupList;
            initUrlList = urlList;
            return;
        }

        initPosition = -1;
        for (int x = 0; x < imageGroupList.size(); x++) {
            if (imageGroupList.get(imageGroupList.keyAt(x)) == i) {
                initPosition = imageGroupList.keyAt(x);
                break;
            }
        }
        if (initPosition < 0) {
            throw new IllegalArgumentException("param ImageView i must be a member of the List <ImageView> imageGroupList!");
        }
        currentPosition = initPosition;

        if (animImageTransform != null) animImageTransform.cancel();
        animImageTransform = null;

        mImageGroupList = imageGroupList;
        mUrlList = urlList;
        iSource = null;

        ImageWatcher.this.setVisibility(View.VISIBLE);
        vPager.setAdapter(adapter = new ImagePagerAdapter());
        vPager.setCurrentItem(initPosition);
        if (indexProvider != null) indexProvider.onPageChanged(this, initPosition, mUrlList);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public Uri getDisplayingUri() {
        return mUrlList != null ? mUrlList.get(getCurrentPosition()) : null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (iSource == null) return true;
        if (isInTransformAnimation) return true;
        if (animFling != null) {
            animFling.cancel();
            animFling = null;
            mTouchMode = TOUCH_MODE_DOWN;
        }

        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_UP:
                onUp(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mPagerPositionOffsetPixels == 0) { // 正在查看一张图片，不处于翻页中。
                    if (mTouchMode != TOUCH_MODE_SCALE) {
                        mFingersDistance = 0;
                        mFingersCenterX = 0;
                        mFingersCenterY = 0;
                        ViewState.write(iSource, ViewState.STATE_TOUCH_SCALE);
                    }
                    mTouchMode = TOUCH_MODE_SCALE; // 变化为缩放状态
                } else {
                    dispatchEventToViewPager(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (mPagerPositionOffsetPixels == 0) { // 正在查看一张图片，不处于翻页中。
                    if (event.getPointerCount() - 1 < 1 + 1) {
                        mTouchMode = TOUCH_MODE_SCALE_LOCK;
                        onUp(event); // 结束缩放状态
                    }
                } else {
                    dispatchEventToViewPager(event);
                }
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mTouchMode = TOUCH_MODE_DOWN;
        dispatchEventToViewPager(e);
        return true;
    }

    private void onUp(MotionEvent e) {
        if (mTouchMode == TOUCH_MODE_AUTO_FLING) {
            // do nothing
        } else if (mTouchMode == TOUCH_MODE_SCALE || mTouchMode == TOUCH_MODE_SCALE_LOCK) {
            handleScaleTouchResult();
        } else if (mTouchMode == TOUCH_MODE_EXIT) {
            handleExitTouchResult();
        } else if (mTouchMode == TOUCH_MODE_DRAG) {
            handleDragTouchResult();
        } else if (mTouchMode == TOUCH_MODE_SLIDE) {
            dispatchEventToViewPager(e);
        }
    }

    private void dispatchEventToViewPager(MotionEvent e) {
        try {
            vPager.onTouchEvent(e);
        } catch (Exception ignore) {
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mTouchMode == TOUCH_MODE_DOWN) {
            final float moveX = (e1 != null) ? e2.getX() - e1.getX() : 0;
            final float moveY = (e1 != null) ? e2.getY() - e1.getY() : 0;
            if (Math.abs(moveX) > mTouchSlop || Math.abs(moveY) > mTouchSlop) {
                final ViewState vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
                final ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
                final String imageOrientation = (String) iSource.getTag(R.id.image_orientation);

                if (vsDefault == null) {
                    // 没有vsDefault标志的View说明图标正在下载中。转化为Slide手势，可以进行viewpager的翻页滑动
                    mTouchMode = TOUCH_MODE_SLIDE;
                } else if (Math.abs(moveX) < mTouchSlop && moveY > Math.abs(moveX) * 3 &&
                        vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2 <= iSource.getTranslationY()) {
                    // 手指垂直下拉。 横图未放大or图片放大且显示出了顶端   转化为退出查看图片操作
                    if (mTouchMode != TOUCH_MODE_EXIT) {
                        ViewState.write(iSource, ViewState.STATE_EXIT);
                    }
                    mTouchMode = TOUCH_MODE_EXIT;
                } else if (vsCurrent.scaleY > vsDefault.scaleY || vsCurrent.scaleX > vsDefault.scaleX ||
                        vsCurrent.scaleY * iSource.getHeight() > mHeight) {
                    // 图片当前为放大状态(宽或高超出了屏幕尺寸)or竖图
                    if (mTouchMode != TOUCH_MODE_DRAG) {
                        ViewState.write(iSource, ViewState.STATE_DRAG);
                    }
                    mTouchMode = TOUCH_MODE_DRAG; // 转化为Drag手势，可以对图片进行拖拽操作

                    if ("horizontal".equals(imageOrientation)) {
                        // 图片位于边界，且仍然尝试向边界外拽动。。转化为Slide手势，可以进行viewpager的翻页滑动
                        float translateXEdge = vsDefault.width * (vsCurrent.scaleX - 1) / 2;
                        if (vsCurrent.translationX >= translateXEdge && moveX > 0) {
                            mTouchMode = TOUCH_MODE_SLIDE;
                        } else if (vsCurrent.translationX <= -translateXEdge && moveX < 0) {
                            mTouchMode = TOUCH_MODE_SLIDE;
                        }
                    } else if ("vertical".equals(imageOrientation)) {
                        if (vsDefault.width * vsCurrent.scaleX <= mWidth) {
                            if (Math.abs(moveY) < mTouchSlop && Math.abs(moveX) > mTouchSlop && Math.abs(moveX) > Math.abs(moveY) * 2) {
                                mTouchMode = TOUCH_MODE_SLIDE;
                            }
                        } else {
                            float translateXRightEdge = vsDefault.width * vsCurrent.scaleX / 2 - vsDefault.width / 2;
                            float translateXLeftEdge = mWidth - vsDefault.width * vsCurrent.scaleX / 2 - vsDefault.width / 2;
                            if (vsCurrent.translationX >= translateXRightEdge && moveX > 0) {
                                mTouchMode = TOUCH_MODE_SLIDE;
                            } else if (vsCurrent.translationX <= translateXLeftEdge && moveX < 0) {
                                mTouchMode = TOUCH_MODE_SLIDE;
                            }
                        }
                    }
                } else if (Math.abs(moveX) > mTouchSlop) {
                    mTouchMode = TOUCH_MODE_SLIDE;    // 左右滑动。转化为Slide手势，可以进行viewpager的翻页滑动
                }
            }
        }

        if (mTouchMode == TOUCH_MODE_SLIDE) {
            dispatchEventToViewPager(e2);
        } else if (mTouchMode == TOUCH_MODE_SCALE) {
            handleScaleGesture(e2);
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
            mExitRef = 0;
        } else {
            iSource.setTag(ViewState.STATE_EXIT, vsDefault);
            mExitRef = 1;
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
        if (pictureLongPressListener != null) {
            pictureLongPressListener.onPictureLongPress(iSource, mUrlList.get(vPager.getCurrentItem()), vPager.getCurrentItem());
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (iSource != null && mTouchMode != TOUCH_MODE_AUTO_FLING && mPagerPositionOffsetPixels == 0) {
            final ViewState vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);
            final ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
            if (vsDefault == null) return false;

            if (getCurrentPosition() <= 0 && velocityX > 0 && vsCurrent.translationX == (vsDefault.width * (vsCurrent.scaleX - 1) / 2)) {
                return false; // 第一张手指右划
            } else if (getCurrentPosition() >= mUrlList.size() - 1 && velocityX < 0 && -vsCurrent.translationX == vsDefault.width * (vsCurrent.scaleX - 1) / 2) {
                return false; // 最后一张手指左划
            } else if (e1 != null && e2 != null && (Math.abs(e1.getX() - e2.getX()) > 50 || Math.abs(e1.getY() - e2.getY()) > 50) && (Math.abs(velocityX) > 500 || Math.abs(velocityY) > 500)) {
                // 满足fling手势
                float maxVelocity = Math.max(Math.abs(velocityX), Math.abs(velocityY));
                float endTranslateX = vsCurrent.translationX + (velocityX * 0.2f);
                float endTranslateY = vsCurrent.translationY + (velocityY * 0.2f);
                if (vsCurrent.scaleY * iSource.getHeight() < mHeight) {
                    endTranslateY = vsCurrent.translationY; // 当前状态下判定为 横图(所显示高度不过全屏)
                    maxVelocity = Math.abs(velocityX);
                }
                if (vsCurrent.scaleY * iSource.getHeight() > mHeight && vsCurrent.scaleX == vsDefault.scaleX) {
                    endTranslateX = vsCurrent.translationX; // 当前状态下判定为 竖图(所显示宽度不过全屏)
                    maxVelocity = Math.abs(velocityY);
                }

                final float overflowX = mWidth * 0.02f;
                float translateXEdge = vsDefault.width * (vsCurrent.scaleX - 1) / 2;
                if (endTranslateX > translateXEdge + overflowX)
                    endTranslateX = translateXEdge + overflowX;
                else if (endTranslateX < -translateXEdge - overflowX)
                    endTranslateX = -translateXEdge - overflowX;

                if (vsCurrent.scaleY * iSource.getHeight() > mHeight) {
                    final float overflowY = mHeight * 0.02f;
                    float translateYTopEdge = vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2;
                    float translateYBottomEdge = mHeight - vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2;
                    if (endTranslateY > translateYTopEdge + overflowY) {
                        endTranslateY = translateYTopEdge + overflowY;
                    } else if (endTranslateY < translateYBottomEdge - overflowY) {
                        endTranslateY = translateYBottomEdge - overflowY;
                    }
                }

                animFling(iSource, ViewState.write(iSource, ViewState.STATE_TEMP).translationX(endTranslateX).translationY(endTranslateY), (long) (1000000 / maxVelocity));
                return true;
            }
        }
        return false;
    }

    /**
     * 处理响应退出图片查看
     */
    private void handleExitGesture(MotionEvent e2, MotionEvent e1) {
        if (iSource == null) return;
        ViewState vsExit = ViewState.read(iSource, ViewState.STATE_EXIT);
        ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (vsExit == null || vsDefault == null) return;

        mExitRef = 1;
        final float moveY = e2.getY() - e1.getY();
        final float moveX = e2.getX() - e1.getX();
        if (moveY > 0) mExitRef -= moveY / (mHeight / 2);
        if (mExitRef < 0) mExitRef = 0;
        setBackgroundColor(mColorEvaluator.evaluate(mExitRef, 0x00000000, 0xFF000000));
        final float exitScale = MIN_SCALE + (vsExit.scaleX - MIN_SCALE) * mExitRef;
        iSource.setScaleX(exitScale);
        iSource.setScaleY(exitScale);
        final float exitTrans = vsDefault.translationX + (vsExit.translationX - vsDefault.translationX) * mExitRef;
        iSource.setTranslationX(exitTrans + moveX);
        iSource.setTranslationY(vsExit.translationY + moveY);
    }

    /**
     * 处理响应单手拖拽平移
     */
    private void handleDragGesture(MotionEvent e2, MotionEvent e1) {
        if (iSource == null) return;
        final ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        final ViewState vsTouchDrag = ViewState.read(iSource, ViewState.STATE_DRAG);
        if (vsDefault == null || vsTouchDrag == null) return;
        final float moveY = e2.getY() - e1.getY();
        final float moveX = e2.getX() - e1.getX();
        float translateXValue = vsTouchDrag.translationX + moveX;
        float translateYValue = vsTouchDrag.translationY + moveY;

        final String imageOrientation = (String) iSource.getTag(R.id.image_orientation);
        if ("horizontal".equals(imageOrientation)) {
            float translateXEdge = vsDefault.width * (vsTouchDrag.scaleX - 1) / 2;
            if (translateXValue > translateXEdge) {
                translateXValue = translateXEdge + (translateXValue - translateXEdge) * edgeResilience;
            } else if (translateXValue < -translateXEdge) {
                translateXValue = -translateXEdge + (translateXValue - (-translateXEdge)) * edgeResilience;
            }
            iSource.setTranslationX(translateXValue);
        } else if ("vertical".equals(imageOrientation)) {
            if (vsDefault.width * vsTouchDrag.scaleX <= mWidth) {
                translateXValue = vsTouchDrag.translationX;
            } else {
                float translateXRightEdge = vsDefault.width * vsTouchDrag.scaleX / 2 - vsDefault.width / 2;
                float translateXLeftEdge = mWidth - vsDefault.width * vsTouchDrag.scaleX / 2 - vsDefault.width / 2;

                if (translateXValue > translateXRightEdge) {
                    translateXValue = translateXRightEdge + (translateXValue - translateXRightEdge) * edgeResilience;
                } else if (translateXValue < translateXLeftEdge) {
                    translateXValue = translateXLeftEdge + (translateXValue - translateXLeftEdge) * edgeResilience;
                }
            }
            iSource.setTranslationX(translateXValue);
        }

        if (vsDefault.height * vsTouchDrag.scaleY > mHeight) {
            float translateYTopEdge = vsDefault.height * vsTouchDrag.scaleY / 2 - vsDefault.height / 2;
            float translateYBottomEdge = mHeight - vsDefault.height * vsTouchDrag.scaleY / 2 - vsDefault.height / 2;
            if (translateYValue > translateYTopEdge) {
                translateYValue = translateYTopEdge + (translateYValue - translateYTopEdge) * edgeResilience;
            } else if (translateYValue < translateYBottomEdge) {
                translateYValue = translateYBottomEdge + (translateYValue - translateYBottomEdge) * edgeResilience;
            }
            iSource.setTranslationY(translateYValue);
        }
    }

    /**
     * 处理响应双手拖拽缩放
     */
    private void handleScaleGesture(MotionEvent e2) {
        if (iSource == null) return;
        final ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        final ViewState vsTouchScale = ViewState.read(iSource, ViewState.STATE_TOUCH_SCALE);
        if (vsDefault == null || vsTouchScale == null) return;

        if (e2.getPointerCount() < 2) return;
        final float deltaX = e2.getX(1) - e2.getX(0);
        final float deltaY = e2.getY(1) - e2.getY(0);
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (mFingersDistance == 0) mFingersDistance = distance;
        float changedScale = (mFingersDistance - distance) / (mWidth * scaleSensitivity);
        float scaleResultX = vsTouchScale.scaleX - changedScale;
        if (scaleResultX < MIN_SCALE) scaleResultX = MIN_SCALE;
        else if (scaleResultX > MAX_SCALE) scaleResultX = MAX_SCALE;
        iSource.setScaleX(scaleResultX);
        float scaleResultY = vsTouchScale.scaleY - changedScale;
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
        float changedCenterXValue = vsTouchScale.translationX - changedCenterX;
        float fitTransX = 0; // to do 缩放中心修正~
        iSource.setTranslationX(changedCenterXValue + fitTransX);
        float changedCenterY = mFingersCenterY - centerY;
        float changedCenterYValue = vsTouchScale.translationY - changedCenterY;
        iSource.setTranslationY(changedCenterYValue);
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
     */
    private void handleScaleTouchResult() {
        if (iSource == null) return;
        ViewState vsDefault = ViewState.read(iSource, ViewState.STATE_DEFAULT);
        if (vsDefault == null) return;
        ViewState vsCurrent = ViewState.write(iSource, ViewState.STATE_CURRENT);

        final float endScaleX, endScaleY;
        endScaleX = vsCurrent.scaleX < vsDefault.scaleX ? vsDefault.scaleX : vsCurrent.scaleX;
        endScaleY = vsCurrent.scaleY < vsDefault.scaleY ? vsDefault.scaleY : vsCurrent.scaleY;

        ViewState vsTemp = ViewState.copy(vsDefault, ViewState.STATE_TEMP).scaleX(endScaleX).scaleY(endScaleY);
        if (iSource.getWidth() * vsCurrent.scaleX > mWidth) {
            final float endTranslateX;
            float translateXEdge = vsCurrent.width * (vsCurrent.scaleX - 1) / 2;
            if (vsCurrent.translationX > translateXEdge) endTranslateX = translateXEdge;
            else if (vsCurrent.translationX < -translateXEdge)
                endTranslateX = -translateXEdge;
            else endTranslateX = vsCurrent.translationX;

            vsTemp.translationX(endTranslateX); // 缩放结果X轴比屏幕宽度长
        }
        if (iSource.getHeight() * vsCurrent.scaleY > mHeight) {
            final float endTranslateY;
            float translateYBottomEdge = vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2;
            float translateYTopEdge = mHeight - vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2;

            if (vsCurrent.translationY > translateYBottomEdge)
                endTranslateY = translateYBottomEdge;
            else if (vsCurrent.translationY < translateYTopEdge)
                endTranslateY = translateYTopEdge;
            else endTranslateY = vsCurrent.translationY;

            vsTemp.translationY(endTranslateY);
        }

        iSource.setTag(ViewState.STATE_TEMP, vsTemp);
        animSourceViewStateTransform(iSource, vsTemp);
        animBackgroundTransform(0xFF000000, 0);
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

            float translateYBottomEdge = vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2;
            float translateYTopEdge = mHeight - vsDefault.height * vsCurrent.scaleY / 2 - vsDefault.height / 2;

            if (vsCurrent.translationY > translateYBottomEdge)
                endTranslateY = translateYBottomEdge;
            else if (vsCurrent.translationY < translateYTopEdge)
                endTranslateY = translateYTopEdge;
            else endTranslateY = vsCurrent.translationY;
        } else {
            return;
        }
        if (vsCurrent.translationX == endTranslateX && vsCurrent.translationY == endTranslateY) {
            return;// 如果没有变化跳过动画实行时间的触摸锁定
        }
        animSourceViewStateTransform(iSource,
                ViewState.write(iSource, ViewState.STATE_TEMP).translationX(endTranslateX).translationY(endTranslateY));

        animBackgroundTransform(0xFF000000, 0);
    }

    /**
     * 处理结束下拉退出的手指事件，进行退出图片查看或者恢复到初始状态的收尾动画<br>
     * 还需要还原背景色
     */
    private void handleExitTouchResult() {
        if (iSource == null) return;

        if (mExitRef > 0.75f) {
            ViewState vsExit = ViewState.read(iSource, ViewState.STATE_EXIT);
            if (vsExit != null) animSourceViewStateTransform(iSource, vsExit);
            animBackgroundTransform(0xFF000000, 0);
        } else {
            ViewState vsOrigin = ViewState.read(iSource, ViewState.STATE_ORIGIN);
            if (vsOrigin != null) {
                if (vsOrigin.alpha == 0) {
                    vsOrigin.translationX(iSource.getTranslationX()).translationY(iSource.getTranslationY());
                }
                animSourceViewStateTransform(iSource, vsOrigin);
            }

            animBackgroundTransform(0x00000000, STATE_EXIT_HIDING);
            ((FrameLayout) iSource.getParent()).getChildAt(2).animate().alpha(0).start();
        }
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
        currentPosition = position;

        if (indexProvider != null) indexProvider.onPageChanged(this, position, mUrlList);

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
        private final SparseArray<ImageView> mImageSparseArray = new SparseArray<>();
        private boolean hasPlayBeginAnimation;

        @Override
        public int getCount() {
            return mUrlList != null ? mUrlList.size() : 0;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            mImageSparseArray.remove(position);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            FrameLayout itemView = new FrameLayout(container.getContext());
            container.addView(itemView);
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemView.addView(imageView);
            mImageSparseArray.put(position, imageView);

            View loadView = null;
            if (loadingUIProvider != null) loadView = loadingUIProvider.initialView(container);
            if (loadView == null) {
                loadView = new View(container.getContext()); // 占位;errorView = getChildAt(2)
            }
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
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
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
                View loadView = itemView.getChildAt(1);

                if (loadingUIProvider != null) {
                    if (loading) loadingUIProvider.start(loadView);
                    else loadingUIProvider.stop(loadView);
                }

                ImageView errorView = (ImageView) itemView.getChildAt(2);
                errorView.setAlpha(1f);
                errorView.setVisibility(error ? View.VISIBLE : View.GONE);
            }
        }

        private boolean setDefaultDisplayConfigs(final ImageView imageView, final int pos, boolean hasPlayBeginAnimation) {
            boolean isFindEnterImagePicture = false;

            ViewState.write(imageView, ViewState.STATE_ORIGIN).alpha(0).scaleXBy(1.5f).scaleYBy(1.5f);

            final ImageView originRef = mImageGroupList.get(pos);
            if (pos == initPosition && !hasPlayBeginAnimation) {
                isFindEnterImagePicture = true;
                iSource = imageView;
            }
            if (originRef != null) {
                final int[] location = new int[2];
                originRef.getLocationOnScreen(location);
                imageView.setTranslationX(location[0]);
                int locationYOfFullScreen = location[1];
                locationYOfFullScreen -= mStatusBarHeight;
                imageView.setTranslationY(locationYOfFullScreen);
                imageView.getLayoutParams().width = originRef.getWidth();
                imageView.getLayoutParams().height = originRef.getHeight();

                ViewState.write(imageView, ViewState.STATE_ORIGIN).width(originRef.getWidth()).height(originRef.getHeight());

                final Drawable bmpMirror = originRef.getDrawable();
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
            ViewState.clear(imageView, ViewState.STATE_DEFAULT);

            loader.load(imageView.getContext(), mUrlList.get(pos), new LoadCallback() {
                @Override
                public void onResourceReady(Drawable resource) {
                    final int sourceDefaultWidth, sourceDefaultHeight, sourceDefaultTranslateX, sourceDefaultTranslateY;
                    int resourceImageWidth = resource.getIntrinsicWidth();
                    int resourceImageHeight = resource.getIntrinsicHeight();
                    if (resourceImageWidth * 1f / resourceImageHeight > mWidth * 1f / mHeight) {
                        sourceDefaultWidth = mWidth;
                        sourceDefaultHeight = (int) (sourceDefaultWidth * 1f / resourceImageWidth * resourceImageHeight);
                        sourceDefaultTranslateX = 0;
                        sourceDefaultTranslateY = (mHeight - sourceDefaultHeight) / 2;
                        imageView.setTag(R.id.image_orientation, "horizontal");
                    } else {
                        sourceDefaultWidth = mWidth;
                        sourceDefaultHeight = (int) (sourceDefaultWidth * 1f / resourceImageWidth * resourceImageHeight);
                        sourceDefaultTranslateX = 0;
                        sourceDefaultTranslateY = 0;
                        imageView.setTag(R.id.image_orientation, "vertical");
                    }
                    imageView.setImageDrawable(resource);
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

                    imageView.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {
                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            Drawable displayingDrawable = imageView.getDrawable();
                            if (displayingDrawable instanceof Animatable) {
                                ((Animatable) displayingDrawable).stop();
                            }
                        }
                    });

                    Drawable displayingDrawable = imageView.getDrawable();
                    if (displayingDrawable instanceof Animatable) {
                        ((Animatable) displayingDrawable).start();
                    }
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    notifyItemChangedState(pos, true, false);
                }

                @Override
                public void onLoadFailed(Drawable errorDrawable) {
                    notifyItemChangedState(pos, false, imageView.getDrawable() == null);
                }
            });

            if (isPlayEnterAnimation) {
                animBackgroundTransform(0xFF000000, STATE_ENTER_DISPLAYING);
            }
            return isPlayEnterAnimation;
        }
    }

    private static class RefHandler extends Handler {
        WeakReference<ImageWatcher> mRef;

        RefHandler(ImageWatcher ref) {
            mRef = new WeakReference<>(ref);
        }

        @Override
        public void handleMessage(Message msg) {
            final ImageWatcher holder = mRef.get();
            if (holder != null) {
                switch (msg.what) {
                    case SINGLE_TAP_UP_CONFIRMED:
                        holder.onSingleTapConfirmed();
                        break;
                    case DATA_INITIAL:
                        holder.internalDisplayDataAfterLayout();
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
    private final AnimatorListenerAdapter mAnimTransitionStateListener = new AnimatorListenerAdapter() {
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

    private final TypeEvaluator<Integer> mColorEvaluator = new TypeEvaluator<Integer>() {
        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            float f = accelerateInterpolator.getInterpolation(fraction);
            int startColor = startValue;
            int endColor = endValue;

            int alpha = (int) (Color.alpha(startColor) + f * (Color.alpha(endColor) - Color.alpha(startColor)));
            int red = (int) (Color.red(startColor) + f * (Color.red(endColor) - Color.red(startColor)));
            int green = (int) (Color.green(startColor) + f * (Color.green(endColor) - Color.green(startColor)));
            int blue = (int) (Color.blue(startColor) + f * (Color.blue(endColor) - Color.blue(startColor)));
            return Color.argb(alpha, red, green, blue);
        }
    };

    private final DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private final AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();

    public void setTranslucentStatus(int statusBarHeight) {
        mStatusBarHeight = statusBarHeight;
    }

    public void setErrorImageRes(int resErrorImage) {
        mErrorImageRes = resErrorImage;
    }

    @Override
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        super.setBackgroundColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mWidth = w;
        mHeight = h;

        if (!isInitLayout) {
            isInitLayout = true;
            mHandler.sendEmptyMessage(DATA_INITIAL);
        }
    }

    private void internalDisplayDataAfterLayout() {
        if (initI != null && initImageGroupList != null && initUrlList != null) {
            show(initI, initImageGroupList, initUrlList);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animImageTransform != null) animImageTransform.cancel();
        animImageTransform = null;
        if (animBackground != null) animBackground.cancel();
        animBackground = null;
        if (animFling != null) animFling.cancel();
        animFling = null;
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
        if (detachedParent) return false;
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
    private void animBackgroundTransform(final int colorResult, final int tag) {
        if (colorResult == mBackgroundColor) return;
        if (animBackground != null) animBackground.cancel();
        final int mCurrentBackgroundColor = mBackgroundColor;
        animBackground = ValueAnimator.ofFloat(0, 1).setDuration(300);
        animBackground.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float p = (float) animation.getAnimatedValue();
                setBackgroundColor(mColorEvaluator.evaluate(p, mCurrentBackgroundColor, colorResult));

                if (stateChangedListener != null) {
                    stateChangedListener.onStateChangeUpdate(ImageWatcher.this, iSource, getCurrentPosition(), getDisplayingUri(), p, tag);
                }
            }
        });
        animBackground.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (stateChangedListener != null) {
                    if (tag == STATE_ENTER_DISPLAYING) {
                        stateChangedListener.onStateChanged(ImageWatcher.this, getCurrentPosition(), getDisplayingUri(), tag);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (stateChangedListener != null) {
                    if (tag == STATE_EXIT_HIDING) {
                        stateChangedListener.onStateChanged(ImageWatcher.this, getCurrentPosition(), getDisplayingUri(), tag);
                    }
                }

                if (detachAffirmative && tag == STATE_EXIT_HIDING) {
                    detachedParent = true;
                    ((ViewGroup) getParent()).removeView(ImageWatcher.this);
                }
            }
        });
        animBackground.start();
    }

    private void animFling(ImageView view, final ViewState vsResult, long duration) {
        if (duration > 800) duration = 800;
        else if (duration < 100) duration = 100;
        if (animFling != null) animFling.cancel();
        animFling = ViewState.restoreByAnim(view, vsResult.mTag).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTouchMode = TOUCH_MODE_AUTO_FLING;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTouchMode = TOUCH_MODE_SCALE_LOCK;
                onUp(null);
            }
        }).create();
        animFling.setInterpolator(decelerateInterpolator);
        animFling.setDuration(duration);
        animFling.start();
    }
}
