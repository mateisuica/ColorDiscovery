package ro.quadroq.colordiscovery.colordetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ro.quadroq.colordiscovery.R;
import ro.quadroq.colordiscovery.colorlist.MainActivity;
import ro.quadroq.commonclasses.Constants;

/**
 * Created by mateisuica on 23/06/15.
 */
public class ColorDetailsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_details_activity);

        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            ColorDetailsFragment colorDetailsFragment = new ColorDetailsFragment();
            if(getIntent() != null) {
                int colorId = getIntent().getIntExtra(Constants.COLOR_ID, 0);
                Bundle args = new Bundle();
                args.putInt(Constants.COLOR_ID, colorId);
                colorDetailsFragment.setArguments(args);
            }
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, colorDetailsFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;
        }
        return false;
    }
}
