package hk.ust.cse.hunkim.questionroom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A dialog for user to choose the image source while adding image
 * Created by Leung Pui Kuen on 17/10/2015.
 */
public class AddImageDialog extends AppCompatDialog {
    private Context context;

    public static final int REQUEST_TAKE_PHOTO = 0;
    public static final int REQUEST_CHOOSE_IMAGE = 1;
    public static final int REQUEST_DRAW_DRAWING = 2;

    private Bundle bundle;

    @Bind(R.id.button_take_photo)
    Button button_take_photo;

    public AddImageDialog(Context context, Bundle bundle) {
        super(context);
        this.context = context;
        this.bundle = bundle;
        setTitle("Add image");
        setContentView(R.layout.dialog_add_image);
        ButterKnife.bind(this);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
/*        if (new Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(context.getPackageManager()) == null) {
            button_take_photo.setVisibility(View.GONE);
        }*/
    }

    @OnClick({R.id.button_take_photo, R.id.button_choose_image, R.id.button_draw_drawing})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_take_photo:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if (takePictureIntent.resolveActivity(context.getPackageManager()) != null)
            {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    // Continue only if the File was successfully created
                    Uri uri = Uri.fromFile(photoFile);
                    bundle.putParcelable("data", uri);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            uri);
                    ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.button_choose_image:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                ((Activity) context).startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
                break;
            case R.id.button_draw_drawing: {
                // Create the File where the photo should go
                try {
                    File photoFile = createImageFile();
                    // Continue only if the File was successfully created
                    Intent drawDrawingIntent = new Intent();
                    Uri uri = Uri.fromFile(photoFile);
                    bundle.putParcelable("data", uri);
                    drawDrawingIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            uri);
                    drawDrawingIntent.setClass(context, CanvasActivity.class);
                    ((Activity) context).startActivityForResult(drawDrawingIntent, REQUEST_DRAW_DRAWING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        dismiss();
    }

    //String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }
}
