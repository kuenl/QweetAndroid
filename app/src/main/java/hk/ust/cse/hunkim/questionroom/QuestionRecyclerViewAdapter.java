package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;

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
        model.setId(key);
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
            castedHolder.setTitle(question.getHeadline());
            castedHolder.setQuestion(question.getMessage());
            castedHolder.setSummary(question.getUpVote(), question.getDownVote(), 0);
            //castedHolder.setTime(question.getCreatedAt());
            if (dbUtil.contains(question.getId(), "like")) {
                setButtonState(castedHolder.mLikeButton, true);
                setButtonState(castedHolder.mDislikeButton, false);
            } else if (dbUtil.contains(question.getId(), "dislike")) {
                setButtonState(castedHolder.mLikeButton, false);
                setButtonState(castedHolder.mDislikeButton, true);
            } else {
                setButtonState(castedHolder.mLikeButton, false);
                setButtonState(castedHolder.mDislikeButton, false);
            }
            castedHolder.mLikeButton.setTag(question.getId());
            castedHolder.mLikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = (String) v.getTag();
                    if (dbUtil.contains(key, "like")) {
                        //activity.updateLike(key, false);
                        dbUtil.delete(key, "like");
                        setButtonState(castedHolder.mLikeButton, false);
                    } else if (!dbUtil.contains(key, "dislike")) {
                        //activity.updateLike(key, true);
                        dbUtil.put(key, "like");
                        setButtonState(castedHolder.mLikeButton, true);
                    }

                }
            });
            castedHolder.mDislikeButton.setTag(question.getId());
            castedHolder.mDislikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = (String) v.getTag();
                    if (dbUtil.contains(key, "dislike")) {
                        //activity.updateDislike(key, false);
                        dbUtil.delete(key, "dislike");
                        setButtonState(castedHolder.mDislikeButton, false);
                    } else if (!dbUtil.contains(key, "like")) {
                        //activity.updateDislike(key, true);
                        dbUtil.put(key, "dislike");
                        setButtonState(castedHolder.mDislikeButton, true);
                    }

                }
            });

            Linkify.addLinks(castedHolder.mQuestionTextView, Linkify.ALL);

            Pattern hashTagPattern = Pattern.compile("#[^\\s`\\-=\\[\\]\\\\;',\\.\\/~!@#$%^&*()+{}\\|:\"<>?]+");
            Linkify.addLinks(castedHolder.mQuestionTextView, hashTagPattern, "", new Linkify.MatchFilter() {
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
    }

    public void setButtonState(LinearLayout button, boolean active) {
        if (active) {
            int color = mContext.getResources().getColor(R.color.light_blue_300);
            ((ImageView) button.getChildAt(0)).setColorFilter(color);
            ((TextView) button.getChildAt(1)).setTextColor(color);
        } else {
            int color = mContext.getResources().getColor(R.color.inactive_icon_light);
            ((ImageView) button.getChildAt(0)).setColorFilter(color);
            ((TextView) button.getChildAt(1)).setTextColor(color);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return QUESTION_TYPE.QUESTION_TYPE_NOTE.ordinal();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
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

        NoteViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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

        public void setTime(long time) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
            String timeStr = dateFormat.format(time) + " at " + timeFormat.format(time).toLowerCase();
            mTimeTextView.setText(timeStr);
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
