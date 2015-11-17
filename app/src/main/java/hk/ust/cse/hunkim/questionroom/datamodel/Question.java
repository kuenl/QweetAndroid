package hk.ust.cse.hunkim.questionroom.datamodel;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.VolleySingleton;
import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;

/**
 * Data model for question
 * Created by Leung Pui Kuen on 7/16/15.
 */
public class Question implements Comparable<Question> {
    private static boolean sortScore = true;

    private String id;
    private String roomId;
    //private String headline;
    private String message;
    private boolean completed;
    private int upVote;
    private int downVote;
    private String image;
    private List<PollOption> pollOptions;
    private List<Comment> comments;
    private double score;
    private Date createdAt;
    private Date updatedAt;

    public Question() {
    }

    public Question(String message) {
        this.message = message;
    }

    /*
    public Question(String headline, String message) {
        this.headline = headline;
        this.message = message;
    }
    */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /*
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }
    */

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getUpVote() {
        return upVote;
    }

    public void setUpVote(int upVote) {
        this.upVote = upVote;
    }

    public int getDownVote() {
        return downVote;
    }

    public void setDownVote(int downVote) {
        this.downVote = downVote;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<PollOption> getPollOptions() {
        return pollOptions;
    }

    public void setPollOptions(List<PollOption> pollOptions) {
        this.pollOptions = pollOptions;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRatingSummary() {
        StringBuilder builder = new StringBuilder();
        if (upVote == 1) {
            builder.append("1 like  ");
        } else if (upVote > 1) {
            builder.append(upVote);
            builder.append(" likes  ");
        }
        if (downVote == 1) {
            builder.append("1 dislike  ");
        } else if (downVote > 1) {
            builder.append(downVote);
            builder.append(" dislikes  ");
        }
        /*
        if (comment == 1) {
            builder.append("1 comment");
        } else if (comment > 1) {
            builder.append(comment);
            builder.append(" comments");
        }*/
        return builder.toString();
    }

    public static void setSortScore(boolean sortScore) {
        Question.sortScore = sortScore;
    }

    /**
     * New one/high echo goes bottom
     *
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Question other) {
        if (sortScore && score != other.score) {
            return score < other.score ? 1 : -1;
        }
        if (!createdAt.equals(other.createdAt)) {
            return createdAt.getTime() < other.createdAt.getTime() ? 1 : -1;
        }
        return 0;
    }

    public void vote(final Context context, boolean upVote, final boolean add) {
        final DBUtil db = new DBUtil(new DBHelper(context));
        final String action = upVote ? "upvote" : "downvote";
        if ((add && !db.contains(id, "upvote") && !db.contains(id, "downvote")) ||
                (!add && db.contains(id, action))) {
            int method = add ? Request.Method.PUT : Request.Method.DELETE;
            JsonObjectRequest request = new JsonObjectRequest(method, "https://qweet-api.herokuapp.com/question/" + id + "/" + action, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (add) {
                        db.put(id, action);
                    } else {
                        db.delete(id, action);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            VolleySingleton.getInstance(context).addToRequestQueue(request);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        return !(id != null ? !id.equals(question.id) : question.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}