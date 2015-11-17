package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.cse.hunkim.questionroom.datamodel.Comment;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;

/**
 * Created by Administrator on 9/11/15.
 */
public class CommentRecyclerViewAdapter  extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "CommentRecyclerViewAdapter";

    private Context mContext;
    private List<Comment> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.commentBodyTextView)
        TextView commentBodyTextView;
        @Bind(R.id.commentTimeTextView)
        TextView commentTimeTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CommentRecyclerViewAdapter(Context context) {
        mContext = context;
        mDataset = new ArrayList<>();
    }

    public void changeData(Question data) {
        if (data != null) {
            if (data.getComments() != null) {
                mDataset = data.getComments();
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = mDataset.get(position);
        holder.commentBodyTextView.setText(comment.getMessage());
        holder.commentTimeTextView.setText(DateUtils.getRelativeTimeSpanString(comment.getCreatedAt().getTime()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
