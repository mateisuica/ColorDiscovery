package ro.quadroq.colordiscovery.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * Created by mateisuica on 21/06/15.
 */
public class ColorContentProvider extends ContentProvider {


    // used for the UriMacher
    private static final int COLORS = 10;
    private static final int COLOR_ID = 20;

    private static final int SCHEMAS = 30;
    private static final int SCHEMA_ID = 40;

    // Creates a UriMatcher object.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    /*
    * Defines a handle to the database helper object. The MainDatabaseHelper class is defined
    * in a following snippet.
    */
    private ColorsDatabaseHelper database;
    private static final String BASE_PATH = "color";
    private static final String AUTHORITY = "ro.quadroq.contentprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);


    static {
        sUriMatcher.addURI(AUTHORITY, BASE_PATH, COLORS);
        sUriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COLOR_ID);
        sUriMatcher.addURI(AUTHORITY, BASE_PATH, SCHEMAS);
        sUriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SCHEMA_ID);
    }

    @Override
    public boolean onCreate() {
        database = new ColorsDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case COLORS:
                break;
            case SCHEMAS:
                break;
            case COLOR_ID:
                queryBuilder.setTables(ColorsDatabaseHelper.COLOR_TABLE);
                // adding the ID to the original query
                queryBuilder.appendWhere(BaseColumns._ID + "="
                        + uri.getLastPathSegment());
                break;
            case SCHEMA_ID:
                queryBuilder.setTables(ColorsDatabaseHelper.SCHEMA_TABLE);
                queryBuilder.appendWhere(BaseColumns._ID + "="
                        + uri.getLastPathSegment());
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        switch (uriType) {
            case COLORS:
                id = sqlDB.insert(ColorsDatabaseHelper.COLOR_TABLE, null, values);
                break;
            case SCHEMAS:
                id = sqlDB.insert(ColorsDatabaseHelper.SCHEMA_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted;
        String id;
        switch (uriType) {
            case COLORS:
                rowsDeleted = sqlDB.delete(ColorsDatabaseHelper.COLOR_TABLE, selection,
                        selectionArgs);
                break;
            case SCHEMAS:
                rowsDeleted = sqlDB.delete(ColorsDatabaseHelper.SCHEMA_TABLE, selection,
                        selectionArgs);
                break;
            case COLOR_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ColorsDatabaseHelper.COLOR_TABLE,
                            BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ColorsDatabaseHelper.COLOR_TABLE,
                            BaseColumns._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case SCHEMA_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ColorsDatabaseHelper.SCHEMA_TABLE,
                            BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ColorsDatabaseHelper.SCHEMA_TABLE,
                            BaseColumns._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        String id;
        switch (uriType) {
            case COLORS:
                rowsUpdated = sqlDB.update(ColorsDatabaseHelper.COLOR_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SCHEMAS:
                rowsUpdated = sqlDB.update(ColorsDatabaseHelper.SCHEMA_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case COLOR_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ColorsDatabaseHelper.COLOR_TABLE,
                            values,
                            BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ColorsDatabaseHelper.COLOR_TABLE,
                            values,
                            BaseColumns._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case SCHEMA_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ColorsDatabaseHelper.SCHEMA_TABLE,
                            values,
                            BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ColorsDatabaseHelper.SCHEMA_TABLE,
                            values,
                            BaseColumns._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
