package ro.quadroq.colordiscovery.colordetails;

import android.app.Fragment;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.database.ColorContentProvider;
import ro.quadroq.colordiscovery.database.ColorItem;
import ro.quadroq.colordiscovery.database.SchemaItem;
import ro.quadroq.commonclasses.Constants;
import ro.quadroq.commonclasses.Utils;

/**
 * Created by mateisuica on 23/06/15.
 */
public class ColorDetailsFragment extends Fragment {


    private int colorId;
    private ImageView imageView;
    private TextView textView;
    private SeekBar redBar;
    private SeekBar blueBar;
    private SeekBar greenBar;
    private int colorCode;
    private String customName;
    private EditText colorNewName;
    private Spinner schemaSpinner;
    private int schemaId = 0;
    private String schemaName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            colorId = getArguments().getInt(Constants.COLOR_ID);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.color_details_fragment, container, false);
        imageView = (ImageView) root.findViewById(R.id.colorPreview);
        textView = (TextView) root.findViewById(R.id.colorName);
        colorNewName = (EditText) root.findViewById(R.id.colorNewName);
        redBar = (SeekBar) root.findViewById(R.id.redBar);
        blueBar = (SeekBar) root.findViewById(R.id.blueBar);
        greenBar = (SeekBar) root.findViewById(R.id.greenBar);
        schemaSpinner = (Spinner) root.findViewById(R.id.schema_spinner);

        Cursor c = getActivity().getContentResolver().query(ColorContentProvider.SCHEMA_CONTENT_URI, null, null, null, null);
        c.setNotificationUri(getActivity().getContentResolver(), ColorContentProvider.SCHEMA_CONTENT_URI);
        SimpleCursorAdapter sca = new SimpleCursorAdapter(getActivity(), R.layout.spinner_item, c, new String[] {SchemaItem.COLUMN_NAME}, new int[]{R.id.spinnerItemTextView}, 0);
        schemaSpinner.setAdapter(sca);
        schemaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                schemaId = c.getInt(c.getColumnIndex(BaseColumns._ID));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (Color.red(colorCode) != progress) {
                    colorCode = colorCode & 0xFF00FFFF;
                    colorCode = colorCode | (progress << 16);
                    bindDataToUI(colorCode, customName);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (Color.green(colorCode) != progress) {
                    colorCode = colorCode & 0xFFFF00FF;
                    colorCode = colorCode | (progress << 8);
                    bindDataToUI(colorCode, customName);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (Color.blue(colorCode) != progress) {
                    colorCode = colorCode & 0xFFFFFF00;
                    colorCode = colorCode | progress;
                    bindDataToUI(colorCode, customName);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        refreshDBdata();
        final EditText colorNewName = (EditText) root.findViewById(R.id.colorNewName);
        FloatingActionButton button = (FloatingActionButton) root.findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String colorNewNameString = colorNewName.getText().toString();
                ContentValues contentValues = new ContentValues();
                contentValues.put(ColorItem.COLUMN_NAME, colorNewNameString);
                contentValues.put(ColorItem.COLUMN_COLOR, colorCode);
                contentValues.put(ColorItem.COLUMN_SCHEMA, schemaId);
                int updated = getActivity().getContentResolver().update(ColorContentProvider.COLOR_CONTENT_URI, contentValues,
                        BaseColumns._ID + "=?", new String[]{Integer.toString(colorId)});
                if(updated > 0) {
                    Toast.makeText(getActivity(), R.string.changes_saved, Toast.LENGTH_SHORT).show();
                    refreshDBdata();
                } else {
                    Toast.makeText(getActivity(), R.string.changes_problem, Toast.LENGTH_SHORT).show();
                }

            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_subject) + " " + Utils.getColorString(colorCode));
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_wallpaper:
                setColorWallpaper(colorCode);
                Toast.makeText(getActivity(), R.string.wallpaper_changed, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_share:
                setColorWallpaper(colorCode);
                Toast.makeText(getActivity(), R.string.wallpaper_changed, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }

    }

    private void refreshDBdata() {
        Cursor colorCursor = getActivity().getContentResolver().query(ColorContentProvider.COLOR_CONTENT_URI,
                null, BaseColumns._ID + "=?", new String[]{Integer.toString(colorId)}, null);

        if(colorCursor != null) {
            if(colorCursor.getCount() > 0) {
                colorCursor.moveToFirst();
                colorCode = colorCursor.getInt(colorCursor.getColumnIndex(ColorItem.COLUMN_COLOR));
                customName = colorCursor.getString(colorCursor.getColumnIndex(ColorItem.COLUMN_NAME));
                if(!colorCursor.isNull(colorCursor.getColumnIndex(ColorItem.COLUMN_SCHEMA))) {
                    schemaId = colorCursor.getInt(colorCursor.getColumnIndex(ColorItem.COLUMN_SCHEMA));
                }
                if(schemaId != 0) {
                    Cursor schemaCursor = getActivity().getContentResolver().query(ColorContentProvider.SCHEMA_CONTENT_URI,
                            null, BaseColumns._ID + "=?", new String[]{Integer.toString(schemaId)}, null);
                    if(schemaCursor != null) {
                        schemaCursor.moveToFirst();
                        schemaName = schemaCursor.getString(schemaCursor.getColumnIndex(SchemaItem.COLUMN_NAME));
                    }
                    SpinnerAdapter spa = schemaSpinner.getAdapter();
                    for(int i  = 0; i < spa.getCount(); i++) {
                        Cursor c = (Cursor) spa.getItem(i);
                        if(c.getString(c.getColumnIndex(SchemaItem.COLUMN_NAME)).equals(schemaName)) {
                            schemaSpinner.setSelection(i);
                            break;
                        }
                    }
                } else {
                    schemaSpinner.setSelection(0);
                }
                colorNewName.setText(customName);
                bindDataToUI(colorCode, customName);
            }
            colorCursor.close();
        }
    }



    private void bindDataToUI(int color, String customName) {

        imageView.setImageDrawable(new ColorDrawable(color));
        String colorName = Utils.getColorString(color);
        if(!TextUtils.isEmpty(customName)) {
            colorName = colorName + " - " + customName;
        }
        textView.setText(colorName);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        Rect redBounds = redBar.getProgressDrawable().getBounds();
        Rect greenBounds = greenBar.getProgressDrawable().getBounds();
        Rect blueBounds = blueBar.getProgressDrawable().getBounds();
        redBar.setProgressDrawable(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.rgb(0, green, blue), Color.rgb(255, green, blue)}));
        redBar.getProgressDrawable().setBounds(redBounds);
        redBar.setProgress(red);
        greenBar.setProgressDrawable(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.rgb(red, 0, blue), Color.rgb(red, 255, blue)}));
        greenBar.getProgressDrawable().setBounds(greenBounds);
        greenBar.setProgress(green);
        blueBar.setProgressDrawable(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.rgb(red, green, 0), Color.rgb(red, green, 255)}));
        blueBar.getProgressDrawable().setBounds(blueBounds);
        blueBar.setProgress(blue);

    }

    private void setColorWallpaper(int colorCode) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Bitmap image = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
        image.eraseColor(colorCode);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getActivity());
        try {
            wallpaperManager.setBitmap(image);
        } catch (IOException e) {

        }
    }
}
