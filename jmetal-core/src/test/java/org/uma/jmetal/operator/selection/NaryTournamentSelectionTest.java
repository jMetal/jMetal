package org.uma.jmetal.operator.selection;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

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
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0) ;

    List<DoubleSolution> population = IntStream.range(0, POPULATION_SIZE).mapToObj(i -> problem.createSolution()).collect(Collectors.toCollection(() -> new ArrayList<>(POPULATION_SIZE)));
      NaryTournamentSelection<DoubleSolution> selection = new NaryTournamentSelection<>() ;

    assertNotNull(selection.execute(population));
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
