package org.uma.jmetal.component.catalogue.common.evaluation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class MultiThreadedEvaluationTest {

  @Test
  void invokeTheConstructorWithANullProblemRaisesAnException() {
    assertThatThrownBy(() -> new MultiThreadedEvaluation<>(4, null)).isInstanceOf(
        NullParameterException.class);
  }

  @Test
  void invokeTheConstructorWithANegativeNumberOfThreadsRaisesAnExceptioin() {
    assertThatThrownBy(
        () -> new MultiThreadedEvaluation<>(-1, new FakeDoubleProblem(1, 1, 1))).isInstanceOf(
        InvalidConditionException.class).
        hasMessageContaining("The number of threads is a negative value: -1");
  }

  @Test
  void TheConstructorInitializesTheNumberOfComputedEvaluationsAndTheNumberOfThreads() {
    var problem = mock(DoubleProblem.class);
    var evaluation = new MultiThreadedEvaluation<DoubleSolution>(1, problem);

    assertThat(evaluation.getComputedEvaluations()).isZero();
    assertThat(evaluation.getNumberOfThreads()).isEqualTo(1) ;
  }

  @Test
  void TheConstructorInitializesTheNumberOfThreadsIfThisValueIsGreaterThanZero() {
    var numberOfThreads = 8 ;
    var problem = mock(DoubleProblem.class);
    var evaluation = new MultiThreadedEvaluation<DoubleSolution>(numberOfThreads, problem);

    assertThat(evaluation.getNumberOfThreads()).isEqualTo(numberOfThreads) ;
  }

  @Test
  void TheConstructorInitializesTheNumberOfThreadsToTheDefaultValueIfTheParameterIsZero() {
    var numberOfThreads = 0 ;
    var problem = mock(DoubleProblem.class);
    var evaluation = new MultiThreadedEvaluation<DoubleSolution>(numberOfThreads, problem);

    assertThat(evaluation.getNumberOfThreads()).isEqualTo(Runtime.getRuntime().availableProcessors()) ;
  }

  @Test
  void evaluateANullListRaisesAnException() {
    var problem = mock(DoubleProblem.class);
    Evaluation<DoubleSolution> evaluation = new MultiThreadedEvaluation<>(4, problem);

    assertThatThrownBy(() -> evaluation.evaluate(null)).isInstanceOf(NullParameterException.class);
  }

  @Test
  void evaluateAnEmptyListDoesNotIncrementTheNumberOfComputedEvaluations() {
    var problem = mock(DoubleProblem.class);
    Evaluation<DoubleSolution> evaluation = new MultiThreadedEvaluation<>(2, problem);

    evaluation.evaluate(new ArrayList<>());

    assertThat(evaluation.getComputedEvaluations()).isZero();
  }

  @Test
  void evaluateAnEmptyListWithASolutionWorksProperly() {
    var problem = mock(DoubleProblem.class);
    Evaluation<DoubleSolution> evaluation = new MultiThreadedEvaluation<>(4, problem);

    evaluation.evaluate(List.of(mock(DoubleSolution.class)));

    assertThat(evaluation.getComputedEvaluations()).isEqualTo(1);
    verify(problem, times(1)).evaluate(Mockito.any());
  }

  @Test
  void evaluateAnListWithNSolutionsWorksProperly() {
    var problem = mock(DoubleProblem.class);
    Evaluation<DoubleSolution> evaluation = new MultiThreadedEvaluation<>(4,problem);

    var numberOfSolutions = 10;
    List<DoubleSolution> solutions = new ArrayList<>(numberOfSolutions);
      for (var i = 0; i < numberOfSolutions; i++) {
          solutions.add(mock(DoubleSolution.class));
      }

      evaluation.evaluate(solutions);

    assertThat(evaluation.getComputedEvaluations()).isEqualTo(numberOfSolutions);
    verify(problem, times(numberOfSolutions)).evaluate(Mockito.any());
  }
}