package ch.ielse.demo.p01;


import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class ImageHelper {
    static final String TAG = "ImageHelper";
    private static final String IMAGE_FROM_CONTENT = "content";
    private static final String IMAGE_FROM_FILE = "file";
    protected static final int REQUEST_CODE_CAPTURE_PICTURE = 412;
    protected static final int REQUEST_CODE_PICK_IMAGE = 413;
    protected static final int REQUEST_CODE_CROP_IMAGE = 414;
    protected static final int CODE_FOR_CAMERA_PERMISSION = 102;
    protected static final int CODE_FOR_PHOTO_PERMISSION = 103;

    private final Activity mHolder;
    private final Callback mCallback;
    private File fCapturePicture;

    public ImageHelper(Activity holder) {
        if (holder == null || !(holder instanceof Callback)) {
            throw new IllegalArgumentException("the activity need implements ImageHelper.Callback");
        }
        mHolder = holder;
        mCallback = (Callback) holder;
    }

    public void openPhotos() {
        int hasPhotosPermission = ContextCompat.checkSelfPermission(mHolder, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d(TAG, "openPhotos hasReadExternalStoragePermission:" + hasPhotosPermission);
        if (hasPhotosPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mHolder, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODE_FOR_PHOTO_PERMISSION);
            Log.d(TAG, "ActivityCompat.requestPermissions READ_EXTERNAL_STORAGE");
            return;
        }

        Intent intentPick = new Intent(Intent.ACTION_PICK);
        intentPick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        try {
            mHolder.startActivityForResult(intentPick, REQUEST_CODE_PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            Intent intentGetContent = new Intent(Intent.ACTION_GET_CONTENT);
            intentGetContent.setType("image/*");
            Intent wrapperIntent = Intent.createChooser(intentGetContent, null);
            try {
                mHolder.startActivityForResult(wrapperIntent, REQUEST_CODE_PICK_IMAGE);
            } catch (ActivityNotFoundException e1) {
                Toast.makeText(mHolder.getApplicationContext(), "没有找到文件浏览器或相册", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cropImage(Uri data) {
        // ImageCropper.crop(mHolder, data.getPath());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                if (imageUri != null && IMAGE_FROM_FILE.equals(imageUri.getScheme())) {
                    Log.d(TAG, "openPhotos pick image " + imageUri.getPath());
                    cropImage(Uri.fromFile(new File(imageUri.getPath())));
                } else if (imageUri != null && IMAGE_FROM_CONTENT.equals(imageUri.getScheme())) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mHolder.getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.d(TAG, "openPhotos pick image " + picturePath);
                        cropImage(Uri.fromFile(new File(picturePath)));
                    } else {
                        Log.e(TAG, "openPhotos pick image cursor null");
                    }
                } else {
                    Toast.makeText(mHolder.getApplicationContext(), "图片获取失败，并没有成功拿到图片路径", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, imageUri != null ? ("openPhotos unknown uri\n" + imageUri.getPath()) : "openPhotos imageUri null");
                }
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "openCamera " + fCapturePicture.getAbsolutePath());
                cropImage(Uri.fromFile(fCapturePicture));
            }
        } else if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri output = data.getParcelableExtra("extra_output_uri");
                fCapturePicture = new File(output.getPath());
                mCallback.onPictureQueryOut(fCapturePicture.getAbsolutePath());
            } else {
                Toast.makeText(mHolder.getApplicationContext(), "图片裁剪失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface Callback {
        void onPictureQueryOut(String path);
    }
}
