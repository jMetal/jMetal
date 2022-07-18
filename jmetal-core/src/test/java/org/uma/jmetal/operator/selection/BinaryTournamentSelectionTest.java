package org.uma.jmetal.operator.selection;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class BinaryTournamentSelectionTest {

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    var selection =
        new BinaryTournamentSelection<Solution<Object>>();
    assertThrows(NullParameterException.class, () -> selection.execute(null));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    var selection =
        new BinaryTournamentSelection<Solution<Object>>();
    assertThrows(EmptyCollectionException.class, () -> selection.execute(new ArrayList<>(0)));
  }

  @Test
  public void shouldExecuteReturnAValidSolutionIsWithCorrectParameters() {
    @SuppressWarnings("unchecked")
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);

    var solution1 = problem.createSolution();
    var solution2 = problem.createSolution();
    var solution3 = problem.createSolution();

    var population = List.of(solution1, solution2, solution3);

    var selection = new BinaryTournamentSelection<DoubleSolution>();
    assertNotNull(selection.execute(population));
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListContainsOneSolution() {
    @SuppressWarnings("unchecked")
    Solution<Object> solution = mock(Solution.class);

    assertThrows(
        InvalidConditionException.class,
        () -> new BinaryTournamentSelection<Solution<Object>>().execute(List.of(solution)));
  }

  @Test
  public void shouldExecuteWorkProperlyIfTheTwoSolutionsInTheListAreNondominated() {
    Comparator<DoubleSolution> comparator = mock(Comparator.class);

    var solution1 = mock(DoubleSolution.class);
    var solution2 = mock(DoubleSolution.class);

    var population = Arrays.<DoubleSolution>asList(solution1, solution2);

    var selection =
        new BinaryTournamentSelection<DoubleSolution>(comparator);
    var result = selection.execute(population);

    assertThat(result, Matchers.either(Matchers.is(solution1)).or(Matchers.is(solution2)));
    verify(comparator).compare(any(DoubleSolution.class), any(DoubleSolution.class));
  }
}
