//  DifferentialEvolutionSelectionTest.java
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
import org.uma.jmetal.operator.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Antonio J. Nebro on 07/07/14.
 */
public class DifferentialEvolutionSelectionTest {
  static final int POPULATION_SIZE = 20 ;
  DifferentialEvolutionSelection selection ;
  SolutionSet solutionSet ;
  Problem problem ;

  @Before
  public void startup() throws ClassNotFoundException {
    problem = new Kursawe("Real", 5) ;
    selection = new DifferentialEvolutionSelection.Builder()
            .build() ;

    solutionSet = new SolutionSet(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      solutionSet.add(new Solution(problem)) ;
    }
  }

  @Test
  public void executeWithCorrectParametersTest() {
    Solution[] selected = (Solution[])selection.execute(new Object[]{solutionSet, 1});
    assertEquals(3, selected.length);
  }

  @Test
  public void theSelectedIndividualsMustBeDifferentTest() {
    Solution[] selected = (Solution[])selection.execute(new Object[]{solutionSet, 1});
    assertNotEquals(selected[0], selected[1]);
    assertNotEquals(selected[0], selected[2]);
    assertNotEquals(selected[1], selected[2]);
  }

  @Test
  public void theSelectedIndividualsMustBeDifferentWithPopulationSizeFourTest()
    throws ClassNotFoundException {
    solutionSet = new SolutionSet(4) ;
    solutionSet.add(new Solution(problem)) ;
    solutionSet.add(new Solution(problem)) ;
    solutionSet.add(new Solution(problem)) ;
    solutionSet.add(new Solution(problem)) ;

    Solution[] selected = (Solution[])selection.execute(new Object[]{solutionSet, 1});
    assertNotEquals(selected[0], selected[1]);
    assertNotEquals(selected[0], selected[2]);
    assertNotEquals(selected[1], selected[2]);
  }

  @Test (expected = JMetalException.class)
  public void executeWithPopulationSizeZeroTest() {
    solutionSet = new SolutionSet(0) ;
    selection.execute(solutionSet) ;
  }

  @Test (expected = JMetalException.class)
  public void executeWithSolutionSetSizeTwoTest() throws ClassNotFoundException {
    solutionSet = new SolutionSet(2) ;
    solutionSet.add(new Solution(problem)) ;
    solutionSet.add(new Solution(problem)) ;
    selection.execute(solutionSet);
  }

  @Test (expected = JMetalException.class)
  public void executeWithSolutionSetSizeThreeTest() {
    solutionSet = new SolutionSet(3) ;
    solutionSet.add(new Solution()) ;
    solutionSet.add(new Solution()) ;
    solutionSet.add(new Solution()) ;
    selection.execute(solutionSet);
  }

  @Test (expected = JMetalException.class)
  public void executeWithNegativeIndex() {
    selection.execute(new Object[]{solutionSet, -5}) ;
  }

  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest() {
    selection.execute(new Integer(4)) ;
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
