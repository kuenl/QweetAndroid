package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ImageButton;

/**
 * Created by hunkim on 7/20/15.
 */
public class QuestionRoomActivityTest extends ActivityUnitTestCase<QuestionRoomActivity> {

    private Intent mStartIntent;
    private ImageButton mButton;

    public QuestionRoomActivityTest() {
        super(QuestionRoomActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // In setUp, you can create any shared test data,
        // or set up mock components to inject
        // into your Activity. But do not call startActivity()
        // until the actual test methods.
        // into your Activity. But do not call startActivity()
        // until the actual test methods.
        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra(Constant.KEY_ROOM_NAME, "all");
    }

    @MediumTest
    public void testPreconditions() {
        startActivity(mStartIntent, null, null);
        mButton = (ImageButton) getActivity().findViewById(R.id.sendButton);
        assertNotNull(getActivity());
        assertNotNull(mButton);

        assertEquals("This is set correctly", "Room name: all", getActivity().getTitle());
    }

/*
    @MediumTest
    public void testPostingMessage() {
        Activity activity = startActivity(mStartIntent, null, null);
        mButton = (ImageButton) activity.findViewById(R.id.sendButton);
        final TextView text = (TextView) activity.findViewById(R.id.messageInput);
        final ListView lView = getActivity().getListView();

        assertNotNull(mButton);
        assertNotNull(text);
        assertNotNull(lView);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                lView.performItemClick(lView, 0, lView.getItemIdAtPosition(0));
            }
        });
        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();

        text.setText("This is test!");
        mButton.performClick();

        // TODO: How to confirm a new text is posted?
        // assertEquals("Child count: ", lView.getChildCount(), 10);
    }
    */
}
