package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hk.ust.cse.hunkim.questionroom.question.Question;

public class NewQuestionActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener,
        View.OnFocusChangeListener,
        EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private String roomName;

    @Bind(R.id.activity_new_question_root)
    CoordinatorLayout rootView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.titleEditText)
    EditText titleEditText;
    //@Bind(R.id.questionEditText)
    //EditText questionEditText;
    @Bind(R.id.pollItemRecyclerView)
    RecyclerView pollItemRecyclerView;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.deleteImageButton)
    ImageView deleteImageButton;

    @Bind(R.id.editEmojicon)
    EmojiconEditText mEmojiEditText;

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

    private ValueEventListener mConnectedListener;

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        roomName = intent.getStringExtra(Constant.KEY_ROOM_NAME);
        if (roomName == null || roomName.isEmpty()) {
            roomName = "all";
        }

        imageBitmap = null;

        pollItemList = new ArrayList<>();
        adapter = new NewPollRecyclerViewAdapter(pollItemList);
        pollItemRecyclerView.setAdapter(adapter);
        pollItemRecyclerView.setLayoutManager(new NewPollRecyclerViewLayoutManager(this));

        bundle = new Bundle();

        emojiKeyboardVisable = false;
        requestedEmojiKeyboard = false;
        insertEmojiButton.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        keyboardButton.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        //  Create a instance of emoji keyboard
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiKeyboard, EmojiconsFragment.newInstance(false))
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

    private void leave() {
        String title = titleEditText.getText().toString().trim();
        String question = mEmojiEditText.getText().toString().trim();
        if (!title.isEmpty() || !question.isEmpty() || imageBitmap != null || pollItemList.size() > 0) {
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

    private void submit() {
        final String title = titleEditText.getText().toString().trim();
        final String question = mEmojiEditText.getText().toString().trim();
        if (!title.isEmpty() || !question.isEmpty()) {
            Firebase.setAndroidContext(this);
            final Firebase mFirebaseRef = new Firebase(Constant.FIREBASE_URL).child(roomName).child("questions");
            mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean connected = (Boolean) dataSnapshot.getValue();
                    if (connected) {
                        Toast.makeText(NewQuestionActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                        mFirebaseRef.push().setValue(new Question(title, question));
                        finish();
                        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
                    } else {
                        Toast.makeText(NewQuestionActivity.this, "Failed to connect to Firebase", Toast.LENGTH_SHORT).show();
                    }
                }
                
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
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

    private class NewPollRecyclerViewLayoutManager extends LinearLayoutManager {
        private int[] mMeasuredDimension = new int[2];

        public NewPollRecyclerViewLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                              int widthSpec, int heightSpec) {
            final int widthMode = View.MeasureSpec.getMode(widthSpec);
            final int heightMode = View.MeasureSpec.getMode(heightSpec);
            final int widthSize = View.MeasureSpec.getSize(widthSpec);
            final int heightSize = View.MeasureSpec.getSize(heightSpec);
            int width = 0;
            int height = 0;
            for (int i = 0; i < getItemCount(); i++) {
                measureScrapChild(recycler, i,
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        mMeasuredDimension);

                if (getOrientation() == HORIZONTAL) {
                    width = width + mMeasuredDimension[0];
                    if (i == 0) {
                        height = mMeasuredDimension[1];
                    }
                } else {
                    height = height + mMeasuredDimension[1];
                    if (i == 0) {
                        width = mMeasuredDimension[0];
                    }
                }
            }
            switch (widthMode) {
                case View.MeasureSpec.EXACTLY:
                    width = widthSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            switch (heightMode) {
                case View.MeasureSpec.EXACTLY:
                    height = heightSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            setMeasuredDimension(width, height);
        }

        private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                       int heightSpec, int[] measuredDimension) {
            View view = recycler.getViewForPosition(position);
            if (view != null) {
                RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                        getPaddingLeft() + getPaddingRight(), p.width);
                int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                        getPaddingTop() + getPaddingBottom(), p.height);
                view.measure(childWidthSpec, childHeightSpec);
                measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                recycler.recycleView(view);
            }
        }
    }


}

