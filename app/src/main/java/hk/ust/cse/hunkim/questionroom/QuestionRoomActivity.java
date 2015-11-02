package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;
import hk.ust.cse.hunkim.questionroom.datamodel.Room;

public class QuestionRoomActivity extends AppCompatActivity implements OnQueryTextListener {
    private static final String TAG = "QuestionRoomActivity";

    private String roomId;

    private Room mRoom;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.questionRecyclerView)
    protected RecyclerView mRecyclerView;
    private QuestionRoomRecyclerViewAdapter mAdapter;

    private Thread roomUpdateThread;

    private String queryStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_room);
        ButterKnife.bind(this);


        Uri uri = getIntent().getData();
        roomId = uri.toString();
        roomId = roomId.substring(roomId.lastIndexOf("/") + 1);

        /*
        Intent intent = getIntent();
        assert (intent != null);

        roomId = intent.getStringExtra(Constant.KEY_ROOM_ID);
        assert (roomId != null);
*/
        setSupportActionBar(toolbar);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new QuestionRoomRecyclerViewAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        queryStr = "";
        mRoom = null;
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        Intent intent = new Intent();
        intent.putExtra(Constant.KEY_ROOM_ID, roomId);
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
    protected void onPause() {
        super.onPause();
        roomUpdateThread.interrupt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        roomUpdateThread = new Thread(new RequestRoomTask(this, roomId));
        roomUpdateThread.start();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        queryStr = query;
        updateRoom(mRoom);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        queryStr = newText;
        updateRoom(mRoom);
        return false;
    }

    private void updateRoom(Room room) {
        if (room != null) {
            toolbar.setTitle(room.getName());
            mAdapter.changeData(getSearchFilteredList(room.getQuestions()));
        }
    }

    private List<Question> getSearchFilteredList(List<Question> ori) {
        if (queryStr.isEmpty()) {
            return ori;
        } else {
            List<Question> filtered = new ArrayList<>();
            for (Question q : ori) {
                if (q.getMessage().toLowerCase().contains(queryStr.toLowerCase())) {
                    filtered.add(q);
                }
            }
            return filtered;
        }
    }

    private class RequestRoomTask implements Runnable {
        private Context context;
        private String roomId;

        public RequestRoomTask(Context context, String roomId) {
            this.context = context;
            this.roomId = roomId;
        }

        @Override
        public void run() {
            while (true) {
                Log.d(TAG, "Polling room object.");
                JsonObjectRequest request = new JsonObjectRequest("https://qweet-api.herokuapp.com/room/" + roomId,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                mRoom = new GsonBuilder()
                                        .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                                        .create()
                                        .fromJson(response.toString(), Room.class);
                                updateRoom(mRoom);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                VolleySingleton.getInstance(context).addToRequestQueue(request);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
