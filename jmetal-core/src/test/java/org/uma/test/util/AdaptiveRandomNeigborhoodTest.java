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

package org.uma.test.util ;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.core.SolutionSet;
import org.uma.jmetal45.problem.singleobjective.Sphere;
import org.uma.jmetal45.util.AdaptiveRandomNeighborhood;
import org.uma.jmetal45.util.JMetalException;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 17/03/14.
 */
public class AdaptiveRandomNeigborhoodTest {
  private int numberOfRandomNeighbours;

  AdaptiveRandomNeighborhood adaptiveRandomNeighborhood;

  @Before
  public void setUp()  {
    numberOfRandomNeighbours = 3 ;
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test (expected = JMetalException.class)
  public void nullSolutionSetTest() {
    adaptiveRandomNeighborhood = new AdaptiveRandomNeighborhood(null, numberOfRandomNeighbours) ;
  }

  @Test
  public void emptySolutionSetTest() {
    adaptiveRandomNeighborhood = new AdaptiveRandomNeighborhood(new SolutionSet(), numberOfRandomNeighbours) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood.getNeighborhood() ;
    assertEquals(0, list.size()) ;
  }

  @Test
  public void numberOfRandomNeighborsTest() {
    adaptiveRandomNeighborhood = new AdaptiveRandomNeighborhood(new SolutionSet(), numberOfRandomNeighbours) ;
    assertEquals(3, adaptiveRandomNeighborhood.getNumberOfRandomNeighbours()) ;
  }

  @Test
  public void solutionSetWithOneElementTest() throws JMetalException {
    SolutionSet solutionSet = new SolutionSet(1) ;
    Solution solution = new Solution(1) ;
    solution.setObjective(0, 1.0);
    solutionSet.add(solution) ;
    adaptiveRandomNeighborhood = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood.getNeighborhood() ;
    assertEquals("AdaptiveRandomNeigborhoodTest.testEmptySolutionSet", 1, list.size()) ;
  }

  @Test
  public void solutionSetWithThreeElementsTest() throws JMetalException {
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
    adaptiveRandomNeighborhood = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood.getNeighborhood() ;
    assertEquals(3, list.size()) ;
    assertEquals(0, (int)(list.get(0).get(0)));
    assertEquals(1, (int)(list.get(1).get(0)));
    assertEquals(2, (int)(list.get(2).get(0)));
  }

  @Test
  public void reecomputeNeighboursWithSolutionSetWithThreeElementsTest() throws JMetalException {
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
    adaptiveRandomNeighborhood = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours) ;
    adaptiveRandomNeighborhood.recompute();
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood.getNeighborhood() ;
    assertEquals(3, list.size()) ;
    assertEquals(0, (int)(list.get(0).get(0)));
    assertEquals(1, (int)(list.get(1).get(0)));
    assertEquals(2, (int)(list.get(2).get(0)));
  }

  @Test(expected=JMetalException.class)
  public void catchExceptionWhenRequestingAnNonExistingNeighborTest() throws JMetalException {
    SolutionSet solutionSet = new SolutionSet(3) ;
    Solution solution = new Solution(1) ;
    solution.setObjective(0, 1.0);
    solutionSet.add(solution) ;
    solution = new Solution(1) ;
    solution.setObjective(0, 2.0);
    solutionSet.add(solution) ;
    adaptiveRandomNeighborhood = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours) ;
    adaptiveRandomNeighborhood.getNeighbors(6) ;
  }

  @Test
  public void usingPopulationOf100IndividualsTest() throws ClassNotFoundException, JMetalException {
    int solutionSetSize = 100 ;
    Problem problem = new Sphere("Real", 10) ;
    SolutionSet solutionSet = new SolutionSet(solutionSetSize) ;

    for (int i = 0; i < 100; i++) {
      Solution solution = new Solution(problem) ;
      problem.evaluate(solution);
      solutionSet.add(solution) ;
    }
    adaptiveRandomNeighborhood = new AdaptiveRandomNeighborhood(solutionSet, numberOfRandomNeighbours) ;
    ArrayList<ArrayList<Integer>> list = adaptiveRandomNeighborhood.getNeighborhood() ;
    assertEquals(solutionSetSize, list.size()) ;
    for (int i = 0 ; i < solutionSetSize; i++)
      assertEquals(i, (int)(list.get(i).get(0)));
  }
}
