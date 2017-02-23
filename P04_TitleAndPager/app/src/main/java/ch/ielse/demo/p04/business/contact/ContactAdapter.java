package ch.ielse.demo.p04.business.contact;


import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.model.ExpandableWrapper;

import java.util.List;

public class ContactAdapter extends ExpandableRecyclerAdapter<Group, Friend, GroupViewHolder, FriendViewHolder> {
    public static final int TYPE_HEADER = 2;

    public ContactAdapter(@NonNull List<Group> parentList) {
        super(parentList);
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        return new GroupViewHolder(parentViewGroup, viewType);
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        return new FriendViewHolder(childViewGroup);
    }

    @Override
    public void onBindParentViewHolder(@NonNull GroupViewHolder parentViewHolder, int parentPosition, @NonNull Group parent) {
        parentViewHolder.refresh(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull FriendViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Friend child) {
        childViewHolder.refresh(child);
    }

    @Override
    public int getParentViewType(int parentPosition) {
        if (mFlatItemList.get(parentPosition).isParent()) {
            if (Group.TYPE_HEADER == mFlatItemList.get(parentPosition).getParent().getType()) {
                return TYPE_HEADER;
            }
        }
        return super.getParentViewType(parentPosition);
    }

    @Override
    public boolean isParentViewType(int viewType) {
        if (viewType == TYPE_HEADER) return true;
        return super.isParentViewType(viewType);
    }

    public ExpandableWrapper<Group, Friend> getItemByPosition(int flatPosition) {
        return mFlatItemList.get(flatPosition);
    }
}
