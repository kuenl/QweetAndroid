package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Query;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * Created by Administrator on 24/9/15.
 */
public class QuestionRecyclerViewAdapter extends FirebaseRecyclerViewAdapter<Question> {

    public static enum QUESTION_TYPE {
        QUESTION_TYPE_NOTE,
        QUESTION_TYPE_VOTE
    }

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;

    /**
     * @param mRef    The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param context The activity containing the ListView
     */
    public QuestionRecyclerViewAdapter(Query mRef, Context context) {
        super(mRef, Question.class);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    protected void sortModels(List<Question> mModels) {
        Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == QUESTION_TYPE.QUESTION_TYPE_VOTE.ordinal()) {
            return new VoteViewHolder(mLayoutInflater.inflate(R.layout.item_vote, parent, false));
        } else {
            return new NoteViewHolder(mLayoutInflater.inflate(R.layout.item_note, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final QuestionRoomActivity activity = (QuestionRoomActivity) mContext;
        final DBUtil dbUtil = activity.getDbutil();
        Question question = getItem(position);
        if (holder instanceof NoteViewHolder) {
            final NoteViewHolder castedHolder = (NoteViewHolder) holder;
            castedHolder.setTitle(question.getTitle());
            castedHolder.setQuestion(question.getMessage());
            castedHolder.setSummary(question.getLike(), question.getDislike(), 0);
            castedHolder.setTime(question.getTimestamp());
            if (dbUtil.contains(question.getKey(), "like")) {
                castedHolder.setLikeButtonState(true);
            }
            castedHolder.mLikeButton.setTag(question.getKey());
            castedHolder.mLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = (String) v.getTag();
                    if (dbUtil.contains(key, "like")) {
                        activity.updateLike(key, false);
                        dbUtil.delete(key, "like");
                        castedHolder.setLikeButtonState(false);
                    } else if (!dbUtil.contains(key, "dislike")) {
                        activity.updateLike(key, true);
                        dbUtil.put(key, "like");
                        castedHolder.setLikeButtonState(true);
                    }

                }
            });
            castedHolder.mDislikeButton.setTag(question.getKey());
            castedHolder.mDislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = (String) v.getTag();
                    if (dbUtil.contains(key, "dislike")) {
                        activity.updateDislike(key, false);
                        dbUtil.delete(key, "dislike");
                        castedHolder.setDislikeButtonState(false);
                    } else if (!dbUtil.contains(key, "like")) {
                        activity.updateDislike(key, true);
                        dbUtil.put(key, "dislike");
                        castedHolder.setDislikeButtonState(true);
                    }

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return QUESTION_TYPE.QUESTION_TYPE_NOTE.ordinal();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private boolean liked;
        private boolean disliked;
        private String key;
        private TextView mTitleTextView;
        private TextView mQuestionTextView;
        private TextView mSummaryTextView;
        private TextView mTimeTextView;
        public LinearLayout mLikeButton;
        public LinearLayout mDislikeButton;

        NoteViewHolder(final View view) {
            super(view);
            mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
            mQuestionTextView = (TextView) view.findViewById(R.id.questionTextView);
            mSummaryTextView = (TextView) view.findViewById(R.id.summaryTextView);
            mTimeTextView = (TextView) view.findViewById(R.id.timeTextView);
            mLikeButton = (LinearLayout) view.findViewById(R.id.likeButton);
            mDislikeButton = (LinearLayout) view.findViewById(R.id.dislikeButton);
            key = null;
            liked = false;
            disliked = false;
        }

        public void setTitle(String title) {
            mTitleTextView.setText(title);
        }

        public void setQuestion(String question) {
            mQuestionTextView.setText(question);
        }

        public void setSummary(int like, int dislike, int comment) {
            StringBuilder builder = new StringBuilder();
            if (like == 1) {
                builder.append("1 like  ");
            } else if (like > 1) {
                builder.append(like);
                builder.append(" likes  ");
            }
            if (dislike == 1) {
                builder.append("1 dislike  ");
            } else if (dislike > 1) {
                builder.append(dislike);
                builder.append(" dislikes  ");
            }
            if (comment == 1) {
                builder.append("1 comment");
            } else if (comment > 1) {
                builder.append(comment);
                builder.append(" comments");
            }
            mSummaryTextView.setText(builder.toString());
        }

        public void setLikeButtonState(boolean active) {
            if (active) {
                ((ImageView)mLikeButton.getChildAt(0)).setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                ((TextView)mLikeButton.getChildAt(1)).setTextColor(Color.rgb(88, 144, 255));
            } else {
                ((ImageView)mLikeButton.getChildAt(0)).setImageResource(R.drawable.ic_thumb_up_grey_24dp);
                ((TextView)mLikeButton.getChildAt(1)).setTextColor(Color.rgb(175, 180, 189));
            }
        }

        public void setDislikeButtonState(boolean active) {
            if (active) {
                ((ImageView)mDislikeButton.getChildAt(0)).setImageResource(R.drawable.ic_thumb_down_blue_24dp);
                ((TextView)mDislikeButton.getChildAt(1)).setTextColor(Color.rgb(88, 144, 255));
            } else {
                ((ImageView)mDislikeButton.getChildAt(0)).setImageResource(R.drawable.ic_thumb_down_grey_24dp);
                ((TextView)mDislikeButton.getChildAt(1)).setTextColor(Color.rgb(175, 180, 189));
            }
        }

        public void setTime(long time) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
            String timeStr = dateFormat.format(time) + " at " + timeFormat.format(time).toLowerCase();
            mTimeTextView.setText(timeStr);
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class VoteViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;

        VoteViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ImageViewHolder", "onClick--> position = " + getPosition());
                }
            });
        }
    }
}
