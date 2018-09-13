package ch.ielse.demo.p02;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ielse.imagewatcher.ImageWatcher;

public class CustomFuncUIProvider implements ImageWatcher.UIProvider {
    private final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    @Override
    public View initialView(Context context) {
        TextView share = new TextView(context);
        share.setText("分享");
        share.setPadding(30, 30, 30, 30);
        share.setBackgroundColor(0x44ffffff);
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        share.setLayoutParams(lp);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext().getApplicationContext(), "666", Toast.LENGTH_SHORT).show();
            }
        });
        return share;
    }
}
