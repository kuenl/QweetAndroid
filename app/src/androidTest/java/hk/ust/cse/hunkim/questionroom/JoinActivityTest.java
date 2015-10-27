package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by hunkim on 7/15/15.
 * based on http://evgenii.com/blog/testing-activity-in-android-studio-tutorial-part-3/
 * and
 * http://developer.android.com/training/testing.html
 */
public class JoinActivityTest extends ActivityInstrumentationTestCase2<JoinActivity> {
    JoinActivity activity;

    private EditText roomNameEditText;
    private ImageButton joinButton;

    private static final int TIMEOUT_IN_MS = 5000;

    public JoinActivityTest() {
        super(JoinActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();
        roomNameEditText = (EditText) activity.findViewById(R.id.room_name);
        joinButton = (ImageButton) activity.findViewById(R.id.joinButton);
    }

    /*
    public void testRoomName() {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.setText("all");
            }
        });

       // getInstrumentation().sendStringSync("all");
        getInstrumentation().waitForIdleSync();

        String actualText = roomNameEditText.getText().toString();
        assertEquals("all", actualText);


    }
*/

    public void testIntentSetting() {

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.setText("all");
            }
        });

        //getInstrumentation().sendStringSync("all");
        getInstrumentation().waitForIdleSync();

        String actualText = roomNameEditText.getText().toString();
        assertEquals("all", actualText);

        // Tap "Join" button
        // ----------------------

        TouchUtils.clickView(this, joinButton);
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();


        Intent intent = activity.getIntent();
        assertNotNull("Intent should be set", intent);

        //assertEquals("all", intent.getStringExtra(Constant.KEY_ROOM_ID));
    }

    public void testIsRoomNameValid() {

        TouchUtils.clickView(this, joinButton);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.setText("!");
            }
        });

        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, joinButton);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.setText("aaaaaaaaaaaaaaaaaaaaaaaaa");
            }
        });

        getInstrumentation().waitForIdleSync();

        TouchUtils.clickView(this, joinButton);

    }

    public void testUpdateAutoCompleteRoomList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, JSONException {
        Class[] cArg = new Class[1];
        cArg[0] = JSONArray.class;

        final Method method = activity.getClass().getDeclaredMethod("updateAutoCompleteRoomList", cArg);
        method.setAccessible(true);

        final Object[] oArg = new Object[1];
        oArg[0] = null;
        method.invoke(activity, oArg);



        final String jsonStr = "\n" +
                "[{\"name\":\"testingRoom\",\"createdAt\":\"2015-10-21T16:11:40.213Z\",\"updatedAt\":\"2015-10-21T16:11:40.221Z\",\"id\":\"5627b93c3cef680300a1d0a3\"},{\"name\":\"dfghjkl\",\"createdAt\":\"2015-10-21T16:13:22.180Z\",\"updatedAt\":\"2015-10-21T16:13:22.334Z\",\"id\":\"5627b9a2f7f5b00300164cd6\"}]";
        oArg[0] = new JSONArray(jsonStr);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(activity, new JSONArray(jsonStr));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
