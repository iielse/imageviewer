package ch.ielse.demo.p05.view;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.ielse.demo.p05.Const;

import ch.ielse.demo.p05.R;

/**
 * 参考<br/>
 * https://github.com/Hamadakram/Sneaker
 */
public class Sneaker {

    final Params params;
    boolean isShowing;

    private Sneaker(@NonNull Params params) {
        this.params = params;
    }

    static class Params {
        static int EDGE_PADDING = 0;
        static int CONTENT_HEIGHT = 0;
        static int MESSAGE_SIZE = 0;
        static int MESSAGE_COLOR = 0;

        int contentHeight = CONTENT_HEIGHT;
        int edgePadding = EDGE_PADDING;
        int messageSize = MESSAGE_SIZE;
        int messageColor = MESSAGE_COLOR;

        boolean isInitLoading;
        int icon;
        int backgroundColor = Color.WHITE;
        int height;
        String message;
        boolean isAutoHide;
        int mDuration = 2400;
        LinearLayout contentView;
        ViewGroup activityDecorView;
        AppCompatImageView ivIcon;
        TextView tvMessage;
        Activity activity;
        CallbackAdapter mListener;
    }


    public static class CallbackAdapter {
        public void onClick(Sneaker sneaker) {
        }

        public void onDismiss(Sneaker sneaker) {
        }
    }


    public static class Builder {
        static final int VIEW_SNEAKER_ID = R.id.view_sneaker;
        final Params p = new Params();

        Builder(@NonNull Activity activity) {
            if (Params.EDGE_PADDING == 0 && Params.CONTENT_HEIGHT == 0 &&
                    Params.MESSAGE_SIZE == 0 && Params.MESSAGE_COLOR == 0) {
                throw new IllegalArgumentException("please invoke method 'Sneaker.setDisplayConfigs()' set display config first ~");
            }
            p.activity = activity;
        }

        public Builder setListener(CallbackAdapter l) {
            p.mListener = l;
            return this;
        }

        public Builder setMessage(String message) {
            p.message = message;
            return this;
        }

        public Builder setIcon(int icon) {
            p.icon = icon;
            return this;
        }

        public Builder setLoading(boolean isLoading) {
            p.isInitLoading = isLoading;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            p.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setAutoHide(boolean autoHide) {
            p.isAutoHide = autoHide;
            return this;
        }

        public Builder setHeightByDimenRes(@DimenRes int dimenId) {
            p.height = p.activity.getResources().getDimensionPixelSize(dimenId);
            return this;
        }

        public Builder setDuration(int duration) {
            p.mDuration = duration;
            return this;
        }

        public Sneaker create() {
            final Sneaker sneaker = new Sneaker(p);
            // Main layout
            p.contentView = new LinearLayout(p.activity);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    p.height = (getStatusBarHeight() + p.contentHeight));
            p.contentView.setLayoutParams(layoutParams);
            p.contentView.setOrientation(LinearLayout.HORIZONTAL);
            p.contentView.setGravity(Gravity.CENTER_VERTICAL);
            p.contentView.setPadding(0, getStatusBarHeight(), p.edgePadding, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                p.contentView.setElevation(12);
            }

            // Background color
            p.contentView.setBackgroundColor(p.backgroundColor);

            // Icon
            // If icon is set
            p.ivIcon = new AppCompatImageView(p.activity);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(convertToDp(44), ViewGroup.LayoutParams.MATCH_PARENT);
            p.ivIcon.setLayoutParams(lp);
            //p.ivIcon.setScaleType(ImageView.ScaleType.CENTER);
            p.ivIcon.setPadding(p.edgePadding, p.edgePadding, p.edgePadding, p.edgePadding);
            p.ivIcon.setClickable(false);
            p.contentView.addView(p.ivIcon);
            if (p.isInitLoading) {
                MaterialProgressDrawable drawable = new MaterialProgressDrawable(p.ivIcon.getContext(), p.ivIcon);
                p.ivIcon.setImageDrawable(drawable);
                drawable.setColors(new int[]{0xFFCC8501, 0xFFFFB834});
                drawable.start();
            } else if (p.icon != 0) {
                p.ivIcon.setImageResource(p.icon);
            } else {
                p.ivIcon.setVisibility(View.GONE);
            }

            // Title and description
            LinearLayout textLayout = new LinearLayout(p.activity);
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textLayout.setLayoutParams(textLayoutParams);
            textLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams lpTv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);


