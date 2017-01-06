package ch.ielse.demo.p02;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration_b extends RecyclerView.ItemDecoration {

    private final Drawable mDivider;

    private int mSpace;

    public SpaceItemDecoration_b(int space, int color) {
        mSpace = space;
        mDivider = new ColorDrawable(color);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != 0)
            outRect.top = mSpace;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);

            final int top = child.getBottom();
            final int bottom = child.getBottom() + mSpace;
            final int left = child.getLeft();
            final int right = child.getRight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}