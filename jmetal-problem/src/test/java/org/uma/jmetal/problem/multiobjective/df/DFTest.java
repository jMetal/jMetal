package org.uma.jmetal.problem.multiobjective.df;

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
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the DF problems of the CEC 2018 dynamic competition")
class DFTest {

  private static DF problem(int number, int variables, int frequency, int severity)
      throws ReflectiveOperationException {
    return (DF)
        Class.forName("org.uma.jmetal.problem.multiobjective.df.DF" + number)
            .getConstructor(int.class, int.class, int.class)
            .newInstance(variables, frequency, severity);
  }

  private static DF problem(int number) throws ReflectiveOperationException {
    return (DF)
        Class.forName("org.uma.jmetal.problem.multiobjective.df.DF" + number)
            .getConstructor()
            .newInstance();
  }

  /**
   * Rows of {@code df/reference-values.csv}, computed with scripts/df_reference_values.py, a
   * transcription of the technical report in NumPy that agrees with pymoo where pymoo follows the
   * report: problem, number of variables n, counter tau (t = tau / 10 with tau_t = 1 and n_t = 10),
   * the n variables and the objectives.
   */
  static Stream<Arguments> referenceValues() throws IOException {
    try (var reader =
        new BufferedReader(
            new InputStreamReader(
                DFTest.class.getResourceAsStream("/df/reference-values.csv"),
                StandardCharsets.UTF_8))) {
      return reader
          .lines()
          .filter(line -> !line.startsWith("#") && !line.isBlank())
          .map(line -> Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray())
          .map(row -> Arguments.of((int) row[0], (int) row[1], (int) row[2], row))
          .toList()
          .stream();
    }
  }

  @Nested
  @DisplayName("When a solution is evaluated")
  class WhenASolutionIsEvaluated {
    @ParameterizedTest(name = "DF{0}, n = {1}, tau = {2}")
    @MethodSource("org.uma.jmetal.problem.multiobjective.df.DFTest#referenceValues")
    @DisplayName("given the variables and the time of a reference row, then the objectives are its")
    void givenAReferenceRow_thenTheObjectivesAreItsObjectives(
        int number, int variables, int tau, double[] row) throws ReflectiveOperationException {
      DF problem = problem(number, variables, 1, 10);
      problem.update(tau);
      DoubleSolution solution = problem.createSolution();
      IntStream.range(0, variables).forEach(i -> solution.variables().set(i, row[3 + i]));

      problem.evaluate(solution);

      double[] expected = Arrays.copyOfRange(row, 3 + variables, row.length);
      assertThat(solution.objectives()).hasSize(expected.length);
      for (int j = 0; j < expected.length; j++) {
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
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14})
    @DisplayName("given the default constructor, then it has the settings of the report")
    void givenTheDefaultConstructor_thenItHasTheSettingsOfTheReport(int number)
        throws ReflectiveOperationException {
      DF problem = problem(number);

      assertThat(problem.name()).isEqualTo("DF" + number);
      assertThat(problem.numberOfVariables()).isEqualTo(10);
      assertThat(problem.numberOfObjectives()).isEqualTo(number <= 9 ? 2 : 3);
      assertThat(problem.frequencyOfChange()).isEqualTo(10);
      assertThat(problem.severityOfChange()).isEqualTo(10);
      assertThat(problem.time()).isZero();
      assertThat(problem.hasChanged()).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
      "1, 0, 0.0, 1.0, 0.0, 1.0",
      "3, 1, 0.0, 1.0, -1.0, 2.0",
      "4, 0, -2.0, 2.0, -2.0, 2.0",
      "7, 1, 1.0, 4.0, 0.0, 1.0",
      "9, 1, 0.0, 1.0, -1.0, 1.0",
      "11, 0, 0.0, 1.0, 0.0, 1.0",
      "12, 2, 0.0, 1.0, -1.0, 1.0"
    })
    @DisplayName("given a problem, then its variables have the bounds of the report")
    void givenAProblem_thenItsVariablesHaveTheBoundsOfTheReport(
        int number, int firstVariables, double firstLower, double firstUpper, double restLower,
        double restUpper) throws ReflectiveOperationException {
      var bounds = problem(number).variableBounds();

      for (int i = 0; i < bounds.size(); i++) {
        boolean first = i < firstVariables;
        assertThat(bounds.get(i).getLowerBound()).isEqualTo(first ? firstLower : restLower);
        assertThat(bounds.get(i).getUpperBound()).isEqualTo(first ? firstUpper : restUpper);
      }
    }

    @Test
    @DisplayName("given fewer variables than objectives, then it raises an exception")
    void givenFewerVariablesThanObjectives_thenItRaisesAnException() {
      assertThatThrownBy(() -> new DF10(2, 10, 10)).isInstanceOf(InvalidConditionException.class);
    }

    @ParameterizedTest
    @CsvSource({"0, 10", "10, 0", "-1, 10"})
    @DisplayName("given a frequency or severity of change that is not positive, then it raises an exception")
    void givenANonPositiveFrequencyOrSeverity_thenItRaisesAnException(
        int frequency, int severity) {
      assertThatThrownBy(() -> new DF1(10, frequency, severity))
          .isInstanceOf(InvalidConditionException.class);
    }
  }

  @Nested
  @DisplayName("When the counter is updated")
  class WhenTheCounterIsUpdated {
    private DF problem;

    @BeforeEach
    void setUp() {
      problem = new DF1();
    }

    @Test
    @DisplayName("given a counter that does not change t, then the problem has not changed")
    void givenACounterThatDoesNotChangeT_thenTheProblemHasNotChanged() {
      problem.update(9);

      assertThat(problem.time()).isZero();
      assertThat(problem.hasChanged()).isFalse();
    }

    @Test
    @DisplayName("given a counter that changes t, then t is floor(tau / tau_t) / n_t and the problem has changed")
    void givenACounterThatChangesT_thenTheProblemHasChanged() {
      problem.update(25);

      assertThat(problem.time()).isEqualTo(0.2);
      assertThat(problem.hasChanged()).isTrue();
    }

    @Test
    @DisplayName("given the change is cleared, when the counter does not change t, then the problem has not changed")
    void givenTheChangeIsCleared_whenTheCounterDoesNotChangeT_thenTheProblemHasNotChanged() {
      problem.update(10);
      problem.clearChanged();

      problem.update(19);

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
