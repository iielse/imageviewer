package ch.ielse.demo.p02;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // MeasureSpec.getSize(widthMeasureSpec);
        // todo

        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
