package ch.ielse.demo.p01;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import ch.ielse.view.imagecropper.ImageCropper;

public class MainActivity extends Activity implements View.OnClickListener, ImageCropper.Callback {
    private ImageView iAvatar, iBackground;
    private ImageCropper vImageCropper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iAvatar = (ImageView) findViewById(R.id.i_avatar);
        iAvatar.setOnClickListener(this);
        iBackground = (ImageView) findViewById(R.id.i_background);
        iBackground.setOnClickListener(this);
        vImageCropper = (ImageCropper) findViewById(R.id.v_image_cropper);
        vImageCropper.setCallback(this);
    }

    @Override
    public void onClick(View v) {
        if (v == iAvatar) {
            vImageCropper.queryPicture("avatar");
        } else if (v == iBackground) {
            vImageCropper.queryPicture("background");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        vImageCropper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        vImageCropper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPictureQueryOut(String path, String tag) {
        if ("avatar".equals(tag)) {
            vImageCropper.crop(path, 100, 100, true, tag);
        } else if ("background".equals(tag)) {
            vImageCropper.crop(path, 240, 150, false, tag);
        }
    }

    @Override
    public void onPictureCropOut(Bitmap bitmap, String tag) {
        if ("avatar".equals(tag)) {
            iAvatar.setImageBitmap(transformCropCircle(bitmap));
        } else if ("background".equals(tag)) {
            iBackground.setImageBitmap(bitmap);
        }
    }

    private Bitmap transformCropCircle(Bitmap resource) {
        Bitmap source = resource;
        int size = Math.min(source.getWidth(), source.getHeight());

        int width = (source.getWidth() - size) / 2;
        int height = (source.getHeight() - size) / 2;

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader =
                new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if (width != 0 || height != 0) {
            // source isn't square, move viewport to center
            Matrix matrix = new Matrix();
            matrix.setTranslate(-width, -height);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return bitmap;
    }
}
