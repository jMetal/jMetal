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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NaryTournamentSelectionTest {
  private static final int POPULATION_SIZE = 20 ;

  private NaryTournamentSelection selection ;
  private List<Solution> population ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldDefaultConstructorSetTheNumberOfSolutionsToBeReturnedEqualsToTwo() {
    selection = new NaryTournamentSelection() ;

    assertEquals(2, ReflectionTestUtils.getField(selection, "numberOfSolutionsToBeReturned"));
  }

  @Test(expected = JMetalException.class)
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    selection = new NaryTournamentSelection() ;
    population = null ;
    selection.execute(population) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    selection = new NaryTournamentSelection() ;

    population = new ArrayList<>(0) ;

    selection.execute(population) ;
  }

  @Test
  public void shouldExecuteReturnAValidSolutionIsWithCorrectParameters() {
    selection = new NaryTournamentSelection() ;
    Solution solution = mock(Solution.class) ;

    Problem problem = mock(Problem.class) ;

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
    selection = new NaryTournamentSelection(1, mock(Comparator.class)) ;
    Solution solution = mock(Solution.class) ;

    population = new ArrayList<>(1) ;
    population.add(solution) ;
    assertSame(solution, selection.execute(population));
  }

  @Test
  public void shouldExecuteReturnTwoSolutionsIfTheListContainsTwoSolutions() {
    selection = new NaryTournamentSelection(2, mock(Comparator.class)) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    population = Arrays.asList(solution1, solution2) ;
    assertEquals(2, population.size());
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListSizeIsOneAndTwoSolutionsAreRequested() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list size (1) is less than " +
        "the number of requested solutions (4)"));

    selection = new NaryTournamentSelection(4, mock(Comparator.class)) ;
    List<Solution> list = new ArrayList<>(1) ;
    list.add(mock(Solution.class)) ;

    selection.execute(list) ;
  }
}
