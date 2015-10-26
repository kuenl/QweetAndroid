package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.EditText;
import android.widget.ImageButton;


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

    /*
    public void testCreatingActivity() {

        //Create and add an ActivityMonitor to monitor interaction between the system and the
        //ReceiverActivity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Send the room name
        getInstrumentation().sendStringSync("comp3111");
        getInstrumentation().waitForIdleSync();

        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

        //Verify that MainActivity was started
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for MainActivity has not been called", 1,
                receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", MainActivity.class,
                mainActivity.getClass());
*/
        /*
        //Read the message received by ReceiverActivity
        final TextView receivedMessage = (TextView) mainActivity
                .findViewById(R.id.received_message_text_view);
        //Verify that received message is correct
        assertNotNull(receivedMessage);
        assertEquals("Wrong received message", TEST_MESSAGE, receivedMessage.getText().toString());
        */
/*
        Intent intent = mainActivity.getIntent();
        assertNotNull("Intent should be set", intent);

        assertEquals("comp3111", intent.getStringExtra(Constant.KEY_ROOM_NAME));

        assertEquals("This is set correctly", "Room name: comp3111", mainActivity.getTitle());

        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);

    }*/
}
