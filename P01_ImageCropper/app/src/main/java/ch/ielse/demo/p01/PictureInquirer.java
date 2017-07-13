package ch.ielse.demo.p01;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * 获得图片，主要手段方式如下<br>
 * 1、相册文件获取 {@link PictureInquirer#queryPictureFromAlbum(String)}
 * 2、即时拍摄获取 {@link PictureInquirer#queryPictureFromCamera(String)}
 * <p>
 * 需要覆写activity onActivityResult的方法 <br>
 * <pre>
 * @Override
 * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *     mPictureInquirer.onActivityResult(requestCode, resultCode, data, new PictureInquirer.Callback() {
 *         @Override
 *         public void onPictureQueryOut(String path, String tag) {
 *         }
 *     });
 * }
 *
 * ps:PictureInquirer中的requestCode的常量值是可以修改的，如果与其它功能的requestCode的值是冲突的话
 * </pre>
 */
@SuppressWarnings("unused")
public class PictureInquirer {

    static final String TAG = "PictureInquirer";
    static final String IMAGE_FROM_CONTENT = "content";
    static final String IMAGE_FROM_FILE = "file";
    static int REQUEST_CODE_CAPTURE_PICTURE = 412;
    static int REQUEST_CODE_PICK_IMAGE = 413;
    private File fCapture;

    private Activity mHolder;
    private String mTag;

    public PictureInquirer(Activity activity) {
        if (activity == null) throw new IllegalArgumentException("activity is null");

        mHolder = activity;
    }

    /**
     * 相册目录获取图片
     */
    public void queryPictureFromAlbum(String tag) {
        mTag = tag;
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

    /**
     * 即时拍摄获取图片
     */
    public void queryPictureFromCamera(String tag) {
        mTag = tag;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fCapture = new File(mHolder.getExternalCacheDir(), "PIC" + System.currentTimeMillis() + ".jpg");
        try {
            if (!fCapture.exists()) fCapture.createNewFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fCapture));
            mHolder.startActivityForResult(intent, REQUEST_CODE_CAPTURE_PICTURE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mHolder.getApplicationContext(), "图片无法保存", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, Callback cb) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                if (imageUri != null && IMAGE_FROM_FILE.equals(imageUri.getScheme())) {
                    Log.d(TAG, "openPhotos pick image " + imageUri.getPath());
                    if (cb != null) cb.onPictureQueryOut(imageUri.getPath(), mTag);
                } else if (imageUri != null && IMAGE_FROM_CONTENT.equals(imageUri.getScheme())) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mHolder.getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.d(TAG, "openPhotos pick image " + picturePath);
                        if (cb != null) cb.onPictureQueryOut(picturePath, mTag);
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
                Log.d(TAG, "openCamera " + fCapture.getAbsolutePath());
                if (cb != null) cb.onPictureQueryOut(fCapture.getAbsolutePath(), mTag);
            }
        }
    }

    public interface Callback {
        /**
         * 获得了一张新的图片
         *
         * @param path 新图片的路径
         * @param tag  自设标志
         */
        void onPictureQueryOut(String path, String tag);
    }
}
