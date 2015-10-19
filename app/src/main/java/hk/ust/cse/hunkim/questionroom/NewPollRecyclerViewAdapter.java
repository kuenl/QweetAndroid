package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An Adapter used for listing polling options while creating new question.
 * Created by Leung Pui Kuen on 7/10/2015.
 */
public class NewPollRecyclerViewAdapter extends
        RecyclerView.Adapter<NewPollRecyclerViewAdapter.ViewHolder> {

    private List<String> itemList;

    public NewPollRecyclerViewAdapter(List<String> itemList) {
        this.itemList = itemList;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public NewPollRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View root = inflater.inflate(R.layout.item_poll_new, parent, false);

        // Return a new holder instance
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String item = itemList.get(position);
        holder.setText(position, item);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemDescription.clearFocus();
                itemList.remove(position);
                notifyDataSetChanged();
                // notifyItemRemoved(position);
            }
        });
        holder.itemDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteButton.setVisibility(View.INVISIBLE);
                    itemList.set(position, ((EditText) v).getText().toString());
                }
            }
        });
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.pollItemNumberTextView)
        TextView itemNumber;
        @Bind(R.id.pollItemEditText)
        EditText itemDescription;
        @Bind(R.id.pollItemDeleteImageButton)
        ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setText(int index, String item) {
            String indexStr = String.valueOf(index + 1) + ".";
            itemNumber.setText(indexStr);
            itemDescription.setText(item);
        }
    }
}