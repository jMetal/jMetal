package org.uma.jmetal.problem.multiobjective.fda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

@DisplayName("Unit tests for the FDA problems (Farina, Deb and Amato, 2004)")
class FDATest {

  /** The problem of a code of the reference file, with tau_T = 1 and n_T = 10 (t = tau / 10). */
  private static FDA problem(int code, int variables, int objectives, int sizeOfXI) {
    return switch (code) {
      case 1 -> new FDA1(variables, objectives, 1, 10);
      case 2 -> new FDA2(variables, objectives, 1, 10);
      case 21 -> new FDA2Mod(variables, objectives, 1, 10);
      case 3 -> new FDA3(variables, objectives, sizeOfXI, 1, 10);
      case 4 -> new FDA4(variables, objectives, 1, 10);
      case 5 -> new FDA5(variables, objectives, 1, 10);
      default -> throw new IllegalArgumentException("Unknown problem code " + code);
    };
  }

  /**
   * Rows of {@code fda/reference-values.csv}, computed with scripts/fda_reference_values.py, a
   * transcription of the papers in NumPy that agrees with pymoo in FDA4 and FDA5: problem code (21
   * is FDA2Mod), n, m, |x_I| of FDA3, counter tau, the n variables and the m objectives.
   */
  static Stream<Arguments> referenceValues() throws IOException {
    try (var reader =
        new BufferedReader(
            new InputStreamReader(
                FDATest.class.getResourceAsStream("/fda/reference-values.csv"),
                StandardCharsets.UTF_8))) {
      return reader
          .lines()
          .filter(line -> !line.startsWith("#") && !line.isBlank())
          .map(line -> Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray())
          .map(
              row ->
                  Arguments.of(
                      (int) row[0], (int) row[1], (int) row[2], (int) row[3], (int) row[4], row))
          .toList()
          .stream();
    }
  }

  @Nested
  @DisplayName("When a solution is evaluated")
  class WhenASolutionIsEvaluated {
    @ParameterizedTest(name = "code {0}, n = {1}, m = {2}, |x_I| = {3}, tau = {4}")
    @MethodSource("org.uma.jmetal.problem.multiobjective.fda.FDATest#referenceValues")
    @DisplayName("given the variables and the time of a reference row, then the objectives are its")
    void givenAReferenceRow_thenTheObjectivesAreItsObjectives(
        int code, int variables, int objectives, int sizeOfXI, int tau, double[] row) {
      FDA problem = problem(code, variables, objectives, sizeOfXI);
      problem.update(tau);
      DoubleSolution solution = problem.createSolution();
      IntStream.range(0, variables).forEach(i -> solution.variables().set(i, row[5 + i]));

      problem.evaluate(solution);

      double[] expected = Arrays.copyOfRange(row, 5 + variables, row.length);
      assertThat(solution.objectives()).hasSize(objectives);
      for (int j = 0; j < objectives; j++) {
        assertThat(solution.objectives()[j])
            .as("f%d", j + 1)
            .isCloseTo(expected[j], within(1e-12 * Math.max(1.0, Math.abs(expected[j]))));
      }
    }
  }

  @Nested
  @DisplayName("When it is created")
  class WhenItIsCreated {
    @ParameterizedTest
    @CsvSource({
      "FDA1, 20, 2, 1",
      "FDA2, 31, 2, 1",
      "FDA2Mod, 31, 2, 1",
      "FDA3, 30, 2, 5",
      "FDA4, 12, 3, 12",
      "FDA5, 12, 3, 12"
    })
    @DisplayName("given the default constructor, then it has the settings of the paper")
    void givenTheDefaultConstructor_thenItHasTheSettingsOfThePaper(
        String name, int variables, int objectives, int variablesInZeroOne)
        throws ReflectiveOperationException {
      FDA problem =
          (FDA)
              Class.forName("org.uma.jmetal.problem.multiobjective.fda." + name)
                  .getConstructor()
                  .newInstance();

      assertThat(problem.name()).isEqualTo(name);
      assertThat(problem.numberOfVariables()).isEqualTo(variables);
      assertThat(problem.numberOfObjectives()).isEqualTo(objectives);
      assertThat(problem.tauT()).isEqualTo(5);
      assertThat(problem.nT()).isEqualTo(10);
      for (int i = 0; i < variables; i++) {
        assertThat(problem.variableBounds().get(i).getLowerBound())
            .isEqualTo(i < variablesInZeroOne ? 0.0 : -1.0);
        assertThat(problem.variableBounds().get(i).getUpperBound()).isEqualTo(1.0);
      }
    }

    @Test
    @DisplayName("given the deprecated constructor of FDA1 with an observable, then it is the default one")
    @SuppressWarnings("deprecation")
    void givenTheDeprecatedConstructorOfFDA1_thenItIsTheDefaultOne() {
      FDA1 problem = new FDA1(new DefaultObservable<>("FDA1 observable"));

      assertThat(problem.numberOfVariables()).isEqualTo(20);
    }

    @Test
    @DisplayName("given FDA4 with five objectives, then it has five objectives")
    void givenFDA4WithFiveObjectives_thenItHasFiveObjectives() {
      assertThat(new FDA4(14, 5).numberOfObjectives()).isEqualTo(5);
    }

    @Test
    @DisplayName("given FDA1 with three objectives, then it raises an exception")
    void givenFDA1WithThreeObjectives_thenItRaisesAnException() {
      assertThatThrownBy(() -> new FDA1(20, 3)).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given FDA3 with no more variables than x_I, then it raises an exception")
    void givenFDA3WithNoMoreVariablesThanXI_thenItRaisesAnException() {
      assertThatThrownBy(() -> new FDA3(5, 2, 5)).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a tau_T that is not positive, then it raises an exception")
    void givenANonPositiveTauT_thenItRaisesAnException() {
      assertThatThrownBy(() -> new FDA1(20, 2, 0, 10))
          .isInstanceOf(InvalidConditionException.class);
    }
  }

  @Nested
  @DisplayName("When the counter is updated")
  class WhenTheCounterIsUpdated {
    private FDA problem;

    @BeforeEach
    void setUp() {
      problem = new FDA2();
    }

    @Test
    @DisplayName("given a counter that does not change t, then the problem has not changed")
    void givenACounterThatDoesNotChangeT_thenTheProblemHasNotChanged() {
      problem.update(4);

      assertThat(problem.time()).isZero();
      assertThat(problem.hasChanged()).isFalse();
    }

    @Test
    @DisplayName("given a counter that changes t, then t is floor(tau / tau_T) / n_T and the problem has changed")
    void givenACounterThatChangesT_thenTheProblemHasChanged() {
      problem.update(12);

      assertThat(problem.time()).isEqualTo(0.2);
      assertThat(problem.hasChanged()).isTrue();
    }

    @Test
    @DisplayName("given the change is cleared, when the counter does not change t, then the problem has not changed")
    void givenTheChangeIsCleared_whenTheCounterDoesNotChangeT_thenTheProblemHasNotChanged() {
      problem.update(5);
      problem.clearChanged();

      problem.update(9);

      assertThat(problem.time()).isEqualTo(0.1);
      assertThat(problem.hasChanged()).isFalse();
    }

    @Test
    @DisplayName("given a negative counter, then it raises an exception")
    void givenANegativeCounter_thenItRaisesAnException() {
      assertThatThrownBy(() -> problem.update(-1)).isInstanceOf(InvalidConditionException.class);
    }
  }
}
