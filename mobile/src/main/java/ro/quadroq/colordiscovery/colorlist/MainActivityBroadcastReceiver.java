package ro.quadroq.colordiscovery.colorlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.database.ColorContentProvider;
import ro.quadroq.colordiscovery.database.SchemaItem;

/**
 * Created by matei.suica on 10/5/2015.
 */
public class MainActivityBroadcastReceiver extends BroadcastReceiver {

    public static final String SCHEMA_SELECTED = "SchemaSelected";
    public static final String SCHEMA_DELETED = "SchemaDeleted";
    public static final String SCHEMA_ID = "SchemaId";

    private final Activity mActivity;

    public MainActivityBroadcastReceiver(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case SCHEMA_SELECTED:
                selectItem(intent.getIntExtra(SCHEMA_ID, -1));
                break;
            case SCHEMA_DELETED:
                selectItem(-1);
                break;
        }
    }

    public boolean selectItem(int id) {
        if (id == -2) {
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
            args.putInt(ColorListFragment.SCHEMA_FILTER, id);
            colorListFragment.setArguments(args);
            ft.replace(R.id.list_fragment, colorListFragment).addToBackStack("").commit();
        }
        return true;
    }
}
