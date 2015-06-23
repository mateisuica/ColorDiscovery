package ro.quadroq.commonclasses.colorgenerator;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import ro.quadroq.commonclasses.Utils;

/**
 * Created by mateisuica on 21/06/15.
 */
public class CodeGeneratorGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG = "CodeGeneratorListener";

    private final Context mContext;
    private ValueAnimator animation;
    private int backgroundColor;
    private final ColorGestureViewHolder viewHolder;
    private ColorGeneratorView.DoubleTapListener doubleTapListener;
    private ColorGeneratorView.LongPressListener longPressListener;


    public CodeGeneratorGestureListener(Context context, int initialColor, ColorGestureViewHolder viewHolder) {
        this.mContext = context;
        this.backgroundColor = initialColor;
        this.viewHolder = viewHolder;
        this.animation = getAnimation(initialColor, Utils.getRgb(), 0);
        viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                animation.setCurrentPlayTime(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public int getColor() {
        return backgroundColor;
    }

    public void setColor(int color) {
        backgroundColor = color;
        if(animation != null) {
            animation.cancel();
        }
        updateUI();
    }

    public void setOnDoubleTapListener(ColorGeneratorView.DoubleTapListener doubleTapListener) {
        this.doubleTapListener = doubleTapListener;
    }

    public void setLongPressListener(ColorGeneratorView.LongPressListener longPressListener) {
        this.longPressListener = longPressListener;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if(animation != null) {
            if (animation.isStarted()) {
                animation.cancel();
            }
            if(viewHolder.seekBar.getVisibility() == View.INVISIBLE) {
                viewHolder.seekBar.setVisibility(View.VISIBLE);
            } else {
                viewHolder.seekBar.setVisibility(View.INVISIBLE);
            }
            return true;
        }
        return false;
    }

        @Override
     public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(viewHolder.seekBar.getVisibility() == View.VISIBLE) {
                int progress = viewHolder.seekBar.getProgress();
                int scrollProgress = (int) distanceX  * (-1);

//                Log.d(TAG, "Progress / scroll progress: " + progress + " " + scrollProgress);
                    if(progress + scrollProgress < 0) {
                        viewHolder.seekBar.setProgress(0);
                    } else if(progress + scrollProgress > 5000) {
                        viewHolder.seekBar.setProgress(5000);
                    } else {
                        viewHolder.seekBar.setProgress(progress + scrollProgress);
                    }
                animation.setCurrentPlayTime(viewHolder.seekBar.getProgress());
                return true;
            }
            return false;
     }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if(doubleTapListener != null) {
            doubleTapListener.onDoubleTap();
        }
         return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if(animation != null) {
            animation.cancel();
        }
        if(longPressListener != null) {
            longPressListener.onLongPress();
        }
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           final float velocityX,  final float velocityY) {
        if(viewHolder.seekBar.getVisibility() == View.INVISIBLE) {
            int maxFlingVelocity = ViewConfiguration.get(mContext).getScaledMaximumFlingVelocity();
            float velocityPercentX = Math.abs(velocityX / maxFlingVelocity);          // the percent is a value in the range of (0, 1]

            if (viewHolder.seekBar.getVisibility() == View.VISIBLE) {
                viewHolder.seekBar.setVisibility(View.INVISIBLE);
            }

            final int toColor = Utils.getRgb();
            if (animation != null) {
                animation.cancel();
            }
            animation = getAnimation(backgroundColor, toColor, velocityPercentX);

            animation.start();
            return true;
        }
        return false;
    }

    private ValueAnimator getAnimation(int initialColor, int finalColor, float velocityPercentX) {
        ValueAnimator animator = ValueAnimator.ofInt(initialColor, finalColor);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new DecelerateInterpolator(1 + velocityPercentX));
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                backgroundColor = (int) animation.getAnimatedValue();
                viewHolder.seekBar.setProgress((int)animation.getCurrentPlayTime());
                updateUI();
            }
        });
        return animator;
    }


    private void updateUI() {
        viewHolder.textView.setText(Utils.getColorString(backgroundColor));
        viewHolder.imageView.setImageDrawable(new ColorDrawable(backgroundColor));
    }
}
