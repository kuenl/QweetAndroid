package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import hk.ust.cse.hunkim.questionroom.question.Question;

public class NewQuestionActivity extends Activity {


    private Firebase mFirebaseRef;
    private String roomName;

    private ValueEventListener mConnectedListener;
    private EditText titleEditText;
    private EditText questionEditText;

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
}
