package ch.ielse.demo.p04.business.contact;


import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.model.ExpandableWrapper;

import java.util.List;

import ch.ielse.demo.p04.utils.Logger;

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
        parentViewHolder.refresh(parent, parentPosition);
    }

    @Override
    public void onBindChildViewHolder(@NonNull FriendViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Friend child) {
        childViewHolder.refresh(child, parentPosition, childPosition);
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

    public List<ExpandableWrapper<Group, Friend>> getFlatItemList() {
        return mFlatItemList;
    }

    public void printListInfo() {
        for (int i = 0; i < mFlatItemList.size(); i++) {
            final ExpandableWrapper<Group, Friend> item = mFlatItemList.get(i);
            String info = "";
            if (item.isParent()) info += "[" + i + "]" + item.getParent().getTitle();
            else info += "[" + i + "]" + item.getChild().getNickname();
            Logger.e("AAA " + info);
        }
    }

    @UiThread
    public int getNearestParentPosition(int flatPosition) {
        int parentCount = -1;
        for (int i = 0; i <= flatPosition; i++) {
            ExpandableWrapper<Group, Friend> listItem = mFlatItemList.get(i);
            if (listItem.isParent() && listItem.getParent().getType() == Group.TYPE_NORMAL) {
                parentCount++;
            }
        }
        return parentCount;
    }

    public ExpandableWrapper<Group, Friend> getParentByParentPosition(int parentPosition) {
        int parentCount = - 1;
        for (int i = 0; i < mFlatItemList.size(); i++) {
            ExpandableWrapper<Group, Friend> listItem = mFlatItemList.get(i);
            if (listItem.isParent() && listItem.getParent().getType() == Group.TYPE_NORMAL) {
                parentCount++;
                if( parentCount == parentPosition) {
                    return listItem;
                }
            }
        }
        return null;
    }
}
