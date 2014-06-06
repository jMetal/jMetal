//  AdaptiveRandomNeigborhoodTest.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package test.util ;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.problems.singleObjective.Sphere;
import jmetal.util.AdaptiveRandomNeighborhood;
import jmetal.util.JMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testEmptySolutionSet() {
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(new SolutionSet(), numberOfRandomNeighbours_) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood_.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 0, list.size()) ;
  }

  @Test
  public void testNumberOfRandomNeighbors() {
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(new SolutionSet(), numberOfRandomNeighbours_) ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testNumberOfNeighbors", 3, adaptiveRandomNeighborhood_.getNumberOfRandomNeighbours()) ;
  }

  @Test
  public void testSolutionSetWithOneElement() throws JMException {
    SolutionSet solutionSet = new SolutionSet(1) ;
    Solution solution = new Solution(1) ;
    solution.setObjective(0, 1.0);
    solutionSet.add(solution) ;
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours_) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood_.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 1, list.size()) ;
  }

  @Test
  public void testSolutionSetWithThreeElements() throws JMException {
    SolutionSet solutionSet = new SolutionSet(3) ;
    Solution solution = new Solution(1) ;
    solution.setObjective(0, 1.0);
    solutionSet.add(solution) ;
    solution = new Solution(1) ;
    solution.setObjective(0, 2.0);
    solutionSet.add(solution) ;
    solution = new Solution(1) ;
    solution.setObjective(0, 3.0);
    solutionSet.add(solution) ;
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours_) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood_.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testSolutionSetWithThreeElements", 3, list.size()) ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testSolutionSetWithThreeElements", 0, (int)(list.get(0).get(0)));
    assertEquals("AdaptiveRandomNeigborhoodTest.testSolutionSetWithThreeElements", 1, (int)(list.get(1).get(0)));
    assertEquals("AdaptiveRandomNeigborhoodTest.testSolutionSetWithThreeElements", 2, (int)(list.get(2).get(0)));
  }

  @Test
  public void testRecomputeNeighboursWithSolutionSetWithThreeElements() throws JMException {
    SolutionSet solutionSet = new SolutionSet(3) ;
    Solution solution = new Solution(1) ;
    solution.setObjective(0, 1.0);
    solutionSet.add(solution) ;
    solution = new Solution(1) ;
    solution.setObjective(0, 2.0);
    solutionSet.add(solution) ;
    solution = new Solution(1) ;
    solution.setObjective(0, 3.0);
    solutionSet.add(solution) ;
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours_) ;
    adaptiveRandomNeighborhood_.recompute();
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood_.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testRecomputeNeighboursWithSolutionSetWithThreeElements", 3, list.size()) ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testRecomputeNeighboursWithSolutionSetWithThreeElements", 0, (int)(list.get(0).get(0)));
    assertEquals("AdaptiveRandomNeigborhoodTest.testRecomputeNeighboursWithSolutionSetWithThreeElements", 1, (int)(list.get(1).get(0)));
    assertEquals("AdaptiveRandomNeigborhoodTest.testRecomputeNeighboursWithSolutionSetWithThreeElements", 2, (int)(list.get(2).get(0)));
  }

  @Test(expected=JMException.class)
  public void testCatchExceptionWhenRequestingAnNonExistingNeighbor() throws JMException{
    SolutionSet solutionSet = new SolutionSet(3) ;
    Solution solution = new Solution(1) ;
    solution.setObjective(0, 1.0);
    solutionSet.add(solution) ;
    solution = new Solution(1) ;
    solution.setObjective(0, 2.0);
    solutionSet.add(solution) ;
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours_) ;
    adaptiveRandomNeighborhood_.getNeighbors(6) ;
  }

  @Test
  public void testUsingPopulationOf100Individuals() throws ClassNotFoundException, JMException {
    int solutionSetSize = 100 ;
    Problem problem = new Sphere("Real", 10) ;
    SolutionSet solutionSet = new SolutionSet(solutionSetSize) ;

    for (int i = 0; i < 100; i++) {
      Solution solution = new Solution(problem) ;
      problem.evaluate(solution);
      solutionSet.add(solution) ;
    }
    adaptiveRandomNeighborhood_ = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours_) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood_.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testUsingPopulationOf100Individuals", solutionSetSize, list.size()) ;
    for (int i = 0 ; i < solutionSetSize; i++)
      assertEquals("AdaptiveRandomNeigborhoodTest.testUsingPopulationOf100Individuals", i, (int)(list.get(i).get(0)));
  }
}
