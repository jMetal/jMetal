package org.uma.jmetal.operator.selection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.checking.exception.EmptyCollectionException;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NaryTournamentSelectionTest {

  private static final int POPULATION_SIZE = 20 ;

  @Test
  public void shouldDefaultConstructorSetTheNumberOfSolutionsToBeReturnedEqualsToTwo() {
    NaryTournamentSelection<IntegerSolution> selection = new NaryTournamentSelection<>() ;

    assertEquals(2, selection.getTournamentSize());
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    NaryTournamentSelection<IntegerSolution> selection = new NaryTournamentSelection<>() ;
    List<IntegerSolution> population ;
    population = null ;

    assertThrows(NullParameterException.class, () -> selection.execute(population)) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    NaryTournamentSelection<IntegerSolution> selection = new NaryTournamentSelection<>() ;

    List<IntegerSolution> population = new ArrayList<>(0) ;

    assertThrows(EmptyCollectionException.class, () -> selection.execute(population)) ;
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
  public void shouldExecuteRaiseAnExceptionIfTheListSizeIsOneAndTwoSolutionsAreRequested() {
    @SuppressWarnings("unchecked")
    NaryTournamentSelection<IntegerSolution> selection =
        new NaryTournamentSelection<>(4, mock(Comparator.class)) ;
    List<IntegerSolution> list = new ArrayList<>(1) ;
    list.add(mock(IntegerSolution.class)) ;

    Executable executable = () -> selection.execute(list);
    
    InvalidConditionException cause = assertThrows(InvalidConditionException.class, executable) ;
    assertThat(cause.getMessage(), containsString("The solution list size (1) is less than " +
        "the number of requested solutions (4)"));
  }
}
