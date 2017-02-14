package ch.ielse.demo.p01;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import ch.ielse.view.imagecropper.ImageCropper;

public class MainActivity extends Activity implements View.OnClickListener, ImageCropper.Callback {
    private static final String TAG = "MainActivity";

    private PictureInquirer mPictureInquirer;
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

        mPictureInquirer = new PictureInquirer(this);
    }

    @Override
    public void onClick(View v) {
        final String tag;
        if (v == iAvatar) {
            tag = "avatar";
        } else if (v == iBackground) {
            tag = "background";
        } else {
            return;
        }

        new SheetDialog.Builder(v.getContext()).setTitle("更换图片")
                .addMenu("从手机相册选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (!PermissionUtils.hasPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            PermissionUtils.requestPermissions(MainActivity.this, PermissionUtils.PERMISSION_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
                            Log.d(TAG, "ActivityCompat.requestPermissions READ_EXTERNAL_STORAGE");
                            return;
                        }
                        mPictureInquirer.queryPictureFromAlbum(tag);
                    }
                })
                .addMenu("拍一张", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (!PermissionUtils.hasPermission(MainActivity.this, Manifest.permission.CAMERA)) {
                            PermissionUtils.requestPermissions(MainActivity.this, PermissionUtils.PERMISSION_CAMERA, Manifest.permission.CAMERA);
                            Log.d(TAG, "ActivityCompat.requestPermissions CAMERA");
                            return;
                        }
                        mPictureInquirer.queryPictureFromCamera(tag);
                    }
                }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPictureInquirer.onActivityResult(requestCode, resultCode, data, new PictureInquirer.Callback() {
            @Override
            public void onPictureQueryOut(String path, String tag) {
                if ("avatar".equals(tag)) {
                    vImageCropper.crop(path, 100, 100, true, tag);
                } else if ("background".equals(tag)) {
                    vImageCropper.crop(path, 240, 150, false, tag);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionUtils.Callback() {
            @Override
            public void onResult(final int requestCode, List<String> grantPermissions, List<String> deniedPermissions) {
                if (PermissionUtils.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("权限提示").setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                            .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, requestCode);
                                }
                            })
                            .setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                } else {
                    // 一部分权限是暂时拒绝(仍为询问状态)，可以提示权限工作原理来告知用户为什么要此权限。
                    // 来增加下次用户同意该权限申请的可能性
                }
            }
        });
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
