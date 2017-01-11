package ch.ielse.demo.p02;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动卡顿
 */
public class MessagePicturesLayout2 extends RecyclerView {
    public static final int MAX_DISPLAY_COUNT = 9;
    private final PicturesAdapter mAdapter;
    private final WrappableGridLayoutManager mLayoutManager;
    private final SpaceItemDecoration mSpaceItemDecoration;

    public void set(List<String> urlList) {
        int column = 3;
        int urlListSize = urlList != null ? urlList.size() : 0;
        if (urlListSize == 1) {
            column = 1;
        } else if (urlListSize == 4) {
            column = 2;
        }
        mLayoutManager.setSpanCount(column);
        mAdapter.set(urlList);
    }

    public MessagePicturesLayout2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAdapter(mAdapter = new PicturesAdapter());
        setLayoutManager(mLayoutManager = new WrappableGridLayoutManager(getContext(), 1));
        addItemDecoration(mSpaceItemDecoration = new SpaceItemDecoration(context).setSpace(2));
    }

    private static class PicturesAdapter extends Adapter {
        private final List<String> mPictureUrlList = new ArrayList<>();

        public void set(List<String> urlList) {
            final int originShownSize = mPictureUrlList.size();
            mPictureUrlList.clear();
            if (urlList != null) {
                mPictureUrlList.addAll(urlList);
            }

            if (originShownSize != mPictureUrlList.size()) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeChanged(0, mPictureUrlList.size());
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tDesc;
            ImageView iPicture;
            String mPictureUrl;

            ViewHolder(View itemView) {
                super(itemView);
                tDesc = (TextView) itemView.findViewById(R.id.t_desc);
                iPicture = (ImageView) itemView.findViewById(R.id.i_picture);
            }

            void refresh(int pos) {
                mPictureUrl = mPictureUrlList.get(pos);
                Glide.with(itemView.getContext()).load(mPictureUrl)
                        .placeholder(R.drawable.default_picture).into(iPicture);
                if (pos >= MAX_DISPLAY_COUNT - 1 && mPictureUrlList.size() > MAX_DISPLAY_COUNT) {
                    tDesc.setVisibility(View.VISIBLE);
                    tDesc.setText("+ " + (mPictureUrlList.size() - pos));
                } else {
                    tDesc.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_message_picture, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).refresh(position);
        }

        @Override
        public int getItemCount() {
            return mPictureUrlList.size() <= MAX_DISPLAY_COUNT ? mPictureUrlList.size() : MAX_DISPLAY_COUNT;
        }
    }
}
