package org.uma.jmetal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;

public class ConstraintHandlingTest {
  private static final double EPSILON = 0.0000000001 ;

  @Test
  public void shouldIsFeasibleReturnTrueIfTheSolutionHasNoConstraints() {
    var solution = new FakeDoubleProblem(2, 2, 0).createSolution() ;

    assertTrue(ConstraintHandling.isFeasible(solution));
  }

  @Test
  public void shouldIsFeasibleReturnTrueIfTheSolutionHasConstraintsAndItIsFeasible() {
    var solution = new FakeDoubleProblem(2, 2, 1).createSolution() ;
    solution.constraints()[0] = 0.0 ;

    assertTrue(ConstraintHandling.isFeasible(solution));
  }

  @Test
  public void shouldIsFeasibleReturnFalseIfTheSolutionIsNotFeasible() {
    var solution = new FakeDoubleProblem(2, 2, 1).createSolution() ;
    solution.constraints()[0] = -1.0 ;

    assertFalse(ConstraintHandling.isFeasible(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnZeroIfTheSolutionHasNoConstraints() {
    var solution = new FakeDoubleProblem(2, 2, 0).createSolution() ;

    assertEquals(0, ConstraintHandling.numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnZeroIfTheSolutionHasNotViolatedConstraints() {
    var solution = new FakeDoubleProblem(2, 2, 1).createSolution() ;
    solution.constraints()[0] = 0.0 ;

    assertEquals(0, ConstraintHandling.numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnTheRightNumberOfViolatedConstraints() {
    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution.constraints()[0] = 0.0 ;
    solution.constraints()[1] = -1.0 ;

    assertEquals(1, ConstraintHandling.numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldOverallConstraintViolationDegreeReturnZeroIfTheSolutionHasNotViolatedConstraints() {
    var solution = new FakeDoubleProblem(2, 2, 1).createSolution() ;
    solution.constraints()[0] = 0.0 ;

    assertEquals(0.0, ConstraintHandling.overallConstraintViolationDegree(solution), EPSILON);
  }

  @Test
  public void shouldOverallConstraintViolationDegreeReturnTheRightViolationDegree() {
    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution.constraints()[0] = -1.0 ;
    solution.constraints()[1] = -2.0 ;

    assertEquals(-3, ConstraintHandling.overallConstraintViolationDegree(solution), EPSILON);
  }

  @Test
  public void shouldFeasibilityRatioRaiseAndExceptionIfTheSolutionListIsEmpty() {
    assertThrows(EmptyCollectionException.class, () -> ConstraintHandling.feasibilityRatio(new ArrayList<>()));
  }

  @Test
  public void shouldFeasibilityRatioReturnZeroIfAllTheSolutionsAreUnFeasible() {
    var solution1 = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution1.constraints()[0] = -1.0 ;
    solution1.constraints()[1] = -2.0 ;

    var solution2 = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution2.constraints()[0] = 0.0 ;
    solution2.constraints()[1] = -1.0 ;

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2) ;

    assertEquals(0.0, ConstraintHandling.feasibilityRatio(solutionList), EPSILON) ;
  }

  @Test
  public void shouldFeasibilityRatioReturnOneIfAllTheSolutionsAreFeasible() {
    var solution1 = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution1.constraints()[0] = 0.0 ;
    solution1.constraints()[1] = 0.0 ;

    var solution2 = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution2.constraints()[0] = 0.0 ;
    solution2.constraints()[1] = 0.0 ;

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2) ;

    assertEquals(1.0, ConstraintHandling.feasibilityRatio(solutionList), EPSILON) ;
  }

  @Test
  public void shouldFeasibilityRatioReturnTheRightPercentageOfFeasibleSolutions() {
    var solution1 = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution1.constraints()[0] = 0.0 ;
    solution1.constraints()[1] = -1.0 ;

    var solution2 = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution2.constraints()[0] = 0.0 ;
    solution2.constraints()[1] = 0.0 ;

    var solution3 = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    solution3.constraints()[0] = -2.0 ;
    solution3.constraints()[1] = 0.0 ;

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2, solution3) ;

    assertEquals(1.0/3, ConstraintHandling.feasibilityRatio(solutionList), EPSILON) ;
  }

  @Test
  public void shouldOverallConstraintViolationDegreeWorkProperly() {
    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    var overallConstraintViolationDegree = -4.0 ;
    ConstraintHandling.overallConstraintViolationDegree(solution, overallConstraintViolationDegree);

    assertEquals(overallConstraintViolationDegree, ConstraintHandling.overallConstraintViolationDegree(solution), EPSILON);
  }

  @Test
  public void shouldOverallNumberOfViolatedConstraintsProperly() {
    var solution = new FakeDoubleProblem(2, 2, 2).createSolution() ;
    var numberOfViolatedConstraints = 2 ;
    ConstraintHandling.numberOfViolatedConstraints(solution, numberOfViolatedConstraints);

    assertEquals(numberOfViolatedConstraints, ConstraintHandling.numberOfViolatedConstraints(solution));
  }
}
