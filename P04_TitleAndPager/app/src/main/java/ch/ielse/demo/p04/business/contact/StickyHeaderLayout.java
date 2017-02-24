package ch.ielse.demo.p04.business.contact;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
import ch.ielse.demo.p04.utils.Logger;

public class StickyHeaderLayout extends LinearLayout implements View.OnClickListener {
    private ViewPropertyAnimator animExpansionToggled;

    private TextView tTitle;
    private ImageView iGroup;

    private int topSpacing;

    public StickyHeaderLayout(Context context) {
        this(context, null);
    }

    public StickyHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.recycler_main_contact_group, this);
        tTitle = (TextView) findViewById(R.id.t_title);
        tTitle.setTextColor(Color.RED);
        tTitle.setText("StickyTitle");
        iGroup = (ImageView) findViewById(R.id.i_group);

        setOnClickListener(this);


        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        topSpacing = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22.5f, mDisplayMetrics) + 0.5f);
    }

    @Override
    public void onClick(View view) {

        if (animExpansionToggled != null) animExpansionToggled.cancel();


        if (mInstead.isExpanded()) {
            mAdapter.collapseParent(mInstead.getParent());
        } else {
            mAdapter.expandParent(mInstead.getParent());
        }


        animExpansionToggled = iGroup.animate().rotation(mInstead.isExpanded() ? 180 : 90).setDuration(200);
        animExpansionToggled.start();

        int scrollToFlatPosition = mAdapter.getFlatItemList().indexOf(mInstead);
        Logger.e("AAA clickSticky scrollToFlatPosition " + scrollToFlatPosition);
        recyclerView.scrollToPosition(scrollToFlatPosition);


        recyclerView.scrollBy(0 , mInstead.getParent().isHasTopSpace() ? topSpacing : 0);
//        recyclerView.scrollToPosition(scrollToFlatPosition);
        setY(0);

//        invalidate();
    }

    public void refresh(ExpandableWrapper<Group, Friend> item) {
        tTitle.setText(item.getParent().getTitle());

        if (animExpansionToggled != null) animExpansionToggled.cancel();
        iGroup.setRotation(item.isExpanded() ? 180 : 90);


        mInstead = item;

    }

    private ExpandableWrapper<Group, Friend> mInstead;
    private ContactAdapter mAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;

    public void attach(ContactAdapter adapter, RecyclerView recyclerView) {
        mAdapter = adapter;
        this.recyclerView = recyclerView;
        layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
    }


    public void invalidate() {
        final int currentFlatPosition = layoutManager.findFirstVisibleItemPosition();
        if (currentFlatPosition < 0) return;

        final View cView = layoutManager.findViewByPosition(currentFlatPosition);
        final int cViewType = mAdapter.getItemViewType(currentFlatPosition);

        // final int FirstComplete = layoutManager.findFirstCompletelyVisibleItemPosition();
        final int nearParentPosition = mAdapter.getNearestParentPosition(currentFlatPosition);

        Logger.e("AAA current[" + currentFlatPosition + "][" + cViewType + "] nearParentPosition" + nearParentPosition);

        final List<ExpandableWrapper<Group, Friend>> flatItemList = mAdapter.getFlatItemList();
        final ExpandableWrapper<Group, Friend> flatItem = flatItemList.get(currentFlatPosition);
        // if (flatItem.isParent())

        if (nearParentPosition >= 0) {
            if (nearParentPosition == 0 && flatItem.isParent()) {
                if (cView.getTop() < 0) {
                    Logger.e("AAA   setVisibility(View.VISIBLE);");
                    setVisibility(View.VISIBLE);
                    setY(0);
                } else {
                    Logger.e("AAA   setVisibility(View.GONE);");
                    setVisibility(View.GONE);
                }
            } else {
                Logger.e("AAA   setVisibility(View.VISIBLE);");
                setVisibility(View.VISIBLE);
            }
        } else {
            Logger.e("AAA   setVisibility(View.GONE);");
            setVisibility(View.GONE);
        }

        final int nextFlatPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        if (nextFlatPosition < 0) return;
        final ExpandableWrapper<Group, Friend> nextFlatItem = flatItemList.get(nextFlatPosition);
        boolean Handled = false;
        if (nextFlatItem.isParent()) {
            final View nextParentView = layoutManager.findViewByPosition(nextFlatPosition);
            if (nextParentView.getTop() < getHeight()) {
                setY(nextParentView.getTop() - getHeight());
                Handled = true;
            }
        }
        if (!Handled && flatItem.isParent()) {
            Logger.e("AAA cView getTop[" + cView.getTop());
            if (cView.getTop() > 0) {
                setY(cView.getTop() - getHeight());
                Handled = true;
            }
        }
        if (!Handled) {
            setY(0);
        }

        final ExpandableWrapper<Group, Friend> nearParent = mAdapter.getParentByParentPosition(nearParentPosition);

        if (nearParent != null) refresh(nearParent);

    }
}
