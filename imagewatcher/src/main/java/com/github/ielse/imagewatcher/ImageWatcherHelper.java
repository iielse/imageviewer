package com.github.ielse.imagewatcher;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;


public class ImageWatcherHelper {
    private static final int VIEW_IMAGE_WATCHER_ID = R.id.view_image_watcher;
    private final ViewGroup activityDecorView;
    private final ImageWatcher mImageWatcher;

    private ImageWatcherHelper(Activity activity) {
        mImageWatcher = new ImageWatcher(activity);
        mImageWatcher.setId(VIEW_IMAGE_WATCHER_ID);
        activityDecorView = (ViewGroup) activity.getWindow().getDecorView();
    }

    public static ImageWatcherHelper with(Activity activity) {
        return new ImageWatcherHelper(activity);
    }

    public ImageWatcherHelper setLoader(ImageWatcher.Loader l) {
        mImageWatcher.setLoader(l);
        return this;
    }

    public ImageWatcherHelper setTranslucentStatus(int statusBarHeight) {
        mImageWatcher.setTranslucentStatus(statusBarHeight);
        return this;
    }

    public ImageWatcherHelper setErrorImageRes(int resErrorImage) {
        mImageWatcher.setErrorImageRes(resErrorImage);
        return this;
    }

    public ImageWatcherHelper setOnPictureLongPressListener(ImageWatcher.OnPictureLongPressListener listener) {
        mImageWatcher.setOnPictureLongPressListener(listener);
        return this;
    }

    public ImageWatcherHelper setIndexProvider(ImageWatcher.IndexProvider ip) {
        mImageWatcher.setIndexProvider(ip);
        return this;
    }

    public ImageWatcherHelper setLoadingUIProvider(ImageWatcher.LoadingUIProvider lp) {
        mImageWatcher.setLoadingUIProvider(lp);
        return this;
    }

    public ImageWatcherHelper setOnStateChangedListener(ImageWatcher.OnStateChangedListener listener) {
        mImageWatcher.setOnStateChangedListener(listener);
        return this;
    }

    public ImageWatcher create() {
        removeExistingOverlayInView(activityDecorView);
        activityDecorView.addView(mImageWatcher);
        return mImageWatcher;
    }

    private void removeExistingOverlayInView(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getId() == VIEW_IMAGE_WATCHER_ID) {
                parent.removeView(child);
            }
            if (child instanceof ViewGroup) {
                removeExistingOverlayInView((ViewGroup) child);
            }
        }
    }
}
