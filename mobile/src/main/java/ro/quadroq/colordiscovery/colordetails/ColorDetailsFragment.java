package ro.quadroq.colordiscovery.colordetails;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.database.ColorContentProvider;
import ro.quadroq.colordiscovery.database.ColorItem;
import ro.quadroq.commonclasses.Constants;
import ro.quadroq.commonclasses.Utils;

/**
 * Created by mateisuica on 23/06/15.
 */
public class ColorDetailsFragment extends Fragment {


    private Cursor colorCursor;
    private int colorId;
    private ImageView imageView;
    private TextView textView;
    private SeekBar redBar;
    private SeekBar blueBar;
    private SeekBar greenBar;
    private int colorCode;
    private String customName;
    private EditText colorNewName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            colorId = getArguments().getInt(Constants.COLOR_ID);
        }
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
                int updated = getActivity().getContentResolver().update(ColorContentProvider.CONTENT_URI, contentValues,
                        ColorItem.COLUMN_ID + "=?", new String[]{Integer.toString(colorId)});
                if(updated > 0) {
                    Toast.makeText(getActivity(), "The changes were saved successfully!", Toast.LENGTH_SHORT).show();
                    refreshDBdata();
                } else {
                    Toast.makeText(getActivity(), "There was a problem saving the changes!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return root;
    }

    private void refreshDBdata() {
        colorCursor = getActivity().getContentResolver().query(ColorContentProvider.CONTENT_URI,
                null, ColorItem.COLUMN_ID + "=?", new String[]{Integer.toString(colorId)}, null);
        if(colorCursor != null) {
            if(colorCursor.getCount() > 0) {
                colorCursor.moveToFirst();
                colorCode = colorCursor.getInt(colorCursor.getColumnIndex(ColorItem.COLUMN_COLOR));
                customName = colorCursor.getString(colorCursor.getColumnIndex(ColorItem.COLUMN_NAME));
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
}
