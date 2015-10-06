package ro.quadroq.colordiscovery.coloradd;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.colorlist.ColorListFragment;
import ro.quadroq.colordiscovery.database.ColorContentProvider;
import ro.quadroq.colordiscovery.database.ColorItem;
import ro.quadroq.commonclasses.Constants;
import ro.quadroq.commonclasses.Utils;
import ro.quadroq.commonclasses.colorgenerator.ColorGeneratorView;

/**
 * Created by mateisuica on 21/06/15.
 */
public class AddColorActivity extends Activity {

    private ColorGeneratorView colorGeneratorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_color_activity);
        final int schemaId = getIntent().getIntExtra(ColorListFragment.SCHEMA_FILTER, 0);

        colorGeneratorView = (ColorGeneratorView) findViewById(R.id.colorGenerator);
        colorGeneratorView.setOnDoubleTapListener(new ColorGeneratorView.DoubleTapListener() {
            @Override
            public void onDoubleTap() {
                int color = colorGeneratorView.getColor();
                addColorToDatabase(color, schemaId);
            }
        });
    }

    private void addColorToDatabase(int color, int schemaId) {

        Cursor c = getContentResolver().query(ColorContentProvider.COLOR_CONTENT_URI, null, ColorItem.COLUMN_COLOR + "=?", new String[]{Integer.toString(color)}, null);
        if(c != null) {
            if (c.getCount() <= 0) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(ColorItem.COLUMN_COLOR, color);
                contentValues.put(ColorItem.COLUMN_SCHEMA, schemaId);
                getContentResolver().insert(ColorContentProvider.COLOR_CONTENT_URI, contentValues);
                Toast.makeText(AddColorActivity.this, getString(R.string.color_was_saved, Utils.getColorString(color)), Toast.LENGTH_SHORT).show();
            }
        c.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        colorGeneratorView.setColor(getSharedPreferences(Constants.SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE).getInt(Constants.SHARED_PREFERANCE_COLOR, Utils.getRgb()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        getSharedPreferences(Constants.SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE).edit().putInt(Constants.SHARED_PREFERANCE_COLOR, colorGeneratorView.getColor()).apply();
    }
}
