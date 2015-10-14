package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import hk.ust.cse.hunkim.questionroom.question.Question;


/**
 * Created by hunkim on 7/15/15.
 */

public class QuestionTest extends TestCase {
    Question q;


    protected void setUp() throws Exception {
        super.setUp();

        q = new Question("Hello?", "This is very nice.");
    }

    @SmallTest
    public void testTitle() {
        assertEquals("Head", "Hello?", q.getTitle());
    }

    @SmallTest
    public void testMessage() {
        assertEquals("Desc", " This is very nice", q.getMessage());
    }

    @SmallTest
    public void testLike() {
        assertEquals("Echo", 0, q.getLike());
    }

    @SmallTest
    public void testLDislike() {
        assertEquals("Echo", 0, q.getDislike());
    }
}
