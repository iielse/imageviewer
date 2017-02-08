package ch.ielse.demo.p01;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by LY on 2017/2/6.
 */

public class MaskView extends View {
    private int mWidth;
    private int mHeight;
    private int mCropSize;
    private int mCropCenterX, mCropCenterY;
    private Bitmap bmpMask;

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCropSize = (int) (Math.min(w, h) * 0.70f);
        mCropCenterX = mWidth / 2;
        mCropCenterY = mHeight / 2;

        bmpMask = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(bmpMask);
        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);


        maskCanvas.drawColor(0x44000000);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        maskCanvas.drawCircle(mCropCenterX, mCropCenterY, mCropSize / 2, maskPaint);
        maskPaint.setXfermode(null);

        maskPaint.setColor(0xFFFAEC0E);
        maskPaint.setStyle(Paint.Style.STROKE);
        maskPaint.setStrokeWidth(3);
        maskCanvas.drawCircle(mCropCenterX, mCropCenterY, mCropSize / 2, maskPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmpMask, 0, 0, null);
    }

    public int getCropWidth() {
        return mCropSize;
    }

    public int getCropHeight() {
        return mCropSize;
    }
}
