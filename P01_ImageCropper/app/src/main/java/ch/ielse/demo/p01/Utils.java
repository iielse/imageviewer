package ch.ielse.demo.p01;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

public class Utils {
    public static int calcStatusBarHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    public static void handlePermissionRequest(final Activity activity, String deniedPermission) {
        String deniedPermissionName = "一些";
        if (Manifest.permission.ACCESS_COARSE_LOCATION.equalsIgnoreCase(deniedPermission)) {
            deniedPermissionName = "位置信息";
        } else if (Manifest.permission.CAMERA.equalsIgnoreCase(deniedPermission)) {
            deniedPermissionName = "相机";
        } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equalsIgnoreCase(deniedPermission)) {
            deniedPermissionName = "存储空间";
        } else if (Manifest.permission.READ_CONTACTS.equalsIgnoreCase(deniedPermission)) {
            deniedPermissionName = "通讯录";
        } else if (Manifest.permission.CALL_PHONE.equalsIgnoreCase(deniedPermission)) {
            deniedPermissionName = "电话";
        } else if (Manifest.permission.RECORD_AUDIO.equalsIgnoreCase(deniedPermission)) {
            deniedPermissionName = "麦克风";
        }

        new AlertDialog.Builder(activity).setTitle("权限提示")
                .setMessage("我们需要的 " + deniedPermissionName + " 权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则部分功能无法正常使用！")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    public static Observable<String> compressFile(final String originalUrl) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception {
                Tiny.getInstance().source(originalUrl).asFile().compress(new FileCallback() {
                    @Override
                    public void callback(boolean isSuccess, String outfile) {
                        if (isSuccess) {
                            e.onNext(outfile);
                            e.onComplete();
                        } else {
                            e.onError(new Throwable("picture file compress failure [" + outfile + "]"));
                        }
                    }
                });
            }
        });
    }

    public static  Bitmap transformCropCircle(Bitmap resource) {
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
