package org.uma.jmetal.util;

import org.junit.Test;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.exception.EmptyCollectionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.uma.jmetal.util.ConstraintHandling.*;

public class ConstraintHandlingTest {

  @Test
  public void shouldIsFeasibleReturnTrueIfTheSolutionHasNoConstraints() {
    Solution<?> solution = mock(Solution.class);
    when(solution.getNumberOfConstraints()).thenReturn(0);

    assertEquals(true, isFeasible(solution));
  }

  @Test
  public void shouldIsFeasibleReturnTrueIfTheSolutionHasConstraintsAndItIsFeasble() {
    Solution<?> solution = mock(Solution.class);
    when(solution.getNumberOfConstraints()).thenReturn(1);
    when(solution.getConstraint(0)).thenReturn(0.0);

    assertEquals(true, isFeasible(solution));
  }

  @Test
  public void shouldIsFeasibleReturnFalseIfTheSolutionIsNotFeasible() {
    Solution<?> solution = mock(Solution.class);
    when(solution.getNumberOfConstraints()).thenReturn(1);
    when(solution.getConstraint(0)).thenReturn(-1.0);

    assertEquals(false, isFeasible(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnZeroIfTheSolutionHasNoConstraints() {
    Solution<?> solution = mock(Solution.class);
    when(solution.getNumberOfConstraints()).thenReturn(0);

    assertEquals(0, numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnZeroIfTheSolutionHasNotViolatedConstraints() {
    Solution<?> solution = mock(Solution.class);
    when(solution.getNumberOfConstraints()).thenReturn(1);
    when(solution.getConstraint(0)).thenReturn(0.0);

    assertEquals(0, numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldNumberOfViolatedConstraintsReturnTheRightNumberOfViolatedConstraints() {
    Solution<?> solution = mock(Solution.class);
    when(solution.getNumberOfConstraints()).thenReturn(2);
    when(solution.getConstraint(0)).thenReturn(0.0);
    when(solution.getConstraint(1)).thenReturn(-1.0);

    assertEquals(1, numberOfViolatedConstraints(solution));
  }

  @Test
  public void shouldOverallConstraintViolationDegreeReturnZeroIfTheSolutionHasNotViolatedConstraints() {
    Solution<?> solution = mock(Solution.class);
    when(solution.getNumberOfConstraints()).thenReturn(1);
    when(solution.getConstraint(0)).thenReturn(0.0);

    assertEquals(0.0, overallConstraintViolationDegree(solution), 0.000000001);
  }

  @Test
  public void shouldOverallConstraintViolationDegreeReturnTheRightViolationDegree() {
    Solution<?> solution = mock(Solution.class);
    when(solution.getNumberOfConstraints()).thenReturn(2);
    when(solution.getConstraint(0)).thenReturn(-1.0);
    when(solution.getConstraint(1)).thenReturn(-2.0);

    assertEquals(-3, overallConstraintViolationDegree(solution), 0.00000000001);
  }

  @Test (expected = EmptyCollectionException.class)
  public void shouldFeasibilityRatioRaiseAndExceptionIfTheSolutionListIsEmpty() {
    List<DoubleSolution> solutionList = new ArrayList<>() ;

    feasibilityRatio(solutionList);
  }

  @Test
  public void shouldFeasibilityRatioReturnZeroIfAllTheSolutionsAreUnFeasible() {
    Solution<?> solution1 = mock(Solution.class);
    when(solution1.getNumberOfConstraints()).thenReturn(2);
    when(solution1.getConstraint(0)).thenReturn(-1.0);
    when(solution1.getConstraint(1)).thenReturn(-2.0);
    Solution<?> solution2 = mock(Solution.class);
    when(solution2.getNumberOfConstraints()).thenReturn(2);
    when(solution2.getConstraint(0)).thenReturn(0.0);
    when(solution2.getConstraint(1)).thenReturn(-1.0);

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2) ;

    assertEquals(0.0, feasibilityRatio(solutionList), 0.00000000001) ;
  }

  @Test
  public void shouldFeasibilityRatioReturnOneIfAllTheSolutionsAreFeasible() {
    Solution<?> solution1 = mock(Solution.class);
    when(solution1.getNumberOfConstraints()).thenReturn(2);
    when(solution1.getConstraint(0)).thenReturn(0.0);
    when(solution1.getConstraint(1)).thenReturn(0.0);
    Solution<?> solution2 = mock(Solution.class);
    when(solution2.getNumberOfConstraints()).thenReturn(2);
    when(solution2.getConstraint(0)).thenReturn(0.0);
    when(solution2.getConstraint(1)).thenReturn(0.0);

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2) ;

    assertEquals(1.0, feasibilityRatio(solutionList), 0.00000000001) ;
  }

  @Test
  public void shouldFeasibilityRatioReturnTheRightPercentageOfFeasibleSolutions() {
    Solution<?> solution1 = mock(Solution.class);
    when(solution1.getNumberOfConstraints()).thenReturn(2);
    when(solution1.getConstraint(0)).thenReturn(0.0);
    when(solution1.getConstraint(1)).thenReturn(-1.0);
    Solution<?> solution2 = mock(Solution.class);
    when(solution2.getNumberOfConstraints()).thenReturn(2);
    when(solution2.getConstraint(0)).thenReturn(0.0);
    when(solution2.getConstraint(1)).thenReturn(0.0);
    Solution<?> solution3 = mock(Solution.class);
    when(solution3.getNumberOfConstraints()).thenReturn(2);
    when(solution3.getConstraint(0)).thenReturn(-2.0);
    when(solution3.getConstraint(1)).thenReturn(0.0);

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2, solution3) ;

    assertEquals(1.0/3, feasibilityRatio(solutionList), 0.00000000001) ;
  }
}
