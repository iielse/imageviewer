package ch.ielse.demo.p01.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ch.ielse.demo.p01.R;

public class FallDownToast {

    private Params mDisplayConfig;


    public void show() {
        View lFallDownToast = LayoutInflater.from(mDisplayConfig.mContext).inflate(R.layout.toast_fall_down, null);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mDisplayConfig.mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);

        Resources res = mDisplayConfig.mContext.getResources();
        int edgePadding = res.getDimensionPixelSize(R.dimen.edge_padding);
        int titleHeight = res.getDimensionPixelSize(R.dimen.title_height);
        int translateOffsetY = titleHeight;

        View lShadowWrapper = lFallDownToast.findViewById(R.id.l_shadow_wrapper);
        lShadowWrapper.getLayoutParams().width = dm.widthPixels;
        View lContent = lFallDownToast.findViewById(R.id.l_content);
        lContent.getLayoutParams().width = dm.widthPixels;
        lContent.getLayoutParams().height = titleHeight + translateOffsetY;
        lContent.setPadding(edgePadding, translateOffsetY, edgePadding, 0);

        lFallDownToast.setTranslationY(-lContent.getLayoutParams().height);

        if (mDisplayConfig.mBackgroundColor != 0) {
            lContent.setBackgroundColor(mDisplayConfig.mBackgroundColor);
        }

        ImageView iIcon = (ImageView) lFallDownToast.findViewById(R.id.i_icon);
        if (mDisplayConfig.mIcon != 0) {
            iIcon.setVisibility(View.VISIBLE);
            iIcon.setImageResource(mDisplayConfig.mIcon);
        } else {
            iIcon.setVisibility(View.GONE);
        }

        TextView tMessage = (TextView) lFallDownToast.findViewById(R.id.t_message);
        tMessage.setText(mDisplayConfig.mMessage);
        tMessage.setTextColor(mDisplayConfig.mMessageColor);

        lFallDownToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDisplayConfig.mClickCallback != null) {
                    mDisplayConfig.mClickCallback.onClick(v);
                }
            }
        });

        lFallDownToast.animate().setInterpolator(new OvershootInterpolator(1.05f)).translationY(-translateOffsetY).setDuration(450).start();

        Toast toast = new Toast(mDisplayConfig.mContext.getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(lFallDownToast);
        toast.show();
    }

    private static class Params {
        Context mContext;
        int mIcon;
        String mMessage;
        int mMessageColor = 0xFF333333;
        int mBackgroundColor = 0xFFFFFFFF;
        View.OnClickListener mClickCallback;
    }

    public static class Builder {
        final Params mParams = new Params();

        public Builder(Context context) {
            mParams.mContext = context;
        }

        public Builder setMessage(String message) {
            mParams.mMessage = message;
            return this;
        }

        public Builder setIcon(int icon) {
            mParams.mIcon = icon;
            return this;
        }

        public Builder setMessageColor(int messageColor) {
            mParams.mMessageColor = messageColor;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            mParams.mBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setOnclickListener(View.OnClickListener callback) {
            mParams.mClickCallback = callback;
            return this;
        }

        public FallDownToast create() {
            FallDownToast fullDownToast = new FallDownToast();
            fullDownToast.mDisplayConfig = mParams;
            return fullDownToast;
        }
    }
}
