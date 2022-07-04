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
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class SequentialEvaluationTest {

  @Test
  void invokeTheConstructorWithANullProblemRaisesAnException() {
    assertThatThrownBy(() -> new SequentialEvaluation<>(null)).isInstanceOf(NullParameterException.class) ;
  }

  @Test
  void TheConstructorInitializesTheNumberOfComputedEvaluations() {
    DoubleProblem problem = mock(DoubleProblem.class) ;
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;

    assertThat(evaluation.getComputedEvaluations()).isZero() ;
  }

  @Test
  void evaluateANullListRaisesAnException() {
    DoubleProblem problem = mock(DoubleProblem.class) ;
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;

    assertThatThrownBy(() -> evaluation.evaluate(null)).isInstanceOf(NullParameterException.class) ;
  }

  @Test
  void evaluateAnEmptyListDoesNotIncrementTheNumberOfComputedEvaluations() {
    DoubleProblem problem = mock(DoubleProblem.class) ;
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;

    evaluation.evaluate(new ArrayList<>()) ;

    assertThat(evaluation.getComputedEvaluations()).isZero() ;
  }

  @Test
  void evaluateAnEmptyListWithASolutionWorksProperly() {
    DoubleProblem problem = mock(DoubleProblem.class) ;
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;

    evaluation.evaluate(List.of(mock(DoubleSolution.class))) ;

    assertThat(evaluation.getComputedEvaluations()).isEqualTo(1) ;
    verify(problem, times(1)).evaluate(Mockito.any()) ;
  }

  @Test
  void evaluateAnListWithNSolutionsWorksProperly() {
    DoubleProblem problem = mock(DoubleProblem.class) ;
    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;

    int numberOfSolutions = 10 ;
    List<DoubleSolution> solutions = new ArrayList<>(numberOfSolutions) ;
    IntStream.range(0,numberOfSolutions).forEach(i -> solutions.add(mock(DoubleSolution.class))) ;

    evaluation.evaluate(solutions) ;

    assertThat(evaluation.getComputedEvaluations()).isEqualTo(numberOfSolutions) ;
    verify(problem, times(numberOfSolutions)).evaluate(Mockito.any()) ;
  }
}