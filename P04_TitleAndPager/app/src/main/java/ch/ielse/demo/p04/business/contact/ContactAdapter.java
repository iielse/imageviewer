package ch.ielse.demo.p04.business.contact;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import ch.ielse.demo.p04.R;

public class ContactAdapter extends ExpandableRecyclerAdapter<Group, Friend, GroupViewHolder, FriendViewHolder> {

    public ContactAdapter(@NonNull List<Group> parentList) {
        super(parentList);
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        return new GroupViewHolder(LayoutInflater.from(parentViewGroup.getContext())
                .inflate(R.layout.recycler_main_contact_group, parentViewGroup, false));
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        return new FriendViewHolder(LayoutInflater.from(childViewGroup.getContext())
                .inflate(R.layout.recycler_main_contact_friend, childViewGroup, false));
    }

    @Override
    public void onBindParentViewHolder(@NonNull GroupViewHolder parentViewHolder, int parentPosition, @NonNull Group parent) {
        parentViewHolder.refresh(parent);
    }

    @Override
    public void onBindChildViewHolder(@NonNull FriendViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Friend child) {
        childViewHolder.refresh(child);
    }
}
