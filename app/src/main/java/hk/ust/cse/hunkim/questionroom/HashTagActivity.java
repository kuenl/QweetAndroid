package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;
import hk.ust.cse.hunkim.questionroom.datamodel.Room;
import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;

public class HashTagActivity extends AppCompatActivity {
    private static final String TAG = "HashTagActivity";

    private String roomId;
    private String tag;

    private DBUtil dbutil;

    //private Room mRoom;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.questionRecyclerView)
    protected RecyclerView mRecyclerView;
    private QuestionRoomRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Thread roomUpdateThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hash_tag);
        ButterKnife.bind(this);

        Uri uri = getIntent().getData();
        String uriStr = uri.toString();

        String file = uriStr.substring(uriStr.lastIndexOf("/") + 1);
        int hashIdx = file.lastIndexOf("#");
        roomId = file.substring(0, hashIdx);
        tag = file.substring(hashIdx);
        Log.d(TAG, roomId);
        Log.d(TAG, tag);

        toolbar.setTitle(tag);
        setSupportActionBar(toolbar);
        toolbar.setTitle(tag);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new QuestionRoomRecyclerViewAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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

    private void updateRoom(Room room) {
        if (room != null) {
            Pattern hashTagPattern = Pattern.compile("([\\s`\\-=\\[\\]\\\\;',.~!@#$%^*()+{}\\|:\"<>?]|^)" + tag + "([\\s`\\-=\\[\\]\\\\;',\\.\\/~!@#$%^&*()+{}\\|:\"<>?]|$)");
            Log.d(TAG, hashTagPattern.toString());
            List<Question> ori = room.getQuestions();
            List<Question> filtered = new ArrayList<>();
            for (Question q : ori) {
                if (hashTagPattern.matcher(q.getMessage()).find()) {
                    filtered.add(q);
                }
            }
            mAdapter.changeData(filtered);
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
                JsonObjectRequest request = new JsonObjectRequest(Constant.BASE_URL + "room/" + roomId,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Room mRoom = new GsonBuilder()
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
