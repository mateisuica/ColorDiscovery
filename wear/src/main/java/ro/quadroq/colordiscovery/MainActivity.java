package ro.quadroq.colordiscovery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DismissOverlayView;
import android.text.TextUtils;
import android.util.Log;

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

import ro.quadroq.commonclasses.Constants;
import ro.quadroq.commonclasses.Utils;
import ro.quadroq.commonclasses.colorgenerator.ColorGeneratorView;

public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";
    private DismissOverlayView mDismissOverlay;

    GoogleApiClient mGoogleApiClient;
    private String nodeId;
    private ColorGeneratorView colorGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorGenerator = (ColorGeneratorView) findViewById(R.id.wearColorGenerator);

        colorGenerator.setOnLongPressListener(new ColorGeneratorView.LongPressListener() {
            @Override
            public void onLongPress() {
                mDismissOverlay.show();
            }
        });

        colorGenerator.setOnDoubleTapListener(new ColorGeneratorView.DoubleTapListener() {
            @Override
            public void onDoubleTap() {
                ByteBuffer b = ByteBuffer.allocate(4);
                b.order(ByteOrder.BIG_ENDIAN);
                if (nodeId != null && !TextUtils.isEmpty(nodeId)) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, Constants.MESSAGE_PATH, b.putInt(colorGenerator.getColor()).array());

                    Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                            getString(R.string.sent_to_phone));
                    startActivity(intent);
                }
            }
        });

        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.showIntroIfNecessary();

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
        colorGenerator.setColor(getSharedPreferences(Constants.SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE).getInt(Constants.SHARED_PREFERANCE_COLOR, Utils.getRgb()));
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        getSharedPreferences(Constants.SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE).edit().putInt(Constants.SHARED_PREFERANCE_COLOR, colorGenerator.getColor()).apply();
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
