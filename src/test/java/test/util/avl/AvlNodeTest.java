package test.util.avl;

import jmetal.util.avl.AvlNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 09/07/13
 * Time: 15:29
 */
public class AvlNodeTest {
    private AvlNode<Integer> node_;

    @Before
    public void setUp() throws Exception {
        node_ = new AvlNode<Integer>(5);
    }

    @After
    public void tearDown() throws Exception {
        node_ = null;
    }

    @Test
    public void testHasLeft() {
        assertFalse("testHasLeft", node_.hasLeft());
        AvlNode<Integer> node2 = new AvlNode<Integer>(6);
        node_.setLeft(node2);
        assertTrue("testHasLeft", node_.hasLeft());
    }

    @Test
    public void testHasRight() {
        assertFalse("testHasRight", node_.hasRight());
        AvlNode<Integer> node2 = new AvlNode<Integer>(6);
        node_.setRight(node2);
        assertTrue("testHasRight", node_.hasRight());
    }

    @Test
    public void shouldSetHeight() {
        int expectedHeight = 1000213;
        node_.setHeight(expectedHeight);
        assertEquals("Height is different from expected.", expectedHeight, node_.getHeight());
    }

    @Test
    public void should() {

    }

}
