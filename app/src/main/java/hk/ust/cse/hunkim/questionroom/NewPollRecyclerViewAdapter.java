package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 7/10/2015.
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = itemList.get(position);
        holder.setText(position, item);
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemNumber;
        private EditText itemDescription;
        private TextView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemNumber = (TextView) itemView.findViewById(R.id.pollItemNumberTextView);
            itemDescription = (EditText) itemView.findViewById(R.id.pollItemEditText);
            deleteButton = (TextView) itemView.findViewById(R.id.pollItemDeleteTextView);
        }

        public void setText(int index, String item) {
            itemNumber.setText(String.valueOf(index + 1) + '.');
            itemDescription.setText(item);
        }
    }
}