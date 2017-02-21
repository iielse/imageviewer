package ch.ielse.demo.p04;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ch.ielse.view.stackview.StackPageLayout;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private StackPageLayout lStackPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                final float contentScale = 1 - slideOffset * 0.1f;

                if (drawerView.getTag().equals("LEFT")) {
                    mContent.setTranslationX(drawerView.getMeasuredWidth() * slideOffset);
                    mContent.setScaleY(contentScale);
                }
            }

            @Override
            public void onDrawerOpened(android.view.View drawerView) {
            }

            @Override
            public void onDrawerClosed(android.view.View drawerView) {
            }
        });


        lStackPage = (StackPageLayout) findViewById(R.id.l_stack_page);
        lStackPage.put(new MainPanelLayout(this));
    }

}
