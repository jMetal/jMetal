package org.uma.jmetal.util.comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class DefaultDominanceComparatorTest {

  private DefaultDominanceComparator<DoubleSolution> dominanceComparator;

  @BeforeEach
  public void setup() {
    dominanceComparator = new DefaultDominanceComparator<>();
  }

  @Test
  public void shouldCompareRaiseAnExceptionIfTheFirstSolutionIsNull() {
    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution();

    assertThrows(NullParameterException.class, () -> dominanceComparator.compare(null, solution2));
  }

  @Test
  public void shouldCompareRaiseAnExceptionIfTheSecondSolutionIsNull() {
    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution();

    assertThrows(NullParameterException.class, () -> dominanceComparator.compare(solution2, null));
  }

  @Test
  public void shouldCompareRaiseAnExceptionIfTheSolutionsHaveNotTheSameNumberOfObjectives() {
    var solution1 = new FakeDoubleProblem(2, 4, 0).createSolution();
    var solution2 = new FakeDoubleProblem(2, 2, 0).createSolution();

    assertThrows(InvalidConditionException.class,
        () -> dominanceComparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareReturnZeroIfTheTwoSolutionsHaveOneObjectiveWithTheSameValue() {
    DoubleProblem problem = new FakeDoubleProblem(2, 1, 1);

    var solution1 = problem.createSolution();
    solution1.objectives()[0] = 4.0;
    var solution2 = problem.createSolution();
    solution2.objectives()[0] = 4.0;

    assertThat(dominanceComparator.compare(solution1, solution2)).isEqualTo(0);
  }

  @Test
  public void shouldCompareReturnOneIfTheTwoSolutionsHasOneObjectiveAndTheSecondOneIsLower() {
    DoubleProblem problem = new FakeDoubleProblem(2, 1, 1);

    var solution1 = problem.createSolution();
    solution1.objectives()[0] = 4.0;
    var solution2 = problem.createSolution();
    solution2.objectives()[0] = 2.0;

    assertThat(dominanceComparator.compare(solution1, solution2)).isEqualTo(1);
  }

  @Test
  public void shouldCompareReturnMinusOneIfTheTwoSolutionsHasOneObjectiveAndTheFirstOneIsLower() {
    DoubleProblem problem = new FakeDoubleProblem(2, 1, 1);

    var solution1 = problem.createSolution();
    solution1.objectives()[0] = -1.0;
    var solution2 = problem.createSolution();
    solution2.objectives()[0] = 2.0;

    assertThat(dominanceComparator.compare(solution1, solution2)).isEqualTo(-1);
  }

  /**
   * Case A: solution1 has objectives [-1.0, 5.0, 9.0] and solution2 has [2.0, 6.0, 15.0]
   */
  @Test
  public void shouldCompareReturnMinusOneIfTheFirstSolutionDominatesTheSecondOneCaseA() {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);

    var solution1 = problem.createSolution();
    solution1.objectives()[0] = -1.0;
    solution1.objectives()[1] = 5.0;
    solution1.objectives()[2] = 9.0;
    var solution2 = problem.createSolution();
    solution2.objectives()[0] = 2.0;
    solution2.objectives()[1] = 6.0;
    solution2.objectives()[2] = 16.0;

    assertThat(dominanceComparator.compare(solution1, solution2)).isEqualTo(-1);
  }

  /**
   * Case B: solution1 has objectives [-1.0, 5.0, 9.0] and solution2 has [-1.0, 5.0, 10.0]
   */
  @Test
  public void shouldCompareReturnMinusOneIfTheFirstSolutionDominatesTheSecondOneCaseB() {
    @SuppressWarnings("unchecked")
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 1);

    var solution1 = problem.createSolution();
    solution1.objectives()[0] = -1.0;
    solution1.objectives()[1] = 5.0;
    solution1.objectives()[2] = 9.0;
    var solution2 = problem.createSolution();
    solution2.objectives()[0] = -1.0;
    solution2.objectives()[1] = 5.0;
    solution2.objectives()[2] = 10.0;

    assertThat(dominanceComparator.compare(solution1, solution2)).isEqualTo(-1);
  }

  /**
   * Case C: solution1 has  objectives [-1.0, 5.0, 9.0] and solution2 has [-2.0, 5.0, 9.0]
   */
  @Test
  public void shouldCompareReturnOneIfTheSecondSolutionDominatesTheFirstOneCaseC() {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);

    var solution1 = problem.createSolution();
    solution1.objectives()[0] = -1.0;
    solution1.objectives()[1] = 5.0;
    solution1.objectives()[2] = 9.0;

    var solution2 = problem.createSolution();
    solution2.objectives()[0] = -2.0;
    solution2.objectives()[1] = 5.0;
    solution2.objectives()[2] = 9.0;

    assertThat(dominanceComparator.compare(solution1, solution2)).isEqualTo(1);
  }

  /**
   * Case D: solution1 has  objectives [-1.0, 5.0, 9.0] and solution2 has [-1.0, 5.0, 8.0]
   */
  @Test
  public void shouldCompareReturnOneIfTheSecondSolutionDominatesTheFirstOneCaseD() {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);

    var solution1 = problem.createSolution();
    solution1.objectives()[0] = -1.0;
    solution1.objectives()[1] = 5.0;
    solution1.objectives()[2] = 9.0;

    var solution2 = problem.createSolution();
    solution2.objectives()[0] = -1.0;
    solution2.objectives()[1] = 5.0;
    solution2.objectives()[2] = 8.0;

    assertThat(dominanceComparator.compare(solution1, solution2)).isEqualTo(1);
  }
}
