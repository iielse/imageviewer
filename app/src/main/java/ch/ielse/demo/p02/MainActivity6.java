package ch.ielse.demo.p02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.ielse.imagewatcher.ImageWatcherHelper;

public class MainActivity6 extends AppCompatActivity implements ImageWatcherHelper.Provider {
    private ImageWatcherHelper iwHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);


        Fresco.initialize(this);

        iwHelper = ImageWatcherHelper.with(this, new FrescoSimpleLoader());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.layFragment, new Fragment1())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (!iwHelper.handleBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public ImageWatcherHelper iwHelper() {
        return iwHelper;
    }
}



