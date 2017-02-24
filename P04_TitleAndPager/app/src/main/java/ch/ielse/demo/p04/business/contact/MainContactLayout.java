package ch.ielse.demo.p04.business.contact;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import ch.ielse.demo.p04.R;
import ch.ielse.demo.p04.view.refresh.WaterRefreshHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class MainContactLayout extends LinearLayout implements View.OnClickListener{
    private PtrFrameLayout lPullToRefresh;
    private RecyclerView vRecycler;
    private ContactAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private StickyHeaderLayout lStickyGroup;

    public MainContactLayout(Context context) {
        this(context, null);
    }

    public MainContactLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_contact, this);
        setOnClickListener(this);

        lPullToRefresh = (PtrFrameLayout) findViewById(R.id.l_pull_to_refresh);
        final WaterRefreshHeader vRefreshHeader = new WaterRefreshHeader(context);
        lPullToRefresh.setHeaderView(vRefreshHeader);
        lPullToRefresh.addPtrUIHandler(vRefreshHeader);
        lPullToRefresh.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return checkContentCanBePulledDown(frame, vRecycler, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                lPullToRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vRefreshHeader.refreshComplete(true, lPullToRefresh);
                    }
                }, 1500);
            }
        });

        vRecycler = (RecyclerView) findViewById(R.id.v_recycler);
        vRecycler.setLayoutManager(layoutManager = new LinearLayoutManager(context));
        vRecycler.setItemAnimator(null);
        vRecycler.setAdapter(mAdapter = new ContactAdapter(Datas.getGroupList()));
        mAdapter.expandAllParents();

        vRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lStickyGroup.invalidate();
            }
        });

        lStickyGroup = (StickyHeaderLayout) findViewById(R.id.l_sticky_group);
        lStickyGroup.attach(vRecycler);
    }

    @Override
    public void onClick(View view) {

    }
}
