package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.question.Question;

public class QuestionRoomActivity extends Activity {

    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private QuestionRecyclerViewAdapter mQuestionRecyclerViewAdapter;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_room);

        Intent intent = getIntent();
        assert (intent != null);

        roomName = intent.getStringExtra(Constant.KEY_ROOM_NAME);
        if (roomName == null || roomName.isEmpty()) {
            roomName = "all";
        }

        Firebase.setAndroidContext(this);

        mFirebaseRef = new Firebase(Constant.FIREBASE_URL).child(roomName).child("questions");

        mRecyclerView = (RecyclerView) findViewById(R.id.questionRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mQuestionRecyclerViewAdapter = new QuestionRecyclerViewAdapter(
                mFirebaseRef.orderByChild("echo").limitToFirst(200), this);

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(QuestionRoomActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuestionRoomActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        mRecyclerView.setAdapter(mQuestionRecyclerViewAdapter);

        ((TextView)findViewById(R.id.postQuestionTextView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constant.KEY_ROOM_NAME, roomName);
                intent.setClass(QuestionRoomActivity.this, NewQuestionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mQuestionRecyclerViewAdapter.cleanup();
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Question question = new Question("", input);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(question);
            inputText.setText("");
        }
    }

    public void updateEcho(String key) {

        final Firebase echoRef = mFirebaseRef.child(key).child("echo");
        echoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long echoValue = (Long) dataSnapshot.getValue();
                        Log.e("Echo update:", "" + echoValue);

                        echoRef.setValue(echoValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue - 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

    }

    public void Close(View view) {
        finish();
    }
}
