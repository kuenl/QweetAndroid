package hk.ust.cse.hunkim.questionroom;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CanvasActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.canvas)
    CanvasView mCanvasView;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_canvas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.item_undo:
                mCanvasView.undo();
                return true;
            case R.id.item_clear:
                onClearPressed();
                return true;
            case R.id.item_done:
                if (save(mCanvasView.getBitmap())) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
                return true;
        }
        return false;
    }

    public void onClearPressed() {
        if (!mCanvasView.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm to clear");
            builder.setMessage("Do you want to clear your drawings?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCanvasView.clear();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mCanvasView.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm to leave");
            builder.setMessage("You have unsaved drawings. Do you want to leave this page and discard your drawings or stay on this page?");
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        } else {
            finish();
        }
    }

    public boolean save(Bitmap bitmap) {
        try {
            File file = new File(uri.getPath());
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
