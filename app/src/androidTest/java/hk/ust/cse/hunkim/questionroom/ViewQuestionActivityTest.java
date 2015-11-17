package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import hk.ust.cse.hunkim.questionroom.datamodel.Question;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_DEFAULT;

/**
 * Created by Administrator on 17/11/15.
 */
public class ViewQuestionActivityTest extends ActivityInstrumentationTestCase2<ViewQuestionActivity> {
    private ViewQuestionActivity activity;

    public ViewQuestionActivityTest() {
        super(ViewQuestionActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Intent mStartIntent = new Intent(getInstrumentation()
                .getTargetContext(), QuestionRoomActivity.class);
        mStartIntent.setAction(ACTION_VIEW);
        mStartIntent.addCategory(CATEGORY_DEFAULT);
        mStartIntent.setData(Uri.parse("content://qweet.kuenl.com/question/0"));
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

    public void testPost() throws NoSuchFieldException, IllegalAccessException {
        Field field = activity.getClass().getDeclaredField("questionId");
        field.setAccessible(true);
        field.set(activity, "");
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.postComment();
            }
        });
        field.set(activity, "0");
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                activity.postComment();
            }
        });
    }

    public void testUpdateActivity() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Class[] cArg = {Question.class};

        final Method method = ViewQuestionActivity.class.getDeclaredMethod("updateActivity", cArg);
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


        String jsonStr = "{\"comments\":[],\"pollOptions\":[],\"roomId\":\"5627b9a6f7f5b00300164cd7\",\"headline\":\"curl test\",\"message\":\"wks????\",\"completed\":false,\"upVote\":0,\"downVote\":0,\"tags\":[],\"createdAt\":\"2015-10-25T23:47:46.551Z\",\"updatedAt\":\"2015-11-16T13:01:47.296Z\",\"image\":\"\",\"score\":6869.1214122,\"id\":\"562d6a2230158e0300cf9793\"}";
        final Question question = new GsonBuilder()
                .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                .create()
                .fromJson(jsonStr, Question.class);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(activity, question);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void testUpdateActivity2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Class[] cArg = {Question.class};

        final Method method = ViewQuestionActivity.class.getDeclaredMethod("updateActivity", cArg);
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


        String jsonStr = "{\"comments\":[{\"message\":\"good\",\"questionId\":\"564b1a530ddc9a0300c3fbc9\",\"createdAt\":\"2015-11-17T14:15:19.859Z\",\"updatedAt\":\"2015-11-17T14:15:19.859Z\",\"id\":\"564b367757b8fe0300efcb6b\"}],\"pollOptions\":[{\"count\":1,\"message\":\"A0\",\"questionId\":\"564b1a530ddc9a0300c3fbc9\",\"createdAt\":\"2015-11-17T12:15:15.156Z\",\"updatedAt\":\"2015-11-17T12:16:38.427Z\",\"id\":\"564b1a530ddc9a0300c3fbca\"},{\"count\":0,\"message\":\"A1\",\"questionId\":\"564b1a530ddc9a0300c3fbc9\",\"createdAt\":\"2015-11-17T12:15:15.157Z\",\"updatedAt\":\"2015-11-17T12:15:15.157Z\",\"id\":\"564b1a530ddc9a0300c3fbcb\"}],\"roomId\":\"5629cf06c332600300192e6b\",\"completed\":false,\"downVote\":0,\"message\":\"testing\uD83D\uDE04\",\"score\":6912.3580474,\"upVote\":0,\"tags\":[],\"image\":\"https://static.dyp.im/IOSkeMZq4W/6f242ea5439aeeec21ffed719ea5859f.jpg\",\"createdAt\":\"2015-11-17T12:15:15.133Z\",\"updatedAt\":\"2015-11-17T12:15:15.188Z\",\"id\":\"564b1a530ddc9a0300c3fbc9\"}";
        final Question question = new GsonBuilder()
                .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                .create()
                .fromJson(jsonStr, Question.class);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(activity, question);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void testUpdateActivity3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Class[] cArg = {Question.class};

        final Method method = ViewQuestionActivity.class.getDeclaredMethod("updateActivity", cArg);
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


        String jsonStr = "{\"roomId\":\"5627b9a6f7f5b00300164cd7\",\"headline\":\"curl test\",\"message\":\"wks????\",\"completed\":false,\"upVote\":0,\"downVote\":0,\"tags\":[],\"createdAt\":\"2015-10-25T23:47:46.551Z\",\"updatedAt\":\"2015-11-16T13:01:47.296Z\",\"score\":6869.1214122,\"id\":\"562d6a2230158e0300cf9793\"}";
        final Question question = new GsonBuilder()
                .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                .create()
                .fromJson(jsonStr, Question.class);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                try {
                    method.invoke(activity, question);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
