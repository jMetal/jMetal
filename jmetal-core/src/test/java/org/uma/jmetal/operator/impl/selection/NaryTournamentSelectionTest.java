package org.uma.jmetal.operator.impl.selection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NaryTournamentSelectionTest {

  private static final int POPULATION_SIZE = 20 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldDefaultConstructorSetTheNumberOfSolutionsToBeReturnedEqualsToTwo() {
    NaryTournamentSelection<IntegerSolution> selection = new NaryTournamentSelection<>() ;

    assertEquals(2, ReflectionTestUtils.getField(selection, "numberOfSolutionsToBeReturned"));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    NaryTournamentSelection<IntegerSolution> selection = new NaryTournamentSelection<>() ;
    List<IntegerSolution> population ;
    population = null ;

    selection.execute(population) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    NaryTournamentSelection<IntegerSolution> selection = new NaryTournamentSelection<>() ;

    List<IntegerSolution> population = new ArrayList<>(0) ;

    selection.execute(population) ;
  }

  @Test
  public void shouldExecuteReturnAValidSolutionIsWithCorrectParameters() {
    NaryTournamentSelection<BinarySolution> selection = new NaryTournamentSelection<>() ;
    BinarySolution solution = mock(BinarySolution.class) ;

    @SuppressWarnings("unchecked")
    Problem<BinarySolution> problem = mock(Problem.class) ;

    Mockito.when(problem.createSolution()).thenReturn(solution) ;

    List<BinarySolution> population = new ArrayList<>(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      population.add(problem.createSolution());
    }
    assertNotNull(selection.execute(population));
    verify(problem, times(POPULATION_SIZE)).createSolution();
  }

  @Test
  public void shouldExecuteReturnTheSameSolutionIfTheListContainsOneSolution() {
    @SuppressWarnings("unchecked")
    NaryTournamentSelection<DoubleSolution>selection =
        new NaryTournamentSelection<DoubleSolution>(1, mock(Comparator.class)) ;
    DoubleSolution solution = mock(DoubleSolution.class) ;

    List<DoubleSolution> population = new ArrayList<>(1) ;
    population.add(solution) ;
    assertSame(solution, selection.execute(population));
  }

  @Test
  public void shouldExecuteReturnTwoSolutionsIfTheListContainsTwoSolutions() {
    IntegerSolution solution1 = mock(IntegerSolution.class) ;
    IntegerSolution solution2 = mock(IntegerSolution.class) ;

    List<IntegerSolution> population = Arrays.asList(solution1, solution2) ;
    assertEquals(2, population.size());
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListSizeIsOneAndTwoSolutionsAreRequested() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list size (1) is less than " +
        "the number of requested solutions (4)"));

    @SuppressWarnings("unchecked")
    NaryTournamentSelection<IntegerSolution> selection =
        new NaryTournamentSelection<>(4, mock(Comparator.class)) ;
    List<IntegerSolution> list = new ArrayList<>(1) ;
    list.add(mock(IntegerSolution.class)) ;

    selection.execute(list) ;
  }
}
