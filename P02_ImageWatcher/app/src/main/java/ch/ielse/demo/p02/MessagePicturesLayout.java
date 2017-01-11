package ch.ielse.demo.p02;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * 至尊流畅;daLao专用;/斜眼笑
 */
public class MessagePicturesLayout extends FrameLayout implements View.OnClickListener {

    public static final int MAX_DISPLAY_COUNT = 9;
    private final FrameLayout.LayoutParams lpChildImage = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private final int mSpace;
    private List<ImageView> iPictureList = new ArrayList<>();

    private Callback mCallback;
    private boolean isInit;
    private List<String> mDataList;

    public MessagePicturesLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        mSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics) + 0.5f);

        for (int i = 0; i < MAX_DISPLAY_COUNT; i++) {
            ImageView squareImageView = new SquareImageView(context);
            squareImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            squareImageView.setVisibility(View.GONE);
            squareImageView.setOnClickListener(this);
            addView(squareImageView);
            iPictureList.add(squareImageView);
        }
    }

    public void set(List<String> urlList) {
        mDataList = urlList;
        if (isInit) {
            notifyDataChanged();
        }
    }

    private void notifyDataChanged() {
        final List<String> dataList = mDataList;
        final int urlListSize = dataList != null ? mDataList.size() : 0;

        int column = 3;
        if (urlListSize == 1) {
            column = 1;
        } else if (urlListSize == 4) {
            column = 2;
        }
        int row = 0;
        if (urlListSize > 6) {
            row = 3;
        } else if (urlListSize > 3) {
            row = 2;
        } else if (urlListSize > 0) {
            row = 1;
        }

        final int imageSize = (int) ((getWidth() * 1f - mSpace * (column - 1)) / column);
        lpChildImage.width = imageSize;
        lpChildImage.height = lpChildImage.width;

        for (int i = 0; i < iPictureList.size(); i++) {
            ImageView iPicture = iPictureList.get(i);
            if (i < urlListSize) {
                iPicture.setVisibility(View.VISIBLE);
                iPicture.setLayoutParams(lpChildImage);
                Glide.with(getContext()).load(dataList.get(i))
                        .placeholder(R.drawable.default_picture).into(iPicture);
                iPicture.setTranslationX((i % column) * (imageSize + mSpace));
                iPicture.setTranslationY((i / column) * (imageSize + mSpace));
            } else {
                iPicture.setVisibility(View.GONE);
            }
        }

        getLayoutParams().height = imageSize * row + mSpace * (row - 1);
    }

    @Override
    public void onClick(View v) {
        int idx = iPictureList.indexOf(v);
        if (mCallback != null && idx >= 0) {
            mCallback.onPictureClick((ImageView) v, idx, iPictureList, mDataList);
        }
    }

    public interface Callback {
        void onPictureClick(ImageView i, int pos, List<ImageView> imageGroupList, List<String> urlList);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isInit = true;
        notifyDataChanged();
    }
}
