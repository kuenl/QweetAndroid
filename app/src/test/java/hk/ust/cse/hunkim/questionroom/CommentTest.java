package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.util.Date;

import hk.ust.cse.hunkim.questionroom.datamodel.Comment;

/**
 * Created by Administrator on 13/11/15.
 */
public class CommentTest extends TestCase {
    private Comment r;
    private Date now;

    protected void setUp() throws Exception {
        super.setUp();

        now = new Date();

        r = new Comment();
        r.setId("id");
        r.setMessage("msg");
        r.setCreatedAt(now);
        r.setUpdatedAt(now);
    }

    @SmallTest
    public void testHashCode() {
        assertEquals("HashCode", r.getId().hashCode(), r.hashCode());
        assertEquals("HashCode", 0, new Comment().hashCode());
    }

    @SmallTest
    public void testEqual() {
        assertEquals("Equal", true, r.equals(r));

        assertEquals("Equal", false, r.equals(null));

        assertEquals("Equal", false, r.equals(0));

        Comment o = new Comment();
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
}
