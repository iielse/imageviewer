package ch.ielse.demo.p04;

import android.app.Application;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by LY on 2017/2/22.
 */

public class App extends Application {
    private static App INSTANCE;

    private CropCircleTransformation cropCircleTransformation;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    public static synchronized App i() {
        return INSTANCE;
    }

    public CropCircleTransformation getCropCircleTransformation() {
        if (cropCircleTransformation == null) {
            cropCircleTransformation = new CropCircleTransformation(this);
        }
        return cropCircleTransformation;
    }
}
