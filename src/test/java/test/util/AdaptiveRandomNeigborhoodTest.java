package test.util ;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.AdaptiveRandomNeighborhood;
import jmetal.util.avl.AvlTree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;

import static junit.framework.Assert.assertEquals;

/**
 * Created by antonio on 17/03/14.
 */
public class AdaptiveRandomNeigborhoodTest {
  private SolutionSet solutionSet_ ;
  private ArrayList<ArrayList<Integer>> list_ ;
  private int numberOfRandomNeighbours_ ;

  AdaptiveRandomNeighborhood adaptiveRandomNeighborhood_ ;

  @Before
  public void setUp()  {
    numberOfRandomNeighbours_ = 2 ;
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(new SolutionSet(), numberOfRandomNeighbours_) ;
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testEmptySolutionSet() {
    assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 0, solutionSet_.size()) ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 0, list_.size()) ;
  }

}
