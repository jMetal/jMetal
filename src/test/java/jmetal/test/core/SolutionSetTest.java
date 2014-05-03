package jmetal.test.core;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 10/06/13
 * Time: 07:41
 */
public class SolutionSetTest {
  int maxSize_ = 10 ;
  SolutionSet solutionSet_ ;

  @Before
  public void setUp() throws Exception {
    solutionSet_ = new SolutionSet(maxSize_) ;

  }

  @After
  public void tearDown() throws Exception {
    solutionSet_ = null ;
  }

  /**
   * Test: Check that adding a new solution to an empty solution set leads this one to have 1 element
   * @throws Exception
   */
  @Test
  public void testAddOneElementToAnEmptySolutionSet() throws Exception {
    boolean result ;
    result = solutionSet_.add(new Solution()) ;
    assertEquals("SolutionSetTest", 1, solutionSet_.size()) ;
    assertTrue("SolutionSetTest", result);
  }

  /**
   * Test: Adding an element to a full solution set must return a false value because it is not added
   */
  @Test
  public void testAddOneElementToAFullSolutionSet() {
    for (int i = 0 ; i < maxSize_ ; i++)
      solutionSet_.add(new Solution()) ;
    boolean result ;
    result = solutionSet_.add(new Solution()) ;
    assertFalse("SolutionSetTest", result);
  }

  /**
   * Test: Getting an element out of founds must raise an exception
   * @throws Exception
   */
  @Test  (expected = IndexOutOfBoundsException.class)
  public void testGetElementOutOfBounds() throws Exception {
    for (int i = 0 ; i < 5 ; i++)
      solutionSet_.add(new Solution()) ;

    Solution solution = solutionSet_.get(6) ;
  }

  @Test
  public void testGetMaxSize() {
    assertEquals("SolutionSetTest", 10, solutionSet_.getMaxSize());
  }
}
