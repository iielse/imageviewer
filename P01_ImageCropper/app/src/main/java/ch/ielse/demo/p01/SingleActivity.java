package ch.ielse.demo.p01;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;

import ch.ielse.view.imagecropper.RxImagePicker;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SingleActivity extends RxAppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private View tMulti;
    private MessagePicturesLayout lMulti;
    private ImageView iAvatar, iBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        (iAvatar = (ImageView) findViewById(R.id.i_avatar)).setOnClickListener(this);
        (iBackground = (ImageView) findViewById(R.id.i_background)).setOnClickListener(this);
        (tMulti = findViewById(R.id.t_multi)).setOnClickListener(this);
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

                        new RxPermissions(SingleActivity.this)
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .flatMap(new Function<Boolean, ObservableSource<Bitmap>>() {
                                    @Override
                                    public ObservableSource<Bitmap> apply(@NonNull Boolean isGranted) throws Exception {
                                        if (!isGranted) {
                                            Utils.handlePermissionRequest(SingleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                            return io.reactivex.Observable.empty();
                                        }
                                        return new RxImagePicker(SingleActivity.this)
                                                .setTranslucentStatusHeight(Utils.calcStatusBarHeight(SingleActivity.this))
                                                .queryAlbum(iTarget == iAvatar ? "设置头像" : "设置背景",
                                                        iTarget.getWidth(), iTarget.getHeight(), iTarget == iAvatar);
                                    }
                                })
                                .compose(SingleActivity.this.<Bitmap>bindUntilEvent(ActivityEvent.DESTROY))
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
                })
                .addMenu("拍一张", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

//                        new RxPermissions(SingleActivity.this)
//                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                                .flatMap(new Function<Boolean, ObservableSource<Bitmap>>() {
//                                    @Override
//                                    public ObservableSource<Bitmap> apply(@NonNull Boolean isGranted) throws Exception {
//                                        if (!isGranted) {
//                                            Utils.handlePermissionRequest(SingleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                                            return io.reactivex.Observable.empty();
//                                        }
//                                        return new RxImagePicker(SingleActivity.this).setTranslucentStatusHeight(Utils.calcStatusBarHeight(SingleActivity.this))
//                                                .takeCamera(iTarget == iAvatar ? "设置头像" : "设置背景",
//                                                        iTarget.getWidth(), iTarget.getHeight(), iTarget == iAvatar);
//                                    }
//                                })
//                        .compose(SingleActivity.this.<Bitmap>bindUntilEvent(ActivityEvent.DESTROY))
//                                .subscribe(new Consumer<Bitmap>() {
//                                    @Override
//                                    public void accept(@NonNull Bitmap output) throws Exception {
//                                        if (iTarget == iAvatar) {
//                                            output = Utils.transformCropCircle(output);
//                                        }
//                                        iTarget.setImageBitmap(output);
//                                    }
//                                });


                        new RxPermissions(SingleActivity.this)
                                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .flatMap(new Function<Boolean, ObservableSource<String>>() {
                                    @Override
                                    public ObservableSource<String> apply(@NonNull Boolean isGranted) throws Exception {
                                        if (!isGranted) {
                                            Utils.handlePermissionRequest(SingleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                            return io.reactivex.Observable.empty();
                                        }
                                        return new RxImagePicker(SingleActivity.this).setTranslucentStatusHeight(Utils.calcStatusBarHeight(SingleActivity.this))
                                                .takeCamera();
                                    }
                                })
                                .compose(SingleActivity.this.<String>bindUntilEvent(ActivityEvent.DESTROY))
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(@NonNull String output) throws Exception {
                                        DrawableTypeRequest request = Glide.with(SingleActivity.this).load(output);
                                        if (iTarget == iAvatar) {
                                            request.bitmapTransform(new CropCircleTransformation(SingleActivity.this));
                                        }
                                        request.into(iTarget);
                                    }
                                });
                    }
                }).create().show();
    }


    private void queryMultiPicture() {
        new RxImagePicker(SingleActivity.this)
                .queryMulti(9)
                .compose(SingleActivity.this.<List<String>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> pictureUrls) throws Exception {

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
}
