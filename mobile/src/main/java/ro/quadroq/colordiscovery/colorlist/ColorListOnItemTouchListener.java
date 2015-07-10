package ro.quadroq.colordiscovery.colorlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mateisuica on 23/06/15.
 */
public class ColorListOnItemTouchListener implements RecyclerView.OnItemTouchListener {

    private final GestureDetector mGestureDetector;
    private final OnItemClickListener mClickListener;
    private View childView;
    private int index;

    public ColorListOnItemTouchListener(Context context, OnItemClickListener clickListener) {
        mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                    if(childView != null && mClickListener != null) {
                        mClickListener.onItemLongPress(childView, index);
                    }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(childView != null && mClickListener != null) {
                    mClickListener.onItemClick(childView, index);
                }
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        childView = rv.findChildViewUnder(e.getX(), e.getY());
        index = rv.getChildPosition(childView);
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    /**
     * A click listener for items.
     */
    public interface OnItemClickListener {

        /**
         * Called when an item is clicked.
         *
         * @param childView View of the item that was clicked.
         * @param position  Position of the item that was clicked.
         */
        void onItemClick(View childView, int position);

        /**
         * Called when an item is long pressed.
         *
         * @param childView View of the item that was long pressed.
         * @param position  Position of the item that was long pressed.
         */
        void onItemLongPress(View childView, int position);

    }


}
