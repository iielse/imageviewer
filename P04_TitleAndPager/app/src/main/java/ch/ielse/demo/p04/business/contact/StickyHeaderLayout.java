package ch.ielse.demo.p04.business.contact;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.model.ExpandableWrapper;

import java.util.List;

import ch.ielse.demo.p04.R;

public class StickyHeaderLayout extends LinearLayout implements View.OnClickListener {

    private ExpandableWrapper<Group, Friend> mInstead;
    private ContactAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    private ViewPropertyAnimator animExpansionToggled;
    private TextView tTitle;
    private ImageView iGroup;
    private int mTopMargin;

    public StickyHeaderLayout(Context context) {
        this(context, null);
    }

    public StickyHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.recycler_main_contact_group, this);
        tTitle = (TextView) findViewById(R.id.t_title);
        iGroup = (ImageView) findViewById(R.id.i_group);

        setOnClickListener(this);

        mTopMargin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22.5f, getResources().getDisplayMetrics()) + 0.5f);
    }

    @Override
    public void onClick(View view) {
        if (mInstead.isExpanded()) {
            adapter.collapseParent(mInstead.getParent());
        } else {
            adapter.expandParent(mInstead.getParent());
        }

        if (animExpansionToggled != null) animExpansionToggled.cancel();
        animExpansionToggled = iGroup.animate().rotation(mInstead.isExpanded() ? 180 : 90).setDuration(200);
        animExpansionToggled.start();

        int scrollToFlatPosition = adapter.getFlatItemList().indexOf(mInstead);
        recyclerView.scrollToPosition(scrollToFlatPosition);
        recyclerView.scrollBy(0, mInstead.getParent().isHasTopSpace() ? mTopMargin : 0);

        invalidate();
    }

    public void refresh(ExpandableWrapper<Group, Friend> item) {
        tTitle.setText(item.getParent().getTitle());

        if (animExpansionToggled != null) animExpansionToggled.cancel();
        iGroup.setRotation(item.isExpanded() ? 180 : 90);

        mInstead = item;
    }


    public void attach(RecyclerView view) {
        recyclerView = view;
        layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        adapter = (ContactAdapter) recyclerView.getAdapter();
    }

    public void invalidate() {
        final int currentFlatPosition = layoutManager.findFirstVisibleItemPosition();
        if (currentFlatPosition < 0) {
            setVisibility(View.GONE);
            return;
        }

        final List<ExpandableWrapper<Group, Friend>> flatItemList = adapter.getFlatItemList();
        final ExpandableWrapper<Group, Friend> currentFlatItem = flatItemList.get(currentFlatPosition);
        final int nearParentPosition = adapter.getNearestParentPosition(currentFlatPosition);
        final View currentViewInRecycler = layoutManager.findViewByPosition(currentFlatPosition);

        if (nearParentPosition < 0 || (nearParentPosition == 0 && currentFlatItem.isParent() && currentViewInRecycler.getTop() > 0)) {
            setVisibility(View.GONE);
            return;
        }

        setVisibility(View.VISIBLE);

        final int nextFlatPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        if (nextFlatPosition < 0) return;

        final ExpandableWrapper<Group, Friend> nextFlatItem = flatItemList.get(nextFlatPosition);
        boolean positionHandled = false;
        if (nextFlatItem.isParent()) {
            final View nextParentView = layoutManager.findViewByPosition(nextFlatPosition);
            if (nextParentView.getTop() < getHeight()) {
                setY(nextParentView.getTop() - getHeight());
                positionHandled = true;
            }
        }
        if (!positionHandled && currentFlatItem.isParent()) {
            if (currentViewInRecycler.getTop() > 0) {
                setY(currentViewInRecycler.getTop() - getHeight());
                positionHandled = true;
            }
        }
        if (!positionHandled) {
            setY(0);
        }

        final ExpandableWrapper<Group, Friend> nearParent = adapter.getParentWrapper(nearParentPosition);
        if (nearParent != null) refresh(nearParent);
    }
}
