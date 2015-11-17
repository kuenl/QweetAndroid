package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Thomas on 27/10/2015.
 */
public class NewQuestionActivityTest extends ActivityInstrumentationTestCase2<NewQuestionActivity> {

    private NewQuestionActivity activity;

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
    }

    public void testEmoji() {
        onView(withId(R.id.editEmojicon)).perform(click());
        onView(withId(R.id.insertEmojiButton)).perform(click());
        onView(withId(R.id.emojiKeyboard)).perform(click());
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.showKeyboard();
            }
        });
    }

    public void testAddPollItem() {
        onView(withId(R.id.addPollItemButton)).perform(click());
    }

    public void testCamera() {
        onView(withId(R.id.addImageItem)).perform(click());
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button_take_photo)).perform(click());
        getInstrumentation().waitForIdleSync();
    }

    public void testGallery() {
        onView(withId(R.id.addImageItem)).perform(click());
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button_choose_image)).perform(click());
    }

    public void testCanvas() {
        onView(withId(R.id.addImageItem)).perform(click());
        getInstrumentation().waitForIdleSync();
        onView(withId(R.id.button_draw_drawing)).perform(click());
    }
}
