package org.uma.jmetal.component.catalogue.common.evaluation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.MultiThreadedEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the evaluations when a problem returns NaN")
class NaNEvaluationTest {

  /** A problem whose second objective is NaN. */
  private static class NaNProblem extends FakeDoubleProblem {
    NaNProblem() {
      super(2, 2, 0);
      name("NaNProblem");
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      solution.objectives()[1] = Double.NaN;
      return solution;
    }
  }

  static Stream<Arguments> evaluations() {
    return Stream.of(
        Arguments.of(
            "sequential",
            (Function<Problem<DoubleSolution>, Evaluation<DoubleSolution>>) SequentialEvaluation::new),
        Arguments.of(
            "sequential with archive",
            (Function<Problem<DoubleSolution>, Evaluation<DoubleSolution>>)
                problem ->
                    new SequentialEvaluationWithArchive<>(
                        problem, new NonDominatedSolutionListArchive<>())),
        Arguments.of(
            "multithreaded",
            (Function<Problem<DoubleSolution>, Evaluation<DoubleSolution>>)
                problem -> new MultiThreadedEvaluation<>(2, problem)),
        Arguments.of(
            "multithreaded with archive",
            (Function<Problem<DoubleSolution>, Evaluation<DoubleSolution>>)
                problem ->
                    new MultiThreadedEvaluationWithArchive<>(
                        2, problem, new NonDominatedSolutionListArchive<>())));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("evaluations")
  @DisplayName("given a problem that returns NaN, when evaluating, then the evaluation fails")
  void givenAProblemThatReturnsNaN_whenEvaluating_thenTheEvaluationFails(
      String name, Function<Problem<DoubleSolution>, Evaluation<DoubleSolution>> factory) {
    // Arrange
    var problem = new NaNProblem();
    Evaluation<DoubleSolution> evaluation = factory.apply(problem);
    List<DoubleSolution> solutions = List.of(problem.createSolution(), problem.createSolution());

    // Act & Assert
    assertThatThrownBy(() -> evaluation.evaluate(solutions))
        .isInstanceOf(InvalidConditionException.class)
        .hasMessageContaining("NaNProblem");
  }
}
