package org.uma.jmetal.util.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.1
 */
public class ObjectiveComparatorTest {

  private ObjectiveComparator<Solution<?>> comparator;

  @Test
  public void shouldCompareRaiseAnExceptionIfTheFirstSolutionIsNull() {
    comparator = new ObjectiveComparator<>(0);

    Solution<?> solution2 = mock(Solution.class);

    assertThrows(NullParameterException.class, () -> comparator.compare(null, solution2));
  }

  @Test
  public void shouldCompareRaiseAnExceptionIfTheSecondSolutionIsNull() {
    comparator = new ObjectiveComparator<>(0);

    Solution<?> solution1 = mock(Solution.class);

    assertThrows(NullParameterException.class, () -> comparator.compare(solution1, null));
  }

  @Test
  public void shouldCompareReturnMinusOneIfTheObjectiveOfSolution1IsLower() {
    comparator = new ObjectiveComparator<>(0);

    DoubleProblem problem = new DummyDoubleProblem(2, 1, 0);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();

    solution1.objectives()[0] = -4.0;
    solution2.objectives()[0] = 5.0;

    assertEquals(-1, comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareReturnOneIfTheObjectiveOfSolution2IsLower() {
    comparator = new ObjectiveComparator<Solution<?>>(2);

    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();

    solution1.objectives()[2] = 7.0;
    solution2.objectives()[2] = 5.0;

    assertEquals(1, comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareReturnZeroIfTheObjectiveOfTheSolutionsIsTheSame() {
    comparator = new ObjectiveComparator<Solution<?>>(2);

    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();

    solution1.objectives()[2] = 7.0;
    solution2.objectives()[2] = 7.0;

    assertEquals(0, comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareReturnMinusOneIfTheObjectiveOfSolution1IsGreaterInDescendingOrder() {
    comparator = new ObjectiveComparator<>(0, ObjectiveComparator.Ordering.DESCENDING);

    DoubleProblem problem = new DummyDoubleProblem(2, 1, 0);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();

    solution1.objectives()[0] = 25.0;
    solution2.objectives()[0] = 5.0;

    assertEquals(-1, comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareReturnOneIfTheObjectiveOfSolution2IsGreaterInDescendingOrder() {
    comparator = new ObjectiveComparator<Solution<?>>(2, ObjectiveComparator.Ordering.DESCENDING);

    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();

    solution1.objectives()[2] = 7.0;
    solution2.objectives()[2] = 25.0;

    assertEquals(1, comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareRaiseAnExceptionIfSolution1HasFewerObjectivesThanTheOneRequested() {

    comparator = new ObjectiveComparator<Solution<?>>(5, ObjectiveComparator.Ordering.DESCENDING);

    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0);
    DoubleProblem problem2 = new DummyDoubleProblem(2, 6, 0);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem2.createSolution();

    assertThrows(InvalidConditionException.class, () -> comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareRaiseAnExceptionIfSolution2HasFewerObjectivesThanTheOneRequested() {
    //exception.expect(JMetalException.class);
    //exception.expectMessage(containsString("The solution2 has 5 objectives and the objective "
    //    + "to sort is 5"));

    comparator = new ObjectiveComparator<Solution<?>>(5, ObjectiveComparator.Ordering.DESCENDING);

    DoubleProblem problem = new DummyDoubleProblem(2, 1, 0);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();

    solution1.objectives()[0] = 7.0;
    solution2.objectives()[0] = 5.0;

    assertThrows(InvalidConditionException.class, () -> comparator.compare(solution1, solution2));
  }
}
