package hk.ust.cse.hunkim.questionroom.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hunkim on 7/15/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_ACTION_HISTORY_NAME = "actionHistory";
    public static final String COL_ACTION_HISTORY_KEY_NAME = "key";
    public static final String COL_ACTION_HISTORY_ACTION_NAME = "action";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_ACTION_HISTORY_NAME + " (" + COL_ACTION_HISTORY_KEY_NAME + " TEXT, "+ COL_ACTION_HISTORY_ACTION_NAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_ACTION_HISTORY_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "echoedKeys.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
