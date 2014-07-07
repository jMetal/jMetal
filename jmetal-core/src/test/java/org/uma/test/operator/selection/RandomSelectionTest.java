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
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.selection.RandomSelection;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.SolutionComparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Antonio J. Nebro on 27/06/14.
 */
public class RandomSelectionTest {
  static final int POPULATION_SIZE = 20 ;
  RandomSelection selection ;
  SolutionSet solutionSet ;

  @Before
  public void startup() {
    selection = new RandomSelection.Builder()
            .build() ;

    solutionSet = new SolutionSet(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      solutionSet.add(new Solution()) ;
    }
  }

  @Test
  public void executeWithCorrectParametersTest() {
    Solution[] solution ;
    solution = (Solution[])selection.execute(solutionSet) ;
    assertNotEquals(solution[0], solution[1]);
  }

  @Test
  public void executeWithCorrectPopulationSizeTwoTest() {
    Solution[] solution ;
    solutionSet = new SolutionSet(2) ;
    solutionSet.add(new Solution()) ;
    solutionSet.add(new Solution()) ;
    solution = (Solution[])selection.execute(solutionSet) ;
    assertNotEquals(solution[0], solution[1]);
  }

  @Test (expected = JMetalException.class)
  public void executeWithSolutionSetSizeZeroTest() {
    solutionSet = new SolutionSet(0) ;
    selection.execute(solutionSet) ;
  }

  @Test (expected = JMetalException.class)
  public void executeWithSolutionSetSizeOneTest() {
    solutionSet = new SolutionSet(1) ;
    solutionSet.add(new Solution()) ;
    selection.execute(solutionSet) ;
  }

  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest() {
    Solution[] parent = {new Solution(), new Solution()};

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
