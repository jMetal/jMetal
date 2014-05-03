package jmetal.test.util.avl;

import jmetal.util.avl.AvlNode;
import jmetal.util.avl.AvlTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 08/07/13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class AvlTreeTest {
  AvlTree<Integer> avlTree_ ;
  Comparator comparator_;

  @Before
  public void setUp() throws Exception {
    comparator_ = new Comparator() {
      public int compare(Object o1, Object o2) {
        if ((int)(Integer)o1 < (int)(Integer)o2)
          return -1 ;
        else if ((int)(Integer)o1 > (int)(Integer)o2)
          return 1;
        else {
          return 0;
        }
      }
    } ;
     avlTree_ = new AvlTree(comparator_) ;
  }

  @After
  public void tearDown() throws Exception {
    avlTree_ = null ;
    comparator_ = null ;
  }

  @Test
  public void testAvlIsEmpty() throws Exception {
    assertTrue("TestAvlIsEmpty", avlTree_.AvlIsEmpty()) ;

    avlTree_.insertTop(new AvlNode(5));
    assertFalse("TestAvlIsEmpty", avlTree_.AvlIsEmpty()) ;
  }

  @Test
  public void testInsertTop() throws Exception {
    AvlNode<Integer> node = new AvlNode(4) ;
    avlTree_.insertTop(node);
    assertEquals("TestInsertTop", node, avlTree_.getTop()) ;
    String tree = " | 4" ;
    assertEquals("TestInsertTop", tree, avlTree_.toString());
  }

  @Test
  public void testCompareNodes() throws Exception{
    AvlNode<Integer> node1 = new AvlNode<Integer>(4);
    AvlNode<Integer> node2 = new AvlNode<Integer>(5);
    AvlNode<Integer> node3 = new AvlNode<Integer>(5);

    assertEquals("testCompareNodes", -1, avlTree_.compareNodes(node1, node2));
    assertEquals("testCompareNodes", 1, avlTree_.compareNodes(node3, node1));
    assertEquals("testCompareNodes", 0, avlTree_.compareNodes(node2, node3));
  }

  /*
  @Test
  public void testInsertingTheFirstElement() throws Exception {
    AvlNode<Integer> node = new AvlNode<Integer>(6) ;
    avlTree_.insertAvlNode(node);
    assertEquals("testInsertingTheFirstElement", node, avlTree_.getTop());
  }
  */

  @Test
  public void testInsertingRightAndLeftElementsJustAfterTop() throws Exception {
    AvlNode<Integer> node = new AvlNode<Integer>(6) ;
    avlTree_.insertAvlNode(node);
    AvlNode<Integer> nodeLeft = new AvlNode<Integer>(4) ;
    AvlNode<Integer> nodeRight = new AvlNode<Integer>(9) ;

    assertEquals("testInsertingSecondSmallerElement", -1, avlTree_.searchClosestNode(nodeLeft));
    assertEquals("testInsertingSecondSmallerElement", node, nodeLeft.getClosestNode());
    assertEquals("testInsertingSecondSmallerElement", +1, avlTree_.searchClosestNode(nodeRight));
    assertEquals("testInsertingSecondSmallerElement", node, nodeRight.getClosestNode());
    assertEquals("testInsertingSecondSmallerElement", 0, avlTree_.searchClosestNode(node));

    node.setLeft(nodeLeft);
    node.setRight(nodeRight);
    AvlNode<Integer> nodeRightLeft = new AvlNode<Integer>(7) ;
    avlTree_.searchClosestNode(nodeRightLeft) ;
    assertEquals("testInsertingSecondSmallerElement", -1, avlTree_.searchClosestNode(nodeRightLeft));
    assertEquals("testInsertingSecondSmallerElement", nodeRight, nodeRightLeft.getClosestNode());

    AvlNode<Integer> nodeLeftRight = new AvlNode<Integer>(5) ;
    assertEquals("testInsertingSecondSmallerElement", 1, avlTree_.searchClosestNode(nodeLeftRight));
    assertEquals("testInsertingSecondSmallerElement", nodeLeft, nodeLeftRight.getClosestNode());

    String tree = " | 6 | 4 | 9";
    assertEquals("testInsertingSecondSmallerElement", tree, avlTree_.toString());
  }

  @Test
  public void testInsertingLeftElement() throws Exception {
    AvlNode<Integer> node = new AvlNode<Integer>(6) ;
    avlTree_.insertAvlNode(node);
    AvlNode<Integer> nodeLeft = new AvlNode<Integer>(4) ;
    avlTree_.insertAvlNode(nodeLeft);

    assertEquals("testInsertingLeftElement", node, nodeLeft.getParent());
    assertEquals("testInsertingLeftElement", nodeLeft, node.getLeft());

    String tree = " | 6 | 4";
    assertEquals("testInsertingLeftElement", tree, avlTree_.toString());
  }

  @Test
  public void testSearchClosestNode() throws Exception {
    int result ;
    AvlNode<Integer> node = new AvlNode<Integer>(7) ;
    result = avlTree_.searchClosestNode(node) ;
    assertEquals("testSearchClosestNode", 0, result);
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(4) ;
    result = avlTree_.searchClosestNode(node) ;
    assertEquals("testSearchClosestNode", -1, result);
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(9) ;
    result = avlTree_.searchClosestNode(node) ;
    assertEquals("testSearchClosestNode", 1, result);
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(6) ;
    result = avlTree_.searchClosestNode(node) ;
    assertEquals("testSearchClosestNode", 1, result);
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(8) ;
    result = avlTree_.searchClosestNode(node) ;
    assertEquals("testSearchClosestNode", -1, result);
    avlTree_.insertAvlNode(node);

    String tree = " | 7 | 4 | 6 | 9 | 8";
    assertEquals("testSearchClosestNode", tree, avlTree_.toString());
  }

  @Test
  public void testInsertingRightElement() throws Exception {
    AvlNode<Integer> node = new AvlNode<Integer>(6) ;
    avlTree_.insertAvlNode(node);
    AvlNode<Integer> nodeRight = new AvlNode<Integer>(9) ;
    avlTree_.insertAvlNode(nodeRight);

    assertEquals("testInsertingRightElement", node, nodeRight.getParent());
    assertEquals("testInsertingRightElement", nodeRight, node.getRight());

    String tree = " | 6 | 9";
    assertEquals("testInsertingRightElement", tree, avlTree_.toString());
  }

  /**
   * Test adding 7 - 4 - 9 - 3 - 5
   * @throws Exception
   */
  @Test
  public void testHeightAndBalanceOfASimpleBalancedTree() throws Exception {
    AvlNode<Integer> node1, node2, node3, node4, node5 ;

    node1 = new AvlNode<Integer>(7) ;
    avlTree_.insertAvlNode(node1);
    assertEquals("testHeightOfASimpleBalancedTree", 0, node1.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 0, avlTree_.getBalance(node1));

    node2 = new AvlNode<Integer>(4) ;
    avlTree_.insertAvlNode(node2);
    assertEquals("testHeightOfASimpleBalancedTree", 0, node2.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 1, node1.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", -1, avlTree_.getBalance(node1));
    assertEquals("testHeightOfASimpleBalancedTree", 0, avlTree_.getBalance(node2));

    node3 = new AvlNode<Integer>(9) ;
    avlTree_.insertAvlNode(node3);
    assertEquals("testHeightOfASimpleBalancedTree", 0, node3.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 1, node1.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 0, avlTree_.getBalance(node1));
    assertEquals("testHeightOfASimpleBalancedTree", 0, avlTree_.getBalance(node3));

    node4 = new AvlNode<Integer>(3) ;
    avlTree_.insertAvlNode(node4);
    assertEquals("testHeightOfASimpleBalancedTree", 0, node4.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 1, node2.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 2, node1.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", -1, avlTree_.getBalance(node2));
    assertEquals("testHeightOfASimpleBalancedTree", -1, avlTree_.getBalance(node1));
    assertEquals("testHeightOfASimpleBalancedTree", 0, avlTree_.getBalance(node4));

    node5 = new AvlNode<Integer>(5) ;
    avlTree_.insertAvlNode(node5);
    assertEquals("testHeightOfASimpleBalancedTree", 0, node5.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 1, node2.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 2, node1.getHeight());
    assertEquals("testHeightOfASimpleBalancedTree", 0, avlTree_.getBalance(node2));
    assertEquals("testHeightOfASimpleBalancedTree", -1, avlTree_.getBalance(node1));
    assertEquals("testHeightOfASimpleBalancedTree", 0, avlTree_.getBalance(node5));

    String tree = " | 7 | 4 | 3 | 5 | 9";
    assertEquals("testHeightOfASimpleBalancedTree", tree, avlTree_.toString());
  }

  /**
   * Testing adding 7 - 4 - 3
   * @throws Exception
   */
  @Test
  public void testInsertingLeftLeftNodeAndRebalance() throws Exception {
    AvlNode<Integer> node1, node2, node3, node4, node5 ;

    node1 = new AvlNode<Integer>(7) ;
    avlTree_.insertAvlNode(node1);
    assertEquals("testInsertingLeftLeftNodeAndRebalance", 0, node1.getHeight());
    assertEquals("testInsertingLeftLeftNodeAndRebalance", 0, avlTree_.getBalance(node1));

    node2 = new AvlNode<Integer>(4) ;
    avlTree_.insertAvlNode(node2);
    assertEquals("testInsertingLeftLeftNodeAndRebalance", 0, node2.getHeight());
    assertEquals("testInsertingLeftLeftNodeAndRebalance", 1, node1.getHeight());
    assertEquals("testInsertingLeftLeftNodeAndRebalance", -1, avlTree_.getBalance(node1));
    assertEquals("testInsertingLeftLeftNodeAndRebalance", 0, avlTree_.getBalance(node2));

    node3 = new AvlNode<Integer>(3) ;
    avlTree_.insertAvlNode(node3);
    assertEquals("testInsertingLeftLeftNodeAndRebalance", node2, avlTree_.getTop());
    assertEquals("testInsertingLeftLeftNodeAndRebalance", node3, node2.getLeft());
    assertEquals("testInsertingLeftLeftNodeAndRebalance", node1, node2.getRight());

    assertEquals("testInsertingLeftLeftNodeAndRebalance", 1, avlTree_.getTop().getHeight());
    assertEquals("testInsertingLeftLeftNodeAndRebalance", 0, avlTree_.getTop().getLeft().getHeight());
    assertEquals("testInsertingLeftLeftNodeAndRebalance", 0, avlTree_.getTop().getRight().getHeight());
    assertEquals("testInsertingLeftLeftNodeAndRebalance", -1, avlTree_.height(node1.getLeft())) ;
    assertEquals("testInsertingLeftLeftNodeAndRebalance", -1, avlTree_.height(node1.getRight())) ;
    assertEquals("testInsertingLeftLeftNodeAndRebalance", -1, avlTree_.height(node3.getLeft())) ;
    assertEquals("testInsertingLeftLeftNodeAndRebalance", -1, avlTree_.height(node3.getRight())) ;

    String tree = " | 4 | 3 | 7";
    assertEquals("testInsertingLeftLeftNodeAndRebalance", tree, avlTree_.toString());
   }

  /**
   * Testing adding 7 - 10 - 14
   * @throws Exception
   */
  @Test
  public void testInsertingRightRightNodeAndRebalance() throws Exception {
    AvlNode<Integer> node1, node2, node3, node4, node5 ;

    node1 = new AvlNode<Integer>(7) ;
    avlTree_.insertAvlNode(node1);
    assertEquals("testInsertingRightRightNodeAndRebalance", 0, node1.getHeight());
    assertEquals("testInsertingRightRightNodeAndRebalance", 0, avlTree_.getBalance(node1));

    node2 = new AvlNode<Integer>(10) ;
    avlTree_.insertAvlNode(node2);
    assertEquals("testInsertingRightRightNodeAndRebalance", 0, node2.getHeight());
    assertEquals("testInsertingRightRightNodeAndRebalance", 1, node1.getHeight());
    assertEquals("testInsertingRightRightNodeAndRebalance", 1, avlTree_.getBalance(node1));
    assertEquals("testInsertingRightRightNodeAndRebalance", 0, avlTree_.getBalance(node2));

    node3 = new AvlNode<Integer>(14) ;
    avlTree_.insertAvlNode(node3);
    assertEquals("testInsertingRightRightNodeAndRebalance", node2, avlTree_.getTop());
    assertEquals("testInsertingRightRightNodeAndRebalance", node1, node2.getLeft());
    assertEquals("testInsertingRightRightNodeAndRebalance", node3, node2.getRight());

    assertEquals("testInsertingRightRightNodeAndRebalance", 1, avlTree_.getTop().getHeight());
    assertEquals("testInsertingRightRightNodeAndRebalance", 0, avlTree_.getTop().getLeft().getHeight());
    assertEquals("testInsertingRightRightNodeAndRebalance", 0, avlTree_.getTop().getRight().getHeight());
    assertEquals("testInsertingRightRightNodeAndRebalance", -1, avlTree_.height(node1.getLeft())) ;
    assertEquals("testInsertingRightRightNodeAndRebalance", -1, avlTree_.height(node1.getRight())) ;
    assertEquals("testInsertingRightRightNodeAndRebalance", -1, avlTree_.height(node3.getLeft())) ;
    assertEquals("testInsertingRightRightNodeAndRebalance", -1, avlTree_.height(node3.getRight())) ;

    String tree = " | 10 | 7 | 14";
    assertEquals("testInsertingRightRightNodeAndRebalance", tree, avlTree_.toString());
 }

  /**
   * Testing adding 7 - 4 - 3 - 2 - 1
   * @throws Exception
   */
  @Test
  public void testInserting7_4_3_2_1() throws Exception {
    AvlNode<Integer> node1, node2, node3, node4, node5 ;

    node1 = new AvlNode<Integer>(7) ;
    node2 = new AvlNode<Integer>(4) ;
    node3 = new AvlNode<Integer>(3) ;
    node4 = new AvlNode<Integer>(2) ;
    node5 = new AvlNode<Integer>(1) ;

    avlTree_.insertAvlNode(node1);
    avlTree_.insertAvlNode(node2);
    avlTree_.insertAvlNode(node3);
    avlTree_.insertAvlNode(node4);
    avlTree_.insertAvlNode(node5);

    assertEquals("testInserting7_4_3_2_1", node2, avlTree_.getTop());
    assertEquals("testInserting7_4_3_2_1", node4, node2.getLeft());
    assertEquals("testInserting7_4_3_2_1", node1, node2.getRight());
    assertEquals("testInserting7_4_3_2_1", node5, node4.getLeft());
    assertEquals("testInserting7_4_3_2_1", node3, node4.getRight());
    assertEquals("testInserting7_4_3_2_1", 0, node1.getHeight());
    assertEquals("testInserting7_4_3_2_1", 2, node2.getHeight());
    assertEquals("testInserting7_4_3_2_1", 1, node4.getHeight());

    String tree = " | 4 | 2 | 1 | 3 | 7";
    assertEquals("testInserting7_4_3_2_1", tree, avlTree_.toString());
  }

  /**
   * Testing adding 7 - 4 - 3 - 2 - 1
   * @throws Exception
   */
  @Test
  public void testInserting7_8_9_10_11() throws Exception {
    AvlNode<Integer> node1, node2, node3, node4, node5 ;

    node1 = new AvlNode<Integer>(7) ;
    node2 = new AvlNode<Integer>(8) ;
    node3 = new AvlNode<Integer>(9) ;
    node4 = new AvlNode<Integer>(10) ;
    node5 = new AvlNode<Integer>(11) ;

    avlTree_.insertAvlNode(node1);
    avlTree_.insertAvlNode(node2);
    avlTree_.insertAvlNode(node3);
    avlTree_.insertAvlNode(node4);
    avlTree_.insertAvlNode(node5);

    assertEquals("testInserting7_8_9_10_11", node2, avlTree_.getTop());
    assertEquals("testInserting7_8_9_10_11", node4, node2.getRight());
    assertEquals("testInserting7_8_9_10_11", node1, node2.getLeft());
    assertEquals("testInserting7_8_9_10_11", node5, node4.getRight());
    assertEquals("testInserting7_8_9_10_11", node3, node4.getLeft());
    assertEquals("testInserting7_8_9_10_11", 2, avlTree_.getTop().getHeight());
    assertEquals("testInserting7_8_9_10_11", 1, node4.getHeight());
    assertEquals("testInserting7_8_9_10_11", 0, node1.getHeight());

    String tree = " | 8 | 7 | 10 | 9 | 11";
    assertEquals("testInserting7_8_9_10_11", tree, avlTree_.toString());
  }

  /**
   * Testing adding 7 - 2 - 3
   * @throws Exception
   */
  @Test
  public void testInsertingLeftRightNodeAndRebalance() throws Exception {
    AvlNode<Integer> node1, node2, node3 ;

    node1 = new AvlNode<Integer>(7) ;
    avlTree_.insertAvlNode(node1);

    node2 = new AvlNode<Integer>(2) ;
    avlTree_.insertAvlNode(node2);

    node3 = new AvlNode<Integer>(3) ;
    avlTree_.insertAvlNode(node3);

    assertEquals("testInsertingLeftRightNodeAndRebalance", node3, avlTree_.getTop());
    assertEquals("testInsertingLeftRightNodeAndRebalance", node2, node3.getLeft());
    assertEquals("testInsertingLeftRightNodeAndRebalance", node1, node3.getRight());

    assertEquals("testInsertingLeftRightNodeAndRebalance", 1, avlTree_.getTop().getHeight());
    assertEquals("testInsertingLeftRightNodeAndRebalance", 0, avlTree_.getTop().getLeft().getHeight());
    assertEquals("testInsertingLeftRightNodeAndRebalance", 0, avlTree_.getTop().getRight().getHeight());
    assertEquals("testInsertingLeftRightNodeAndRebalance", -1, avlTree_.height(node2.getLeft())) ;
    assertEquals("testInsertingLeftRightNodeAndRebalance", -1, avlTree_.height(node2.getRight())) ;
    assertEquals("testInsertingLeftRightNodeAndRebalance", -1, avlTree_.height(node1.getLeft())) ;
    assertEquals("testInsertingLeftRightNodeAndRebalance", -1, avlTree_.height(node1.getRight())) ;

    String tree = " | 3 | 2 | 7";
    assertEquals("testInsertingLeftRightNodeAndRebalance", tree, avlTree_.toString());
  }

  /**
   * Testing adding 7 - 9 - 8
   * @throws Exception
   */
  @Test
  public void testInsertingRightLeftNodeAndRebalance() throws Exception {
    AvlNode<Integer> node1, node2, node3 ;

    node1 = new AvlNode<Integer>(7) ;
    avlTree_.insertAvlNode(node1);

    node2 = new AvlNode<Integer>(9) ;
    avlTree_.insertAvlNode(node2);

    node3 = new AvlNode<Integer>(8) ;
    avlTree_.insertAvlNode(node3);

    assertEquals("testInsertingRightLeftNodeAndRebalance", node3, avlTree_.getTop());
    assertEquals("testInsertingRightLeftNodeAndRebalance", node1, node3.getLeft());
    assertEquals("testInsertingRightLeftNodeAndRebalance", node2, node3.getRight());

    assertEquals("testInsertingRightLeftNodeAndRebalance", 1, avlTree_.getTop().getHeight());
    assertEquals("testInsertingRightLeftNodeAndRebalance", 0, avlTree_.getTop().getLeft().getHeight());
    assertEquals("testInsertingRightLeftNodeAndRebalance", 0, avlTree_.getTop().getRight().getHeight());
    assertEquals("testInsertingRightLeftNodeAndRebalance", -1, avlTree_.height(node2.getLeft())) ;
    assertEquals("testInsertingRightLeftNodeAndRebalance", -1, avlTree_.height(node2.getRight())) ;
    assertEquals("testInsertingRightLeftNodeAndRebalance", -1, avlTree_.height(node1.getLeft())) ;
    assertEquals("testInsertingRightLeftNodeAndRebalance", -1, avlTree_.height(node1.getRight())) ;

    String tree = " | 8 | 7 | 9";
    assertEquals("testInsertingRightLeftNodeAndRebalance", tree, avlTree_.toString());
  }

  @Test
  public void testSearchNode() throws Exception {
    AvlNode<Integer> node1, node2, node3, node4, node5 ;

    node1 = new AvlNode<Integer>(7) ;
    avlTree_.insertAvlNode(node1);

    node2 = new AvlNode<Integer>(9) ;
    avlTree_.insertAvlNode(node2);

    node3 = new AvlNode<Integer>(8) ;
    avlTree_.insertAvlNode(node3);

    node4 = new AvlNode<Integer>(2) ;
    avlTree_.insertAvlNode(node4);

    node5 = new AvlNode<Integer>(3) ;
    avlTree_.insertAvlNode(node5);

    assertEquals("testSearchNode", node1, avlTree_.search(7));
    assertEquals("testSearchNode", node2, avlTree_.search(9));
    assertEquals("testSearchNode", node3, avlTree_.search(8));
    assertEquals("testSearchNode", (Integer)2, avlTree_.searchNode(new AvlNode<Integer>(2)).getItem());
    assertEquals("testSearchNode", node4, avlTree_.search(2));
    assertEquals("testSearchNode", node5, avlTree_.search(3));
    assertNull("testInsertNode", avlTree_.search(14)) ;
    assertNull("testSearchNode", avlTree_.search(0)) ;

    String tree = " | 8 | 3 | 2 | 7 | 9";
    assertEquals("testSearchNode", tree, avlTree_.toString());
  }

  @Test
  public void testFindSuccessor() throws Exception {
    AvlNode<Integer> node ;

    node = new AvlNode<Integer>(20) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(8) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(22) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(4) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(12) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(24) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(10) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(14) ;
    avlTree_.insertAvlNode(node);

    node = avlTree_.search(8) ;
    assertEquals("testFindSuccessor", avlTree_.search(10) , avlTree_.findSuccessor(node));
    node = avlTree_.search(10) ;
    assertEquals("testFindSuccessor", avlTree_.search(12) , avlTree_.findSuccessor(node));
    node = avlTree_.search(14) ;
    assertEquals("testFindSuccessor", avlTree_.search(20) , avlTree_.findSuccessor(node));

    String tree = " | 20 | 8 | 4 | 12 | 10 | 14 | 22 | 24";
    assertEquals("testSearchNode", tree, avlTree_.toString());
  }

  @Test
  public void testDeletingLeafNodes() throws Exception {
    AvlNode<Integer> node1, node2, node3, node4, node5 ;

    node1 = new AvlNode<Integer>(7) ;
    avlTree_.insertAvlNode(node1);

    node2 = new AvlNode<Integer>(9) ;
    avlTree_.insertAvlNode(node2);

    node3 = new AvlNode<Integer>(2) ;
    avlTree_.insertAvlNode(node3);

    node4 = new AvlNode<Integer>(8) ;
    avlTree_.insertAvlNode(node4);

    node5 = new AvlNode<Integer>(3) ;
    avlTree_.insertAvlNode(node5);

    String tree = " | 7 | 2 | 3 | 9 | 8";
    assertEquals("testDeletingLeafNodes", tree, avlTree_.toString());

    avlTree_.delete(3); // right leaf node
    assertEquals("testDeletingLeafNodes", null, node3.getRight());
    assertEquals("testDeletingLeafNodes", 0, node3.getHeight());
    assertEquals("testDeletingLeafNodes", 2, avlTree_.getTop().getHeight());
    assertEquals("testDeletingLeafNodes", " | 7 | 2 | 9 | 8", avlTree_.toString());


    avlTree_.delete(8); // left leaf node
    assertEquals("testDeletingLeafNodes", null, node2.getLeft());
    assertEquals("testDeletingLeafNodes", 0, node2.getHeight());
    assertEquals("testDeletingLeafNodes", 1, avlTree_.getTop().getHeight());
    assertEquals("testDeletingLeafNodes", " | 7 | 2 | 9", avlTree_.toString());

    avlTree_.delete(2); // left leaf node
    assertEquals("testDeletingLeafNodes", null, node1.getLeft());
    assertEquals("testDeletingLeafNodes", 1, node1.getHeight());
    assertEquals("testDeletingLeafNodes", " | 7 | 9", avlTree_.toString());

    avlTree_.delete(9); // right leaf node
    assertEquals("testDeletingLeafNodes", null, node1.getRight());
    assertEquals("testDeletingLeafNodes", 0, node1.getHeight());
    assertEquals("testDeletingLeafNodes", " | 7", avlTree_.toString());

    avlTree_.delete(7); // left leaf node
    assertEquals("testDeletingLeafNodes", null, avlTree_.getTop());
    assertEquals("testDeletingLeafNodes", "", avlTree_.toString());
  }

  @Test
  public void testDeletingNodesWithOneLeaf() throws Exception {
    AvlNode<Integer> node1, node2, node3, node4, node5 ;

    node1 = new AvlNode<Integer>(7) ;
    avlTree_.insertAvlNode(node1);

    node2 = new AvlNode<Integer>(9) ;
    avlTree_.insertAvlNode(node2);

    node3 = new AvlNode<Integer>(2) ;
    avlTree_.insertAvlNode(node3);

    node4 = new AvlNode<Integer>(8) ;
    avlTree_.insertAvlNode(node4);

    node5 = new AvlNode<Integer>(3) ;
    avlTree_.insertAvlNode(node5);

    String tree = " | 7 | 2 | 3 | 9 | 8";
    assertEquals("testDeletingNodesWithOneLeaf", tree, avlTree_.toString());

    avlTree_.delete(2);
    assertEquals("testDeletingNodesWithOneLeaf", node3.getItem(), node1.getLeft().getItem());
    assertEquals("testDeletingNodesWithOneLeaf", null, node3.getRight());
    assertEquals("testDeletingNodesWithOneLeaf", 0, node3.getHeight());
    assertEquals("testDeletingNodesWithOneLeaf", 2, avlTree_.getTop().getHeight());
    assertEquals("testDeletingNodesWithOneLeaf", " | 7 | 3 | 9 | 8", avlTree_.toString());

    avlTree_.delete(9);
    assertEquals("testDeletingNodesWithOneLeaf", node2.getItem(), node1.getRight().getItem());
    assertEquals("testDeletingNodesWithOneLeaf", null, node2.getLeft());
    assertEquals("testDeletingNodesWithOneLeaf", 0, node2.getHeight());
    assertEquals("testDeletingNodesWithOneLeaf", 1, avlTree_.getTop().getHeight());
    assertEquals("testDeletingNodesWithOneLeaf", " | 7 | 3 | 8", avlTree_.toString());
  }

  @Test
  public void testDeletingNodesWithTwoLeaves() throws Exception {
    AvlNode<Integer> node ;

    node = new AvlNode<Integer>(20) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(8) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(22) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(4) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(12) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(24) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(10) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(14) ;
    avlTree_.insertAvlNode(node);

    String expected = " | 20 | 8 | 4 | 12 | 10 | 14 | 22 | 24";
    assertEquals("testDeletingNodesWithTwoLeaves", expected, avlTree_.toString());

    avlTree_.delete(12);
    node = avlTree_.search(8) ;
    assertEquals("testDeletingNodesWithTwoLeaves", 14, node.getRight().getItem());
    assertEquals("testDeletingNodesWithTwoLeaves", " | 20 | 8 | 4 | 14 | 10 | 22 | 24", avlTree_.toString());


    avlTree_.delete(8);
    assertEquals("testDeletingNodesWithTwoLeaves", 10, avlTree_.getTop().getLeft().getItem());
    assertEquals("testDeletingNodesWithTwoLeaves", " | 20 | 10 | 4 | 14 | 22 | 24", avlTree_.toString());
  }

  @Test
  public void testDeletingAndRebalancing() throws Exception {
    AvlNode<Integer> node ;

    node = new AvlNode<Integer>(20) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(8) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(22) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(4) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(12) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(24) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(10) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(14) ;
    avlTree_.insertAvlNode(node);

    assertEquals("testDeletingDeepLeafNode", 3, avlTree_.getTop().getHeight());

    avlTree_.delete(22);
    //node = avlTree_.search(8) ;
    assertEquals("testDeletingDeepLeafNode", 12, (int)avlTree_.getTop().getItem());
    assertEquals("testDeletingDeepLeafNode", avlTree_.search(8), avlTree_.getTop().getLeft());
    assertEquals("testDeletingDeepLeafNode", avlTree_.search(20), avlTree_.getTop().getRight());
  }

  @Test
  public void testDeletingTopNode() throws Exception {
    AvlNode<Integer> node ;

    node = new AvlNode<Integer>(20) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(8) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(22) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(4) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(12) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(24) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(10) ;
    avlTree_.insertAvlNode(node);

    node = new AvlNode<Integer>(14) ;
    avlTree_.insertAvlNode(node);

    assertEquals("testDeletingTopNode", 3, avlTree_.getTop().getHeight());

    avlTree_.delete(20);
    System.out.println(": " + avlTree_.toString());
    assertEquals("testDeletingTopNode", " | 12 | 8 | 4 | 10 | 22 | 14 | 24", avlTree_.toString());

    /*
    assertEquals("testDeletingDeepLeafNode", 12, (int)avlTree_.getTop().getItem());
    assertEquals("testDeletingDeepLeafNode", avlTree_.search(8), avlTree_.getTop().getLeft());
    assertEquals("testDeletingDeepLeafNode", avlTree_.search(20), avlTree_.getTop().getRight());
    */
  }
}
