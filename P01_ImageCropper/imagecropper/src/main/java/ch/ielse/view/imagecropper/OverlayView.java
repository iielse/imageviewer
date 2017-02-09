package ch.ielse.view.imagecropper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class OverlayView extends View {
    private int mWidth;
    private int mHeight;
    private int mOverlayWidth;
    private int mOverlayHeight;
    private int mCenterX;
    private int mCenterY;
    private Bitmap mBmpOverlay;
    private boolean isCircleOverlay;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
    }

    public void reset(int outputWidth, int outputHeight, boolean isCircleOverlay) {
        this.isCircleOverlay = isCircleOverlay;
        mOverlayWidth = (int) (Math.min(mWidth, mHeight) * (isCircleOverlay ? 0.70f : 0.95f));
        mOverlayHeight = (int) (1f * mOverlayWidth * outputHeight / outputWidth);
        mBmpOverlay = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(mBmpOverlay);
        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawColor(0x66000000);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        if (isCircleOverlay()) {
            maskCanvas.drawCircle(mCenterX, mCenterY, mOverlayWidth / 2, maskPaint);
        } else {
            maskCanvas.drawRect(mCenterX - mOverlayWidth / 2, mCenterY - mOverlayHeight / 2, mCenterX + mOverlayWidth / 2, mCenterY + mOverlayHeight / 2, maskPaint);
        }
        maskPaint.setXfermode(null);
        maskPaint.setColor(Color.WHITE);
        maskPaint.setStyle(Paint.Style.STROKE);
        maskPaint.setStrokeWidth(4);
        if (isCircleOverlay()) {
            maskCanvas.drawCircle(mCenterX, mCenterY, mOverlayWidth / 2, maskPaint);
        } else {
            maskCanvas.drawRect(mCenterX - mOverlayWidth / 2, mCenterY - mOverlayHeight / 2, mCenterX + mOverlayWidth / 2, mCenterY + mOverlayHeight / 2, maskPaint);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBmpOverlay, 0, 0, null);
    }

    public int getOverlayWidth() {
        return mOverlayWidth;
    }

    public int getOverlayHeight() {
        return mOverlayHeight;
    }

    public boolean isCircleOverlay() {
        return isCircleOverlay && mOverlayWidth == mOverlayHeight;
    }
}
