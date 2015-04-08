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

package org.uma.jmetal.operator.impl.selection;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class BinaryTournamentSelectionTest {
  private static final int POPULATION_SIZE = 20 ;

  private BinaryTournamentSelection selection ;

  @Mock private Problem problem ;
  private List<Solution> population ;

  @Before
  public void startup() {
    selection = new BinaryTournamentSelection() ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    population = null ;
    selection.execute(population) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    population = new ArrayList<>(0) ;
    selection.execute(population) ;
  }

  @Test
  public void shouldExecuteReturnAValidSolutionIsWithCorrectParameters() {
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
  public void shouldExecuteReturnTheSameSolutionIfTheListContainsOneSolution() {
    Solution solution = mock(Solution.class) ;

    population = new ArrayList<>(1) ;
    population.add(solution) ;
    assertSame(solution, selection.execute(population));
  }

  @Test
  public void shouldExecuteReturnTwoSolutionsIfTheListContainsTwoSolutions() {
    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    population = Arrays.asList(solution1, solution2) ;
    assertEquals(2, population.size());
  }

  @Test
  public void shouldExecuteReturnTheFirstSelectedSolutionIsIsDominatedByTheSecondOne() {
    Comparator<Solution> comparator = mock(Comparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Mockito.when(solution1.getNumberOfObjectives()).thenReturn(2) ;
    Mockito.when(solution1.getObjective(0)).thenReturn(2.0) ;
    Mockito.when(solution1.getObjective(1)).thenReturn(3.0) ;

    Solution solution2 = mock(Solution.class) ;
    Mockito.when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    Mockito.when(solution2.getObjective(0)).thenReturn(1.0) ;
    Mockito.when(solution2.getObjective(1)).thenReturn(2.0) ;

    Mockito.when(comparator.compare(solution1, solution2)).thenReturn(1) ;
    Mockito.when(comparator.compare(solution2, solution1)).thenReturn(-1) ;

    List<Solution> population = Arrays.asList(solution1, solution2);

    selection = new BinaryTournamentSelection(comparator) ;

    Solution result = selection.execute(population) ;

    assertEquals(solution2, result) ;
    assertNotEquals(solution1, result);
    verify(comparator).compare(any(Solution.class), any(Solution.class));
  }

  @Test
  public void shouldExecuteReturnTheSecondSelectedSolutionIsIsDominatedByTheFirstOne() {
    Comparator<Solution> comparator = mock(Comparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Mockito.when(solution1.getNumberOfObjectives()).thenReturn(2) ;
    Mockito.when(solution1.getObjective(0)).thenReturn(1.0) ;
    Mockito.when(solution1.getObjective(1)).thenReturn(2.0) ;

    Solution solution2 = mock(Solution.class) ;
    Mockito.when(solution2.getNumberOfObjectives()).thenReturn(2) ;
    Mockito.when(solution2.getObjective(0)).thenReturn(2.0) ;
    Mockito.when(solution2.getObjective(1)).thenReturn(3.0) ;

    Mockito.when(comparator.compare(solution1, solution2)).thenReturn(-1) ;
    Mockito.when(comparator.compare(solution2, solution1)).thenReturn(1) ;

    List<Solution> population = Arrays.asList(solution1, solution2);

    selection = new BinaryTournamentSelection(comparator) ;

    Solution result = selection.execute(population);

    assertEquals(solution1, result);
    assertNotEquals(solution2, result);
    verify(comparator).compare(any(Solution.class), any(Solution.class));
  }

  @Test
  public void shouldExecuteWorkProperlyIfTheTwoSolutionsInTheListAreNondominated() {
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

    List<Solution> population = Arrays.<Solution>asList(solution1, solution2);

    selection = new BinaryTournamentSelection(comparator) ;
    Solution result = selection.execute(population) ;

    assertThat(result, Matchers.either(Matchers.is(solution1)).or(Matchers.is(solution2))) ;
    verify(comparator).compare(any(Solution.class), any(Solution.class));
  }

  @After
  public void tearDown() {
    selection = null ;
    population = null ;
    problem = null ;
  }
}
