package ro.quadroq.colordiscovery.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by mateisuica on 21/06/15.
 */
public class ColorsDatabaseHelper extends SQLiteOpenHelper {

    public static final String COLOR_TABLE = "color";
    public static final String SCHEMA_TABLE = "schema";
    private static final String DBNAME = "colors.db";
    private static final int version = 3;

    private static final String SQL_CREATE_COLORS = "CREATE TABLE " +
            COLOR_TABLE + " " +                       // Table's name
            "(" +                           // The columns in the table
            " " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + ColorItem.COLUMN_NAME + " TEXT, " +
            " " + ColorItem.COLUMN_COLOR + " INTEGER, " +
            " " + ColorItem.COLUMN_SCHEMA + " INTEGER DEFAULT 0"
            + ")";

    private static final String SQL_CREATE_SCHEMA = "CREATE TABLE " +
            SCHEMA_TABLE + " " +
            "(" +                           // The columns in the table
            " " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " " + SchemaItem.COLUMN_NAME + " TEXT " + ")";

    private static final String SQL_ADD_UNCATEGORIZED_SCHEMA = "INSERT INTO " + SCHEMA_TABLE + " values(0, \"Uncategorized\")";


    public ColorsDatabaseHelper(Context context) {
        super(context, DBNAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creates the main table
        db.execSQL(SQL_CREATE_COLORS);
        db.execSQL(SQL_CREATE_SCHEMA);
        db.execSQL(SQL_ADD_UNCATEGORIZED_SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + COLOR_TABLE + " ADD COLUMN  " + ColorItem.COLUMN_NAME + " TEXT;");
            case 2:
                db.execSQL(SQL_CREATE_SCHEMA);
                db.execSQL(SQL_ADD_UNCATEGORIZED_SCHEMA);
                db.execSQL("ALTER TABLE " + COLOR_TABLE + " ADD COLUMN  " + ColorItem.COLUMN_SCHEMA + " INTEGER DEFAULT 0;");
                break;
            default:
                throw new IllegalStateException(
                        "onUpgrade() with unknown oldVersion" + oldVersion);
        }
    }
}
