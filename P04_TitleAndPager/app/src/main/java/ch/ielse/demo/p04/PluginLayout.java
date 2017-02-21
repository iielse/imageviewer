package ch.ielse.demo.p04;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import ch.ielse.demo.p04.utils.Logger;
import ch.ielse.view.refresh.WaterRefreshHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by LY on 2017/2/20.
 */

public class PluginLayout extends LinearLayout {
    private PtrFrameLayout lPullToRefresh;

    public PluginLayout(Context context) {
        this(context, null);
    }

    public PluginLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_plugin, this);

        lPullToRefresh = (PtrFrameLayout) findViewById(R.id.l_pull_to_refresh);

        final WaterRefreshHeader header = new WaterRefreshHeader(context);
        lPullToRefresh.setHeaderView(header);
        lPullToRefresh.addPtrUIHandler(header);

        lPullToRefresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                lPullToRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        header.refreshComplete(true, lPullToRefresh);
                    }
                }, 1500);
            }
        });
    }
}
