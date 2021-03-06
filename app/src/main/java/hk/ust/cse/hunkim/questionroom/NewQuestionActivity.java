package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hk.ust.cse.hunkim.questionroom.datamodel.PollOption;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;

import static com.android.volley.Request.Method;

public class NewQuestionActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener,
        View.OnFocusChangeListener,
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private static final String TAG = "NewQuestionActivity";

    private String roomId;

    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private final OkHttpClient client = new OkHttpClient();

    @Bind(R.id.activity_new_question_root)
    CoordinatorLayout rootView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //@Bind(R.id.titleEditText)
    //EditText titleEditText;
    //@Bind(R.id.questionEditText)
    //EditText questionEditText;
    @Bind(R.id.pollItemRecyclerView)
    RecyclerView pollItemRecyclerView;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.deleteImageButton)
    ImageView deleteImageButton;

    //@Bind(R.id.editEmojicon)
    //EmojiconEditText mEmojiEditText;
    @Bind(R.id.editEmojicon)
    EditText mEmojiEditText;

    // Variable for controlling the emoji keyboard
    @Bind(R.id.insertEmojiButton)
    ImageButton insertEmojiButton;
    @Bind(R.id.keyboardButton)
    ImageButton keyboardButton;
    @Bind(R.id.emojiKeyboard)
    FrameLayout emojiKeyboard;
    private boolean emojiKeyboardVisable;
    private boolean requestedEmojiKeyboard;
    private int roorViewMaxHeight = 0;
    private int roorViewMinHeight = Integer.MAX_VALUE;

    private Bitmap imageBitmap;

    private List<String> pollItemList;
    private NewPollRecyclerViewAdapter adapter;

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        assert (intent != null);

        roomId = intent.getStringExtra(Constant.KEY_ROOM_ID);
        assert (roomId != null);

        imageBitmap = null;

        pollItemList = new ArrayList<>();
        adapter = new NewPollRecyclerViewAdapter(pollItemList);
        pollItemRecyclerView.setAdapter(adapter);
        pollItemRecyclerView.setLayoutManager(new RecyclerViewWrapContentLayoutManager(this));

        bundle = new Bundle();

        emojiKeyboardVisable = false;
        requestedEmojiKeyboard = false;

        //  Create a instance of emoji keyboard
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiKeyboard, EmojiconsFragment.newInstance(true))
                .commit();

        // Listen to keyboard change
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mEmojiEditText.setOnFocusChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                leave();
                return true;
            case R.id.addImageItem:
                new AddImageDialog(this, bundle).show();
                return true;
            case R.id.okItem:
                submit();
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddImageDialog.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                Uri uri = bundle.getParcelable("data");
                imageBitmap = Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(imageBitmap);
                imageView.setVisibility(View.VISIBLE);
                deleteImageButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == AddImageDialog.REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Image in not valid.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                imageBitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(imageBitmap);
                imageView.setVisibility(View.VISIBLE);
                deleteImageButton.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == AddImageDialog.REQUEST_DRAW_DRAWING && resultCode == RESULT_OK) {
            try {
                Uri uri = bundle.getParcelable("data");
                imageBitmap = Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(imageBitmap);
                imageView.setVisibility(View.VISIBLE);
                deleteImageButton.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (emojiKeyboardVisable) {
            hideEmojiKeyboard();
        } else {
            leave();
        }
    }

    public void leave() {
        //String title = titleEditText.getText().toString().trim();
        String question = mEmojiEditText.getText().toString().trim();
        //if (!title.isEmpty() || !question.isEmpty() || imageBitmap != null || pollItemList.size() > 0) {
        if (!question.isEmpty() || imageBitmap != null || pollItemList.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm to leave");
            builder.setMessage("You have unsaved question. Do you want to leave this page and discard your question or stay on this page?");
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

    public void submit() {
        //final String title = titleEditText.getText().toString().trim();
        final String message = mEmojiEditText.getText().toString().trim();
        //if (!title.isEmpty() && !message.isEmpty()) {
        if (!message.isEmpty()) {
            List<PollOption> pollOptions = new ArrayList<>();
            for (String str : pollItemList) {
                str = str.trim();
                if (!str.isEmpty()) {
                    pollOptions.add(new PollOption(str));
                }
            }
            if (pollItemList.size() > 0 && pollOptions.size() < 2) {
                return;
            }
            Question question = new Question(message);
            question.setRoomId(roomId);
            question.setPollOptions(pollOptions);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(new Gson().toJson(question));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, jsonObject.toString());
            JsonObjectRequest request = new JsonObjectRequest(Method.POST, Constant.BASE_URL + "question", jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(NewQuestionActivity.this, "Question posted.", Toast.LENGTH_SHORT).show();
                            if (imageBitmap != null) {
                                Question q = new GsonBuilder()
                                        .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                                        .create()
                                        .fromJson(response.toString(), Question.class);
                                new UploadTask(q.getId()).execute();
                            } else {
                                finish();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.getMessage() != null) {
                        Log.d(TAG, error.getMessage());
                        String msg = error.getMessage() + "Please try again later.";
                        Snackbar.make(NewQuestionActivity.this.rootView, msg, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            VolleySingleton.getInstance(this).addToRequestQueue(request);
        }
    }


    @OnClick(R.id.addPollItemButton)
    public void onClick() {
        pollItemList.add("");
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.deleteImageButton)
    public void onDeleteImageButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageBitmap = null;
                imageView.setImageBitmap(null);
                imageView.setVisibility(View.GONE);
                deleteImageButton.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        int screenHeight = rootView.getRootView().getHeight();
        int heightDiff = screenHeight - (r.bottom - r.top);
        roorViewMinHeight = Math.min(roorViewMinHeight, heightDiff);
        roorViewMaxHeight = Math.max(roorViewMaxHeight, heightDiff);
        if (roorViewMaxHeight > roorViewMinHeight) {
            ViewGroup.LayoutParams params = emojiKeyboard.getLayoutParams();
            params.height = roorViewMaxHeight - roorViewMinHeight;
        }
        if (heightDiff != roorViewMinHeight && !requestedEmojiKeyboard) {
            hideEmojiKeyboard();
            requestedEmojiKeyboard = false;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mEmojiEditText) {
            if (!hasFocus) {
                hideEmojiKeyboard();
            }
        }
    }

    @OnClick(R.id.editEmojicon)
    public void onEmojiEditTextClick() {
        hideEmojiKeyboard();
    }

    @OnClick(R.id.insertEmojiButton)
    public void showEmojiKeyboard() {
        if (mEmojiEditText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEmojiEditText.getWindowToken(), 0);
            requestedEmojiKeyboard = true;
            emojiKeyboard.setVisibility(View.VISIBLE);
            insertEmojiButton.setVisibility(View.GONE);
            keyboardButton.setVisibility(View.VISIBLE);
            emojiKeyboardVisable = true;
        }
    }

    public void hideEmojiKeyboard() {
        emojiKeyboard.setVisibility(View.GONE);
        insertEmojiButton.setVisibility(View.VISIBLE);
        keyboardButton.setVisibility(View.GONE);
        emojiKeyboardVisable = false;
    }

    @OnClick(R.id.keyboardButton)
    public void showKeyboard() {
        if (mEmojiEditText.requestFocus()) {
            hideEmojiKeyboard();
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEmojiEditText, 0);
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEmojiEditText);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mEmojiEditText, emojicon);
    }

    private class UploadTask extends AsyncTask<Void, Void, Void> {

        String questionId;
        File file;

        public UploadTask(String questionId) {
            this.questionId = questionId;
            try {
                file = new File(NewQuestionActivity.this.getCacheDir(), "image.jpg");
                file.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                NewQuestionActivity.this.imageBitmap.compress(CompressFormat.JPEG, 80, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (file != null) {
                while (true) {
                    RequestBody requestBody = new MultipartBuilder()
                            .type(MultipartBuilder.FORM)
                            .addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                            .build();

                    Request request = new Request.Builder()
                            .url(Constant.BASE_URL + "question/" + questionId + "/image")
                            .post(requestBody)
                            .build();
                    try {
                        com.squareup.okhttp.Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            return null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(NewQuestionActivity.this, "Image uploaded.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}

