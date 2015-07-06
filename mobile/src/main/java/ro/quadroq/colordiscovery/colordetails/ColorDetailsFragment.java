package ro.quadroq.colordiscovery.colordetails;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import ro.quadroq.colordiscovery.ColorContentProvider;
import ro.quadroq.colordiscovery.ColorItem;
import ro.quadroq.colordiscovery.R;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            colorId = getArguments().getInt("colorId");
            colorCursor = getActivity().getContentResolver().query(ColorContentProvider.CONTENT_URI,
                    null, ColorItem.COLUMN_ID + "=?", new String[]{Integer.toString(colorId)}, null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.color_details_fragment, container, false);
        imageView = (ImageView) root.findViewById(R.id.colorPreview);
        textView = (TextView) root.findViewById(R.id.colorName);
        redBar = (SeekBar) root.findViewById(R.id.redBar);
        blueBar = (SeekBar) root.findViewById(R.id.blueBar);
        greenBar = (SeekBar) root.findViewById(R.id.greenBar);
        bindDataToUI(colorCursor);
        final EditText colorNewName = (EditText) root.findViewById(R.id.colorNewName);
        FloatingActionButton button = (FloatingActionButton) root.findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String colorNewNameString = colorNewName.getText().toString();

                if (!TextUtils.isEmpty(colorNewNameString)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ColorItem.COLUMN_NAME, colorNewNameString);
                    getActivity().getContentResolver().update(ColorContentProvider.CONTENT_URI, contentValues,
                            ColorItem.COLUMN_ID + "=?", new String[]{Integer.toString(colorId)});
                }
            }
        });
        return root;
    }

    private void bindDataToUI(Cursor c) {
        if(c != null && c.getCount() > 0) {
            c.moveToFirst();
            int colorCode = colorCursor.getInt(colorCursor.getColumnIndex(ColorItem.COLUMN_COLOR));
            imageView.setImageDrawable(new ColorDrawable(colorCode));
            String colorName = Utils.getColorString(colorCode);
            String customName = colorCursor.getString(colorCursor.getColumnIndex(ColorItem.COLUMN_NAME));
            if(customName != null) {
                colorName = colorName + " - " + customName;
            }
            textView.setText(colorName);
            int red = Color.red(colorCode);
            int green = Color.green(colorCode);
            int blue = Color.blue(colorCode);

            redBar.setProgress(red);
            blueBar.setProgress(blue);
            greenBar.setProgress(green);
        }
    }
}
