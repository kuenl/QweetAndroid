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

import java.util.ArrayList;
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
public class QuestionRoomRecyclerViewAdapter extends RecyclerView.Adapter<QuestionRoomRecyclerViewAdapter.ViewHolder> {
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
        final Question data = mDataset.get(position);
        final String id = data.getId();
        holder.mTitleTextView.setText(data.getHeadline());
        holder.mQuestionTextView.setText(data.getMessage());
        holder.mSummaryTextView.setText(data.getRatingSummary());
        holder.mTimeTextView.setText(DateUtils.getRelativeTimeSpanString(data.getCreatedAt().getTime()));
        changeVoteViewState(holder.mLikeButton, dbUtil.contains(id, "upvote"));
        changeVoteViewState(holder.mDislikeButton, dbUtil.contains(id, "downvote"));
        holder.mLikeButton.setTag(id);
        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.vote(mContext, true, !dbUtil.contains(data.getId(), "upvote"));
            }
        });
        holder.mDislikeButton.setTag(id);
        holder.mDislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.vote(mContext, false, !dbUtil.contains(data.getId(), "downvote"));
            }
        });
        Linkify.addLinks(holder.mQuestionTextView, Linkify.ALL);

        String url = "content://qweet.kuenl.com/room/" + data.getRoomId();

        Pattern hashTagPattern = Pattern.compile("#[^\\s`\\-=\\[\\]\\\\;',\\.\\/~!@#$%^&*()+{}\\|:\"<>?]+");
        Linkify.addLinks(holder.mQuestionTextView, hashTagPattern, url, new Linkify.MatchFilter() {
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

    private void changeVoteViewState(LinearLayout v, boolean active) {
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
