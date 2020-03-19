package org.uma.jmetal.component.evaluation.impl;

import org.junit.Test;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public abstract class EvaluationTestCases<S extends Solution<?>> {

  protected Evaluation<S> evaluation;
  protected Problem<S> problem ;

  /** Case 1: the solution list is empty */
  @Test
  public void shouldACallToEvaluateComputeTheRightNumberOfEvaluationsCase1() {
    List<S> solutions = new ArrayList<>();

    evaluation.evaluate(solutions);

    assertEquals(0, evaluation.getComputedEvaluations());
  }

  /** Case 2: the solution list has one element */
  @Test
  public void shouldACallToEvaluateComputeTheRightNumberOfEvaluationsCase2() {
    List<S> solutions = new ArrayList<>();
    solutions.add(problem.createSolution());

    evaluation.evaluate(solutions);

    assertEquals(1, evaluation.getComputedEvaluations());
  }

  /** Case 3: the solution list has 20 elements and the evaluate() method is called twice */
  @Test
  public void shouldACallToEvaluateComputeTheRightNumberOfEvaluationsCase3() {
    List<S> solutions = new ArrayList<>();
    IntStream.range(0, 20).forEach(i -> solutions.add(problem.createSolution()));

    evaluation.evaluate(solutions);
    evaluation.evaluate(solutions);

    assertEquals(40, evaluation.getComputedEvaluations());
  }
}
