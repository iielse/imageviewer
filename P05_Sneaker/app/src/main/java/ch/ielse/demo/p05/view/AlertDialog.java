package ch.ielse.demo.p05.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import ch.ielse.demo.p05.R;


@SuppressWarnings("unused")
public class AlertDialog extends Dialog {

    Params mParams;

    private AlertDialog(Context context, int theme) {
        super(context, theme);
    }

    void setParams(Params p) {
        mParams = p;
    }

    static class Params {
        boolean hasShadow = true;
        boolean canCancel = true;
        Context mContext;
        String mTitle, mMessage, cancelText, confirmText;
        TextView tTitle, tCancel, tConfirm, tMessage;
        OnClickListener cancelCallback, confirmCallback;
    }

    public static class Builder {
        private Params p;

        public Builder setCancelable(boolean cc) {
            p.canCancel = cc;
            return this;
        }

        public Builder(Context context) {
            p = new Params();
            p.mContext = context;
        }

        public Builder setTitle(String title) {
            p.mTitle = title;
            return this;
        }

        public Builder setMessage(String message) {
            p.mMessage = message;
            return this;
        }

        public Builder setNegativeButton(String text, OnClickListener callback) {
            p.cancelText = text;
            p.cancelCallback = callback;
            return this;
        }

        public Builder setPositiveButton(String text, OnClickListener callback) {
            p.confirmText = text;
            p.confirmCallback = callback;
            return this;
        }

        public AlertDialog create() {
            final AlertDialog dialog = new AlertDialog(p.mContext, p.hasShadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);

            int dip1 = (int) (p.mContext.getResources().getDisplayMetrics().density + 0.5f);

            Window window = dialog.getWindow();
            window.getDecorView().setPadding(dip1 * 30, 0, dip1 * 30, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            View view = LayoutInflater.from(p.mContext).inflate(R.layout.alert_dialog, null);
            p.tTitle = (TextView) view.findViewById(R.id.t_title);
            p.tMessage = (TextView) view.findViewById(R.id.t_message);
            p.tCancel = (TextView) view.findViewById(R.id.t_cancel);
            p.tConfirm = (TextView) view.findViewById(R.id.t_confirm);

            p.tTitle.setText(p.mTitle);
            p.tTitle.setVisibility(!TextUtils.isEmpty(p.mTitle) ? View.VISIBLE : View.GONE);
            p.tMessage.setText(p.mMessage);
            p.tMessage.setVisibility(!TextUtils.isEmpty(p.mMessage) ? View.VISIBLE : View.GONE);

            if (p.cancelCallback != null) {
                p.tCancel.setText(p.cancelText);
                p.tCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        p.cancelCallback.onClick(dialog, 0);
                    }
                });
            } else {
                p.tCancel.setVisibility(View.GONE);
            }

            if (p.confirmCallback != null) {
                p.tConfirm.setText(p.confirmText);
                p.tConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        p.confirmCallback.onClick(dialog, 0);
                    }
                });
            }

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(p.canCancel);
            dialog.setCancelable(p.canCancel);
            dialog.setParams(p);
            return dialog;
        }
    }
}
