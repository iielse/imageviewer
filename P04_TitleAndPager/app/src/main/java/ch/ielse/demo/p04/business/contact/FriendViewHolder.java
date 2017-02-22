package ch.ielse.demo.p04.business.contact;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bumptech.glide.Glide;

import ch.ielse.demo.p04.App;
import ch.ielse.demo.p04.R;

/**
 * Created by LY on 2017/2/22.
 */

public class FriendViewHolder extends ChildViewHolder {

    private ImageView iAvatar;
    private TextView tNickname;

    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        iAvatar = (ImageView) itemView.findViewById(R.id.i_avatar);
        tNickname = (TextView) itemView.findViewById(R.id.t_nickname);
    }

    public void refresh(Friend friend) {
        tNickname.setText(friend.getNickname());
        Glide.with(iAvatar.getContext()).load(friend.getAvatar()).bitmapTransform(App.i().getCropCircleTransformation())
                .into(iAvatar);
    }
}
