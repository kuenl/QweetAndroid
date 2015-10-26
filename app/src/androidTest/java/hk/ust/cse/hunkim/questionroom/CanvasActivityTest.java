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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 26/10/2015.
 */
public class CanvasActivityTest extends ActivityInstrumentationTestCase2<CanvasActivity> {
    private CanvasActivity activity;

    public CanvasActivityTest(Class<CanvasActivity> activityClass) {
        super(activityClass);
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

    public void testCanvas() {

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
