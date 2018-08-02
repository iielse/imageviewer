package ch.ielse.demo.p02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * QQ 517309507
 * 参考 http://www.jianshu.com/p/e742df6f59e2
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mDividerPaint = new Paint();
    private DisplayMetrics mDisplayMetrics;
    private int mSpace;
    private int mEdgeSpace;

    public SpaceItemDecoration(Context context) {
        mDisplayMetrics = context.getResources().getDisplayMetrics();
    }

    public SpaceItemDecoration setSpace(int space) {
        mSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space, mDisplayMetrics) + 0.5f);
        return this;
    }

    public SpaceItemDecoration setEdgeSpace(int edgeSpace) {
        mEdgeSpace = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, edgeSpace, mDisplayMetrics) + 0.5f);
        return this;
    }

    public SpaceItemDecoration setSpaceColor(int spaceColor) {
        mDividerPaint.setColor(spaceColor);
        return this;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int childPosition = parent.getChildAdapterPosition(view);
        final int itemCount = parent.getAdapter().getItemCount();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {
                setGrid(((GridLayoutManager) manager).getOrientation(), ((GridLayoutManager) manager).getSpanCount(), outRect, childPosition, itemCount);
            } else if (manager instanceof LinearLayoutManager) {
                setLinear(((LinearLayoutManager) manager).getOrientation(), outRect, childPosition, itemCount);
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {

            } else if (manager instanceof LinearLayoutManager) {
                drawLinear(((LinearLayoutManager) manager).getOrientation(), c, parent);
            }
        }
    }

    private void setLinear(int orientation, Rect outRect, int childPosition, int itemCount) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            if (childPosition == 0) {
                outRect.set(0, mEdgeSpace, 0, mSpace);
            } else if (childPosition == itemCount - 1) {
                outRect.set(0, 0, 0, mEdgeSpace);
            } else {
                outRect.set(0, 0, 0, mSpace);
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            if (childPosition == 0) {
                outRect.set(mEdgeSpace, 0, mSpace, 0);
            } else if (childPosition == itemCount - 1) {
                outRect.set(0, 0, mEdgeSpace, 0);
            } else {
                outRect.set(0, 0, mSpace, 0);
            }
        }
    }

    private void setGrid(int orientation, int spanCount, Rect outRect, int childPosition, int itemCount) {
        float totalSpace = mSpace * (spanCount - 1) + mEdgeSpace * 2;
        float eachSpace = totalSpace / spanCount;
        int column = childPosition % spanCount;
        int row = childPosition / spanCount;

        float left;
        float right;
        float top;
        float bottom;
        if (orientation == GridLayoutManager.VERTICAL) {
            top = 0;
            bottom = mSpace;

            if (childPosition < spanCount) {
                top = mEdgeSpace;
            }
            if (itemCount % spanCount != 0 && itemCount / spanCount == row ||
                    itemCount % spanCount == 0 && itemCount / spanCount == row + 1) {
                bottom = mEdgeSpace;
            }

            if (spanCount == 1) {
                left = mEdgeSpace;
                right = left;
            } else {
                left = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace;
                right = eachSpace - left;
            }
        } else {
            left = 0;
            right = mSpace;

            if (childPosition < spanCount) {
                left = mEdgeSpace;
            }
            if (itemCount % spanCount != 0 && itemCount / spanCount == row ||
                    itemCount % spanCount == 0 && itemCount / spanCount == row + 1) {
                right = mEdgeSpace;
            }

            if (spanCount == 1) {
                top = mEdgeSpace;
                bottom = top;
            } else {
                top = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace;
                bottom = eachSpace - top;
            }
        }
        outRect.set((int) left, (int) top, (int) right, (int) bottom);
    }


    private void drawLinear(int orientation, Canvas c, RecyclerView parent) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                final int bottom = top + mSpace;
                c.drawRect(left, top, right, bottom, mDividerPaint);
            }

        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin +
                        Math.round(ViewCompat.getTranslationX(child));
                final int right = left + mSpace;
                c.drawRect(left, top, right, bottom, mDividerPaint);
            }
        }
    }
}