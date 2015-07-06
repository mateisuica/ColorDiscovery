package ro.quadroq.colordiscovery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mateisuica on 21/06/15.
 */
public class ColorsDatabaseHelper extends SQLiteOpenHelper {

    public static final String COLOR_TABLE = "color";
    private static final String DBNAME = "colors.db";
    private static final int version = 2;

    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +
            COLOR_TABLE + " " +                       // Table's name
            "(" +                           // The columns in the table
            " " + ColorItem.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + ColorItem.COLUMN_NAME + " TEXT, " +
            " " + ColorItem.COLUMN_COLOR + " INTEGER" + ")";

    public ColorsDatabaseHelper(Context context) {
        super(context, DBNAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates the main table
        db.execSQL(SQL_CREATE_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + COLOR_TABLE + " ADD COLUMN  " + ColorItem.COLUMN_NAME + " TEXT;");
                break;
            default:
                throw new IllegalStateException(
                        "onUpgrade() with unknown oldVersion" + oldVersion);
        }
    }
}
