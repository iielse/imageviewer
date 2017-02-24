package ch.ielse.demo.p04.business.contact;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import ch.ielse.demo.p04.R;


public class GroupViewHolder extends ParentViewHolder {
    private int mViewType;
    private ViewPropertyAnimator animExpansionToggled;

    private TextView tTitle;
    private ImageView iGroup;

    private int mTopMargin;

    public GroupViewHolder(ViewGroup parent, int viewType) {
        super(initialByViewType(parent, viewType));
        mViewType = viewType;
        switch (mViewType) {
            case ExpandableRecyclerAdapter.TYPE_PARENT:
                tTitle = (TextView) itemView.findViewById(R.id.t_title);
                iGroup = (ImageView) itemView.findViewById(R.id.i_group);
                break;
        }

        mTopMargin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22.5f,
                parent.getResources().getDisplayMetrics()) + 0.5f);
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        switch (mViewType) {
            case ExpandableRecyclerAdapter.TYPE_PARENT:
                if (animExpansionToggled != null) animExpansionToggled.cancel();
                animExpansionToggled = iGroup.animate().rotation(expanded ? 90 : 180).setDuration(200);
                animExpansionToggled.start();
                break;
        }
    }

    public void refresh(Group group) {
        switch (mViewType) {
            case ExpandableRecyclerAdapter.TYPE_PARENT:
                tTitle.setText(group.getTitle());
                iGroup.setRotation(isExpanded() ? 180 : 90);
                ((ViewGroup.MarginLayoutParams) itemView.getLayoutParams()).topMargin = group.isHasTopSpace() ? mTopMargin : 0;
                break;
        }
    }

    private static View initialByViewType(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ExpandableRecyclerAdapter.TYPE_PARENT:
                return LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main_contact_group, parent, false);
            case ContactAdapter.TYPE_HEADER:
                return LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main_contact_header, parent, false);
            default:
                throw new IllegalArgumentException("unhandled viewType " + viewType);
        }
    }
}
