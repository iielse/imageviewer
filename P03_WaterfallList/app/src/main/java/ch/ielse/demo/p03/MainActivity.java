package ch.ielse.demo.p03;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.List;

public class MainActivity extends Activity {

    private RecyclerView vRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vRecycler = (RecyclerView) findViewById(R.id.v_recycler);
        vRecycler.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayout.VERTICAL));
        vRecycler.setAdapter(adapter);
    }

    RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
        final List<Data> dataList = Data.get();

        class ImageViewHolder extends RecyclerView.ViewHolder {
            FrameLayout.LayoutParams lpImage =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            RatioImageView iPicture;
            Data mData;

            ImageViewHolder(View itemView) {
                super(itemView);
                iPicture = (RatioImageView) itemView.findViewById(R.id.i_picture);
                iPicture.setLayoutParams(lpImage);
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
