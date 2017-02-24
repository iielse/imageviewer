package ch.ielse.demo.p04.business.contact;

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

/**
 * Created by LY on 2017/2/24.
 */

public class ContactTitleLayout extends RelativeLayout implements TitleLayout.TitleComponent {
    private ImageView iAvatar;
    private TextView tTitle;
    private TextView tAdd;

    public ContactTitleLayout(Context context) {
        this(context, null);
    }

    public ContactTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_title_main_contact, this);
        iAvatar = (ImageView) findViewById(R.id.i_avatar);
        tTitle = (TextView) findViewById(R.id.t_title);
        tAdd = (TextView) findViewById(R.id.t_add);

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
        return tAdd;
    }

    @Override
    public void dealTranslucentStatusTheme(int statusBarHeight) {
        setPadding(0, statusBarHeight, 0, 0);
    }
}
