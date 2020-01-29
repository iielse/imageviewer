package com.github.iielse.imagewatcher.demo;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.github.iielse.imageviewer.demo.R;

public class SheetDialog extends Dialog {

    public SheetDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Params {
        private final List<Sheet> menuList = new ArrayList<>();
        private DialogInterface.OnClickListener cancelListener;
        private String menuTitle;
        private String cancelText;
        private Context context;
    }

    public static class Builder {
        private boolean canCancel = true;
        private boolean shadow = true;
        private final Params p;

        public Builder(Context context) {
            p = new Params();
            p.context = context;
        }

        public Builder setTitle(String title) {
            this.p.menuTitle = title;
            return this;
        }

        public Builder addSheet(String text, DialogInterface.OnClickListener listener) {
            if (listener == null || text == null)
                throw new NullPointerException("text or  listener null");

            Sheet bm = new Sheet(text, listener);
            this.p.menuList.add(bm);
            return this;
        }

        public Builder setCancelListener(DialogInterface.OnClickListener cancelListener) {
            p.cancelListener = cancelListener;
            return this;
        }

        public Builder setCancelText(String text) {
            p.cancelText = text;
            return this;
        }

        public SheetDialog create() {
            final SheetDialog dialog = new SheetDialog(p.context, shadow ? R.style.Theme_Light_NoTitle_Dialog : R.style.Theme_Light_NoTitle_NoShadow_Dialog);
            Window window = dialog.getWindow();
            window.setWindowAnimations(R.style.Animation_Bottom_Rising);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM);

            View view = LayoutInflater.from(p.context).inflate(R.layout.sheet_dialog, null);

            TextView vCancel = (TextView) view.findViewById(R.id.vCancel);
            ViewGroup layContainer = (ViewGroup) view.findViewById(R.id.layContainer);
            LinearLayout.LayoutParams lpItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lpDivider = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            int dip1 = (int) (1 * p.context.getResources().getDisplayMetrics().density + 0.5f);
            int spacing = dip1 * 12;

            boolean hasTitle = !TextUtils.isEmpty(p.menuTitle);
            if (hasTitle) {
                TextView tTitle = new TextView(p.context);
                tTitle.setLayoutParams(lpItem);
                tTitle.setGravity(Gravity.CENTER);
                tTitle.setTextColor(0xFF8F8F8F);
                tTitle.setText(p.menuTitle);
                tTitle.setTextSize(17);
                tTitle.setPadding(0, spacing, 0, spacing);
                tTitle.setBackgroundResource(R.drawable.sheet_dialog_top_selector);
                layContainer.addView(tTitle);

                View viewDivider = new View(p.context);
                viewDivider.setLayoutParams(lpDivider);
                viewDivider.setBackgroundColor(0xFFCED2D6);
                layContainer.addView(viewDivider);
            }

            for (int i = 0; i < p.menuList.size(); i++) {
                final Sheet sheet = p.menuList.get(i);
                TextView bbm = new TextView(p.context);
                bbm.setLayoutParams(lpItem);
                int backgroundResId = R.drawable.sheet_dialog_center_selector;
                if (p.menuList.size() > 1) {
                    if (i == 0) {
                        if (hasTitle) {
                            backgroundResId = R.drawable.sheet_dialog_center_selector;
                        } else {
                            backgroundResId = R.drawable.sheet_dialog_top_selector;
                        }
                    } else if (i == p.menuList.size() - 1) {
                        backgroundResId = R.drawable.sheet_dialog_bottom_selector;
                    }
                } else if (p.menuList.size() == 1) {
                    backgroundResId = R.drawable.sheet_dialog_singleton_selector;
                }
                bbm.setBackgroundResource(backgroundResId);
                bbm.setPadding(0, spacing, 0, spacing);
                bbm.setGravity(Gravity.CENTER);
                bbm.setText(sheet.funName);
                bbm.setTextColor(0xFF007AFF);
                bbm.setTextSize(19);
                final int finalI = i;
                bbm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sheet.listener.onClick(dialog, finalI);
                    }
                });

                layContainer.addView(bbm);

                if (i != p.menuList.size() - 1) {
                    View viewDivider = new View(p.context);
                    viewDivider.setLayoutParams(lpDivider);
                    viewDivider.setBackgroundColor(0xFFCED2D6);
                    layContainer.addView(viewDivider);
                }
            }

            if (!TextUtils.isEmpty(p.cancelText)) {
                vCancel.setText(p.cancelText);
            }

            if (p.cancelListener != null) {
                vCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        p.cancelListener.onClick(dialog, 0);
                    }
                });
            } else {
                vCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            dialog.setContentView(view);
            dialog.setCanceledOnTouchOutside(canCancel);
            dialog.setCancelable(canCancel);
            return dialog;
        }


    }

    static class Sheet {
        public String funName;
        public DialogInterface.OnClickListener listener;

        public Sheet(String funName, DialogInterface.OnClickListener listener) {
            this.funName = funName;
            this.listener = listener;
        }
    }
}
