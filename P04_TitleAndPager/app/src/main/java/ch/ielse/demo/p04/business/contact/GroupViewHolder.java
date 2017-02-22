package ch.ielse.demo.p04.business.contact;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import ch.ielse.demo.p04.R;


public class GroupViewHolder extends ParentViewHolder {

    private TextView tTitle;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);
        tTitle = (TextView) itemView.findViewById(R.id.t_title);
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
    }

    public void refresh(Group group) {
        tTitle.setText(group.getTitle());
    }
}
