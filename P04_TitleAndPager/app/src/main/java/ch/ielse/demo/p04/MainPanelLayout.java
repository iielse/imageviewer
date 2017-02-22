package ch.ielse.demo.p04;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ch.ielse.demo.p04.business.contact.MainContactLayout;

public class MainPanelLayout extends LinearLayout implements MainPanelMenuLayout.Callback {

    private MainPanelMenuLayout lMainPanelMenu;
    private FrameLayout lMainContent;

    public MainPanelLayout(Context context) {
        this(context, null);
    }

    public MainPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_main_panel, this);

        (lMainPanelMenu = (MainPanelMenuLayout) findViewById(R.id.l_main_panel_menu)).addCallback(this);

        lMainContent = (FrameLayout) findViewById(R.id.l_main_content);
        lMainContent.addView(new MainContactLayout(context));
    }

    @Override
    public void onPanelMenuClick(String which) {
    }
}
