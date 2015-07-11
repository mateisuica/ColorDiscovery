package ro.quadroq.colordiscovery.colorlist;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.coloradd.AddColorActivity;
import ro.quadroq.colordiscovery.database.ColorContentProvider;
import ro.quadroq.colordiscovery.database.ColorItem;

/**
 * Created by mateisuica on 11/07/15.
 */
public class ColorListFragment extends Fragment  implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private ColorsCursorAdapter adapter;
    private RecyclerView colorList;
    private CardView noColorsSign;
    OnColorSelectedListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnColorSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.color_list_fragment, container, false);
        colorList = (RecyclerView) root.findViewById(R.id.colorList);
        noColorsSign = (CardView) root.findViewById(R.id.no_color_sign);

        colorList.setLayoutManager(new LinearLayoutManager(getActivity()));
        colorList.addOnItemTouchListener(new ColorListOnItemTouchListener(getActivity(), new ColorListOnItemTouchListener.OnItemClickListener() {

            @Override
            public void onItemClick(final View childView, final int position) {

                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                int color = c.getInt(c.getColumnIndex(ColorItem.COLUMN_ID));

                if(mListener != null) {
                    mListener.onColorSelected(color);
                }

//                int colorCode = c.getInt(c.getColumnIndex(ColorItem.COLUMN_COLOR));
//                setColorWallpaper(colorCode);

            }

            @Override
            public void onItemLongPress(final View childView, final int position) {
                Cursor c = adapter.getCursor();
                c.moveToPosition(position);
                final int colorId = c.getInt(c.getColumnIndex(ColorItem.COLUMN_ID));
                final int colorColor = c.getInt(c.getColumnIndex(ColorItem.COLUMN_COLOR));

                Snackbar.make(root.findViewById(R.id.snackbarPosition), R.string.color_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(ColorItem.COLUMN_COLOR, colorColor);
                                getActivity().getContentResolver().insert(ColorContentProvider.CONTENT_URI, contentValues);
                            }
                        })
                        .show();
                getActivity().getContentResolver().delete(ColorContentProvider.CONTENT_URI, ColorItem.COLUMN_ID + "=?", new String[]{Integer.toString(colorId)});
            }
        }));
        adapter = new ColorsCursorAdapter(null);
        colorList.setAdapter(adapter);

        FloatingActionButton button = (FloatingActionButton) root.findViewById(R.id.addButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddColorActivity.class);
                startActivity(intent);
            }
        });



        return root;
    }

    public void setColorList(Cursor data) {
        if(data != null) {

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ColorContentProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
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

    public interface OnColorSelectedListener {
        public void onColorSelected(int colorId);
    }

}
