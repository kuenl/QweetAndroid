package hk.ust.cse.hunkim.questionroom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.cse.hunkim.questionroom.datamodel.PollOption;
import hk.ust.cse.hunkim.questionroom.datamodel.Question;
import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;

/**
 * Adapter to show the polling stat.
 * Created by Leung Pui Kuen on 27/10/2015.
 */
public class PollRecyclerViewAdapter extends RecyclerView.Adapter<PollRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "PollRecyclerViewAdapter";

    private Context mContext;
    private List<PollOption> mDataset;
    private DBUtil dbUtil;

    private int maxVote;
    private int totalVote;
    private boolean voted;

    private int selectedIndex;
    private String selectedId;

    private List<RadioButton> radios;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.pollItemTextView)
        TextView pollItemTextView;
        @Bind(R.id.pollItemRadioButton)
        RadioButton pollItemRadioButton;
        @Bind(R.id.pollItemProgressBar)
        ProgressBar pollItemProgressBar;
        @Bind(R.id.pollItemPercentageTextView)
        TextView pollItemPercentageTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PollRecyclerViewAdapter(Context context) {
        mContext = context;
        mDataset = new ArrayList<>();
        dbUtil = new DBUtil(new DBHelper(mContext));
        maxVote = 1;
        selectedIndex = -1;
        selectedId = "";
        radios = new ArrayList<>();
    }

    public void changeData(Question data) {
        if (data != null) {
            voted = dbUtil.contains(data.getId(), "voted");
            if (data.getPollOptions() != null) {
                mDataset = data.getPollOptions();
                maxVote = 1;
                totalVote = 0;
                /*
                for (PollOption p : mDataset) {
                    p.setCount((int) (Math.random() * 100));
                }*/
                for (PollOption p : mDataset) {
                    totalVote += p.getCount();
                    maxVote = Math.max(maxVote, p.getCount());
                }
                if (totalVote == 0) {
                    totalVote = 1;
                }
                radios.clear();
                notifyDataSetChanged();
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PollRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_poll, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final PollOption data = mDataset.get(position);
        final String id = data.getId();
        if (voted) {
            holder.pollItemRadioButton.setVisibility(View.GONE);
        } else {
            holder.pollItemRadioButton.setTag(id);
            radios.add(holder.pollItemRadioButton);
            updateRadio();
            //holder.pollItemRadioButton.setChecked(selectedId.equals(id));
            holder.pollItemRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedId = id;
                    updateRadio();
                }
            });
        }
        String voteStr = data.getMessage() + " (" + String.valueOf(data.getCount());
        if (data.getCount() < 2) {
            voteStr += " Vote)";
        } else {
            voteStr += " Votes)";
        }
        holder.pollItemTextView.setText(voteStr);
        holder.pollItemProgressBar.setMax(maxVote);
        holder.pollItemProgressBar.setProgress(data.getCount());
        String percent = String.format("%3d", (int) Math.round(data.getCount() * 100.0 / totalVote)) + "%";
        holder.pollItemPercentageTextView.setText(percent);
    }

    private void updateRadio() {
        for (RadioButton i : radios) {
            i.setChecked(i.getTag().equals(selectedId));
        }
    }

    public void vote() {
        if (!selectedId.isEmpty()) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Constant.BASE_URL + "pollOption/" + selectedId + "/poll",
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                dbUtil.put(response.getJSONObject("questionId").getString("id"), "voted");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            VolleySingleton.getInstance(mContext).addToRequestQueue(request);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
