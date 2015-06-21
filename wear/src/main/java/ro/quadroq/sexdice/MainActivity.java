package ro.quadroq.sexdice;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DismissOverlayView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import ro.quadroq.commonclasses.Utils;

public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";
    private ImageView imageView;
    private GestureDetector mDetector;
    private DismissOverlayView mDismissOverlay;
    int backgroundColor;
    private ValueAnimator animation;
    private int maxFlingVelocity;
    private TextView textView;

    GoogleApiClient mGoogleApiClient;
    private String nodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        backgroundColor = Utils.getRgb();

        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);

        mDismissOverlay.showIntroIfNecessary();
        mDetector = new GestureDetector(this, new MyGestureListener());
        maxFlingVelocity = ViewConfiguration.get(this).getScaledMaximumFlingVelocity();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API

                        PendingResult<NodeApi.GetConnectedNodesResult> result = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
                        result.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                List<Node> nodes = getConnectedNodesResult.getNodes();

                                if (nodes.size() > 0) {
                                    nodeId = nodes.get(0).getId();
                                }

                            }
                        });


                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        backgroundColor = getSharedPreferences("randomColor", Context.MODE_PRIVATE).getInt("Color", Utils.getRgb());
        updateUI();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        getSharedPreferences("randomColor", Context.MODE_PRIVATE).edit().putInt("Color", backgroundColor).apply();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(animation != null) {
                if (!animation.isPaused()) {
                    animation.pause();
                    return true;
                }
            }
            return false;

        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.order(ByteOrder.BIG_ENDIAN);
            if (nodeId != null && !TextUtils.isEmpty(nodeId)) {
                Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "/savedColor", b.putInt(backgroundColor).array());

                Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                        getString(R.string.sent_to_phone));
                startActivity(intent);
            }            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(animation != null) {
                animation.cancel();
            }
            mDismissOverlay.show();
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               final float velocityX,  final float velocityY) {

            float velocityPercentX    = Math.abs(velocityX / maxFlingVelocity);          // the percent is a value in the range of (0, 1]

            final int toColor = Utils.getRgb();
            if(animation != null) {
                animation.cancel();
            }
            animation = ValueAnimator.ofArgb(backgroundColor, toColor);
            animation.setDuration(Math.round(10000 / Math.abs(1 + velocityPercentX)));
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
    }



    private void updateUI() {
        textView.setText(Utils.getColorString(backgroundColor));
        imageView.setImageDrawable(new ColorDrawable(backgroundColor));
    }

}
