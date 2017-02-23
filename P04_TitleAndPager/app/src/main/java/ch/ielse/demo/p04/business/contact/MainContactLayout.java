package ch.ielse.demo.p04.business.contact;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import ch.ielse.demo.p04.R;
import ch.ielse.demo.p04.utils.Logger;
import ch.ielse.demo.p04.view.refresh.WaterRefreshHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class MainContactLayout extends LinearLayout {
    private PtrFrameLayout lPullToRefresh;
    private RecyclerView vRecycler;
    private ContactAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private StickyGroupLayout lStickyGroup;

    public MainContactLayout(Context context) {
        this(context, null);
    }

    public MainContactLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_contact, this);

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
        vRecycler.setAdapter(mAdapter = new ContactAdapter(Datas.getGroupList()));


        mAdapter.notifyDataSetChanged();



        vRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int current = layoutManager.findFirstVisibleItemPosition();
                final int cViewType = mAdapter.getItemViewType(current);

                String log = "current[" + current + "]   cViewType[" + cViewType + "]";
                if (cViewType == ExpandableRecyclerAdapter.TYPE_PARENT) {
                    cGroup = mAdapter.getItemByPosition(current).getParent();
                    cParent = layoutManager.findViewByPosition(current);
//                    int offset = 0;
//                    if (cGroup.isHasTopSpace()) {
//                        offset = cParent.findViewById(R.id.v_top_space).getHeight();
//                    }
                    if (cParent.getTop() < 0) {
                        lStickyGroup.setVisibility(View.VISIBLE);
                        lStickyGroup.push(cGroup);
                        lStickyGroup.setY(0);
                    } else {
                        lStickyGroup.pop(cGroup);
                    }

                    log += " cParent.getTop [" + cParent.getTop() + "]";
                }

                final int next = current + 1;
                if (mAdapter.getItemCount() > next) {
                    final int nViewType = mAdapter.getItemViewType(next);
                    if (nViewType == ExpandableRecyclerAdapter.TYPE_PARENT) {
                        nGroup = mAdapter.getItemByPosition(next).getParent();
                        nParent = layoutManager.findViewByPosition(next);

                        // if (cGroup != null) lStickyGroup.pop(nGroup);
                        if (nParent.getTop() < lStickyGroup.getHeight()) {
                            lStickyGroup.pop(nGroup);
                            lStickyGroup.setY(nParent.getTop() - lStickyGroup.getHeight());
                        }


//                        int offset = 0;
//                        if (nGroup.isHasTopSpace()) {
//                            offset = nParent.findViewById(R.id.v_top_space).getHeight();
//                        }
                        log += " nParent.getTop [" + nParent.getTop() + "]";

                    }
                }

                Logger.e(log);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }
        });
        lStickyGroup = (StickyGroupLayout) findViewById(R.id.l_sticky_group);
        lStickyGroup.setAlpha(0.8f);
    }
    Group cGroup;
    View cParent;
    Group nGroup;
    View nParent;
    // private View s;
}
