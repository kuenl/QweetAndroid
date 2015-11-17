package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import com.google.gson.GsonBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import hk.ust.cse.hunkim.questionroom.datamodel.Room;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_DEFAULT;

/**
 * Created by Thomas on 29/10/2015.
 */
public class HashTagActivityTest extends ActivityInstrumentationTestCase2<HashTagActivity> {
    private HashTagActivity activity;

    public HashTagActivityTest() {
        super(HashTagActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Intent mStartIntent = new Intent(getInstrumentation()
                .getTargetContext(), HashTagActivity.class);
        mStartIntent.setAction(ACTION_VIEW);
        mStartIntent.addCategory(CATEGORY_DEFAULT);
        mStartIntent.setData(Uri.parse("hash://qweet.kuenl.com/room/0#0"));
        setActivityIntent(mStartIntent);
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    public void testNothing() {
        CustomMenuItem na  = new CustomMenuItem();
        na.setItemId(0);
        activity.onOptionsItemSelected(na);
    }


    public void testHome() {
        CustomMenuItem na  = new CustomMenuItem();
        na.setItemId(android.R.id.home);
        activity.onOptionsItemSelected(na);
    }

    public void testUpdateRoom() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Class[] cArg = {Room.class};

        final Method method = HashTagActivity.class.getDeclaredMethod("updateRoom", cArg);
        method.setAccessible(true);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(activity, (Object) null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        String jsonStr = "{\"questions\":[{\"roomId\":\"5627b9a6f7f5b00300164cd7\",\"headline\":\"q1\",\"message\":\"#0\",\"completed\":false,\"upVote\":0,\"downVote\":0,\"tags\":[],\"createdAt\":\"2015-10-25T23:46:26.573Z\",\"updatedAt\":\"2015-10-25T23:46:26.591Z\",\"id\":\"562d69d230158e0300cf9792\"},{\"roomId\":\"5627b9a6f7f5b00300164cd7\",\"headline\":\"q2\",\"message\":\"#q2\",\"completed\":false,\"upVote\":0,\"downVote\":0,\"image\":\"\",\"tags\":[],\"createdAt\":\"2015-10-25T23:47:46.551Z\",\"updatedAt\":\"2015-10-25T23:47:46.691Z\",\"id\":\"562d6a2230158e0300cf9793\"},{\"roomId\":\"5627b9a6f7f5b00300164cd7\",\"headline\":\"q2\",\"message\":\"#q, #i /#i\",\"completed\":false,\"upVote\":0,\"downVote\":0,\"image\":\"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\"tags\":[],\"createdAt\":\"2015-10-25T23:47:46.551Z\",\"updatedAt\":\"2015-10-25T23:47:46.691Z\",\"id\":\"562d6a2230158e0300cf9794\"}],\"name\":\"dfghjkl\",\"createdAt\":\"2015-10-21T16:13:26.948Z\",\"updatedAt\":\"2015-10-21T16:13:37.522Z\",\"id\":\"5627b9a6f7f5b00300164cd7\"}";
        final Room room = new GsonBuilder()
                .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                .create()
                .fromJson(jsonStr, Room.class);

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
