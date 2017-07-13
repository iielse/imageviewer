package ch.ielse.demo.p01;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.ielse.view.imagecropper.RxImagePicker;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class MainActivity extends RxAppCompatActivity implements View.OnClickListener {


    private View tMulti;
    private MessagePicturesLayout lMulti;
    private ImageView iAvatar, iBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        (iAvatar = (ImageView) findViewById(R.id.i_avatar)).setOnClickListener(this);
        (iBackground = (ImageView) findViewById(R.id.i_background)).setOnClickListener(this);
        (tMulti = findViewById(R.id.t_multi)).setOnClickListener(this);
        lMulti = (MessagePicturesLayout) findViewById(R.id.l_multi);
    }

    @Override
    public void onClick(View v) {
        if (v == iAvatar || v == iBackground) {
            queryOrTakePicture((ImageView) v);
        } else if (v == tMulti) {
            queryMultiPicture();
        }
    }

    private void queryOrTakePicture(final ImageView iTarget) {
        new SheetDialog.Builder(iTarget.getContext()).setTitle("更换图片")
                .addMenu("从手机相册选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        requestPermissions(Collections.singletonList(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                , new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@NonNull Boolean isAllGranted) throws Exception {
                                        if (isAllGranted) queryAlbum(iTarget);
                                    }
                                });
                    }
                })
                .addMenu("拍一张", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                        requestPermissions(Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                                , new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@NonNull Boolean isAllGranted) throws Exception {
                                        if (isAllGranted) takeCamera(iTarget);
                                    }
                                });
                    }
                }).create().show();
    }

    private void queryAlbum(final ImageView iTarget) {
        new RxImagePicker(MainActivity.this).setTranslucentStatusHeight(Utils.calcStatusBarHeight(MainActivity.this))
                .queryAlbum(iTarget == iAvatar ? "设置头像" : "设置背景",
                        iTarget.getWidth(), iTarget.getHeight(), iTarget == iAvatar)
                .compose(MainActivity.this.<Bitmap>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(@NonNull Bitmap output) throws Exception {
                        if (iTarget == iAvatar) {
                            output = Utils.transformCropCircle(output);
                        }
                        iTarget.setImageBitmap(output);
                    }
                });
    }


    private void takeCamera(final ImageView iTarget) {
        new RxImagePicker(MainActivity.this).setTranslucentStatusHeight(Utils.calcStatusBarHeight(MainActivity.this))
                .takeCamera(iTarget == iAvatar ? "设置头像" : "设置背景",
                        iTarget.getWidth(), iTarget.getHeight(), iTarget == iAvatar)
                .compose(MainActivity.this.<Bitmap>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(@NonNull Bitmap output) throws Exception {
                        if (iTarget == iAvatar) {
                            output = Utils.transformCropCircle(output);
                        }
                        iTarget.setImageBitmap(output);
                    }
                });
    }

    private void queryMultiPicture() {
        new RxImagePicker(MainActivity.this)
                .queryMulti(9)
                .compose(MainActivity.this.<List<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> pictureUrls) throws Exception {
                        lMulti.set(pictureUrls, pictureUrls);
                    }
                });
    }


//    try {
//        File cacheFolder = Utils.getDiskCacheDir(getActivity(), "rxPicker");
//        File output = File.createTempFile("TEMP", ".jpg", cacheFolder);
//        boolean cropResult = Utils.saveBitmapToTargetFile(output, bitmap);
//        if (cropResult) {
//            subject.onNext(output.getAbsolutePath());
//            subject.onComplete();
//        }
//    } catch (Exception e) {
//        e.printStackTrace();
//        onResultFailure(SUBJECT_CAMERA, e);
//    }


    void requestPermissions(List<String> permissions, Consumer<Boolean> requestResult) {
        new RxPermissions(this)
                .requestEach(permissions.toArray(new String[permissions.size()]))
                .all(new Predicate<Permission>() {
                    @Override
                    public boolean test(@NonNull Permission permission) throws Exception {
                        return Utils.handlePermissionRequestResult(MainActivity.this, permission);
                    }
                })
                .compose(this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(requestResult);
    }
}
