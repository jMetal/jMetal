package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByMutualDominationRate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NegativeValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class TerminationByMutualDominationRate")
class TerminationByMutualDominationRateTest {
  private static final double[][] FRONT = {{0.2, 0.8}, {0.5, 0.5}, {0.8, 0.2}};
  private static final double[][] BETTER_FRONT = {{0.1, 0.7}, {0.4, 0.4}, {0.7, 0.1}};

  private TerminationByMutualDominationRate termination;

  @BeforeEach
  void setUp() {
    termination = new TerminationByMutualDominationRate(0.12);
  }

  private static Map<String, Object> statusWith(double[][] front) {
    List<DoubleSolution> population =
        Arrays.stream(front)
            .map(
                objectives -> {
                  DoubleSolution solution =
                      new DefaultDoubleSolution(
                          List.of(Bounds.create(0.0, 1.0)), objectives.length, 0);
                  System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
                  return solution;
                })
            .toList();
    Map<String, Object> status = new HashMap<>();
    status.put("POPULATION", population);
    return status;
  }

  @Nested
  @DisplayName("When it is created")
  class WhenItIsCreated {
    @Test
    @DisplayName("given only a threshold, then it takes the default noises")
    void givenOnlyAThreshold_thenItTakesTheDefaultNoises() {
      assertThat(termination.threshold()).isEqualTo(0.12);
      assertThat(termination.observationNoise())
          .isEqualTo(TerminationByMutualDominationRate.DEFAULT_OBSERVATION_NOISE);
      assertThat(termination.processNoise()).isZero();
      assertThat(termination.estimate()).isEqualTo(1.0);
      assertThat(termination.lastMutualDominationRate()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 1.0, -0.1, 1.5})
    @DisplayName("given a threshold out of (0, 1), then it raises an exception")
    void givenAThresholdOutOfRange_thenItRaisesAnException(double threshold) {
      assertThatThrownBy(() -> new TerminationByMutualDominationRate(threshold))
          .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given an observation noise that is not positive, then it raises an exception")
    void givenANonPositiveObservationNoise_thenItRaisesAnException() {
      assertThatThrownBy(() -> new TerminationByMutualDominationRate(0.1, 0.0, 0.0))
          .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a negative process noise, then it raises an exception")
    void givenANegativeProcessNoise_thenItRaisesAnException() {
      assertThatThrownBy(() -> new TerminationByMutualDominationRate(0.1, 0.05, -0.01))
          .isInstanceOf(NegativeValueException.class);
    }
  }

  @Nested
  @DisplayName("When it is checked")
  class WhenItIsChecked {
    @Test
    @DisplayName("given a status without population, then it raises an exception")
    void givenAStatusWithoutPopulation_thenItRaisesAnException() {
      assertThatThrownBy(() -> termination.isMet(new HashMap<>()))
          .isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given the first check, then it only stores the population")
    void givenTheFirstCheck_thenItOnlyStoresThePopulation() {
      assertThat(termination.isMet(statusWith(FRONT))).isFalse();

      assertThat(termination.lastMutualDominationRate()).isEmpty();
      assertThat(termination.estimate()).isEqualTo(1.0);
    }

    @ParameterizedTest
    @CsvSource({"BETTER, 1.0", "SAME, 0.0", "WORSE, -1.0"})
    @DisplayName("given two populations, when the second is checked, then the MDR is the expected")
    void givenTwoPopulations_whenTheSecondIsChecked_thenTheMdrIsTheExpected(
        String change, double expectedMutualDominationRate) {
      double[][] first = change.equals("WORSE") ? BETTER_FRONT : FRONT;
      double[][] second = change.equals("BETTER") ? BETTER_FRONT : FRONT;

      termination.isMet(statusWith(first));
      termination.isMet(statusWith(second));

      assertThat(termination.lastMutualDominationRate()).hasValue(expectedMutualDominationRate);
    }

    @Test
    @DisplayName("given a population that dominates part of the previous one, then the MDR is the fraction")
    void givenAPartialImprovement_thenTheMdrIsTheFraction() {
      double[][] partiallyBetter = {{0.1, 0.7}, {0.5, 0.5}, {0.8, 0.2}};

      termination.isMet(statusWith(FRONT));
      termination.isMet(statusWith(partiallyBetter));

      // {0.1, 0.7} dominates {0.2, 0.8}; nothing of the new population is dominated
      assertThat(termination.lastMutualDominationRate().getAsDouble())
          .isCloseTo(1.0 / 3.0, within(1e-12));
    }

    @Test
    @DisplayName("given the objectives of the population change after a check, then the stored ones do not")
    void givenTheObjectivesChangeAfterACheck_thenTheStoredOnesDoNot() {
      Map<String, Object> status = statusWith(FRONT);
      termination.isMet(status);
      @SuppressWarnings("unchecked")
      List<DoubleSolution> population = (List<DoubleSolution>) status.get("POPULATION");
      population.forEach(solution -> Arrays.fill(solution.objectives(), 0.0));

      termination.isMet(statusWith(FRONT));

      assertThat(termination.lastMutualDominationRate()).hasValue(0.0);
    }
  }

  @Nested
  @DisplayName("When the estimate is updated")
  class WhenTheEstimateIsUpdated {
    @Test
    @DisplayName("given no process noise, then the estimate is the mean of 1 and the MDR values")
    void givenNoProcessNoise_thenTheEstimateIsTheMeanOfTheInitialEstimateAndTheMdrValues() {
      double[][][] fronts = {FRONT, BETTER_FRONT, BETTER_FRONT, FRONT, FRONT};
      double[] mutualDominationRates = {1.0, 0.0, -1.0, 0.0};

      termination.isMet(statusWith(fronts[0]));
      double sum = 1.0;
      for (int i = 1; i < fronts.length; i++) {
        termination.isMet(statusWith(fronts[i]));
        sum += mutualDominationRates[i - 1];

        assertThat(termination.estimate()).isCloseTo(sum / (i + 1), within(1e-12));
      }
    }

    @Test
    @DisplayName("given a population that does not change, then it stops when 1 / (n + 1) is below the threshold")
    void givenAPopulationThatDoesNotChange_thenItStopsWhenTheMeanIsBelowTheThreshold() {
      termination.isMet(statusWith(FRONT));

      int comparisons = 0;
      boolean stop = false;
      while (!stop) {
        stop = termination.isMet(statusWith(FRONT));
        comparisons++;
      }

      // 1/8 = 0.125 is above 0.12 and 1/9 is below it
      assertThat(comparisons).isEqualTo(8);
    }

    @Test
    @DisplayName("given a positive process noise, then the recent MDR values weigh more")
    void givenAPositiveProcessNoise_thenTheRecentMdrValuesWeighMore() {
      TerminationByMutualDominationRate withProcessNoise =
          new TerminationByMutualDominationRate(0.12, 0.05, 0.05);
      double[][][] fronts = {FRONT, BETTER_FRONT, BETTER_FRONT, BETTER_FRONT, BETTER_FRONT};

      for (double[][] front : fronts) {
        termination.isMet(statusWith(front));
        withProcessNoise.isMet(statusWith(front));
      }

      assertThat(withProcessNoise.estimate()).isLessThan(termination.estimate());
    }
  }
}
