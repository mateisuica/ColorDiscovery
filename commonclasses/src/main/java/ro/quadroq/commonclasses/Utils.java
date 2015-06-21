package ro.quadroq.commonclasses;

import android.graphics.Color;

/**
 * Created by mateisuica on 21/06/15.
 */
public class Utils {

    public static String getColorString(int color) {
        if(color != 0) {
            return "#" + Integer.toHexString(color).substring(2).toUpperCase();
        }
        return "";
    }

    public static int getRgb() {
        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }
}
