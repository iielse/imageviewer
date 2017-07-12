package ch.ielse.view.imagecropper;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.subjects.PublishSubject;

@SuppressWarnings("unchecked")
public class RxImagePickerFragment extends Fragment {
    final String SUBJECT_ALBUM = "album";
    final String SUBJECT_CAMERA = "camera";
    final String SUBJECT_MULTI = "multi";

    final String TAG = "PictureInquirer";
    final String IMAGE_FROM_CONTENT = "content";
    final String IMAGE_FROM_FILE = "file";
    final int REQUEST_CODE_CAPTURE_PICTURE = 412;
    final int REQUEST_CODE_PICK_IMAGE = 413;

    private ImageCropper mImageCropper;
    private int mTranslucentStatusHeight;
    private final Map<String, ImagePickerInfo> mPickerTasks = new HashMap<>();
    private File fCapture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setTranslucentStatusHeight(int translucentStatusHeight) {
        mTranslucentStatusHeight = translucentStatusHeight;
    }

    private void onQueryOrTakeSuccess(@NonNull String type, @NonNull List<String> pictures) {
        final ImagePickerInfo pickerTask = mPickerTasks.remove(type);
        if (pickerTask != null && pickerTask.subject != null) {
            if (SUBJECT_ALBUM.equals(type) || SUBJECT_CAMERA.equals(type)) {
                if (pictures.size() > 0) {

                    if (pickerTask.crop) {
                        mImageCropper.crop(pictures.get(0), pickerTask.outputWidth, pickerTask.outputHeight, pickerTask.isCircleOverlay, "");
                    } else {
                        pickerTask.subject.onNext(pictures.get(0));
                        pickerTask.subject.onComplete();
                    }
                }
            } else if ("multiple".equals(type)) {
                pickerTask.subject.onNext(pictures);
                pickerTask.subject.onComplete();
            }
        }
    }

    private void onResultFailure(String type, Throwable t) {
        ImagePickerInfo pickerTask = mPickerTasks.remove(type);
        if (pickerTask != null && pickerTask.subject != null) {
            pickerTask.subject.onError(t);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                if (imageUri != null && IMAGE_FROM_FILE.equals(imageUri.getScheme())) {
                    Log.d(TAG, "openPhotos pick image " + imageUri.getPath());
                    onQueryOrTakeSuccess(SUBJECT_ALBUM, Collections.singletonList(imageUri.getPath()));
                } else if (imageUri != null && IMAGE_FROM_CONTENT.equals(imageUri.getScheme())) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.d(TAG, "openPhotos pick image " + picturePath);
                        onQueryOrTakeSuccess(SUBJECT_ALBUM, Collections.singletonList(imageUri.getPath()));
                    } else {
                        Log.e(TAG, "openPhotos pick image cursor null");
                        onResultFailure(SUBJECT_ALBUM, new Exception("openPhotos pick image cursor null"));
                    }
                } else {
                    Log.e(TAG, imageUri != null ? ("openPhotos unknown uri\n" + imageUri.getPath()) : "openPhotos imageUri null");
                    onResultFailure(SUBJECT_ALBUM, new Exception(imageUri != null ? ("openPhotos unknown uri\n" + imageUri.getPath()) : "openPhotos imageUri null"));
                }
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "openCamera " + fCapture.getAbsolutePath());
                onQueryOrTakeSuccess(SUBJECT_CAMERA, Collections.singletonList(fCapture.getAbsolutePath()));
            }
        }
    }

    public PublishSubject queryAlbum(String cropTitle, int outputWidth, int outputHeight, boolean isCircleOverlay, boolean crop) {
        ImagePickerInfo pickerTask = mPickerTasks.get(SUBJECT_ALBUM);
        final PublishSubject subject;
        if (pickerTask == null) {
            pickerTask = new ImagePickerInfo(cropTitle, outputWidth, outputHeight, isCircleOverlay, crop, subject = PublishSubject.create());
            mPickerTasks.put(SUBJECT_ALBUM, pickerTask);

            if (crop) initImageCropperWithTitle(subject, cropTitle);
        } else {
            subject = pickerTask.subject;
        }

        try {
            Intent intentPick = new Intent(Intent.ACTION_PICK);
            intentPick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intentPick, REQUEST_CODE_PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            Intent intentGetContent = new Intent(Intent.ACTION_GET_CONTENT);
            intentGetContent.setType("image/*");
            Intent wrapperIntent = Intent.createChooser(intentGetContent, null);
            try {
                startActivityForResult(wrapperIntent, REQUEST_CODE_PICK_IMAGE);
            } catch (ActivityNotFoundException e1) {
                onResultFailure(SUBJECT_ALBUM, e1);
            }
        }

        return subject;
    }

    public PublishSubject takeCamera(String cropTitle, int outputWidth, int outputHeight, boolean isCircleOverlay, boolean crop) {
        ImagePickerInfo pickerTask = mPickerTasks.get(SUBJECT_CAMERA);
        final PublishSubject subject;
        if (pickerTask == null) {
            pickerTask = new ImagePickerInfo(cropTitle, outputWidth, outputHeight, isCircleOverlay, crop, subject = PublishSubject.create());
            mPickerTasks.put(SUBJECT_CAMERA, pickerTask);

            if (crop) initImageCropperWithTitle(subject, cropTitle);
        } else {
            subject = pickerTask.subject;
        }

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File cacheFolder = Utils.getDiskCacheDir(getActivity(), "rxPicker");
            fCapture = File.createTempFile("TEMP", ".jpg", cacheFolder);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fCapture));
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_PICTURE);
        } catch (IOException e) {
            onResultFailure(SUBJECT_CAMERA, e);
        }

        return subject;
    }

    private void initImageCropperWithTitle(final PublishSubject subject, String cropTitle) {
        mImageCropper = ImageCropper.Helper.with(getActivity())
                .setTitle(cropTitle)
                .setTranslucentStatusHeight(mTranslucentStatusHeight)
                .setCallback(new ImageCropper.Callback() {
                    @Override
                    public void onPictureCropOut(Bitmap bitmap, String tag) {
                        subject.onNext(bitmap);
                        subject.onComplete();
                    }
                }).create();
    }


    public PublishSubject queryMulti(int fetchCount) {
        ImagePickerInfo pickerTask = mPickerTasks.get(SUBJECT_MULTI);
        final PublishSubject subject;
        if (pickerTask == null) {
            pickerTask = new ImagePickerInfo(subject = PublishSubject.create());
            mPickerTasks.put(SUBJECT_CAMERA, pickerTask);
        } else {
            subject = pickerTask.subject;
        }

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File cacheFolder = Utils.getDiskCacheDir(getActivity(), "rxPicker");
            fCapture = File.createTempFile("TEMP", ".jpg", cacheFolder);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fCapture));
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_PICTURE);
        } catch (IOException e) {
            onResultFailure(SUBJECT_CAMERA, e);
        }

        return subject;

    }


}
