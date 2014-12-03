//  BinaryTournamentTest.java
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

package org.uma.jmetal.operator.selection;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Antonio J. Nebro on 15/06/14.
 */

@RunWith(MockitoJUnitRunner.class)
public class BinaryTournamentSelectionTest {
  static final int POPULATION_SIZE = 20 ;

  private BinaryTournamentSelection selection ;
  @Mock private Problem problem ;
  private List<Solution> population ;

  @Before
  public void startup() throws ClassNotFoundException {
    selection = new BinaryTournamentSelection() ;

    //population = new ArrayList<>(POPULATION_SIZE) ;
    //for (int i = 0 ; i < POPULATION_SIZE; i++) {
    //  population.add(problem.createSolution()) ;
    //}
  }

  @Test (expected = JMetalException.class)
  public void listOfSolutionsIsNull() {
    population = null ;
    selection.execute(population) ;
  }

  @Test (expected = JMetalException.class)
  public void listOfSolutionsIsEmpty() {
    population = new ArrayList<>(0) ;
    selection.execute(population) ;
  }

  @Test
  public void executeWithCorrectParametersTest() {
    Solution solution = mock(Solution.class) ;
    Mockito.when(problem.createSolution()).thenReturn(solution) ;

    population = new ArrayList<>(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      population.add(problem.createSolution());
    }
    assertNotNull(selection.execute(population));
    verify(problem, times(POPULATION_SIZE)).createSolution();
  }

  @Test
  public void executeWithPopulationSizeOne() {
    Solution solution = mock(Solution.class) ;

    population = new ArrayList<>(1) ;
    population.add(solution) ;
    assertEquals(solution, selection.execute(population));
  }

  @Test
  public void executeWithPopulationSizeTwo() {
    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    population = Arrays.asList(solution1, solution2) ;
    assertEquals(2, population.size());
  }

  @Test
  public void theFirstSelectionSolutionIsDominatedByTheSecondOne() {
    Comparator comparator = mock(Comparator.class) ;
    //Solution solution1 = mock(Solution.class) ;
    //Solution solution2 = mock(Solution.class) ;
    selection = new BinaryTournamentSelection(comparator) ;

    Mockito.when(comparator.compare(any(Solution.class), any(Solution.class))).thenReturn(-1) ;

    population = new ArrayList<>(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      population.add(problem.createSolution()) ;
    }
    selection.execute(population) ;
    verify(comparator, times(1)).compare(anyObject(), anyObject());
  }



  @After
  public void tearDown() {
    selection = null ;
    population = null ;
    problem = null ;
  }

}
