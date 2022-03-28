package org.uma.jmetal.util.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class DominanceComparatorTest {

  @Test
  public void shouldCompareRaiseAnExceptionIfTheFirstSolutionIsNull() {
    var comparator = new DominanceWithConstraintsComparator<>();

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;

    assertThrows(NullParameterException.class, () -> comparator.compare(null, solution2));
  }

  @Test
  public void shouldCompareRaiseAnExceptionIfTheSecondSolutionIsNull() {
    var comparator = new DominanceWithConstraintsComparator<>();

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;

    assertThrows(NullParameterException.class, () -> comparator.compare(solution2, null));
  }

  @Test
  public void shouldCompareRaiseAnExceptionIfTheSolutionsHaveNotTheSameNumberOfObjectives() {
    DoubleSolution solution1 = new DummyDoubleProblem(2, 4, 0).createSolution() ;
    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 0).createSolution() ;

    var comparator = new DominanceWithConstraintsComparator<>();

    assertThrows(InvalidConditionException.class, () -> comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareReturnTheValueReturnedByTheConstraintViolationComparator() {
    OverallConstraintViolationDegreeComparator<DoubleSolution> violationComparator = new OverallConstraintViolationDegreeComparator<>() ;

    DoubleProblem problem = new DummyDoubleProblem(2, 2, 1) ;

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();

    solution1.constraints()[0] = 0.0 ;
    solution2.constraints()[0] = -1.0 ;

    var comparator = new DominanceWithConstraintsComparator<>(violationComparator);
    int obtainedValue = comparator.compare(solution1, solution2);

    assertEquals(-1, obtainedValue);
  }

  @Test
  public void shouldCompareReturnZeroIfTheTwoSolutionsHaveOneObjectiveWithTheSameValue() {
    DoubleProblem problem = new DummyDoubleProblem(2, 1, 1) ;

    DoubleSolution solution1 = problem.createSolution();
    solution1.objectives()[0] = 4.0 ;
    DoubleSolution solution2 = problem.createSolution();
    solution2.objectives()[0] = 4.0 ;

    var comparator = new DominanceWithConstraintsComparator<>();

    assertEquals(0, comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareReturnOneIfTheTwoSolutionsHasOneObjectiveAndTheSecondOneIsLower() {
    DoubleProblem problem = new DummyDoubleProblem(2, 1, 1) ;

    DoubleSolution solution1 = problem.createSolution();
    solution1.objectives()[0] = 4.0 ;
    DoubleSolution solution2 = problem.createSolution();
    solution2.objectives()[0] = 2.0;

    var comparator = new DominanceWithConstraintsComparator<>(new OverallConstraintViolationDegreeComparator<>());

    assertEquals(1, comparator.compare(solution1, solution2));
  }

  @Test
  public void shouldCompareReturnMinusOneIfTheTwoSolutionsHasOneObjectiveAndTheFirstOneIsLower() {
    DoubleProblem problem = new DummyDoubleProblem(2, 1, 1) ;

    DoubleSolution solution1 = problem.createSolution();
    solution1.objectives()[0] = -1.0 ;
    DoubleSolution solution2 = problem.createSolution();
    solution2.objectives()[0] = 2.0;

    var comparator = new DominanceWithConstraintsComparator<>(new OverallConstraintViolationDegreeComparator<>());

    assertEquals(-1, comparator.compare(solution1, solution2));
  }

  /**
   * Case A: solution1 has objectives [-1.0, 5.0, 9.0] and solution2 has [2.0, 6.0, 15.0]
   */
  @Test
  public void shouldCompareReturnMinusOneIfTheFirstSolutionDominatesTheSecondOneCaseA() {
    @SuppressWarnings("unchecked")
    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0) ;

    DoubleSolution solution1 = problem.createSolution();
    solution1.objectives()[0] = -1.0 ;
    solution1.objectives()[1] = 5.0 ;
    solution1.objectives()[2] = 9.0 ;
    DoubleSolution solution2 = problem.createSolution();
    solution2.objectives()[0] = 2.0;
    solution2.objectives()[1] = 6.0;
    solution2.objectives()[2] = 16.0;

    var comparator = new DominanceWithConstraintsComparator<>();

    assertEquals(-1, comparator.compare(solution1, solution2));
  }

  /**
   * Case B: solution1 has objectives [-1.0, 5.0, 9.0] and solution2 has [-1.0, 5.0, 10.0]
   */
  @Test
  public void shouldCompareReturnMinusOneIfTheFirstSolutionDominatesTheSecondOneCaseB() {
    @SuppressWarnings("unchecked")
    DoubleProblem problem = new DummyDoubleProblem(2, 3, 1) ;

    DoubleSolution solution1 = problem.createSolution();
    solution1.objectives()[0] = -1.0 ;
    solution1.objectives()[1] = 5.0 ;
    solution1.objectives()[2] = 9.0 ;
    DoubleSolution solution2 = problem.createSolution();
    solution2.objectives()[0] = -1.0;
    solution2.objectives()[1] = 5.0;
    solution2.objectives()[2] = 10.0;

    var comparator = new DominanceWithConstraintsComparator<>(new OverallConstraintViolationDegreeComparator<>());

    assertEquals(-1, comparator.compare(solution1, solution2));
  }

  /**
   * Case C: solution1 has  objectives [-1.0, 5.0, 9.0] and solution2 has [-2.0, 5.0, 9.0]
   */
  @Test
  public void shouldCompareReturnOneIfTheSecondSolutionDominatesTheFirstOneCaseC() {
    OverallConstraintViolationDegreeComparator<DoubleSolution> violationComparator = new OverallConstraintViolationDegreeComparator<>();

    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0) ;

    DoubleSolution solution1 = problem.createSolution() ;
    solution1.objectives()[0] = -1.0 ;
    solution1.objectives()[1] = 5.0 ;
    solution1.objectives()[1] = 9.0 ;

    DoubleSolution solution2 = problem.createSolution() ;
    solution2.objectives()[0] = -2.0 ;
    solution2.objectives()[1] = 5.0 ;
    solution2.objectives()[1] = 9.0 ;

    var comparator = new DominanceWithConstraintsComparator<>(violationComparator);

    assertEquals(1, comparator.compare(solution1, solution2));
  }

  /**
   * Case D: solution1 has  objectives [-1.0, 5.0, 9.0] and solution2 has [-1.0, 5.0, 8.0]
   */
  @Test
  public void shouldCompareReturnOneIfTheSecondSolutionDominatesTheFirstOneCaseD() {
    OverallConstraintViolationDegreeComparator<DoubleSolution> violationComparator = new OverallConstraintViolationDegreeComparator<>();

    DoubleProblem problem = new DummyDoubleProblem(2, 3, 0) ;

    DoubleSolution solution1 = problem.createSolution() ;
    solution1.objectives()[0] = -1.0 ;
    solution1.objectives()[1] = 5.0 ;
    solution1.objectives()[1] = 9.0 ;

    DoubleSolution solution2 = problem.createSolution() ;
    solution2.objectives()[0] = -1.0 ;
    solution2.objectives()[1] = 5.0 ;
    solution2.objectives()[1] = 8.0 ;

    var comparator = new DominanceWithConstraintsComparator<>(violationComparator);

    assertEquals(1, comparator.compare(solution1, solution2));
  }
}
