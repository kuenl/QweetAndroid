package hk.ust.cse.hunkim.questionroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;

public class QuestionRoomActivity extends AppCompatActivity implements OnQueryTextListener {

    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private QuestionRecyclerViewAdapter mQuestionRecyclerViewAdapter;
    private DBUtil dbutil;

    @Bind(R.id.questionRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_room);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        assert (intent != null);

        roomName = intent.getStringExtra(Constant.KEY_ROOM_NAME);
        if (roomName == null || roomName.isEmpty()) {
            roomName = "all";
        }

        boolean newRoom = intent.getBooleanExtra(Constant.KEY_ROOM_NEW, false);
        if (newRoom) {
            roomName = "all";
        }

        toolbar.setTitle(roomName);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);

        mFirebaseRef = new Firebase(Constant.FIREBASE_URL).child(roomName).child("questions");

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

        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);

    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_ROOM_NAME, roomName);
        intent.setClass(QuestionRoomActivity.this, NewQuestionActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_question_room, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mQuestionRecyclerViewAdapter.cleanup();
    }

    public DBUtil getDbutil() {
        return dbutil;
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

    public void updateLike(String key, final boolean add) {
        final Firebase likeRef = mFirebaseRef.child(key).child("like");
        likeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long likeValue = (Long) dataSnapshot.getValue();
                        Log.e("Echo update:", "" + likeValue);
                        if (add) {
                            likeRef.setValue(likeValue + 1);
                        } else {
                            likeRef.setValue(likeValue - 1);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
    }

    public void updateDislike(String key, final boolean add) {
        final Firebase dislikeRef = mFirebaseRef.child(key).child("dislike");
        dislikeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long dislikeValue = (Long) dataSnapshot.getValue();
                        Log.e("Echo update:", "" + dislikeValue);
                        if (add) {
                            dislikeRef.setValue(dislikeValue + 1);
                        } else {
                            dislikeRef.setValue(dislikeValue - 1);
                        }
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
