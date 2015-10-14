package hk.ust.cse.hunkim.questionroom.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hunkim on 7/15/15.
 */
public class DBUtil {
    SQLiteOpenHelper helper;

    public DBUtil(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public long put(String key, String action) {

        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_ACTION_HISTORY_KEY_NAME, key);
        values.put(DBHelper.COL_ACTION_HISTORY_ACTION_NAME, action);

        return db.insert(
                DBHelper.TABLE_ACTION_HISTORY_NAME,
                null,
                values);
    }


    public boolean contains(String key, String action) {
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + DBHelper.TABLE_ACTION_HISTORY_NAME +
                        " WHERE " + DBHelper.COL_ACTION_HISTORY_KEY_NAME +
                        " = ? AND " + DBHelper.COL_ACTION_HISTORY_ACTION_NAME +
                        " = ?", new String[]{key, action});
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public void delete(String key, String action) {
        // Gets the data repository in write mode
        SQLiteDatabase db = helper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = DBHelper.COL_ACTION_HISTORY_KEY_NAME + " = ? AND " +
                DBHelper.COL_ACTION_HISTORY_ACTION_NAME + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {key, action};
        // Issue SQL statement.
        db.delete(DBHelper.TABLE_ACTION_HISTORY_NAME, selection, selectionArgs);
    }
}
