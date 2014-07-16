//  RandomSelectionTest.java
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

package org.uma.test.operator.selection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.selection.RandomSelection;
import org.uma.jmetal.problem.ZDT.ZDT1;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by Antonio J. Nebro on 27/06/14.
 */
public class RandomSelectionTest {
  static final int POPULATION_SIZE = 20 ;
  RandomSelection selection ;
  SolutionSet solutionSet ;
  Problem problem ;

  @Before
  public void startup() throws ClassNotFoundException {
    problem = new ZDT1("Real") ;
    selection = new RandomSelection.Builder()
            .build() ;

    solutionSet = new SolutionSet(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      solutionSet.add(new Solution(problem)) ;
    }
  }

  @Test
  public void executeWithCorrectParametersTest() {
    Solution[] solution ;
    solution = (Solution[])selection.execute(solutionSet) ;
    assertNotEquals(solution[0], solution[1]);
  }

  @Test
  public void executeWithCorrectPopulationSizeTwoTest() throws ClassNotFoundException {
    Solution[] solution ;
    solutionSet = new SolutionSet(2) ;
    solutionSet.add(new Solution(problem)) ;
    solutionSet.add(new Solution(problem)) ;
    solution = (Solution[])selection.execute(solutionSet) ;
    assertNotEquals(solution[0], solution[1]);
  }

  @Test (expected = JMetalException.class)
  public void executeWithSolutionSetSizeZeroTest() {
    solutionSet = new SolutionSet(0) ;
    selection.execute(solutionSet) ;
  }

  @Test (expected = JMetalException.class)
  public void executeWithSolutionSetSizeOneTest() throws ClassNotFoundException {
    solutionSet = new SolutionSet(1) ;
    solutionSet.add(new Solution(problem)) ;
    selection.execute(solutionSet) ;
  }

  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest() throws ClassNotFoundException {
    Solution[] parent = {new Solution(problem), new Solution(problem)};

    selection.execute(parent) ;
  }

  @Test (expected = JMetalException.class)
  public void nullParameterTest() {
    selection.execute(null) ;
  }

  @After
  public void tearDown() {
    selection = null ;
    solutionSet = null ;
  }
}
