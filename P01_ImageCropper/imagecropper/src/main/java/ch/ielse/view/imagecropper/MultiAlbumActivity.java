package ch.ielse.view.imagecropper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class MultiAlbumActivity extends Activity implements MultiAlbumAdapter.Callback, View.OnClickListener {
    private View iBack, iSubmit;
    private TextView tCount;
    private Disposable mQueryTask;
    private MultiAlbumAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_album_activity);

        (iBack = findViewById(R.id.i_back)).setOnClickListener(this);
        (iSubmit = findViewById(R.id.i_submit)).setOnClickListener(this);

        int fetchCount = getIntent().getIntExtra("extra_fetch_count", 9);

        tCount = (TextView) findViewById(R.id.t_count);
        tCount.setText(0 + "/" + fetchCount);

        RecyclerView vRecycler = (RecyclerView) findViewById(R.id.v_recycler);
        vRecycler.setLayoutManager(new GridLayoutManager(MultiAlbumActivity.this, 3));
        vRecycler.addItemDecoration(new SpaceItemDecoration(this).setEdgeSpace(3).setSpace(3));
        vRecycler.setAdapter(new AlphaInAnimationAdapter(adapter = new MultiAlbumAdapter(fetchCount).setCallback(this)));

        queryDataFromSystemAlbum();
    }

    void queryDataFromSystemAlbum() {
        Observable.fromCallable(new Callable<List<MultiAlbumAdapter.AlbumPicture>>() {
            @Override
            public List<MultiAlbumAdapter.AlbumPicture> call() throws Exception {
                final List<MultiAlbumAdapter.AlbumPicture> pictureList = new ArrayList<>();

                final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
                final String orderBy = MediaStore.Images.Media._ID;
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            String item = cursor.getString(dataColumnIndex);
                            pictureList.add(MultiAlbumAdapter.AlbumPicture.create(item));
                        }
                    }
                    cursor.close();
                }
                Collections.reverse(pictureList);
                return pictureList;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MultiAlbumAdapter.AlbumPicture>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mQueryTask = d;
                    }

                    @Override
                    public void onNext(@NonNull List<MultiAlbumAdapter.AlbumPicture> albumPictures) {
                        adapter.set(albumPictures);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("MultiAlbumActivity", "queryDataFromSystemAlbum error ");
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "系统相册资源读取失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onItemStateChanged(String fetchInfo) {
        tCount.setText(fetchInfo);
    }

    @Override
    public void onClick(View v) {
        if (v == iBack) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if (v == iSubmit) {
            Intent intent = new Intent();
            final List<String> selectedPictureUrls = adapter.getSelectedPictureUrls();
            intent.putExtra("extra_multi_selected", selectedPictureUrls.toArray(new String[selectedPictureUrls.size()]));
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mQueryTask != null && !mQueryTask.isDisposed()) mQueryTask.dispose();
    }
}
