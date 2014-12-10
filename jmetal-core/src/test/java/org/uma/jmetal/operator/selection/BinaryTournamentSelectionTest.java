//  BinaryTournamentSelectionTest.java
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

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
  public void theFirstSelectedSolutionIsDominatedByTheSecondOne() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    Comparator<Solution> comparator = mock(Comparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Mockito.when(solution1.getNumberOfObjectives()).thenReturn(2) ;
    Mockito.when(solution1.getObjective(0)).thenReturn(2.0) ;
    Mockito.when(solution1.getObjective(1)).thenReturn(3.0) ;

    Solution solution2 = mock(Solution.class) ;
    Mockito.when(solution2.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution2.getObjective(0)).thenReturn(1.0) ;
    Mockito.when(solution2.getObjective(1)).thenReturn(2.0) ;

    Mockito.when(comparator.compare(solution1, solution2)).thenReturn(1) ;
    Mockito.when(randomGenerator.nextInt(0, 1)).thenReturn(0, 1) ;

    List<Solution> population = Arrays.asList(solution1, solution2);

    selection = new BinaryTournamentSelection(comparator) ;
    ReflectionTestUtils.setField(selection, "randomGenerator", randomGenerator);

    Solution result = selection.execute(population) ;

    assertEquals(solution2, result) ;
    assertNotEquals(solution1, result);
    verify(comparator).compare(any(Solution.class), any(Solution.class));
    verify(randomGenerator, times(2)).nextInt(0, 1);
  }

  @Test
  public void theSecondSelectedSolutionIsDominatedByTheFirstOne() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    Comparator<Solution> comparator = mock(Comparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Mockito.when(solution1.getNumberOfObjectives()).thenReturn(2) ;
    Mockito.when(solution1.getObjective(0)).thenReturn(1.0) ;
    Mockito.when(solution1.getObjective(1)).thenReturn(2.0) ;

    Solution solution2 = mock(Solution.class) ;
    Mockito.when(solution2.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution2.getObjective(0)).thenReturn(2.0) ;
    Mockito.when(solution2.getObjective(1)).thenReturn(3.0) ;

    Mockito.when(comparator.compare(solution1, solution2)).thenReturn(-1) ;
    Mockito.when(randomGenerator.nextInt(0, 1)).thenReturn(0, 1) ;

    List<Solution> population = Arrays.asList(solution1, solution2);

    selection = new BinaryTournamentSelection(comparator) ;
    ReflectionTestUtils.setField(selection, "randomGenerator", randomGenerator);

    Solution result = selection.execute(population) ;

    assertEquals(solution1, result) ;
    assertNotEquals(solution2, result);
    verify(comparator).compare(any(Solution.class), any(Solution.class));
    verify(randomGenerator, times(2)).nextInt(0, 1);
  }


  @Test
  public void theBothSelectedSolutionsAreNonDominated() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;
    Comparator<Solution> comparator = mock(Comparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Mockito.when(solution1.getNumberOfObjectives()).thenReturn(2) ;
    Mockito.when(solution1.getObjective(0)).thenReturn(1.0) ;
    Mockito.when(solution1.getObjective(1)).thenReturn(2.0) ;

    Solution solution2 = mock(Solution.class) ;
    Mockito.when(solution2.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution2.getObjective(0)).thenReturn(2.0) ;
    Mockito.when(solution2.getObjective(1)).thenReturn(1.0) ;

    Mockito.when(comparator.compare(solution1, solution2)).thenReturn(0) ;
    Mockito.when(randomGenerator.nextInt(0, 1)).thenReturn(0, 1) ;

    List<Solution> population = Arrays.<Solution>asList(solution1, solution2);

    selection = new BinaryTournamentSelection(comparator) ;
    ReflectionTestUtils.setField(selection, "randomGenerator", randomGenerator);

    Solution result = selection.execute(population) ;

    assertThat(result, Matchers.either(Matchers.is(solution1)).or(Matchers.is(solution2))) ;
    verify(comparator).compare(any(Solution.class), any(Solution.class));
    verify(randomGenerator, times(2)).nextInt(0, 1);
  }

  @Test
  public void theSameSolutionIsSelectedTwiceTheFirstTime() {
    JMetalRandom randomGenerator = mock(JMetalRandom.class) ;

    Solution solution1 = mock(Solution.class) ;
    Mockito.when(solution1.getNumberOfObjectives()).thenReturn(2) ;
    Mockito.when(solution1.getObjective(0)).thenReturn(2.0) ;
    Mockito.when(solution1.getObjective(1)).thenReturn(3.0) ;

    Solution solution2 = mock(Solution.class) ;
    Mockito.when(solution2.getNumberOfObjectives()).thenReturn(1) ;
    Mockito.when(solution2.getObjective(0)).thenReturn(1.0) ;
    Mockito.when(solution2.getObjective(1)).thenReturn(2.0) ;

    Mockito.when(randomGenerator.nextInt(0, 1)).thenReturn(0, 0, 1) ;

    List<Solution> population = Arrays.asList(solution1, solution2);

    selection = new BinaryTournamentSelection() ;
    ReflectionTestUtils.setField(selection, "randomGenerator", randomGenerator);

    selection.execute(population) ;

    verify(randomGenerator, times(3)).nextInt(0, 1);
  }

  @After
  public void tearDown() {
    selection = null ;
    population = null ;
    problem = null ;
  }

}
