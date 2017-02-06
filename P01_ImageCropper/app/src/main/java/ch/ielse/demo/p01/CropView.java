package ch.ielse.demo.p01;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by LY on 2017/2/6.
 */

public class CropView extends View implements GestureDetector.OnGestureListener {
    private final Paint mPaint = new Paint();

    private static final int TOUCH_MODE_NONE = 0; // 无状态
    private static final int TOUCH_MODE_DOWN = 1; // 按下
    private static final int TOUCH_MODE_DRAG = 2; // 单点拖拽
    private static final int TOUCH_MODE_SCALE = 5; // 缩放
    private static final int TOUCH_MODE_LOCK = 6; // 缩放锁定
    private static final int TOUCH_MODE_AUTO_FLING = 7; // 动画中

    private int mWidth;
    private int mHeight;
    private int mCropSize;
    private int mCropLeft, mCropTop;
    private int mCropCenterX, mCropCenterY;
    private Bitmap bmpMask;
    private Bitmap bmpSource;
    private final Rect rSrc = new Rect();
    private final Rect rDst = new Rect();
    private Matrix matrix = new Matrix();
    private int mSourceLeft, mSourceTop;

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mGestureDetector = new GestureDetector(context, this);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCropSize = (int) (Math.min(w, h) * 0.70f);
        mCropLeft = (mWidth - mCropSize) / 2;
        mCropTop = (mHeight - mCropSize) / 2;
        mCropCenterX = mWidth / 2;
        mCropCenterY = mHeight / 2;

        bmpMask = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(bmpMask);
        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawColor(0x44000000);
        maskPaint.setXfermode(xfermode);
        maskCanvas.drawCircle(mCropCenterX, mCropCenterY, mCropSize / 2, maskPaint);
        maskPaint.setXfermode(null);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.avatar, options);

        options.inSampleSize = Utils.calculateInSampleSize(options, mWidth, mHeight);
        Log.e("TTT", "AAA  options.inSampleSize " + options.inSampleSize);
        options.inJustDecodeBounds = false;
        bmpSource = BitmapFactory.decodeResource(getResources(), R.mipmap.avatar, options);

        if (bmpSource.getWidth() >= bmpSource.getHeight() && bmpSource.getHeight() < mCropSize) {
            int valueWidth = (int) (bmpSource.getWidth() * (mCropSize * 1f / bmpSource.getHeight()));
            bmpSource = Bitmap.createScaledBitmap(bmpSource, valueWidth, mCropSize, true);
        } else if (bmpSource.getWidth() < bmpSource.getHeight() && bmpSource.getWidth() < mCropSize) {
            int valueHeight = (int) (bmpSource.getHeight() * (mCropSize * 1f / bmpSource.getWidth()));
            bmpSource = Bitmap.createScaledBitmap(bmpSource, mCropSize, valueHeight, true);
        }

        mSourceLeft = (mWidth - bmpSource.getWidth()) / 2;
        mSourceTop = (mHeight - bmpSource.getHeight()) / 2;
    }

    Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private final GestureDetector mGestureDetector;

//    private float tx, ty;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(mSourceLeft, mSourceTop);
        canvas.drawBitmap(bmpSource, matrix, mPaint);
        canvas.restore();

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        canvas.drawCircle(mCropCenterX, mCropCenterY, mCropSize / 2, mPaint);
        canvas.drawBitmap(bmpMask, 0, 0, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
//        matrix.postScale(1.1f, 1.1f);
//        invalidate();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        matrix.postTranslate(-distanceX, -distanceY);
        Log.e("TTT", "AAA onScroll distanceX " + distanceX);



        invalidate();

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
