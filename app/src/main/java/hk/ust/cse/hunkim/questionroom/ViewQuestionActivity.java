package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;
import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;

public class ViewQuestionActivity extends AppCompatActivity {
    private static final String TAG = "ViewQuestionActivity";

    //@Bind(R.id.titleTextView)
    //TextView mTitleTextView;
    @Bind(R.id.questionTextView)
    TextView mQuestionTextView;
    @Bind(R.id.pollRecyclerView)
    RecyclerView pollRecyclerView;
    @Bind(R.id.summaryTextView)
    TextView mSummaryTextView;
    @Bind(R.id.timeTextView)
    TextView mTimeTextView;
    @Bind(R.id.likeButton)
    LinearLayout mLikeButton;
    @Bind(R.id.dislikeButton)
    LinearLayout mDislikeButton;
    @Bind(R.id.voteButton)
    Button mVoteButton;

    private DBUtil dbUtil;


    private String questionId;

    private Question mQuestion;

    private Thread mRequestUpdateRunnable;

    private PollRecyclerViewAdapter mPollAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);
        ButterKnife.bind(this);

        Uri uri = getIntent().getData();
        questionId = uri.toString();

        questionId = questionId.substring(questionId.lastIndexOf("/") + 1);

        /*

        toolbar.setTitle(tag);
        setSupportActionBar(toolbar);
        toolbar.setTitle(tag);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new QuestionRoomRecyclerViewAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
*/
        DBHelper mDbHelper = new DBHelper(this);
        dbUtil = new DBUtil(mDbHelper);
        mPollAdapter = new PollRecyclerViewAdapter(this);
        pollRecyclerView.setLayoutManager(new RecyclerViewWrapContentLayoutManager(this));
        pollRecyclerView.setHasFixedSize(true);
        pollRecyclerView.setAdapter(mPollAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRequestUpdateRunnable.interrupt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRequestUpdateRunnable = new Thread(new RequestUpdateRunnable(this, questionId));
        mRequestUpdateRunnable.start();
    }

    private void updateActivity(final Question data) {
        final String id = data.getId();
        //mTitleTextView.setText(data.getHeadline());
        mQuestionTextView.setText(data.getMessage());
        mSummaryTextView.setText(data.getRatingSummary());
        mTimeTextView.setText(DateUtils.getRelativeTimeSpanString(data.getCreatedAt().getTime()));
        mPollAdapter.changeData(data);

        if (data.getPollOptions().size() > 0) {
            mPollAdapter.changeData(data);
            if (!dbUtil.contains(data.getId(), "voted")) {
                mVoteButton.setVisibility(View.VISIBLE);
                mVoteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPollAdapter.vote();
                    }
                });
            } else {
                mVoteButton.setVisibility(View.GONE);
            }
        } else {
            pollRecyclerView.setVisibility(View.GONE);
        }
        changeVoteViewState(mLikeButton, dbUtil.contains(id, "upvote"));
        changeVoteViewState(mDislikeButton, dbUtil.contains(id, "downvote"));
        mLikeButton.setTag(id);
        mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.vote(ViewQuestionActivity.this, true, !dbUtil.contains(data.getId(), "upvote"));
            }
        });
        mDislikeButton.setTag(id);
        mDislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.vote(ViewQuestionActivity.this, false, !dbUtil.contains(data.getId(), "downvote"));
            }
        });
    }

    private void changeVoteViewState(LinearLayout v, boolean active) {
        if (active) {
            int color = getResources().getColor(R.color.colorAccent);
            ((ImageView) v.getChildAt(0)).setColorFilter(color);
            ((TextView) v.getChildAt(1)).setTextColor(color);
        } else {
            int color = getResources().getColor(R.color.inactive_icon_light);
            ((ImageView) v.getChildAt(0)).setColorFilter(color);
            ((TextView) v.getChildAt(1)).setTextColor(color);
        }
    }

    private class RequestUpdateRunnable implements Runnable {
        private Context context;
        private String questionId;

        public RequestUpdateRunnable(Context context, String questionId) {
            this.context = context;
            this.questionId = questionId;
        }

        @Override
        public void run() {
            while (true) {
                Log.d(TAG, "Polling room object.");
                JsonObjectRequest request = new JsonObjectRequest(Constant.BASE_URL + "question/" + questionId,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                mQuestion = new GsonBuilder()
                                        .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                                        .create()
                                        .fromJson(response.toString(), Question.class);
                                updateActivity(mQuestion);
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
