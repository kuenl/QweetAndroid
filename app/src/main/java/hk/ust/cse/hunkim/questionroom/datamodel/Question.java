package hk.ust.cse.hunkim.questionroom.datamodel;

import java.util.Date;

/**
 * Created by hunkim on 7/16/15.
 */
public class Question implements Comparable<Question> {
    private String id;
    private String roomId;
    private String headline;
    private String message;
    private boolean completed;
    private int upVote;
    private int downVote;
    private Date createdAt;
    private Date updatedAt;

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

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    /**
     * New one/high echo goes bottom
     *
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Question other) {
        /*
        if (this.upVote == other.upVote) {
            if (other.timestamp == this.timestamp) {
                return 0;
            }
            return other.timestamp > this.timestamp ? -1 : 1;
        }*/
        return this.upVote - other.upVote;
    }


    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Question) {
            Question other = (Question) o;
            if (id != null && other.id != null) {
                return id.equals(other.id);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}