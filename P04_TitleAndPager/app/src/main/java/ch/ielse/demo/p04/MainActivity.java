package ch.ielse.demo.p04;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ch.ielse.demo.p04.business.contact.ContactTitleLayout;
import ch.ielse.view.stack.PageLayout;
import ch.ielse.view.stack.TitleLayout;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    private TitleLayout lTitle;

    private PageLayout lStackPage;
    private MainPanelLayout lMainPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.BLACK);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(0x33000000);
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                if (drawerView.getTag().equals("LEFT")) {
                    mContent.setTranslationX(drawerView.getMeasuredWidth() * slideOffset);
                }
            }

            @Override
            public void onDrawerOpened(android.view.View drawerView) {
            }

            @Override
            public void onDrawerClosed(android.view.View drawerView) {
            }
        });


        lTitle = (TitleLayout) findViewById(R.id.l_title);
        lStackPage = (PageLayout) findViewById(R.id.l_stack_page);
        lStackPage.put(lMainPanel = new MainPanelLayout(this).attach(lTitle));
    }

}
