package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.datamodel.Question;
import hk.ust.cse.hunkim.questionroom.datamodel.Room;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_DEFAULT;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Thomas on 30/10/2015.
 */
public class QuestionRoomActivityTest extends ActivityInstrumentationTestCase2<QuestionRoomActivity> {
    private QuestionRoomActivity activity;

    public QuestionRoomActivityTest() {
        super(QuestionRoomActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        Intent mStartIntent = new Intent(getInstrumentation()
                .getTargetContext(), QuestionRoomActivity.class);
        mStartIntent.setAction(ACTION_VIEW);
        mStartIntent.addCategory(CATEGORY_DEFAULT);
        mStartIntent.setData(Uri.parse("content://qweet.kuenl.com/room/5635e858ac6f9f030004ca29"));
        setActivityIntent(mStartIntent);
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
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
        Method method = QuestionRoomActivity.class.getDeclaredMethod("getSearchFilteredList", cArg);
        method.setAccessible(true);
        method.invoke(activity, (List<Question>) null);
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

    public void testNothing() {
        CustomMenuItem na  = new CustomMenuItem();
        na.setItemId(0);
        activity.onOptionsItemSelected(na);
    }

/*
    public void testHome() {
        CustomMenuItem na  = new CustomMenuItem();
        na.setItemId(android.R.id.home);
        activity.onOptionsItemSelected(na);
    }
*/
    private class CustomMenuItem implements MenuItem {
        private int itemId;

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        @Override
        public int getItemId() {
            return itemId;
        }

        @Override
        public int getGroupId() {
            return 0;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public MenuItem setTitle(CharSequence title) {
            return null;
        }

        @Override
        public MenuItem setTitle(int title) {
            return null;
        }

        @Override
        public CharSequence getTitle() {
            return null;
        }

        @Override
        public MenuItem setTitleCondensed(CharSequence title) {
            return null;
        }

        @Override
        public CharSequence getTitleCondensed() {
            return null;
        }

        @Override
        public MenuItem setIcon(Drawable icon) {
            return null;
        }

        @Override
        public MenuItem setIcon(int iconRes) {
            return null;
        }

        @Override
        public Drawable getIcon() {
            return null;
        }

        @Override
        public MenuItem setIntent(Intent intent) {
            return null;
        }

        @Override
        public Intent getIntent() {
            return null;
        }

        @Override
        public MenuItem setShortcut(char numericChar, char alphaChar) {
            return null;
        }

        @Override
        public MenuItem setNumericShortcut(char numericChar) {
            return null;
        }

        @Override
        public char getNumericShortcut() {
            return 0;
        }

        @Override
        public MenuItem setAlphabeticShortcut(char alphaChar) {
            return null;
        }

        @Override
        public char getAlphabeticShortcut() {
            return 0;
        }

        @Override
        public MenuItem setCheckable(boolean checkable) {
            return null;
        }

        @Override
        public boolean isCheckable() {
            return false;
        }

        @Override
        public MenuItem setChecked(boolean checked) {
            return null;
        }

        @Override
        public boolean isChecked() {
            return false;
        }

        @Override
        public MenuItem setVisible(boolean visible) {
            return null;
        }

        @Override
        public boolean isVisible() {
            return false;
        }

        @Override
        public MenuItem setEnabled(boolean enabled) {
            return null;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean hasSubMenu() {
            return false;
        }

        @Override
        public SubMenu getSubMenu() {
            return null;
        }

        @Override
        public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            return null;
        }

        @Override
        public ContextMenu.ContextMenuInfo getMenuInfo() {
            return null;
        }

        @Override
        public void setShowAsAction(int actionEnum) {

        }

        @Override
        public MenuItem setShowAsActionFlags(int actionEnum) {
            return null;
        }

        @Override
        public MenuItem setActionView(View view) {
            return null;
        }

        @Override
        public MenuItem setActionView(int resId) {
            return null;
        }

        @Override
        public View getActionView() {
            return null;
        }

        @Override
        public MenuItem setActionProvider(ActionProvider actionProvider) {
            return null;
        }

        @Override
        public ActionProvider getActionProvider() {
            return null;
        }

        @Override
        public boolean expandActionView() {
            return false;
        }

        @Override
        public boolean collapseActionView() {
            return false;
        }

        @Override
        public boolean isActionViewExpanded() {
            return false;
        }

        @Override
        public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
            return null;
        }
    }

}
