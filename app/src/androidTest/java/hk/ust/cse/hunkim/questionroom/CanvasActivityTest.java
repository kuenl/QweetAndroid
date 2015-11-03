package hk.ust.cse.hunkim.questionroom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Thomas on 28/10/2015.
 */
public class CanvasActivityTest extends ActivityInstrumentationTestCase2<CanvasActivity> {
    private CanvasActivity activity;

    public CanvasActivityTest() {
        super(CanvasActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Intent drawDrawingIntent = new Intent();
            Uri uri = Uri.fromFile(photoFile);
            Bundle bundle = new Bundle();
            bundle.putParcelable("data", uri);
            drawDrawingIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    uri);
        }

        setActivityIntent(intent);
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

    public void testMenuItem() throws NoSuchFieldException, URISyntaxException, IllegalAccessException {
        Field uri = activity.getClass().getDeclaredField("uri");
        uri.setAccessible(true);
        uri.set(activity, new Uri.Builder().path("/.tmp/").build());

        onView(withId(R.id.item_undo)).perform(click());
        onView(withId(R.id.item_clear)).perform(click());
        onView(withId(R.id.item_done)).perform(click());
    }

    public void testCanvas() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final CanvasView canvas = activity.mCanvasView;
        Class[] startTouchClassArg = new Class[2];
        startTouchClassArg[0] = float.class;
        startTouchClassArg[1] = float.class;

        final Method startTouch = canvas.getClass().getDeclaredMethod("startTouch", startTouchClassArg);
        startTouch.setAccessible(true);

        Class[] moveTouchClassArg = new Class[2];
        moveTouchClassArg[0] = float.class;
        moveTouchClassArg[1] = float.class;

        final Method moveTouch = canvas.getClass().getDeclaredMethod("moveTouch", moveTouchClassArg);
        moveTouch.setAccessible(true);

        final Method upTouch = canvas.getClass().getDeclaredMethod("upTouch");
        upTouch.setAccessible(true);

        startTouch.invoke(canvas, 0, 0);
        moveTouch.invoke(canvas, 0, 0);
        moveTouch.invoke(canvas, 0, 100);
        moveTouch.invoke(canvas, 100, 100);
        moveTouch.invoke(canvas, 100, 0);
        moveTouch.invoke(canvas, 0, 0);
        upTouch.invoke(canvas);

        startTouch.invoke(canvas, 0, 0);
        moveTouch.invoke(canvas, 100, 100);
        upTouch.invoke(canvas);

        canvas.getBitmap();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                canvas.undo();
                canvas.undo();
            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

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