package ch.ielse.view.imagecropper;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ImageCropper extends FrameLayout implements GestureDetector.OnGestureListener, View.OnClickListener {
    static final String TAG = "ImageCropper";
    static final String IMAGE_FROM_CONTENT = "content";
    static final String IMAGE_FROM_FILE = "file";
    static final int REQUEST_CODE_CAPTURE_PICTURE = 412;
    static final int REQUEST_CODE_PICK_IMAGE = 413;
    static final int CODE_FOR_CAMERA_PERMISSION = 102;
    static final int CODE_FOR_ALBUM_PERMISSION = 103;
    private File fCapture;


    private boolean isMultiTouch = false;
    private View iBack;
    private View iSubmit;
    private ImageView iSource;
    private OverlayView vOverlay;
    private int mOutputWidth;
    private int mOutputHeight;
    private String mTag;
    private float mLastScale;
    private float mLastTranslateX;
    private float mLastTranslateY;
    private float mLastFingersDistance;
    private float mLastFingersCenterX;
    private float mLastFingersCenterY;
    private final GestureDetector mGestureDetector;
    private Callback mCallback;
    private ViewPropertyAnimator animRestore;
    private float initMinScale;
    private Bitmap bmpSource;

    public ImageCropper(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mGestureDetector = new GestureDetector(context, this);
        LayoutInflater.from(context).inflate(R.layout.layout_image_cropper, this);
        iBack = findViewById(R.id.i_back);
        iBack.setOnClickListener(this);
        iSubmit = findViewById(R.id.i_submit);
        iSubmit.setOnClickListener(this);
        iSource = (ImageView) findViewById(R.id.i_source);
        vOverlay = (OverlayView) findViewById(R.id.v_overlay);
        setBackgroundColor(Color.BLACK);
        setVisibility(View.INVISIBLE);
    }

    public void crop(String sourceFilePath, int outputWidth, int outputHeight, boolean isCircleOverlay, String tag) {
        final int mWidth = getWidth();
        final int mHeight = getHeight();
        if (mWidth * mHeight == 0) return;
        setVisibility(View.VISIBLE);
        mTag = tag;
        mOutputWidth = outputWidth;
        mOutputHeight = outputHeight;

        vOverlay.reset(mOutputWidth, mOutputHeight, isCircleOverlay);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourceFilePath, options);
        Log.e("TTT", "AAA  BitmapFactory.decodeFile w " + options.outWidth + "#h " + options.outHeight);
        options.inSampleSize = Utils.calculateInSampleSize(options, mWidth, mHeight);
        options.inJustDecodeBounds = false;
        bmpSource = BitmapFactory.decodeFile(sourceFilePath, options);
        Log.e("TTT", "AAA  bmpSource width" + bmpSource.getWidth());

        if (1f * bmpSource.getWidth() / bmpSource.getHeight() > 1f * iSource.getWidth() / iSource.getHeight()) {
            Log.e("TTT", "AAA  横图");
            int bitmapHeightAfterFitCenter = (int) (1f * bmpSource.getHeight() * iSource.getWidth() / bmpSource.getWidth());
            bmpSource = Bitmap.createScaledBitmap(bmpSource, iSource.getWidth(), bitmapHeightAfterFitCenter, true);
            float initMinScaleX = 1f * vOverlay.getOverlayWidth() / iSource.getWidth();
            float initMinScaleY = 1f * vOverlay.getOverlayHeight() / bitmapHeightAfterFitCenter;
            initMinScale = Math.max(initMinScaleX, initMinScaleY);
        } else {
            Log.e("TTT", "AAA  竖图");
            int bitmapWidthAfterFitCenter = (int) (1f * bmpSource.getWidth() * iSource.getHeight() / bmpSource.getHeight());
            bmpSource = Bitmap.createScaledBitmap(bmpSource, bitmapWidthAfterFitCenter, iSource.getHeight(), true);

            float initMinScaleX = 1f * vOverlay.getOverlayWidth() / bitmapWidthAfterFitCenter;
            float initMinScaleY = 1f * vOverlay.getOverlayHeight() / iSource.getHeight();
            initMinScale = Math.max(initMinScaleX, initMinScaleY);
            Log.e("TTT", "AAA  initMinScale " + initMinScale + "##initMinScaleX " + initMinScaleX + "##initMinScaleY " + initMinScaleY + "##vOverlay.getOverlayWidth()" + vOverlay.getOverlayWidth()
                    + "###bitmapWidthAfterFitCenter " + bitmapWidthAfterFitCenter);
            Log.e("TTT", "AAA  mWidth" + mWidth + "##iSourceWidth:" + iSource.getWidth());
        }

        final float defaultScale = initMinScale > 1 ? initMinScale : 1;
        iSource.setTranslationX(0);
        iSource.setTranslationY(0);
        iSource.setScaleX(defaultScale);
        iSource.setScaleY(defaultScale);
        iSource.setImageBitmap(bmpSource);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_UP:
                final float startScale = iSource.getScaleX();
                final float endScale;
                if (startScale < initMinScale) endScale = initMinScale;
                else endScale = startScale;

                final float edgeTranslateX = (bmpSource.getWidth() * endScale - vOverlay.getOverlayWidth()) / 2;
                final float startTranslateX = iSource.getTranslationX();
                final float endTranslateX;
                if (startTranslateX > edgeTranslateX) endTranslateX = edgeTranslateX;
                else if (startTranslateX < -edgeTranslateX) endTranslateX = -edgeTranslateX;
                else endTranslateX = startTranslateX;

                final float edgeTranslateY = (bmpSource.getHeight() * endScale - vOverlay.getOverlayHeight()) / 2;
                final float startTranslateY = iSource.getTranslationY();
                final float endTranslateY;
                if (startTranslateY > edgeTranslateY) endTranslateY = edgeTranslateY;
                else if (startTranslateY < -edgeTranslateY) endTranslateY = -edgeTranslateY;
                else endTranslateY = startTranslateY;

                if (endScale == startScale && endTranslateY == startTranslateY && endTranslateX == startTranslateX)
                    break;

                if (animRestore != null) animRestore.cancel();
                animRestore = iSource.animate().scaleX(endScale).scaleY(endScale).translationX(endTranslateX)
                        .translationY(endTranslateY);
                animRestore.start();

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!isMultiTouch) {
                    isMultiTouch = true;
                    mLastFingersDistance = 0;
                    mLastFingersCenterX = 0;
                    mLastFingersCenterY = 0;
                    mLastScale = iSource.getScaleX();
                    mLastTranslateX = iSource.getTranslationX();
                    mLastTranslateY = iSource.getTranslationY();
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() - 1 < 1 + 1) {
                    isMultiTouch = false;
                }
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e) {
        mLastScale = iSource.getScaleX();
        mLastTranslateX = iSource.getTranslationX();
        mLastTranslateY = iSource.getTranslationY();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isMultiTouch) {
            iSource.setTranslationX(iSource.getTranslationX() - distanceX);
            iSource.setTranslationY(iSource.getTranslationY() - distanceY);
        } else {
            final float deltaX = e2.getX(1) - e2.getX(0);
            final float deltaY = e2.getY(1) - e2.getY(0);
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (mLastFingersDistance == 0) mLastFingersDistance = distance;

            float changedScale = (distance - mLastFingersDistance) / (getWidth() * 0.8f);
            float changedScaleValue = mLastScale + changedScale;

            if (changedScaleValue < initMinScale / 2) changedScaleValue = initMinScale / 2;
            iSource.setScaleX(changedScaleValue);
            iSource.setScaleY(changedScaleValue);

            float centerX = (e2.getX(1) + e2.getX(0)) / 2;
            float centerY = (e2.getY(1) + e2.getY(0)) / 2;
            if (mLastFingersCenterX == 0 && mLastFingersCenterY == 0) {
                mLastFingersCenterX = centerX;
                mLastFingersCenterY = centerY;
            }

            float changedCenterX = centerX - mLastFingersCenterX;
            iSource.setTranslationX(mLastTranslateX + changedCenterX);
            float changedCenterY = centerY - mLastFingersCenterY;
            iSource.setTranslationY(mLastTranslateY + changedCenterY);
        }
        return false;
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (animRestore != null) animRestore.cancel();

        if (v == iSubmit) {
            final float x = ((bmpSource.getWidth() * iSource.getScaleX() - vOverlay.getOverlayWidth()) / 2 - iSource.getTranslationX()) / iSource.getScaleX();
            final float width = vOverlay.getOverlayWidth() / iSource.getScaleX();
            final float y = ((bmpSource.getHeight() * iSource.getScaleY() - vOverlay.getOverlayHeight()) / 2 - iSource.getTranslationY()) / iSource.getScaleY();
            final float height = vOverlay.getOverlayHeight() / iSource.getScaleY();
            Log.e("TTT", "AAA x " + x + "##width " + width + "##y " + y + "##height " + height);
            Log.e("TTT", "AAA bmpSource w " + bmpSource.getWidth() + " ## h " + bmpSource.getHeight());
            Bitmap clip = Bitmap.createBitmap(bmpSource, (int) x, (int) y, (int) width, (int) (height));
            Bitmap output = clip;
            // output = mOutputWidth * mOutputHeight != 0 ? clip.createScaledBitmap(clip, mOutputWidth, mOutputHeight, true) :
            if (mCallback != null) {
                mCallback.onPictureCropOut(output, mTag);
                animate().alpha(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setVisibility(View.GONE);
                        setAlpha(1f);
                    }
                }).start();
            }
        } else if (v == iBack) {
            ((Activity) v.getContext()).onBackPressed();
        }
    }

    public interface Callback {
        void onPictureCropOut(Bitmap bitmap, String tag);

        void onPictureQueryOut(String path, String tag);
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    public void queryPicture(final String tag) {
        mTag = tag;
        new SheetDialog.Builder(getContext()).setTitle("更换图片")
                .addMenu("从手机相册选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openAlbum();
                    }
                })
                .addMenu("拍一张", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openCamera();
                    }
                }).create().show();
    }

    private void openAlbum() {
        if (!PermissionUtils.hasPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODE_FOR_ALBUM_PERMISSION);
            Log.d(TAG, "ActivityCompat.requestPermissions READ_EXTERNAL_STORAGE");
            return;
        }

        Intent intentPick = new Intent(Intent.ACTION_PICK);
        intentPick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        try {
            ((Activity) getContext()).startActivityForResult(intentPick, REQUEST_CODE_PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            Intent intentGetContent = new Intent(Intent.ACTION_GET_CONTENT);
            intentGetContent.setType("image/*");
            Intent wrapperIntent = Intent.createChooser(intentGetContent, null);
            try {
                ((Activity) getContext()).startActivityForResult(wrapperIntent, REQUEST_CODE_PICK_IMAGE);
            } catch (ActivityNotFoundException e1) {
                Toast.makeText(getContext().getApplicationContext(), "没有找到文件浏览器或相册", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        if (!PermissionUtils.hasPermission(getContext(), Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CAMERA},
                    CODE_FOR_CAMERA_PERMISSION);
            Log.d(TAG, "ActivityCompat.requestPermissions CAMERA");
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fCapture = new File(getContext().getExternalCacheDir(), "PIC" + System.currentTimeMillis() + ".jpg");
        try {
            if (!fCapture.exists()) fCapture.createNewFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fCapture));
            ((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE_CAPTURE_PICTURE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(), "图片无法保存", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                if (imageUri != null && IMAGE_FROM_FILE.equals(imageUri.getScheme())) {
                    Log.d(TAG, "openPhotos pick image " + imageUri.getPath());
                    if (mCallback != null) mCallback.onPictureQueryOut(imageUri.getPath(), mTag);
                } else if (imageUri != null && IMAGE_FROM_CONTENT.equals(imageUri.getScheme())) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContext().getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.d(TAG, "openPhotos pick image " + picturePath);
                        if (mCallback != null) mCallback.onPictureQueryOut(picturePath, mTag);
                    } else {
                        Log.e(TAG, "openPhotos pick image cursor null");
                    }
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "图片获取失败，并没有成功拿到图片路径", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, imageUri != null ? ("openPhotos unknown uri\n" + imageUri.getPath()) : "openPhotos imageUri null");
                }
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "openCamera " + fCapture.getAbsolutePath());
                if (mCallback != null)
                    mCallback.onPictureQueryOut(fCapture.getAbsolutePath(), mTag);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult requestCode:" + requestCode);
        for (int i = 0; i < permissions.length; i++) {
            String result = grantResults.length > i ? String.valueOf(grantResults[i]) : "unknown";
            Log.d(TAG, "onRequestPermissionsResult permissions[]:" + permissions[i] + "#grantResults[]:" + result);
        }

        if (requestCode == CODE_FOR_CAMERA_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.CAMERA)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.CAMERA)) {
                showPermissionRationaleDialog("需要赋予访问相机的权限，不开启将无法工作！");
            }
        } else if (requestCode == CODE_FOR_ALBUM_PERMISSION) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionRationaleDialog("需要赋予读取手机文件的权限，不开启将无法工作！");
            }
        }
    }

    private void showPermissionRationaleDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("权限提示").setMessage(message)
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
