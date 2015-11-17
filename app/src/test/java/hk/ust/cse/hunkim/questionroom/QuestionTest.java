package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.datamodel.Question;


/**
 * JUnit Test for Room
 * Created by Leung Pui Kuen on 7/15/15.
 */

public class QuestionTest extends TestCase {
    private Question q;
    private Date now;

    protected void setUp() throws Exception {
        super.setUp();

        now = new Date();

        q = new Question();
        q.setId("Id");
        q.setRoomId("Id");
        //q.setHeadline("Hello?");
        q.setMessage("This is very nice.");
        q.setCompleted(true);
        q.setUpVote(0);
        q.setDownVote(0);
        q.setCreatedAt(now);
        q.setUpdatedAt(now);
    }


    @SmallTest
    public void testId() {
        assertEquals("Id", "Id", q.getId());
    }

    @SmallTest
    public void testRoomId() {
        assertEquals("RoomId", "Id", q.getRoomId());
    }

    /*
    @SmallTest
    public void testHeadline() {
        assertEquals("Headline", "Hello?", q.getHeadline());
    }
    */

    @SmallTest
    public void testMessage() {
        assertEquals("Message", "This is very nice.", q.getMessage());
    }

    @SmallTest
    public void testCompleted() {
        assertEquals("Completed", true, q.isCompleted());
    }

    @SmallTest
    public void testUpVote() {
        assertEquals("UpVote", 0, q.getUpVote());
    }

    @SmallTest
    public void testDownVote() {
        assertEquals("DownVote", 0, q.getDownVote());
    }

    @SmallTest
    public void testCreatedAt() {
        assertEquals("CreatedAt", now, q.getCreatedAt());
    }

    @SmallTest
    public void testUpdatedAt() {
        assertEquals("UpdatedAt", now, q.getUpdatedAt());
    }

    @SmallTest
    public void testEqual() {
        assertEquals("Equal", true, q.equals(q));

        assertEquals("Equal", false, q.equals(null));

        assertEquals("Equal", false, q.equals(0));

        Question o = new Question();
        q.setId(null);
        o.setId(null);
        assertEquals("Equal", true, q.equals(o));
        q.setId(null);
        o.setId("0");
        assertEquals("Equal", false, q.equals(o));
        q.setId("0");
        o.setId(null);
        assertEquals("Equal", false, q.equals(o));
        q.setId("Id");
        o.setId("Id");
        assertEquals("Equal", true, q.equals(o));
        q.setId("Id");
        o.setId("id");
        assertEquals("Equal", false, q.equals(o));
    }

    @SmallTest
    public void testHashCode() {
        assertEquals("Equal", q.getId().hashCode(), q.hashCode());
        Question q2 = new Question();
        assertEquals("Equal", q2.hashCode(), 0);
    }

    @SmallTest
    public void testRatingSummary() {
        Question o = new Question();
        o.setUpVote(0);
        o.setDownVote(0);
        assertEquals("RatingSummary", "", o.getRatingSummary());
        o.setUpVote(1);
        assertEquals("RatingSummary", "1 like  ", o.getRatingSummary());
        o.setUpVote(2);
        assertEquals("RatingSummary", "2 likes  ", o.getRatingSummary());
        o.setDownVote(1);
        assertEquals("RatingSummary", "2 likes  1 dislike  ", o.getRatingSummary());
        o.setDownVote(2);
        assertEquals("RatingSummary", "2 likes  2 dislikes  ", o.getRatingSummary());
    }
}
