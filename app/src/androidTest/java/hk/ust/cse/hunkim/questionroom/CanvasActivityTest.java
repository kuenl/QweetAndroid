package hk.ust.cse.hunkim.questionroom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.ActivityInstrumentationTestCase2;

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
        Class[] args = {float.class, float.class};

        final Method startTouch = canvas.getClass().getDeclaredMethod("startTouch", args);
        startTouch.setAccessible(true);

        final Method moveTouch = canvas.getClass().getDeclaredMethod("moveTouch", args);
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

}
