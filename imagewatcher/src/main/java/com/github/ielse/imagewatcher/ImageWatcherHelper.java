package com.github.ielse.imagewatcher;

import android.app.Activity;
import android.net.Uri;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;


public class ImageWatcherHelper {
    private static final int VIEW_IMAGE_WATCHER_ID = R.id.view_image_watcher;
    private final Activity holder;
    private final ViewGroup activityDecorView;
    private ImageWatcher mImageWatcher;
    private ImageWatcher.Loader loader;
    private Integer statusBarHeight;
    private Integer resErrorImage;
    private ImageWatcher.OnPictureLongPressListener listener;
    private ImageWatcher.IndexProvider indexProvider;
    private ImageWatcher.LoadingUIProvider loadingUIProvider;
    private ImageWatcher.OnStateChangedListener onStateChangedListener;

    private ImageWatcherHelper(Activity activity) {
        holder = activity;
        activityDecorView = (ViewGroup) activity.getWindow().getDecorView();
    }

    public static ImageWatcherHelper with(Activity activity, ImageWatcher.Loader l) { // attach
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

    public ImageWatcherHelper setIndexProvider(ImageWatcher.IndexProvider ip) {
        indexProvider = ip;
        return this;
    }

    public ImageWatcherHelper setLoadingUIProvider(ImageWatcher.LoadingUIProvider lp) {
        loadingUIProvider = lp;
        return this;
    }

    public ImageWatcherHelper setOnStateChangedListener(ImageWatcher.OnStateChangedListener listener) {
        onStateChangedListener = listener;
        return this;
    }

    public void show(ImageView i, SparseArray<ImageView> imageGroupList, final List<Uri> urlList) {
        init();
        mImageWatcher.show(i, imageGroupList, urlList);
    }

    private void init() {
        mImageWatcher = new ImageWatcher(holder);
        mImageWatcher.setId(VIEW_IMAGE_WATCHER_ID);
        mImageWatcher.setLoader(loader);
        mImageWatcher.setDetachAffirmative(true); // helper
        if (statusBarHeight != null) mImageWatcher.setTranslucentStatus(statusBarHeight);
        if (resErrorImage != null) mImageWatcher.setErrorImageRes(resErrorImage);
        if (listener != null) mImageWatcher.setOnPictureLongPressListener(listener);
        if (indexProvider != null) mImageWatcher.setIndexProvider(indexProvider);
        if (loadingUIProvider != null) mImageWatcher.setLoadingUIProvider(loadingUIProvider);
        if (onStateChangedListener != null)
            mImageWatcher.setOnStateChangedListener(onStateChangedListener);

        removeExistingOverlayInView(activityDecorView); // 理论上是无意义的操作。在ImageWatcher 'dismiss' 时会移除自身。但检查一下不错
        activityDecorView.addView(mImageWatcher);
    }

    public boolean handleBackPressed() {
        return mImageWatcher != null && mImageWatcher.handleBackPressed();
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
