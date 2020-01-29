package com.github.iielse.imagewatcher.demo;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import com.github.iielse.imageviewer.demo.R;

/**
 * QQ 517309507
 * 至尊流畅;daLao专用;/斜眼笑
 */
public class MessagePicturesLayout extends FrameLayout implements View.OnClickListener {

    public static final int MAX_DISPLAY_COUNT = 9;
    private final FrameLayout.LayoutParams lpChildImage = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private final int mSingleMaxSize;
    private final int mSpace;
    private final List<ImageView> iPictureList = new ArrayList<>();
    private final SparseArray<ImageView> mVisiblePictureList = new SparseArray<>();
    private final TextView tOverflowCount;

    private Callback mCallback;
    private boolean isInit;
    private List<Uri> mDataList;
    private List<Uri> mThumbDataList;

    public MessagePicturesLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        mSingleMaxSize = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 216, mDisplayMetrics) + 0.5f);
        mSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics) + 0.5f);

        for (int i = 0; i < MAX_DISPLAY_COUNT; i++) {
            ImageView squareImageView = new SquareImageView(context);
            squareImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            squareImageView.setVisibility(View.GONE);
            squareImageView.setOnClickListener(this);
            addView(squareImageView);
            iPictureList.add(squareImageView);
        }

        tOverflowCount = new TextView(context);
        tOverflowCount.setTextColor(0xFFFFFFFF);
        tOverflowCount.setTextSize(24);
        tOverflowCount.setGravity(Gravity.CENTER);
        tOverflowCount.setBackgroundColor(0x66000000);
        tOverflowCount.setVisibility(View.GONE);
        addView(tOverflowCount);
    }

    public void set(List<Uri> urlThumbList, List<Uri> urlList) {
        mThumbDataList = urlThumbList;
        mDataList = urlList;
        if (isInit) {
            notifyDataChanged();
        }
    }

    private void notifyDataChanged() {
        final List<Uri> thumbList = mThumbDataList;
        final int urlListSize = thumbList != null ? mThumbDataList.size() : 0;

        if (thumbList == null || thumbList.size() < 1) {
            setVisibility(View.GONE);
            return;
        } else {
            setVisibility(View.VISIBLE);
        }

        if (thumbList.size() > mDataList.size()) {
            throw new IllegalArgumentException("dataList.size(" + mDataList.size() + ") > thumbDataList.size(" + thumbList.size() + ")");
        }

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

        final int imageSize = urlListSize == 1 ? mSingleMaxSize :
                (int) ((getWidth() * 1f - mSpace * (column - 1)) / column);

        lpChildImage.width = imageSize;
        lpChildImage.height = lpChildImage.width;

        tOverflowCount.setVisibility(urlListSize > MAX_DISPLAY_COUNT ? View.VISIBLE : View.GONE);
        tOverflowCount.setText("+ " + (urlListSize - MAX_DISPLAY_COUNT));
        tOverflowCount.setLayoutParams(lpChildImage);

        mVisiblePictureList.clear();
        for (int i = 0; i < iPictureList.size(); i++) {
            final ImageView iPicture = iPictureList.get(i);
            if (i < urlListSize) {
                iPicture.setVisibility(View.VISIBLE);
                mVisiblePictureList.put(i, iPicture);
                iPicture.setLayoutParams(lpChildImage);
                iPicture.setBackgroundResource(R.drawable.default_picture);
                Glide.with(getContext()).load(thumbList.get(i)).into(iPicture);
                iPicture.setTranslationX((i % column) * (imageSize + mSpace));
                iPicture.setTranslationY((i / column) * (imageSize + mSpace));
            } else {
                iPicture.setVisibility(View.GONE);
            }

            if (i == MAX_DISPLAY_COUNT - 1) {
                tOverflowCount.setTranslationX((i % column) * (imageSize + mSpace));
                tOverflowCount.setTranslationY((i / column) * (imageSize + mSpace));
            }
        }
        getLayoutParams().height = imageSize * row + mSpace * (row - 1);
    }

    @Override
    public void onClick(View v) {
        if (mCallback != null) {
            mCallback.onThumbPictureClick((ImageView) v, mVisiblePictureList, mDataList);
        }
    }

    public interface Callback {
        void onThumbPictureClick(ImageView i, SparseArray<ImageView> imageGroupList, List<Uri> urlList);
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
