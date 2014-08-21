package org.uma.test.util.avl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.avl.AvlNode;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 09/07/13
 * Time: 15:29
 */
public class AvlNodeTest {
    private AvlNode<Integer> node;

    @Before
    public void setUp() throws Exception {
        node = new AvlNode<Integer>(5);
    }

    @After
    public void tearDown() throws Exception {
        node = null;
    }

    @Test
    public void testHasLeft() {
        assertFalse("testHasLeft", node.hasLeft());
        AvlNode<Integer> node2 = new AvlNode<Integer>(6);
        node.setLeft(node2);
        assertTrue("testHasLeft", node.hasLeft());
    }

    @Test
    public void testHasRight() {
        assertFalse("testHasRight", node.hasRight());
        AvlNode<Integer> node2 = new AvlNode<Integer>(6);
        node.setRight(node2);
        assertTrue("testHasRight", node.hasRight());
    }

    @Test
    public void shouldSetHeight() {
        int expectedHeight = 1000213;
        node.setHeight(expectedHeight);
        assertEquals("Height is different from expected.", expectedHeight, node.getHeight());
    }

    @Test
    public void should() {

    }

}
