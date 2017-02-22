package ch.ielse.demo.p04.business.profile;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import ch.ielse.demo.p04.App;
import ch.ielse.demo.p04.MainPanelMenuLayout;
import ch.ielse.demo.p04.R;

public class MainProfileLayout extends LinearLayout implements MainPanelMenuLayout.Callback {

    private ImageView iAvatar;


    public MainProfileLayout(Context context) {
        this(context, null);
    }

    public MainProfileLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_profile, this);

        iAvatar = (ImageView) findViewById(R.id.i_avatar);
        Glide.with(getContext()).load(R.mipmap.avatar).bitmapTransform(App.i().getCropCircleTransformation())
                .into(iAvatar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getParent() instanceof DrawerLayout) {
            int selfExpectedResultWidth = (int) (((DrawerLayout) getParent()).getWidth() / 640f * 510);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(selfExpectedResultWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onPanelMenuClick(String which) {
    }
}
