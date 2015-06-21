package ro.quadroq.sexdice;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import ro.quadroq.commonclasses.Constants;

/**
 * Created by mateisuica on 21/06/15.
 */

public class ColorWatchListenerService extends WearableListenerService {

    private static final String TAG = "WearableService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }
        // Loop through the events and send a message
        // to the node that created the data item.
        String path = messageEvent.getPath();
        if (path.compareTo(Constants.MESSAGE_PATH) == 0) {
            byte[] data = messageEvent.getData();
            ByteBuffer bb = ByteBuffer.wrap(data);
            int color = bb.getInt();
            Cursor c = getContentResolver().query(ColorContentProvider.CONTENT_URI, null, ColorItem.COLUMN_COLOR + "=?", new String[] {Integer.toString(color) }, null);
            if(c != null) {
                if (c.getCount() <= 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ColorItem.COLUMN_COLOR, color);
                    Log.d(TAG, Integer.toHexString(color));
                    getContentResolver().insert(ColorContentProvider.CONTENT_URI, contentValues);
                }
                c.close();
            }
        }
    }
}
