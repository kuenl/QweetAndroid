package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;
import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;

/**
 * Created by Administrator on 23/10/2015.
 */
public class QuestionRoomRecyclerViewAdapter extends RecyclerView.Adapter<QuestionRoomRecyclerViewAdapter.ViewHolder>  {
    private Context mContext;
    private List<Question> mDataset;
    private DBUtil dbUtil;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.titleTextView)
        TextView mTitleTextView;
        @Bind(R.id.questionTextView)
        TextView mQuestionTextView;
        @Bind(R.id.summaryTextView)
        TextView mSummaryTextView;
        @Bind(R.id.timeTextView)
        TextView mTimeTextView;
        @Bind(R.id.likeButton)
        LinearLayout mLikeButton;
        @Bind(R.id.dislikeButton)
        LinearLayout mDislikeButton;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public QuestionRoomRecyclerViewAdapter(Context context, List<Question> myDataset) {
        mContext = context;
        mDataset = new ArrayList<>();
        dbUtil = new DBUtil(new DBHelper(context));
    }

    public void changeData(List<Question> data) {
        mDataset = data;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public QuestionRoomRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_question_simple, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Question data = mDataset.get(position);
        String id = data.getId();
        holder.mTitleTextView.setText(data.getHeadline());
        holder.mQuestionTextView.setText(data.getMessage());
        holder.mSummaryTextView.setText(data.getRatingSummary());
        holder.mTimeTextView.setText(DateUtils.getRelativeTimeSpanString(data.getCreatedAt().getTime()));
        changeVoteViewState(holder.mLikeButton, dbUtil.contains(id, "like"));
        changeVoteViewState(holder.mDislikeButton, dbUtil.contains(id, "dislike"));
        holder.mLikeButton.setTag(id);
        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = (String) v.getTag();
                if (dbUtil.contains(key, "like")) {
                    //activity.updateLike(key, false);
                    dbUtil.delete(key, "like");
                    changeVoteViewState(holder.mLikeButton, false);
                } else if (!dbUtil.contains(key, "dislike")) {
                    //activity.updateLike(key, true);
                    onUpDownVoteViewClick(holder.mLikeButton, true);
                    dbUtil.put(key, "like");
                    changeVoteViewState(holder.mLikeButton, true);
                }

            }
        });
        holder.mDislikeButton.setTag(id);
        holder.mDislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = (String) v.getTag();
                if (dbUtil.contains(key, "dislike")) {
                    //activity.updateDislike(key, false);
                    dbUtil.delete(key, "dislike");
                    changeVoteViewState(holder.mDislikeButton, false);
                } else if (!dbUtil.contains(key, "like")) {
                    //activity.updateDislike(key, true);

                    onUpDownVoteViewClick(holder.mLikeButton, false);
                    dbUtil.put(key, "dislike");
                    changeVoteViewState(holder.mDislikeButton, true);
                }

            }
        });

        Linkify.addLinks(holder.mQuestionTextView, Linkify.ALL);

        Pattern hashTagPattern = Pattern.compile("#[^\\s`\\-=\\[\\]\\\\;',\\.\\/~!@#$%^&*()+{}\\|:\"<>?]+");
        Linkify.addLinks(holder.mQuestionTextView, hashTagPattern, "", new Linkify.MatchFilter() {
            @Override
            public boolean acceptMatch(CharSequence s, int start, int end) {
                if (start == 0) {
                    return true;
                } else if (start > 0) {
                    Pattern pattern = Pattern.compile("[\\s`\\-=\\[\\]\\\\;',.~!@#$%^*()+{}\\|:\"<>?]");
                    Matcher matcher = pattern.matcher(String.valueOf(s.charAt(start - 1)));
                    if (matcher.find()) {
                        return true;
                    }
                }
                return false;
            }
        }, null);

    }

    private void changeVoteViewState(LinearLayout v, boolean active){
        if (active) {
            int color = mContext.getResources().getColor(R.color.colorAccent);
            ((ImageView) v.getChildAt(0)).setColorFilter(color);
            ((TextView) v.getChildAt(1)).setTextColor(color);
        } else {
            int color = mContext.getResources().getColor(R.color.inactive_icon_light);
            ((ImageView) v.getChildAt(0)).setColorFilter(color);
            ((TextView) v.getChildAt(1)).setTextColor(color);
        }
    }

    private void onUpDownVoteViewClick(final LinearLayout v, boolean upvote){
        String id = (String) v.getTag();
        if (upvote) {
            if (!dbUtil.contains(id, "like") && !dbUtil.contains(id, "dislike")) {
                JsonObjectRequest request = new JsonObjectRequest(Method.PUT, "https://qweet-api.herokuapp.com/question/" + id + "/upvote", new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Question question = new GsonBuilder()
                                .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                                .create()
                                .fromJson(response.toString(), Question.class);
                        final String id = question.getId();
                        changeVoteViewState(v, true);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        changeVoteViewState(v, false);
                    }
                });
                VolleySingleton.getInstance(mContext).addToRequestQueue(request);
            }
        } else {
            if (!dbUtil.contains(id, "like") && !dbUtil.contains(id, "dislike")) {
                JsonObjectRequest request = new JsonObjectRequest(Method.POST, "https://qweet-api.herokuapp.com/question/" + id + "/downvote", new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Question question = new GsonBuilder()
                                .registerTypeAdapter(Date.class, ISO8601UTCDateTypeAdapter.getInstance())
                                .create()
                                .fromJson(response.toString(), Question.class);
                        final String id = question.getId();
                        changeVoteViewState(v, true);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        changeVoteViewState(v, false);
                    }
                });
                VolleySingleton.getInstance(mContext).addToRequestQueue(request);
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
