package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hk.ust.cse.hunkim.questionroom.question.Question;

public class NewQuestionActivity extends AppCompatActivity {

    private Firebase mFirebaseRef;
    private String roomName;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.titleEditText)
    EditText titleEditText;
    @Bind(R.id.questionEditText)
    EditText questionEditText;
    @Bind(R.id.pollItemRecyclerView)
    RecyclerView pollItemRecyclerView;
    @Bind(R.id.imageView)
    ImageView imageView;

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

        Firebase.setAndroidContext(this);

        mFirebaseRef = new Firebase(Constant.FIREBASE_URL).child(roomName).child("questions");

        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(NewQuestionActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewQuestionActivity.this, "Failed to connect to Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        pollItemList = new ArrayList<>();
        adapter = new NewPollRecyclerViewAdapter(pollItemList);
        pollItemRecyclerView.setAdapter(adapter);
        pollItemRecyclerView.setLayoutManager(new NewPollRecyclerViewLayoutManager(this));

        bundle = new Bundle();
    }

    @OnClick(R.id.addPollItemButton)
    public void onClick() {
        pollItemList.add("");
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.addImageItem:
                new AddImageDialog(this, bundle).show();
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
                Bitmap imageBitmap = Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(imageBitmap);
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
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == AddImageDialog.REQUEST_DRAW_DRAWING && resultCode == RESULT_OK) {
            try {
                Uri uri = bundle.getParcelable("data");
                Bitmap imageBitmap = Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String title = titleEditText.getText().toString();
        String question = questionEditText.getText().toString();
        if (!title.isEmpty() || !question.isEmpty()) {
            // Create our 'model', a Chat object
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(new Question(title, question));
        }
    }

    private void sendMessage() {
        String title = titleEditText.getText().toString();
        String question = questionEditText.getText().toString();
        if (!title.isEmpty() || !question.isEmpty()) {
            // Create our 'model', a Chat object
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(new Question(title, question));
        }
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

