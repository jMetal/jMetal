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

import static org.junit.Assert.assertEquals;

/**
 * Created by antonio on 17/03/14.
 */
public class AdaptiveRandomNeigborhoodTest {
  //private SolutionSet solutionSet_ ;
  //private ArrayList<ArrayList<Integer>> list_ ;
  private int numberOfRandomNeighbours_ ;

  AdaptiveRandomNeighborhood adaptiveRandomNeighborhood_ ;

  @Before
  public void setUp()  {
    numberOfRandomNeighbours_ = 3 ;
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(new SolutionSet(), numberOfRandomNeighbours_) ;
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testEmptySolutionSet() {
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood_.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 0, list.size()) ;
    //assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 2, adaptiveRandomNeighborhood_.getNumberOfRandomNeighbours()) ;
  }

  @Test
  public void testSolutionSetWithOneElement() {
    SolutionSet solutionSet = new SolutionSet(1) ;
    Solution solution = new Solution(1) ;
    solution.setObjective(0, 1.0);
    solutionSet.add(solution) ;
    System.out.println("Solset sie: " + solutionSet.size()) ;
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours_) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood_.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 1, list.size()) ;
  }

  @Test
  public void testSolutionSetWithThreeElements() {
    SolutionSet solutionSet = new SolutionSet(3) ;
    Solution solution = new Solution(1) ;
    solution.setObjective(0, 1.0);
    solution = new Solution(1) ;
    solution.setObjective(0, 2.0);
    solutionSet.add(solution) ;
    solution = new Solution(1) ;
    solution.setObjective(0, 3.0);
    solutionSet.add(solution) ;
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours_) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood_.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 3, list.size()) ;
  }
}