            if (!TextUtils.isEmpty(p.message)) {
                p.tvMessage = new TextView(p.activity);
                p.tvMessage.setLayoutParams(lpTv);
                p.tvMessage.setGravity(Gravity.CENTER_VERTICAL);
                p.tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, p.messageSize);
                p.tvMessage.setMaxLines(1);
                p.tvMessage.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                p.tvMessage.setTextColor(p.messageColor);
                p.tvMessage.setText(p.message);
                p.tvMessage.setClickable(false);
                textLayout.addView(p.tvMessage);
            }
            p.contentView.addView(textLayout);
            p.contentView.setId(VIEW_SNEAKER_ID);

            // getActivityDecorView
            p.activityDecorView = (ViewGroup) p.activity.getWindow().getDecorView();
            removeExistingOverlayInView(p.activityDecorView);

            p.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (p.mListener != null) {
                        p.mListener.onClick(sneaker);
                    }
                }
            });
            return sneaker;
        }

        private int getStatusBarHeight() {
            Rect rectangle = new Rect();
            Window window = p.activity.getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
            return rectangle.top;
        }

        private int convertToDp(float sizeInDp) {
            float scale = p.activity.getResources().getDisplayMetrics().density;
            return (int) (sizeInDp * scale + 0.5f);
        }


        void removeExistingOverlayInView(ViewGroup parent) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (child.getId() == VIEW_SNEAKER_ID) {
                    parent.removeView(child);
                }
                if (child instanceof ViewGroup) {
                    removeExistingOverlayInView((ViewGroup) child);
                }
            }
        }
    }

    public static Sneaker.Builder with(Activity activity) {
        return new Sneaker.Builder(activity);
    }

    public static void setDisplayConfigs(Context ctx, int edgePadding, int messageColor, int messageSize, int contentHeight) {
        final Resources res = ctx.getResources();
        Params.EDGE_PADDING = res.getDimensionPixelSize(edgePadding);
        Params.MESSAGE_COLOR = ContextCompat.getColor(ctx, messageColor);
        Params.MESSAGE_SIZE = res.getDimensionPixelSize(messageSize);
        Params.CONTENT_HEIGHT = res.getDimensionPixelSize(contentHeight);
    }

    public void show() {
        if (isShowing) return;
        isShowing = true;

        params.activityDecorView.addView(params.contentView);
        params.contentView.startAnimation(AnimationUtils.loadAnimation(params.activity, R.anim.sneaker_show));
        if (params.isAutoHide) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, params.mDuration);
        }

        Const.INSTANCE.setStatusBarDarkTheme(!Const.INSTANCE.isDarkColorTheme(params.backgroundColor),
                params.activity);
    }

    public void dismiss() {
        dismiss(0);
    }

    private void dismiss(long delayMillis) {
        params.contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowing && !params.activity.isFinishing()) {
                    isShowing = false;

                    params.contentView.startAnimation(AnimationUtils.loadAnimation(params.activity, R.anim.sneaker_hide));
                    params.activityDecorView.removeView(params.contentView);

                    int colorPrimary = ContextCompat.getColor(params.activity, R.color.colorPrimary);
                    Const.INSTANCE.setStatusBarDarkTheme(!Const.INSTANCE.isDarkColorTheme(colorPrimary),
                            params.activity);

                    if (params.mListener != null) {
                        params.mListener.onDismiss(Sneaker.this);
                    }
                }
            }
        }, delayMillis);
    }

    public void notifyStateChanged(@DrawableRes final int resultResId, final String message) {
        if (isShowing) {
            Animation animation = AnimationUtils.loadAnimation(params.activity, R.anim.sneaker_fade_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (isShowing) {
                        params.tvMessage.setText(message);
                        Animation animation2 = AnimationUtils.loadAnimation(params.activity, R.anim.sneaker_fade_in);
                        params.tvMessage.startAnimation(animation2);
                        animation2.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dismiss(800);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });

                        if (params.isInitLoading) {
                            Drawable drawable = params.ivIcon.getDrawable();
                            if (drawable instanceof AnimationDrawable) {
                                ((AnimationDrawable) drawable).stop();
                            }
                        }
                        params.ivIcon.setImageResource(resultResId);
                        params.ivIcon.startAnimation(AnimationUtils.loadAnimation(params.activity, R.anim.sneaker_scale_out));
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            params.tvMessage.startAnimation(animation);
            params.ivIcon.startAnimation(AnimationUtils.loadAnimation(params.activity, R.anim.sneaker_scale_in));

        }
    }
}

