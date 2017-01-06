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
 * @author QQ 517309507
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mDividerPaint = new Paint();
    //0xFFECECEC
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
//                setGrid(((GridLayoutManager) manager).getOrientation(), ((GridLayoutManager) manager).getSpanCount(), outRect, childPosition, itemCount);
            } else if (manager instanceof LinearLayoutManager) {
                // manager为LinearLayoutManager时
                setLinear(((LinearLayoutManager) manager).getOrientation(), outRect, childPosition, itemCount);
            }
        }
    }



    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

//        RecyclerView.LayoutManager manager = parent.getLayoutManager();
//        if (manager instanceof LinearLayoutManager) {
//            drawLinear(((LinearLayoutManager) manager).getOrientation(), c, parent);
//
//        }

        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int itemCount = parent.getAdapter().getItemCount();
        if (manager != null) {
            if (manager instanceof GridLayoutManager) {
//                setGrid(((GridLayoutManager) manager).getOrientation(), ((GridLayoutManager) manager).getSpanCount(), outRect, childPosition, itemCount);
            } else if (manager instanceof LinearLayoutManager) {
                // manager为LinearLayoutManager时
                drawLinear(((LinearLayoutManager) manager).getOrientation(), c, parent);
            }
        }
    }

    private void setLinear(int orientation, Rect outRect, int childPosition, int itemCount) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            if (childPosition == 0) {
                outRect.top = mEdgeSpace;
            } else if (childPosition == itemCount - 1) {
                outRect.bottom = mEdgeSpace;
            }
            if (childPosition < itemCount - 1) {
                outRect.bottom += mSpace;
            }
        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
            if (childPosition == 0) {
                outRect.left = mEdgeSpace;
            } else if (childPosition == itemCount - 1) {
                outRect.right = mEdgeSpace;
            }
            if (childPosition < itemCount - 1) {
                outRect.right += mSpace;
            }
        }
    }

    /**
     * 画divider (orientation为vertical)
     *
     * @param c
     * @param parent
     */
    private void drawLinear(int orientation, Canvas c, RecyclerView parent) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
//                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
//                        .getLayoutParams();
//                final int left = child.getRight() + params.rightMargin +
//                        Math.round(ViewCompat.getTranslationX(child));
//                final int right = (int) (left + mSpace);


                int left = child.getLeft();
                int top = child.getTop();
                int right = child.getRight();
                int bottom = child.getBottom();

                c.drawRect(left, top, right, bottom, mDividerPaint);
            }
        }

//        // recyclerView是否设置了paddingLeft和paddingRight
//        if (orientation == LinearLayoutManager.HORIZONTAL) {
//            // 和drawVertical差不多 left right 与 top和bottom对调一下
//            final int top = parent.getPaddingTop();
//            final int bottom = parent.getHeight() - parent.getPaddingBottom();
//            final int childCount = parent.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                final View child = parent.getChildAt(i);
//                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
//                        .getLayoutParams();
//                final int left = child.getRight() + params.rightMargin +
//                        Math.round(ViewCompat.getTranslationX(child));
//                final int right = (int) (left + mSpace);
//                c.drawRect(left, top, right, bottom, mDividerPaint);
//            }
//        } else {
//            final int left = parent.getPaddingLeft();
//            final int right = parent.getWidth() - parent.getPaddingRight();
//            final int childCount = parent.getChildCount();
//            for (int i = 0; i < childCount; i++) {
//                final View child = parent.getChildAt(i);
//                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
//                        .getLayoutParams();
//                // divider的top 应该是 item的bottom 加上 marginBottom 再加上 Y方向上的位移
//                final int top = child.getBottom() + params.bottomMargin +
//                        Math.round(ViewCompat.getTranslationY(child));
//                // divider的bottom就是top加上divider的高度了
//                final int bottom = (int) (top + mSpace);
//                c.drawRect(left, top, right, bottom, mDividerPaint);
//            }
//        }
    }
}