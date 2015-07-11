package ro.quadroq.colordiscovery.colorlist;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.colordetails.ColorDetailsActivity;
import ro.quadroq.colordiscovery.colordetails.ColorDetailsFragment;
import ro.quadroq.commonclasses.Constants;


public class MainActivity extends AppCompatActivity implements ColorListFragment.OnColorSelectedListener {

    // private Cursor cursor;
    int selectedColor;
    private FrameLayout detailsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        return super.onOptionsItemSelected(item);
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
}
