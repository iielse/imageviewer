package ch.ielse.demo.p01;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by LY on 2017/2/6.
 */

public class ImageCropper extends FrameLayout {

    private ImageView iBack;
    private TextView tSubmit;
    private TextView tTitle;

    private int Padding;
    private int tHeight;


    public ImageCropper(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
        DisplayMetrics mDisplayMetrics = context.getResources().getDisplayMetrics();
        Padding = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, mDisplayMetrics) + 0.5f);
        tHeight = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, mDisplayMetrics) + 0.5f);

        setPadding(0, Utils.calcStatusBarHeight(context), 0, 0);

        tTitle = new TextView(context);
//        tTitle.setBackgroundColor(0xFFCC00FF);
        tTitle.setTextColor(0xFFFFFFFF);
        tTitle.setText("移动和缩放");
        LayoutParams lpTitle = generateLayoutPramas();
        lpTitle.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        tTitle.setLayoutParams(lpTitle);

        iBack = new ImageView(context);
        iBack.setImageResource(R.mipmap.back);
        LayoutParams lpBack = generateLayoutPramas();
        iBack.setLayoutParams(lpBack);

        tSubmit = new TextView(context);
//        tSubmit.setBackgroundColor(0xFF00CC55);
        LayoutParams lpSubmit = generateLayoutPramas();
        lpTitle.gravity = Gravity.RIGHT | Gravity.TOP;
        tSubmit.setTextColor(0xFFFFFFFF);
        tSubmit.setText("确定");
        tSubmit.setLayoutParams(lpSubmit);

        addView(tTitle);
        addView(iBack);
        addView(tSubmit);
    }

    public static void crop(Activity act, String path) {
        ImageCropper imageCropper;

        FrameLayout actDecorView = ((FrameLayout) act.getWindow().getDecorView());
        actDecorView.addView(imageCropper = new ImageCropper(act));
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private FrameLayout.LayoutParams generateLayoutPramas() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, tHeight);
    }
}
