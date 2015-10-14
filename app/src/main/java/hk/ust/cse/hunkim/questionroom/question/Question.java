package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

/**
 * Created by hunkim on 7/16/15.
 */
public class Question implements Comparable<Question> {

    /**
     * Must be synced with firebase JSON structure
     * Each must have getters
     */
    private String key;
    private String message;
    private String title;
    private long timestamp;
    private String[] tags;
    private int like;
    private int dislike;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Question() {
    }

    public Question(String title, String message) {
        this.title = title;
        this.message = message;
        this.like = 0;
        this.dislike = 0;
        this.timestamp = new Date().getTime();
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * New one/high echo goes bottom
     *
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Question other) {

        if (this.like == other.like) {
            if (other.timestamp == this.timestamp) {
                return 0;
            }
            return other.timestamp > this.timestamp ? -1 : 1;
        }
        return this.like - other.like;
    }


    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Question) {
            Question other = (Question) o;
            if (key!= null && other.key != null) {
                return key.equals(other.key);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
