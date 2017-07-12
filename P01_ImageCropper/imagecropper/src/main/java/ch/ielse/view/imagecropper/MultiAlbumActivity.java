package ch.ielse.view.imagecropper;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiAlbumActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_album);

        RecyclerView vRecycler = (RecyclerView) findViewById(R.id.v_recycler);
        vRecycler.setLayoutManager(new GridLayoutManager(MultiAlbumActivity.this, 3));
        // TODO vRecycler.setAdapter();
    }

    public List<AlbumPicture> queryAllPicturesFromSystem() {
        final List<AlbumPicture> galleryList = new ArrayList<>();

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String item = cursor.getString(dataColumnIndex);
                galleryList.add(AlbumPicture.create(item));
            }
        }
        Collections.reverse(galleryList);
        return galleryList;
    }


    static class AlbumPicture {
        String image;
        boolean isSelected;

        static AlbumPicture create(String image) {
            AlbumPicture albumPicture = new AlbumPicture();
            albumPicture.image = image;
            return albumPicture;
        }
    }
}
