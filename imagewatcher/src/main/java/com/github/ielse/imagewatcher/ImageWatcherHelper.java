package com.github.ielse.imagewatcher;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class ImageWatcherHelper {
    private static final int VIEW_DECORATION_ID = R.id.view_decoration;
    private static final int VIEW_IMAGE_WATCHER_ID = R.id.view_image_watcher;
    private final FragmentActivity holder;
    private final ViewGroup activityDecorView;
    private ImageWatcher mImageWatcher;
    private ImageWatcher.Loader loader;
    private Integer statusBarHeight;
    private Integer resErrorImage;
    private ImageWatcher.OnPictureLongPressListener listener;
    private ImageWatcher.IndexProvider indexProvider;
    private ImageWatcher.LoadingUIProvider loadingUIProvider;
    private final List<ViewPager.OnPageChangeListener> onPageChangeListeners = new ArrayList<>();
    private final List<ImageWatcher.OnStateChangedListener> onStateChangedListeners = new ArrayList<>();
    private View otherView;


    private ImageWatcherHelper(FragmentActivity activity) {
        holder = activity;
        activityDecorView = (ViewGroup) activity.getWindow().getDecorView();
    }

    public static ImageWatcherHelper with(FragmentActivity activity, ImageWatcher.Loader l) { // attach
        if (activity == null) throw new NullPointerException("activity is null");
        if (l == null) throw new NullPointerException("loader is null");
        ImageWatcherHelper iwh = new ImageWatcherHelper(activity);
        iwh.loader = l;
        return iwh;
    }

    public ImageWatcherHelper setTranslucentStatus(int statusBarHeight) {
        this.statusBarHeight = statusBarHeight;
        return this;
    }

    public ImageWatcherHelper setErrorImageRes(int resErrorImage) {
        this.resErrorImage = resErrorImage;
        return this;
    }

    public ImageWatcherHelper setOnPictureLongPressListener(ImageWatcher.OnPictureLongPressListener listener) {
        this.listener = listener;
        return this;
    }

    public ImageWatcherHelper addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (!onPageChangeListeners.contains(listener)) {
            onPageChangeListeners.add(listener);
        }
        return this;
    }

    public ImageWatcherHelper setOtherView(View other) {
        otherView = other;
        return this;
    }

    public ImageWatcherHelper setIndexProvider(ImageWatcher.IndexProvider ip) {
        indexProvider = ip;
        return this;
    }

    public ImageWatcherHelper setLoadingUIProvider(ImageWatcher.LoadingUIProvider lp) {
        loadingUIProvider = lp;
        return this;
    }

    @Deprecated
    public ImageWatcherHelper setOnStateChangedListener(ImageWatcher.OnStateChangedListener listener) {
        return addOnStateChangedListener(listener);
    }

    public ImageWatcherHelper addOnStateChangedListener(ImageWatcher.OnStateChangedListener listener) {
        if (!onStateChangedListeners.contains(listener)) {
            onStateChangedListeners.add(listener);
        }
        return this;
    }

    public void show(ImageView i, SparseArray<ImageView> imageGroupList, List<Uri> urlList) {
        init();
        final boolean displaySuccess = mImageWatcher.show(i, imageGroupList, urlList);
        if (displaySuccess) {
            appendOtherView();
        }
    }

    public void show(int initPosition, SparseArray<ImageView> imageGroupList, List<Uri> urlList) {
        init();
        final boolean displaySuccess = mImageWatcher.show(initPosition, imageGroupList, urlList);
        if (displaySuccess) {
            appendOtherView();
        }
    }

    public void show(List<Uri> urlList, int initPos) {
        init();
        mImageWatcher.show(urlList, initPos);
        appendOtherView();
    }

    private void init() {
        mImageWatcher = new ImageWatcher(holder);
        mImageWatcher.setId(VIEW_IMAGE_WATCHER_ID);
        mImageWatcher.setLoader(loader);
        mImageWatcher.setDetachAffirmative(); // helper
        if (statusBarHeight != null) mImageWatcher.setTranslucentStatus(statusBarHeight);
        if (resErrorImage != null) mImageWatcher.setErrorImageRes(resErrorImage);
        if (listener != null) mImageWatcher.setOnPictureLongPressListener(listener);
        if (indexProvider != null) mImageWatcher.setIndexProvider(indexProvider);
        if (loadingUIProvider != null) mImageWatcher.setLoadingUIProvider(loadingUIProvider);
        if (!onStateChangedListeners.isEmpty()) {
            for (ImageWatcher.OnStateChangedListener onStateChangedListener : onStateChangedListeners) {
                mImageWatcher.addOnStateChangedListener(onStateChangedListener);
            }
        }
        if (!onPageChangeListeners.isEmpty()) {
            for (ViewPager.OnPageChangeListener onPageChangeListener : onPageChangeListeners) {
                mImageWatcher.addOnPageChangeListener(onPageChangeListener);
            }
        }

        mImageWatcher.addOnStateChangedListener(new ImageWatcher.OnStateChangedListener() {
            @Override
            public void onStateChangeUpdate(ImageWatcher imageWatcher, ImageView clicked, int position, Uri uri, float animatedValue, int actionTag) {

            }

            @Override
            public void onStateChanged(ImageWatcher imageWatcher, int position, Uri uri, int actionTag) {
                if (actionTag == ImageWatcher.STATE_ENTER_DISPLAYING) {
                    addToBackStack(holder, ImageWatcherHelper.this);
                } else if (actionTag == ImageWatcher.STATE_EXIT_HIDING) {
                    removeFromBackStack(holder);
                }
            }
        });


        removeExistingOverlayInView(activityDecorView, mImageWatcher.getId()); // 理论上是无意义的操作。在ImageWatcher 'dismiss' 时会移除自身。但检查一下不错
        activityDecorView.addView(mImageWatcher);

    }

    public ImageWatcher getImageWatcher() {
        if (mImageWatcher == null) {
            Log.i("ImageWatcherHelper", "please invoke 'show' first");
        }
        return mImageWatcher;
    }

    public boolean handleBackPressed() {
        return mImageWatcher != null && mImageWatcher.handleBackPressed();
    }

    private void removeExistingOverlayInView(ViewGroup parent, int viewId) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getId() == viewId) {
                parent.removeView(child);
            }

            if (child instanceof ViewGroup) {
                removeExistingOverlayInView((ViewGroup) child, viewId);
            }
        }
    }

    private void appendOtherView() {
        if (otherView != null) {
            if (otherView.getId() == View.NO_ID) otherView.setId(VIEW_DECORATION_ID);
            removeExistingOverlayInView(activityDecorView, otherView.getId());
            activityDecorView.addView(otherView);
            mImageWatcher.addOnStateChangedListener(new ImageWatcher.OnStateChangedListener() {
                @Override
                public void onStateChangeUpdate(ImageWatcher imageWatcher, ImageView clicked, int position, Uri uri, float animatedValue, int actionTag) {
                }

                @Override
                public void onStateChanged(ImageWatcher imageWatcher, int position, Uri uri, int actionTag) {
                    if (actionTag == ImageWatcher.STATE_EXIT_HIDING) {
                        if (otherView != null) {
                            if (otherView.getParent() != null) {
                                ((ViewGroup) otherView.getParent()).removeView(otherView);
                            }
                        }
                    }
                }
            });
        }
    }

    private void addToBackStack(final FragmentActivity activity, final ImageWatcherHelper helper) {
        activity.getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
            }
        });

        final BackPressedFragment backPressedFragment = new BackPressedFragment();
        backPressedFragment.cb = new Runnable() {
            @Override
            public void run() {
                if (handleBackPressed()) {
                    addToBackStack(activity, helper);
                }
            }
        };

        activity.getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, backPressedFragment)
                .addToBackStack("back")
                .commitAllowingStateLoss();
    }

    private void removeFromBackStack(FragmentActivity activity) {
        activity.getSupportFragmentManager().popBackStack("back", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public interface Provider {
        ImageWatcherHelper iwHelper();
    }

    public static class BackPressedFragment extends Fragment {
        Runnable cb;

        @Override
        public void onDetach() {
            if (cb != null) cb.run();
            super.onDetach();
        }
    }
}
