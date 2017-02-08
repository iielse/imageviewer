package ch.ielse.demo.p01;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by LY on 2017/2/7.
 */

public class SourceView extends ImageView {

    public SourceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        Log.e("TTT" , "AAA SourceView bm width " + bm.getWidth() + "#" + bm.getHeight());
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

        Log.e("TTT" , "AAA SourceView getDrawable().getBounds().width()" + getDrawable().getBounds().width());
    }
}
