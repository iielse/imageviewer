package com.github.iielse.imageviewer.demo;

import android.content.Context;
import android.os.Bundle;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.iielse.imageviewer.ImageViewerBuilder;
import com.github.iielse.imageviewer.core.DataProvider;
import com.github.iielse.imageviewer.core.ImageLoader;
import com.github.iielse.imageviewer.core.Transformer;

public class MainActivity7 extends AppCompatActivity {
    ImageView pView4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_9);

        pView4 = findViewById(R.id.pView4);
        pView4.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(pView4).load(MyTestDataProviderKt.getMyData4().getUrl())
                .apply(RequestOptions.centerCropTransform()).into(pView4);
        pView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder().show();
            }
        });
    }

    private ImageViewerBuilder builder() {
        final Context context = this;
        final long initKey = MyTestDataProviderKt.getMyData4().getId();
        final DataProvider dataProvider = new MyTestDataProvider(MyTestDataProviderKt.getMyData4());
        final ImageLoader imageLoader = new MySimpleLoader();
        final LongSparseArray mapping = new LongSparseArray<ImageView>();
        mapping.put(initKey, pView4);
        final Transformer transformer = new MyTransformer(mapping);
        return new ImageViewerBuilder(context, imageLoader, dataProvider, transformer, initKey);
    }
}
