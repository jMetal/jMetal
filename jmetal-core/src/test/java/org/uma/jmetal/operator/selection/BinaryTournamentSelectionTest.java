package org.uma.jmetal.operator.selection;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.exception.EmptyCollectionException;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class BinaryTournamentSelectionTest {

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    BinaryTournamentSelection<Solution<Object>> selection = new BinaryTournamentSelection<Solution<Object>>() ;
    assertThrows(NullParameterException.class, () -> selection.execute(null)) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    BinaryTournamentSelection<Solution<Object>> selection = new BinaryTournamentSelection<Solution<Object>>() ;
    assertThrows(EmptyCollectionException.class, () -> selection.execute(new ArrayList<>(0))) ;
  }

  @Test
  public void shouldExecuteReturnAValidSolutionIsWithCorrectParameters() {
	  @SuppressWarnings("unchecked")
    Solution<Object> solution1 = Mockito.mock(Solution.class) ;
    Solution<Object> solution2 = Mockito.mock(Solution.class) ;
    Solution<Object> solution3 = Mockito.mock(Solution.class) ;

    List<Solution<Object>> population = List.of(solution1, solution2, solution3) ;

    BinaryTournamentSelection<Solution<Object>> selection = new BinaryTournamentSelection<Solution<Object>>() ;
    assertNotNull(selection.execute(population));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListContainsOneSolution() {
	  @SuppressWarnings("unchecked")
	  Solution<Object> solution = mock(Solution.class) ;

    assertThrows(InvalidConditionException.class, () -> new BinaryTournamentSelection<Solution<Object>>().execute(List.of(solution))) ;
  }

  @Test
  public void shouldExecuteReturnTwoSolutionsIfTheListContainsTwoSolutions() {
    @SuppressWarnings("unchecked")
    Solution<Object> solution1 = mock(Solution.class) ;
    @SuppressWarnings("unchecked")
    Solution<Object> solution2 = mock(Solution.class) ;

    assertEquals(2, Arrays.asList(solution1, solution2));
  }

  @Test
  public void shouldExecuteWorkProperlyIfTheTwoSolutionsInTheListAreNondominated() {
    Comparator<DoubleSolution> comparator = mock(Comparator.class) ;

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    List<DoubleSolution> population = Arrays.<DoubleSolution>asList(solution1, solution2);

    BinaryTournamentSelection<DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(comparator) ;
    DoubleSolution result = selection.execute(population);

    assertThat(result, Matchers.either(Matchers.is(solution1)).or(Matchers.is(solution2))) ;
    verify(comparator).compare(any(DoubleSolution.class), any(DoubleSolution.class));
  }
}
