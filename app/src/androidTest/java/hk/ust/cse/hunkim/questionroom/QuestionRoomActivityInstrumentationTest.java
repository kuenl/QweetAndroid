package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.google.gson.GsonBuilder;

import org.junit.Before;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.datamodel.Room;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Thomas on 30/10/2015.
 */
public class QuestionRoomActivityInstrumentationTest extends ActivityInstrumentationTestCase2<QuestionRoomActivity> {
    private QuestionRoomActivity activity;

    public QuestionRoomActivityInstrumentationTest() {
        super(QuestionRoomActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        Intent mStartIntent = new Intent(getInstrumentation()
                .getTargetContext(), QuestionRoomActivity.class);
        mStartIntent.putExtra(Constant.KEY_ROOM_ID, "5627b9a6f7f5b00300164cd7");
        setActivityIntent(mStartIntent);
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    public void testHome(){
        onView(withContentDescription("Navigate up")).perform(click());
    }

    public void testSearch(){
        onView(withId(R.id.search)).perform(click());
        onView(isAssignableFrom(EditText.class)).perform(typeText("test"), pressKey(66));
    }

    public void testFab(){

        onView(withId(R.id.questionRecyclerView)).perform();
        onView(withId(R.id.fab)).perform(click());
    }

    public void testFunc() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class[] cArg = new Class[1];
        cArg[0] = List.class;
        Object[] oArg = new Object[1];
        oArg[0] = null;
        Method method = QuestionRoomActivity.class.getDeclaredMethod("getSearchFilteredList", cArg);
        method.setAccessible(true);
        method.invoke(activity, oArg);
    }

    public void testUpdateRoom() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Class[] cArg = new Class[1];
        cArg[0] = Room.class;

        final Method method = QuestionRoomActivity.class.getDeclaredMethod("updateRoom", cArg);
        method.setAccessible(true);

        Object[] oArg = new Object[1];
        oArg[0] = null;
        method.invoke(activity, oArg);



        String jsonStr = "\n" +
                "{\"questions\":[{\"roomId\":\"5627b9a6f7f5b00300164cd7\",\"headline\":\"q1\",\"message\":\"q1\",\"completed\":false,\"upVote\":0,\"downVote\":0,\"tags\":[],\"createdAt\":\"2015-10-25T23:46:26.573Z\",\"updatedAt\":\"2015-10-25T23:46:26.591Z\",\"id\":\"562d69d230158e0300cf9792\"},{\"roomId\":\"5627b9a6f7f5b00300164cd7\",\"headline\":\"q2\",\"message\":\"q2\",\"completed\":false,\"upVote\":0,\"downVote\":0,\"tags\":[],\"createdAt\":\"2015-10-25T23:47:46.551Z\",\"updatedAt\":\"2015-10-25T23:47:46.691Z\",\"id\":\"562d6a2230158e0300cf9793\"},{\"roomId\":\"5627b9a6f7f5b00300164cd7\",\"headline\":\"q2\",\"message\":\"#q, #i /#i\",\"completed\":false,\"upVote\":0,\"downVote\":0,\"tags\":[],\"createdAt\":\"2015-10-25T23:47:46.551Z\",\"updatedAt\":\"2015-10-25T23:47:46.691Z\",\"id\":\"562d6a2230158e0300cf9794\"}],\"name\":\"dfghjkl\",\"createdAt\":\"2015-10-21T16:13:26.948Z\",\"updatedAt\":\"2015-10-21T16:13:37.522Z\",\"id\":\"5627b9a6f7f5b00300164cd7\"}";
        final Room room = new GsonBuilder()
                .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                .create()
                .fromJson(jsonStr, Room.class);
        oArg[0] = room;

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(activity, room);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        Field field = activity.getClass().getDeclaredField("queryStr");
        field.setAccessible(true);
        field.set(activity, "q1");

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(activity, room);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
