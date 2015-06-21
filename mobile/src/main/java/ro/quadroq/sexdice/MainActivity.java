package ro.quadroq.sexdice;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // private Cursor cursor;
    private ColorsCursorAdapter adapter;
    private ListView colorList;
    private LinearLayout noColorsSign;

    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MobileMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorList = (ListView) findViewById(R.id.colorList);
        noColorsSign = (LinearLayout) findViewById(R.id.no_color_sign);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
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

        colorList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                int color = c.getInt(c.getColumnIndex(ColorItem.COLUMN_COLOR));

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");

                // Add data to the intent, the receiving app will decide what to do with it.
                intent.putExtra(Intent.EXTRA_SUBJECT, "Look at this color");
                intent.putExtra(Intent.EXTRA_TEXT, "I've discovered this new cool color using " + getString(R.string.app_name) + "! The color code is: " + getColorString(color));
                startActivity(Intent.createChooser(intent, "Share the color"));
            }
        });

        colorList.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getAdapter().getItem(position);
                int colorId = c.getInt(c.getColumnIndex(ColorItem.COLUMN_ID));
                getContentResolver().delete(ColorContentProvider.CONTENT_URI, ColorItem.COLUMN_ID + "=?", new String[]{Integer.toString(colorId)});
                Wearable.DataApi.deleteDataItems(mGoogleApiClient, Uri.parse("wear://savedColor"));
                return true;
            }
        });
        adapter = new ColorsCursorAdapter(this, null, 0);
        colorList.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ColorContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() > 0) {
            colorList.setVisibility(View.VISIBLE);
            noColorsSign.setVisibility(View.GONE);
        } else {
            colorList.setVisibility(View.GONE);
            noColorsSign.setVisibility(View.VISIBLE);
        }
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private String getColorString(int color) {
        return "#" + Integer.toHexString(color).substring(2).toUpperCase();
    }
}
