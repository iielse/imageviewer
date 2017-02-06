package ch.ielse.demo.p01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener, ImageHelper.Callback {
    private ImageHelper mImageHelper;

    private Button bOpenCamera, bOpenPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bOpenPhoto = (Button) findViewById(R.id.b_open_photo);
        bOpenCamera = (Button) findViewById(R.id.b_open_camera);

        bOpenPhoto.setOnClickListener(this);
        bOpenCamera.setOnClickListener(this);

        mImageHelper = new ImageHelper(this);
    }

    @Override
    public void onClick(View v) {
        if (v == bOpenCamera) {

        } else if (v == bOpenPhoto) {
            mImageHelper.openPhotos();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPictureQueryOut(String path) {

    }
}
