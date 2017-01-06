package ch.ielse.demo.p02;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class MessagePicturesLayout extends RecyclerView {
    private static final int MAX_DISPLAY_COUNT = 9;
    private final PicturesAdapter mAdapter;
    private final WrappableGridLayoutManager mLayoutManager;
    private final SpaceGridItemDecoration mSpaceItemDecoration;

    public void set(List<String> urlList) {
        int column = 3;
        int urlListSize = urlList != null ? urlList.size() : 0;
        if (urlListSize == 1) {
            column = 1;
        } else if (urlListSize == 4) {
            column = 2;
        }
        mLayoutManager.setSpanCount(column);
        mSpaceItemDecoration.setColumn(column, urlListSize);

        mAdapter.set(urlList);
    }

    public MessagePicturesLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAdapter(mAdapter = new PicturesAdapter());
        setLayoutManager(mLayoutManager = new WrappableGridLayoutManager(getContext(), 1));
        addItemDecoration(mSpaceItemDecoration = new SpaceGridItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.line_space_picture)
        ));
        setBackgroundColor(0xFFcc00ff);
    }

    private static class PicturesAdapter extends RecyclerView.Adapter {
        private final List<String> mPictureUrlList = new ArrayList<>();

        public void set(List<String> urlList) {
            mPictureUrlList.clear();
            if (urlList != null) {
                mPictureUrlList.addAll(urlList);
            }
            notifyDataSetChanged();
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
                if (pos >= MAX_DISPLAY_COUNT && mPictureUrlList.size() > MAX_DISPLAY_COUNT) {
                    tDesc.setVisibility(View.VISIBLE);
                    tDesc.setText("+" + (mPictureUrlList.size() - pos));
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
            return mPictureUrlList.size() <= 9 ? mPictureUrlList.size() : 9;
        }
    }


    private static class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {


        private int mColumn;
        private int mSpace;
        private int mShownDataSize;

        public void setColumn(int column, int totalDataSize) {
            if (column < 1 || totalDataSize < 0)
                throw new IllegalArgumentException("SpaceGridItemDecoration setColumn column [" + mColumn + "] ##totalDataSize [" + totalDataSize + "]");

            mColumn = column;
            mShownDataSize = totalDataSize;
            if (mShownDataSize > MessagePicturesLayout.MAX_DISPLAY_COUNT) {
                mShownDataSize = MessagePicturesLayout.MAX_DISPLAY_COUNT;
            }
        }

        public SpaceGridItemDecoration(int space) {
            mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final int position = parent.getChildAdapterPosition(view);
            Log.e("TTT", "position:" + position + "#mColumn:" + mColumn + "##((GridLayoutManager)parent.getLayoutManager()).getOrientation()" + ((GridLayoutManager)parent.getLayoutManager()).getOrientation());


//            if ((position + 1) % mColumn != 0) {
//                outRect.right = mSpace;
//                Log.e("TTT", "  outRect.right = mSpace" + mSpace);
//            } else {
//                outRect.right = 0;
//                Log.d("TTT", "  outRect.right = mSpace" + 0);
//            }

//            Log.e("TTT", "  mTotalDataSize" + mShownDataSize + "##(mTotalDataSize - mTotalDataSize % mColumn)" + (mShownDataSize - mShownDataSize % mColumn));
//            if (position < (mShownDataSize - mShownDataSize % mColumn - mColumn)) {
//                outRect.bottom = mSpace;
//                Log.d("TTT", "  outRect.bottom = mSpace" + mSpace);
//            } else {
//                outRect.bottom = 0;
//                Log.d("TTT", "  outRect.bottom = mSpace" + 0);
//            }
        }
    }
}
