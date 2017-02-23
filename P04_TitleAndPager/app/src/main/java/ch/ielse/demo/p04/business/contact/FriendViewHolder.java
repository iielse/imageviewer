package ch.ielse.demo.p04.business.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bumptech.glide.Glide;

import java.util.List;

import ch.ielse.demo.p04.App;
import ch.ielse.demo.p04.R;


public class FriendViewHolder extends ChildViewHolder implements View.OnClickListener {

    private ImageView iAvatar;
    private TextView tNickname;
    private TextView tState;
    private TextView tMotto;
    private View vDivideLine;

    public FriendViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_main_contact_friend, parent, false));
        iAvatar = (ImageView) itemView.findViewById(R.id.i_avatar);
        tNickname = (TextView) itemView.findViewById(R.id.t_nickname);
        tState = (TextView) itemView.findViewById(R.id.t_state);
        tMotto = (TextView) itemView.findViewById(R.id.t_motto);
        vDivideLine = itemView.findViewById(R.id.v_divide_line);

        itemView.setOnClickListener(this);
    }

    public void refresh(Friend friend) {
        tNickname.setText(friend.getNickname());
        tState.setText(friend.getState());
        tMotto.setText(friend.getMotto());
        Glide.with(iAvatar.getContext()).load(friend.getAvatar())
                .placeholder(R.drawable.default_avatar).bitmapTransform(App.i().getCropCircleTransformation())
                .into(iAvatar);

        final List<Friend> sameGroupFriends = friend.getGroup().getChildList();
        final boolean isTheOnlyOrLast = (sameGroupFriends == null || sameGroupFriends.size() < 2
                || sameGroupFriends.indexOf(friend) >= sameGroupFriends.size() - 1);
        vDivideLine.setVisibility(isTheOnlyOrLast ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
    }
}
