package ro.quadroq.sexdice.colordetails;

import android.app.Activity;
import android.os.Bundle;

import ro.quadroq.sexdice.R;

/**
 * Created by mateisuica on 23/06/15.
 */
public class ColorDetailsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_details_layout);
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            ColorDetailsFragment colorDetailsFragment = new ColorDetailsFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, colorDetailsFragment).commit();
        }

    }
}
