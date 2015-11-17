package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.datamodel.Question;
import hk.ust.cse.hunkim.questionroom.datamodel.Room;

/**
 * JUnit Test for Room
 * Created by Leung Pui Kuen on 26/10/2015.
 */
public class RoomTest extends TestCase {
    private Room r;
    private Date now;

    protected void setUp() throws Exception {
        super.setUp();

        now = new Date();

        r = new Room();
        r.setId("Id");
        r.setName("Name");
        r.setAdminKey("Key");
        r.setQuestions(null);
        r.setCreatedAt(now);
        r.setUpdatedAt(now);
    }


    @SmallTest
    public void testId() {
        assertEquals("Id", "Id", r.getId());
    }

    @SmallTest
    public void testName() {
        assertEquals("Name", "Name", r.getName());
    }

    @SmallTest
    public void testAdminKey() {
        assertEquals("AdminKey", "Key", r.getAdminKey());
    }

    @SmallTest
    public void testQuestions() {
        assertEquals("Questions", null, r.getQuestions());
    }

    @SmallTest
    public void testCreatedAt() {
        assertEquals("CreatedAt", now, r.getCreatedAt());
    }

    @SmallTest
    public void testUpdatedAt() {
        assertEquals("UpdatedAt", now, r.getUpdatedAt());
    }

    @SmallTest
    public void testEqual() {
        assertEquals("Equal", true, r.equals(r));

        assertEquals("Equal", false, r.equals(null));

        assertEquals("Equal", false, r.equals(0));

        Room o = new Room();
        r.setId(null);
        o.setId(null);
        assertEquals("Equal", true, r.equals(o));
        r.setId(null);
        o.setId("0");
        assertEquals("Equal", false, r.equals(o));
        r.setId("0");
        o.setId(null);
        assertEquals("Equal", false, r.equals(o));
        r.setId("Id");
        o.setId("Id");
        assertEquals("Equal", true, r.equals(o));
        r.setId("Id");
        o.setId("id");
        assertEquals("Equal", false, r.equals(o));
    }

    @SmallTest
    public void testHashCode() {
        assertEquals("HashCode", r.getId().hashCode(), r.hashCode());
        assertEquals("HashCode", 0, new Room().hashCode());
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
