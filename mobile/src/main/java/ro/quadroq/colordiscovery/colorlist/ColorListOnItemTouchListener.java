package ro.quadroq.colordiscovery.colorlist;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

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
                if(childView != null) {
                    ValueAnimator positionAnimator = ValueAnimator.ofFloat(childView.getX(), childView.getX() - childView.getWidth() / 2, childView.getX());
                    positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    positionAnimator.setDuration(500);
                    positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            childView.setX((float) animation.getAnimatedValue());
                        }
                    });
                    positionAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (childView != null && mClickListener != null) {
                                mClickListener.onItemClick(childView, index);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    positionAnimator.start();
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
        index = rv.getChildAdapterPosition(childView);
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

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
