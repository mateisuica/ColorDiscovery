package ro.quadroq.colordiscovery.colorlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.database.ColorContentProvider;
import ro.quadroq.colordiscovery.database.SchemaItem;

/**
 * Created by mateisuica on 04/10/15.
 */
public class NavigationViewClickListener implements  NavigationView.OnNavigationItemSelectedListener{

    private final Activity mActivity;

    public NavigationViewClickListener(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == -2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.add_new_schema);
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.add_schema_dialog, null, false);
            final TextView schemaName = (TextView) dialogView.findViewById(R.id.schemaName);
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SchemaItem.COLUMN_NAME, schemaName.getText().toString());
                    mActivity.getContentResolver().insert(ColorContentProvider.SCHEMA_CONTENT_URI, contentValues);
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } else {
            FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
            ColorListFragment colorListFragment = new ColorListFragment();
            Bundle args = new Bundle();
            args.putInt(ColorListFragment.SCHEMA_FILTER, menuItem.getItemId());
            colorListFragment.setArguments(args);
            ft.replace(R.id.list_fragment, colorListFragment).addToBackStack("").commit();
        }
        return true;
    }

}
