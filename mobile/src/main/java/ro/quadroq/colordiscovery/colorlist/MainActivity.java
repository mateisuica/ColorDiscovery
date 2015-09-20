package ro.quadroq.colordiscovery.colorlist;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.FrameLayout;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.colordetails.ColorDetailsActivity;
import ro.quadroq.colordiscovery.colordetails.ColorDetailsFragment;
import ro.quadroq.colordiscovery.database.ColorContentProvider;
import ro.quadroq.colordiscovery.database.SchemaItem;
import ro.quadroq.commonclasses.Constants;


public class MainActivity extends AppCompatActivity implements ColorListFragment.OnColorSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private Toolbar toolbar;
    // private Cursor cursor;
    int selectedColor;
    private FrameLayout detailsContainer;
    private Menu drawerMenu;
    private SubMenu subMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        detailsContainer = (FrameLayout) findViewById(R.id.details_fragment);

        if (savedInstanceState != null) {
            selectedColor = savedInstanceState.getInt(Constants.COLOR_ID);
            if(selectedColor != 0) {
                onColorSelected(selectedColor);
            }
            return;
        }

        if (findViewById(R.id.list_fragment) != null) {
            ColorListFragment colorListFragment = new ColorListFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.list_fragment, colorListFragment).commit();
        }

        if (detailsContainer != null && selectedColor != 0) {
            ColorDetailsFragment colorDetailsFragment = getColorDetailsFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.details_fragment, colorDetailsFragment).commit();
        }

        NavigationView drawer = (NavigationView)findViewById(R.id.navigationDrawer);
        drawerMenu = drawer.getMenu();
        drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Log.d("MENU ITEM", "User clicked " + Integer.toString(menuItem.getItemId()) + " with the title:  " + menuItem.getTitle());
                return false;
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(Constants.COLOR_ID, selectedColor);
    }

    @Override
    public void onColorSelected(int colorId) {
        selectedColor = colorId;
        if(detailsContainer != null) {
            ColorDetailsFragment colorDetailsFragment = getColorDetailsFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.details_fragment, colorDetailsFragment).commit();
        } else {
            Intent intent = new Intent(this, ColorDetailsActivity.class);
            intent.putExtra(Constants.COLOR_ID, selectedColor);
            startActivity(intent);
        }
    }

    private ColorDetailsFragment getColorDetailsFragment() {
        ColorDetailsFragment colorDetailsFragment = new ColorDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.COLOR_ID, selectedColor);
        colorDetailsFragment.setArguments(args);
        return colorDetailsFragment;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        ColorContentProvider.SCHEMA_CONTENT_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(drawerMenu != null) {
            removeSchemas();
            if(data != null) {
                subMenu = drawerMenu.addSubMenu("Schemas");
                while(data.moveToNext()) {
                    int id = data.getInt(data.getColumnIndex(SchemaItem._ID));
                    String name = data.getString(data.getColumnIndex(SchemaItem.COLUMN_NAME));
                    subMenu.add(Menu.NONE, id, Menu.NONE, name);
                }
                subMenu.add(Menu.NONE, -1, Menu.NONE, "Add new schema");
                data.close();
            }

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        removeSchemas();
    }

    private void removeSchemas() {
        if(subMenu != null) {
            subMenu.clear();
        }
    }
}
