package ch.ielse.demo.p05.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ch.ielse.demo.p05.R;


@SuppressWarnings("unused")
public class ProgressLoadingDialog extends Dialog {

    Params mParams;

    private ProgressLoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public void setParams(Params p) {
        mParams = p;
    }

    public static class Params {
        boolean hasShadow = false;
        boolean canCancel = true;
        Context mContext;
        String mTitle;

        TextView tTitle;
        View iProgress;
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

        public ProgressLoadingDialog create() {
            final ProgressLoadingDialog dialog = new ProgressLoadingDialog(p.mContext, p.hasShadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);

            View view = LayoutInflater.from(p.mContext).inflate(R.layout.progress_loading_dialog, null);
            p.tTitle = (TextView) view.findViewById(R.id.t_title);
            p.iProgress = view.findViewById(R.id.i_progress);

            if (p.tTitle != null) p.tTitle.setText(p.mTitle);

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(p.canCancel);
            dialog.setCancelable(p.canCancel);
            dialog.setParams(p);
            return dialog;
        }
    }
}
