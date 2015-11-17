package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hk.ust.cse.hunkim.questionroom.datamodel.Comment;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;
import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_DEFAULT;

public class ViewQuestionActivity extends AppCompatActivity {
    private static final String TAG = "ViewQuestionActivity";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    //@Bind(R.id.titleTextView)
    //TextView mTitleTextView;
    @Bind(R.id.questionTextView)
    TextView mQuestionTextView;
    @Bind(R.id.pollRecyclerView)
    RecyclerView pollRecyclerView;
    @Bind(R.id.questionImageView)
    ImageView questionImageView;
    @Bind(R.id.commentRecyclerView)
    RecyclerView commentRecyclerView;
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
    @Bind(R.id.commentEditText)
    EditText commentEditText;

    private DBUtil dbUtil;

    private String roomId;

    private String questionId;

    //private Question mQuestion;

    private Thread mRequestUpdateRunnable;

    private PollRecyclerViewAdapter mPollAdapter;
    private CommentRecyclerViewAdapter mCommentAdapter;

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

        setSupportActionBar(toolbar);

        DBHelper mDbHelper = new DBHelper(this);
        dbUtil = new DBUtil(mDbHelper);
        mPollAdapter = new PollRecyclerViewAdapter(this);
        pollRecyclerView.setLayoutManager(new RecyclerViewWrapContentLayoutManager(this));
        pollRecyclerView.setHasFixedSize(true);
        pollRecyclerView.setAdapter(mPollAdapter);

        mCommentAdapter = new CommentRecyclerViewAdapter(this);
        commentRecyclerView.setLayoutManager(new RecyclerViewWrapContentLayoutManager(this));
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setAdapter(mCommentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    Log.d(TAG, "if case");
                    upIntent.setAction(ACTION_VIEW);
                    upIntent.addCategory(CATEGORY_DEFAULT);
                    upIntent.setData(Uri.parse("content://qweet.kuenl.com/room/" + roomId));
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    Log.d(TAG, "else case");
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            case R.id.item_share:
                shareTextUrl();
                return true;
        }
        return false;
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

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Check out the question in Qweet");
        share.putExtra(Intent.EXTRA_TEXT, Constant.BASE_URL + "/question/" + questionId);
        startActivity(Intent.createChooser(share, "Share"));
    }

    private void updateActivity(final Question data) {
        final String id = data.getId();
        //mTitleTextView.setText(data.getHeadline());
        roomId = data.getRoomId();
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

        if(data.getComments() != null && data.getComments().size() > 0) {
            mCommentAdapter.changeData(data);
        }


        if (data.getImage() != null && !data.getImage().isEmpty()) {
            ImageRequest request = new ImageRequest(data.getImage(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    questionImageView.setImageBitmap(response);
                }
            }, 0, 0, null, null);
            VolleySingleton.getInstance(this).addToRequestQueue(request);
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
                JsonObjectRequest request = new JsonObjectRequest(Constant.BASE_URL + "question/" + questionId,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Question question = new GsonBuilder()
                                        .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                                        .create()
                                        .fromJson(response.toString(), Question.class);
                                updateActivity(question);
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

    @OnClick(R.id.commentImageButton)
    public void postComment() {
        String msg = commentEditText.getText().toString();
        if (!questionId.trim().isEmpty() && !msg.trim().isEmpty()) {
            Comment comment = new Comment();
            comment.setQuestionId(questionId);
            comment.setMessage(msg);
            try {
                JSONObject jsonObject = new JSONObject(new Gson().toJson(comment));
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.BASE_URL + "comment", jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                commentEditText.setText("");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                VolleySingleton.getInstance(this).addToRequestQueue(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
