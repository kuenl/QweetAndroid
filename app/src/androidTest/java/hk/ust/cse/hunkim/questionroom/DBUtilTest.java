package hk.ust.cse.hunkim.questionroom;

import android.test.AndroidTestCase;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;

/**
 * Created by hunkim on 7/15/15.
 */
public class DBUtilTest extends AndroidTestCase {

    DBUtil dbutil;

    public DBUtilTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(getContext());
        dbutil = new DBUtil(mDbHelper);
    }

    public void testPut () {
        String key = "1234";
        dbutil.put(key, "like");
        assertTrue("Put the key", dbutil.contains(key, "like"));

        dbutil.delete(key, "like");

        assertFalse("Key is deleted!", dbutil.contains(key, "like"));
    }
}
