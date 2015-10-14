package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.question.Question;

public class NewQuestionActivity extends AppCompatActivity {


    private Firebase mFirebaseRef;
    private String roomName;

    private ValueEventListener mConnectedListener;
    private EditText titleEditText;
    private EditText questionEditText;
    private List<String> pollItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        questionEditText = (EditText) findViewById(R.id.questionEditText);

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
        final NewPollRecyclerViewAdapter adapter = new NewPollRecyclerViewAdapter(pollItemList);
        RecyclerView pollItemRecyclerView = (RecyclerView) findViewById(R.id.pollItemRecyclerView);
        pollItemRecyclerView.setAdapter(adapter);
        pollItemRecyclerView.setLayoutManager(new NewPollRecyclerViewLayoutManager(this));
        LinearLayout addPollItemButton = (LinearLayout) findViewById(R.id.addPollItemButton);
        addPollItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pollItemList.add("");
                adapter.notifyDataSetChanged();
            }
        });
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

    private class NewPollRecyclerViewLayoutManager extends LinearLayoutManager

    {
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

