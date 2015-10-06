package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.Collections;
import java.util.List;

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
     * @param mRef        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param context     The activity containing the ListView
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
        Question question = getItem(position);
        if (holder instanceof NoteViewHolder) {
            NoteViewHolder castedHolder = (NoteViewHolder) holder;
            castedHolder.mTitleTextView.setText(question.getHead());
            castedHolder.mQuestionTextView.setText(question.getWholeMsg());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return QUESTION_TYPE.QUESTION_TYPE_NOTE.ordinal();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTextView;
        TextView mQuestionTextView;

        NoteViewHolder(View view) {
            super(view);
            mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
            mQuestionTextView = (TextView) view.findViewById(R.id.questionTextView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TextViewHolder", "onClick--> position = " + getPosition());
                }
            });
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
