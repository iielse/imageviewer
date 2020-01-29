package com.github.iielse.imagewatcher.demo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.iielse.imagewatcher.ImageWatcherHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.github.iielse.imageviewer.demo.R;


public class Fragment1 extends Fragment {
    final String SUBJECT_ALBUM = "album";
    final String SUBJECT_CAMERA = "camera";
    final String SUBJECT_MULTI = "multiple";
    final String IMAGE_FROM_CONTENT = "content";
    final String IMAGE_FROM_FILE = "file";
    final String TAG = "PictureInquirer";
    final int REQUEST_CODE_PICK_IMAGE = 413;
    final int REQUEST_CODE_PICK_MULTI = 414;

    private ImageWatcherHelper iwHelper;
    private final List<Uri> pictureList = new ArrayList<>();
    private TextView vPictureUris;
    private EditText vIdx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment, container, false);

        vIdx = itemView.findViewById(R.id.vIdx);
        vPictureUris = itemView.findViewById(R.id.vPictureUris);
        View vAddLocal = itemView.findViewById(R.id.vAddLocal);

        vAddLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAlbum();
            }
        });

        View vNoInitPicture = itemView.findViewById(R.id.vNoInitPicture);
        vNoInitPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iwHelper != null) {
                    int currIdx = 0;
                    try {
                        if (!TextUtils.isEmpty(vIdx.getText())) {
                            currIdx = Integer.parseInt(vIdx.getText().toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    iwHelper.show(pictureList, currIdx);
                }
            }
        });

        return itemView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof ImageWatcherHelper.Provider) {
            iwHelper = ((ImageWatcherHelper.Provider) getActivity()).iwHelper();
        }

        List<Uri> tmp = new ArrayList<>();
        tmp.add(Uri.parse("http://c.hiphotos.baidu.com/image/h%3D300/sign=4bc239aadda20cf45990f8df46094b0c/9d82d158ccbf6c81924a92c5b13eb13533fa4099.jpg"));
        notifyPictureListChanged(tmp);
    }

    private void queryAlbum() {
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
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                if (imageUri != null && IMAGE_FROM_FILE.equals(imageUri.getScheme())) {
                    logd(TAG + " openPhotos pick image " + imageUri.getPath());
                    onQueryOrTakeSuccess(SUBJECT_ALBUM, Collections.singletonList(imageUri));
                } else if (imageUri != null && IMAGE_FROM_CONTENT.equals(imageUri.getScheme())) {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        logd(TAG + " openPhotos pick image " + picturePath);
                        onQueryOrTakeSuccess(SUBJECT_ALBUM, Collections.singletonList(imageUri));
                    } else {
                        logd(TAG + " openPhotos pick image cursor null");
                        onResultFailure(SUBJECT_ALBUM, new Exception("openPhotos pick image cursor null"));
                    }
                } else {
                    logd(TAG + (imageUri != null ? (" openPhotos unknown uri\n" + imageUri.getPath()) : " openPhotos imageUri null"));
                    onResultFailure(SUBJECT_ALBUM, new Exception(imageUri != null ? ("openPhotos unknown uri\n" + imageUri.getPath()) : "openPhotos imageUri null"));
                }
            }
        }
    }

    private void logd(String message) {
        Log.d("app", message);
    }

    private void onQueryOrTakeSuccess(@NonNull String type, @NonNull List<Uri> pictures) {
        notifyPictureListChanged(pictures);
    }

    private void onResultFailure(String type, Throwable t) {
        t.printStackTrace();
        if (getActivity() != null) {
            Toast.makeText(getActivity().getApplicationContext(), "图片获取失败 " + type, Toast.LENGTH_SHORT).show();
        }
    }


    private void notifyPictureListChanged(List<Uri> pictures) {
        pictureList.addAll(0, pictures);

        StringBuilder sb = new StringBuilder();
        for (Uri p : pictureList) {
            sb.append(p.getScheme()).append("//:").append(p.getPath()).append("\n").append("\n");
        }
        vPictureUris.setText(sb);
    }


}
