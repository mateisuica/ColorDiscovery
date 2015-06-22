package ro.quadroq.commonclasses.colorgenerator;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import ro.quadroq.commonclasses.Utils;

/**
 * Created by mateisuica on 21/06/15.
 */
public class CodeGeneratorGestureListener extends GestureDetector.SimpleOnGestureListener {

    private final Context mContext;
    private ValueAnimator animation;
    private int backgroundColor;
    private final TextView textView;
    private final ImageView imageView;
    private ColorGeneratorView.DoubleTapListener doubleTapListener;
    private ColorGeneratorView.LongPressListener longPressListener;


    public CodeGeneratorGestureListener(Context context, int initialColor, TextView textView, ImageView imageView) {
        this.mContext = context;
        this.backgroundColor = initialColor;
        this.textView = textView;
        this.imageView = imageView;
        this.animation = new ValueAnimator();
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
                return true;
            }
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

        int maxFlingVelocity = ViewConfiguration.get(mContext).getScaledMaximumFlingVelocity();
        float velocityPercentX    = Math.abs(velocityX / maxFlingVelocity);          // the percent is a value in the range of (0, 1]

        final int toColor = Utils.getRgb();
        if(animation != null) {
            animation.cancel();
        }
        animation = ValueAnimator.ofInt(backgroundColor, toColor);
        animation.setEvaluator(new ArgbEvaluator());
        animation.setInterpolator(new DecelerateInterpolator(1 + velocityPercentX));
        animation.setDuration(5000);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                backgroundColor = (int) animation.getAnimatedValue();
                updateUI();
            }

        });

        animation.start();

        return true;
    }



    private void updateUI() {
        textView.setText(Utils.getColorString(backgroundColor));
        imageView.setImageDrawable(new ColorDrawable(backgroundColor));
    }
}
