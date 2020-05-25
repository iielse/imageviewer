package com.github.iielse.imageviewer.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.iielse.imageviewer.ImageViewerBuilder;
import com.github.iielse.imageviewer.ImageViewerDialogFragment;
import com.github.iielse.imageviewer.core.ImageLoader;
import com.github.iielse.imageviewer.core.Transformer;
import com.github.iielse.imageviewer.demo.data.MyData;
import com.github.iielse.imageviewer.demo.utils.UtilsKt;
import com.github.iielse.imageviewer.demo.utils.X;
import com.github.iielse.imageviewer.demo.viewer.MyCustomController;
import com.github.iielse.imageviewer.demo.viewer.MySimpleLoader;
import com.github.iielse.imageviewer.demo.viewer.MyTestDataProvider;
import com.github.iielse.imageviewer.demo.viewer.MyTransformer;
import com.github.iielse.imageviewer.demo.viewer.Trans;
import com.github.iielse.imageviewer.utils.Config;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainJavaActivity7 extends AppCompatActivity {
    private DataViewModel viewModel;
    private MyCustomController myCustomController;
    private DataAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Trans.INSTANCE.getMapping().clear();
        adapter.setListener(null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DataViewModel.class);
        myCustomController = new MyCustomController(this);
        adapter = new DataAdapter();

        X.INSTANCE.setAppContext(getApplicationContext());
        Config.INSTANCE.setTRANSITION_OFFSET_Y(UtilsKt.statusBarHeight());
        setContentView(R.layout.activity_10);
        final TextView orientation = findViewById(R.id.orientation);
        orientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean orientationH = false;
                if (v.getTag() != null) orientationH = (boolean) v.getTag();
                orientationH = !orientationH;
                if (orientationH) {
                    orientation.setText("HORIZONTAL");
                    Config.INSTANCE.setVIEWER_ORIENTATION(ViewPager2.ORIENTATION_HORIZONTAL);
                } else {
                    orientation.setText("VERTICAL");
                    Config.INSTANCE.setVIEWER_ORIENTATION(ViewPager2.ORIENTATION_VERTICAL);
                }
                v.setTag(orientationH);
            }
        });

        final TextView fullScreen = findViewById(R.id.fullScreen);
        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFullScreen = false;
                if (v.getTag() != null) isFullScreen = (boolean) v.getTag();
                isFullScreen = !isFullScreen;
                if (isFullScreen) {
                    orientation.setText("FullScreen(on)");
                    Config.INSTANCE.setVIEWER_ORIENTATION(ViewPager2.ORIENTATION_HORIZONTAL);
                    Config.INSTANCE.setTRANSITION_OFFSET_Y(0);
                } else {
                    orientation.setText("FullScreen(off)");
                    Config.INSTANCE.setVIEWER_ORIENTATION(ViewPager2.ORIENTATION_VERTICAL);
                    Config.INSTANCE.setTRANSITION_OFFSET_Y(UtilsKt.statusBarHeight());
                }
                v.setTag(isFullScreen);
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 3));
        recyclerView.setAdapter(adapter);

        adapter.setListener(new Function2<String, Object, Unit>() {
            @Override
            public Unit invoke(String s, Object o) {
                handleAdapterListener(s, o);
                return null;
            }
        });

        viewModel.getDataList().observe(this, new Observer<PagedList<MyData>>() {
            @Override
            public void onChanged(PagedList<MyData> myData) {
                adapter.submitList(myData);
            }
        });
    }

    private void handleAdapterListener(String s, Object o) {
        if (MainActivity6Kt.ITEM_CLICKED.equals(s)) {
            show((MyData) o);
        }
    }

    private void show(MyData item) {
        builder(item).show();
    }

    private ImageViewerBuilder builder(MyData clickedData) {
        final MyTestDataProvider dataProvider = new MyTestDataProvider(clickedData);
        final ImageLoader imageLoader = new MySimpleLoader();
        final Transformer transformer = new MyTransformer();
        final ImageViewerBuilder builder = new ImageViewerBuilder(this, imageLoader, dataProvider, transformer, clickedData.getId());
        myCustomController.init(builder);

        final TextView fullScreen = findViewById(R.id.fullScreen);
        boolean isFullScreen = false;
        if (fullScreen.getTag() != null) isFullScreen = (boolean) fullScreen.getTag();
        if (isFullScreen) {
            builder.setViewerFactory(new ImageViewerDialogFragment.Factory() {
                @NotNull
                @Override
                public ImageViewerDialogFragment build() {
                    return new FullScreenImageViewerDialogFragment();
                }
            });
        }

        return builder;
    }
}
