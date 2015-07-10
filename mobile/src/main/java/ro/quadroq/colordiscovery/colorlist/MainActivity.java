package ro.quadroq.colordiscovery.colorlist;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.coloradd.AddColorActivity;
import ro.quadroq.colordiscovery.colordetails.ColorDetailsActivity;
import ro.quadroq.colordiscovery.database.ColorContentProvider;
import ro.quadroq.colordiscovery.database.ColorItem;
import ro.quadroq.commonclasses.Constants;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // private Cursor cursor;
    private ColorsCursorAdapter adapter;
    private RecyclerView colorList;
    private CardView noColorsSign;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorList = (RecyclerView) findViewById(R.id.colorList);
        noColorsSign = (CardView) findViewById(R.id.no_color_sign);
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.addButton);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddColorActivity.class);
                startActivity(intent);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
        colorList.setLayoutManager(new LinearLayoutManager(this));
        colorList.addOnItemTouchListener(new ColorListOnItemTouchListener(this, new ColorListOnItemTouchListener.OnItemClickListener() {

            @Override
            public void onItemClick(final View childView, final int position) {

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
                        Cursor c = adapter.getCursor();
                        c.moveToPosition(position);
                        int color = c.getInt(c.getColumnIndex(ColorItem.COLUMN_ID));

                        Intent intent = new Intent(MainActivity.this, ColorDetailsActivity.class);
                        intent.putExtra(Constants.COLOR_ID, color);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                positionAnimator.start();
//                int colorCode = c.getInt(c.getColumnIndex(ColorItem.COLUMN_COLOR));
//                setColorWallpaper(colorCode);

            }

            @Override
            public void onItemLongPress(final View childView, final int position) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                final int colorId = c.getInt(c.getColumnIndex(ColorItem.COLUMN_ID));
                final int colorColor = c.getInt(c.getColumnIndex(ColorItem.COLUMN_COLOR));

                Snackbar.make(findViewById(R.id.snackbarPosition), R.string.color_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(ColorItem.COLUMN_COLOR, colorColor);
                                getContentResolver().insert(ColorContentProvider.CONTENT_URI, contentValues);
                            }
                        })
                        .show();
                getContentResolver().delete(ColorContentProvider.CONTENT_URI, ColorItem.COLUMN_ID + "=?", new String[]{Integer.toString(colorId)});
            }
        }));
        adapter = new ColorsCursorAdapter(null);
        colorList.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this);
    }

//    private void setColorWallpaper(int colorCode) {
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        Bitmap image = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
//        image.eraseColor(colorCode);
//        WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
//        try {
//            wallpaperManager.setBitmap(image);
//        } catch (IOException e) {
//
//        }
//    }

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

}
