package org.uma.jmetal.util.evaluator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.evaluator.impl.MultiThreadedSolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

@DisplayName("Unit tests for the check of evaluated solutions")
class EvaluationCheckTest {

  /** A problem with 2 objectives and 1 constraint whose evaluation sets the given values. */
  private static class SettingProblem extends FakeDoubleProblem {
    private final transient Consumer<DoubleSolution> values;

    SettingProblem(Consumer<DoubleSolution> values) {
      super(2, 2, 1);
      name("SettingProblem");
      this.values = values;
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      values.accept(solution);
      return solution;
    }
  }

  private static DoubleSolution evaluated(Consumer<DoubleSolution> values) {
    var problem = new SettingProblem(values);
    return problem.evaluate(problem.createSolution());
  }

  static Stream<Arguments> nanValues() {
    return Stream.of(
        Arguments.of("an objective", (Consumer<DoubleSolution>) s -> s.objectives()[1] = Double.NaN),
        Arguments.of("a constraint", (Consumer<DoubleSolution>) s -> s.constraints()[0] = Double.NaN),
        Arguments.of(
            "the precomputed overall constraint violation degree",
            (Consumer<DoubleSolution>)
                s -> ConstraintHandling.overallConstraintViolationDegree(s, Double.NaN)));
  }

  static Stream<Arguments> evaluators() {
    return Stream.of(
        Arguments.of("sequential", new SequentialSolutionListEvaluator<DoubleSolution>()),
        Arguments.of("multithreaded", new MultiThreadedSolutionListEvaluator<DoubleSolution>(2)));
  }

  @Nested
  @DisplayName("When checking a solution")
  class SolutionTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.util.evaluator.EvaluationCheckTest#nanValues")
    @DisplayName("given a NaN value, then the check fails naming the problem")
    void givenANaNValue_whenChecked_thenItFailsNamingTheProblem(
        String value, Consumer<DoubleSolution> values) {
      // Arrange
      DoubleSolution solution = evaluated(values);
      Problem<DoubleSolution> problem = new SettingProblem(values);

      // Act & Assert
      assertThatThrownBy(() -> EvaluationCheck.noNaNValues(solution, problem))
          .isInstanceOf(InvalidConditionException.class)
          .hasMessageContaining("SettingProblem");
    }

    @Test
    @DisplayName("given finite and infinite values, then the check passes")
    void givenFiniteAndInfiniteValues_whenChecked_thenItPasses() {
      // Arrange
      Consumer<DoubleSolution> values =
          s -> {
            s.objectives()[0] = Double.POSITIVE_INFINITY;
            s.objectives()[1] = 0.5;
            s.constraints()[0] = Double.NEGATIVE_INFINITY;
          };
      DoubleSolution solution = evaluated(values);

      // Act & Assert
      assertThatCode(() -> EvaluationCheck.noNaNValues(solution, new SettingProblem(values)))
          .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("given an object that is not a solution, then it is not checked")
    void givenAnObjectThatIsNotASolution_whenChecked_thenItPasses() {
      // Act & Assert
      assertThatCode(() -> EvaluationCheck.noNaNValues("not a solution", new SettingProblem(s -> {})))
          .doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("When a solution list evaluator evaluates a solution to NaN")
  class SolutionListEvaluatorTests {

    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.util.evaluator.EvaluationCheckTest#evaluators")
    @DisplayName("given a problem that returns NaN, then the evaluation fails")
    void givenAProblemThatReturnsNaN_whenEvaluated_thenItFails(
        String name, SolutionListEvaluator<DoubleSolution> evaluator) {
      // Arrange
      var problem = new SettingProblem(s -> s.objectives()[0] = Double.NaN);
      List<DoubleSolution> solutions = List.of(problem.createSolution(), problem.createSolution());

      // Act & Assert
      assertThatThrownBy(() -> evaluator.evaluate(solutions, problem))
          .isInstanceOf(InvalidConditionException.class)
          .hasMessageContaining("SettingProblem");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.util.evaluator.EvaluationCheckTest#evaluators")
    @DisplayName("given a problem that returns numbers, then the evaluation succeeds")
    void givenAProblemThatReturnsNumbers_whenEvaluated_thenItSucceeds(
        String name, SolutionListEvaluator<DoubleSolution> evaluator) {
      // Arrange
      var problem = new SettingProblem(s -> s.objectives()[0] = 1.0);
      List<DoubleSolution> solutions = List.of(problem.createSolution(), problem.createSolution());

      // Act & Assert
      assertThatCode(() -> evaluator.evaluate(solutions, problem)).doesNotThrowAnyException();
    }
  }
}
