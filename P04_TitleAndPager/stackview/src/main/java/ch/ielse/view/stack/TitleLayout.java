package ch.ielse.view.stack;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import java.util.Stack;


public class TitleLayout extends FrameLayout {
    private boolean isTranslucentStatus;
    private int statusBarHeight;
    private final Stack<View> mContents = new Stack<>();

    public TitleLayout(Context context) {
        this(context, null);
    }

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        isTranslucentStatus = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) getContext()).getWindow();
            isTranslucentStatus = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN == (window.getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        statusBarHeight = Utils.calculateStatusBarHeight(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int resultHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            resultHeight = heightSize;
        } else {
            resultHeight = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 47f, getResources().getDisplayMetrics()) + 0.5f);
        }

        resultHeight += isTranslucentStatus ? statusBarHeight : 0;

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(resultHeight, MeasureSpec.EXACTLY));
    }


    public void push(View v) {
        mContents.push(v);
        //v.setPadding();

        if (isTranslucentStatus && v instanceof TitleComponent) {
            ((TitleComponent) v).dealTranslucentStatusTheme(statusBarHeight);
        }
        addView(v);
    }

    public void put(View v) {
        View replaced = mContents.pop();
        removeView(replaced);
        push(v);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
    }

    public interface TitleComponent {
        View left();

        View center();

        View right();

        void dealTranslucentStatusTheme(int statusBarHeight);
    }
}
