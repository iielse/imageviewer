package ch.ielse.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

public class MainActivity extends Activity {

    private ImageWatcher vImageWatcher;
    private RecyclerView vRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.fitsSystemWindows(findViewById(R.id.v_fit));

        vRecycler = (RecyclerView) findViewById(R.id.v_recycler);
        vRecycler.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        vRecycler.setAdapter(adapter);

        vImageWatcher = (ImageWatcher) findViewById(R.id.v_image_watcher);
    }

    RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
        final List<Data> dataList = Data.get();

        class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            FrameLayout.LayoutParams lpImage =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            RatioImageView iPicture;
            Data mData;

            ImageViewHolder(View itemView) {
                super(itemView);
                iPicture = (RatioImageView) itemView.findViewById(R.id.i_picture);
                iPicture.setLayoutParams(lpImage);
                iPicture.setOnClickListener(this);
            }

            void refresh(Data data) {
                mData = data;
                if (data == null) {
                    itemView.setVisibility(View.GONE);
                } else {
                    itemView.setVisibility(View.VISIBLE);
                    iPicture.setRatio(data.getWidth(), data.getHeight());
                    Glide.with(itemView.getContext()).load(mData.getRes()).into(iPicture);
                }
            }

            @Override
            public void onClick(View v) {
                if (v == iPicture) {
                    vImageWatcher.show(iPicture);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ImageViewHolder) holder).refresh(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    };
}
