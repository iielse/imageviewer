package ch.ielse.demo.p04.business.conversation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ch.ielse.demo.p04.App;
import ch.ielse.demo.p04.R;
import ch.ielse.view.stack.TitleLayout;

public class ConversationTitleLayout extends RelativeLayout implements TitleLayout.TitleComponent {
    private ImageView iAvatar;
    private TextView tTitle;
    private TextView tMore;

    public ConversationTitleLayout(Context context) {
        this(context, null);
    }

    public ConversationTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_title_main_conversation, this);
        iAvatar = (ImageView) findViewById(R.id.i_avatar);
        tTitle = (TextView) findViewById(R.id.t_title);
        tMore = (TextView) findViewById(R.id.t_more);

        setBackgroundColor(0xFF12B7F6);

        Glide.with(iAvatar.getContext()).load(R.mipmap.avatar)
                .bitmapTransform(App.i().getCropCircleTransformation()).into(iAvatar);
    }

    @Override
    public View left() {
        return iAvatar;
    }

    @Override
    public View center() {
        return tTitle;
    }

    @Override
    public View right() {
        return tMore;
    }

    @Override
    public void dealTranslucentStatusTheme(int statusBarHeight) {
        setPadding(0, statusBarHeight, 0, 0);
    }
}
