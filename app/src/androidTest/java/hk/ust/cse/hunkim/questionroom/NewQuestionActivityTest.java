package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Thomas on 27/10/2015.
 */
public class NewQuestionActivityTest extends ActivityInstrumentationTestCase2<NewQuestionActivity> {

    private NewQuestionActivity activity;
    private EditText editEmojicon;

    public NewQuestionActivityTest() {
        super(NewQuestionActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Intent mStartIntent = new Intent(getInstrumentation()
                .getTargetContext(), NewQuestionActivity.class);
        mStartIntent.putExtra(Constant.KEY_ROOM_ID, "");
        setActivityIntent(mStartIntent);
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();

        editEmojicon = (EditText) activity.findViewById(R.id.editEmojicon);
    }

    public void testEmoji() {
        onView(withId(R.id.editEmojicon)).perform(click());
        onView(withId(R.id.insertEmojiButton)).perform(click());
        onView(withId(R.id.emojiKeyboard)).perform(click());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.onBackPressed();
                activity.onFocusChange(null, false);
            }
        });
        onView(withId(R.id.insertEmojiButton)).perform(click());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.showKeyboard();
                activity.onBackPressed();
                activity.onBackPressed();
            }
        });
    }

    public void testAddPollItem() {
        onView(withId(R.id.addPollItemButton)).perform(click());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.submit();
            }
        });
    }

    /*
    public void testCamera() {
        onView(withId(R.id.addImageItem)).perform(click());
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button_take_photo)).perform(click());
        getInstrumentation().waitForIdleSync();
    }*/
/*
    public void testGallery() {
        onView(withId(R.id.addImageItem)).perform(click());
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button_choose_image)).perform(click());
        getInstrumentation().onDestroy();
    }
*/

    public void testNothing() {
        CustomMenuItem na = new CustomMenuItem();
        na.setItemId(0);
        activity.onOptionsItemSelected(na);
    }


    public void testHome() {
        CustomMenuItem na = new CustomMenuItem();
        na.setItemId(android.R.id.home);
        activity.onOptionsItemSelected(na);
    }

    public void testCanvas() {
        onView(withId(R.id.addImageItem)).perform(click());
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button_draw_drawing)).perform(click());
    }

    public void testSubmit() {
        onView(withId(R.id.addPollItemButton)).perform(click());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editEmojicon.setText("!");
            }
        });
        onView(withId(R.id.okItem)).perform(click());
    }

}
