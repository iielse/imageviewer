package ch.ielse.demo.p02;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.ielse.imagewatcher.ImageWatcher;


public class CustomLoadingUIProvider2 implements ImageWatcher.LoadingUIProvider {
    private final FrameLayout.LayoutParams lpCenterInParent = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private final Handler mHandler = new Handler();

    private Runnable runDelayDisplay;

    @Override
    public View initialView(Context context) {
        ImageView load = new ImageView(context);
        lpCenterInParent.gravity = Gravity.CENTER;
        load.setLayoutParams(lpCenterInParent);
        load.setImageResource(R.mipmap.loading);
        return load;
    }

    @Override
    public void start(final View loadView) {
        if (runDelayDisplay != null) mHandler.removeCallbacks(runDelayDisplay);
        runDelayDisplay = new Runnable() {
            @Override
            public void run() {
                loadView.setVisibility(View.VISIBLE);
            }
        };
        mHandler.postDelayed(runDelayDisplay, 500);
    }

    @Override
    public void stop(View loadView) {
        if (runDelayDisplay != null) mHandler.removeCallbacks(runDelayDisplay);
        runDelayDisplay = null;
        loadView.setVisibility(View.GONE);
    }
}
