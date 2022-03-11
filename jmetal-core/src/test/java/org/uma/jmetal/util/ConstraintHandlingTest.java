package org.uma.jmetal.util;

import static org.junit.Assert.assertEquals;
import static org.uma.jmetal.util.ConstraintHandling.feasibilityRatio;
import static org.uma.jmetal.util.ConstraintHandling.isFeasible;
import static org.uma.jmetal.util.ConstraintHandling.numberOfViolatedConstraints;
import static org.uma.jmetal.util.ConstraintHandling.overallConstraintViolationDegree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;

public class ConstraintHandlingTest {

  @Test
  public void shouldIsFeasibleReturnTrueIfTheSolutionHasNoConstraints() {
    DoubleSolution solution = new DummyDoubleProblem(2, 2, 0).createSolution() ;

    assertEquals(true, isFeasible(solution));
  }

  @Test
  public void shouldIsFeasibleReturnTrueIfTheSolutionHasConstraintsAndItIsFeasble() {
    DoubleSolution solution = new DummyDoubleProblem(2, 2, 1).createSolution() ;
    solution.constraints()[0] = 0.0 ;

    assertEquals(true, isFeasible(solution));
  }

  @Test
  public void shouldIsFeasibleReturnFalseIfTheSolutionIsNotFeasible() {
    DoubleSolution solution = new DummyDoubleProblem(2, 2, 1).createSolution() ;
    solution.constraints()[0] = -1.0 ;

    assertEquals(false, isFeasible(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnZeroIfTheSolutionHasNoConstraints() {
    DoubleSolution solution = new DummyDoubleProblem(2, 2, 0).createSolution() ;

    assertEquals(0, numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnZeroIfTheSolutionHasNotViolatedConstraints() {
    DoubleSolution solution = new DummyDoubleProblem(2, 2, 1).createSolution() ;
    solution.constraints()[0] = 0.0 ;

    assertEquals(0, numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnTheRightNumberOfViolatedConstraints() {
    DoubleSolution solution = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution.constraints()[0] = 0.0 ;
    solution.constraints()[1] = -1.0 ;

    assertEquals(1, numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldOverallConstraintViolationDegreeReturnZeroIfTheSolutionHasNotViolatedConstraints() {
    DoubleSolution solution = new DummyDoubleProblem(2, 2, 1).createSolution() ;
    solution.constraints()[0] = 0.0 ;

    assertEquals(0.0, overallConstraintViolationDegree(solution), 0.000000001);
  }

  @Test
  public void shouldOverallConstraintViolationDegreeReturnTheRightViolationDegree() {
    DoubleSolution solution = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution.constraints()[0] = -1.0 ;
    solution.constraints()[1] = -2.0 ;

    assertEquals(-3, overallConstraintViolationDegree(solution), 0.00000000001);
  }

  @Test (expected = EmptyCollectionException.class)
  public void shouldFeasibilityRatioRaiseAndExceptionIfTheSolutionListIsEmpty() {
    List<DoubleSolution> solutionList = new ArrayList<>() ;

    feasibilityRatio(solutionList);
  }

  @Test
  public void shouldFeasibilityRatioReturnZeroIfAllTheSolutionsAreUnFeasible() {
    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution1.constraints()[0] = -1.0 ;
    solution1.constraints()[1] = -2.0 ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution2.constraints()[0] = 0.0 ;
    solution2.constraints()[1] = -1.0 ;

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2) ;

    assertEquals(0.0, feasibilityRatio(solutionList), 0.00000000001) ;
  }

  @Test
  public void shouldFeasibilityRatioReturnOneIfAllTheSolutionsAreFeasible() {
    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution1.constraints()[0] = 0.0 ;
    solution1.constraints()[1] = 0.0 ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution2.constraints()[0] = 0.0 ;
    solution2.constraints()[1] = 0.0 ;

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2) ;

    assertEquals(1.0, feasibilityRatio(solutionList), 0.00000000001) ;
  }

  @Test
  public void shouldFeasibilityRatioReturnTheRightPercentageOfFeasibleSolutions() {
    DoubleSolution solution1 = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution1.constraints()[0] = 0.0 ;
    solution1.constraints()[1] = -1.0 ;

    DoubleSolution solution2 = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution2.constraints()[0] = 0.0 ;
    solution2.constraints()[1] = 0.0 ;

    DoubleSolution solution3 = new DummyDoubleProblem(2, 2, 2).createSolution() ;
    solution3.constraints()[0] = -2.0 ;
    solution3.constraints()[1] = 0.0 ;

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2, solution3) ;

    assertEquals(1.0/3, feasibilityRatio(solutionList), 0.00000000001) ;
  }
}
