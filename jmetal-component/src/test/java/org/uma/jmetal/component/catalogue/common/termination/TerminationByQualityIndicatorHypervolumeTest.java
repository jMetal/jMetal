package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByQualityIndicator;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("TerminationByQualityIndicator with real indicators")
class TerminationByQualityIndicatorHypervolumeTest {
  private static final double[][] REFERENCE_FRONT = {{0.0, 1.0}, {0.5, 0.5}, {1.0, 0.0}};

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  private static Map<String, Object> status(List<DoubleSolution> population, int evaluations) {
    Map<String, Object> status = new HashMap<>();
    status.put("POPULATION", population);
    status.put("EVALUATIONS", evaluations);
    return status;
  }

  @Test
  @DisplayName("given a population on the reference front, when checked, then the hypervolume reaches the percentage and the run stops")
  void givenAPopulationOnTheReferenceFront_whenChecked_thenTheRunStops() {
    var termination =
        new TerminationByQualityIndicator(new PISAHypervolume(), REFERENCE_FRONT, 0.98, 1000);
    List<DoubleSolution> population =
        List.of(solution(0.0, 1.0), solution(0.5, 0.5), solution(1.0, 0.0));

    assertThat(termination.isMet(status(population, 10))).isTrue();
    assertThat(termination.getComputedIndicatorValue()).isGreaterThan(0.0);
  }

  @Test
  @DisplayName("given a population far from the reference front, when checked, then the run goes on")
  void givenAPopulationFarFromTheReferenceFront_whenChecked_thenTheRunGoesOn() {
    var termination =
        new TerminationByQualityIndicator(new PISAHypervolume(), REFERENCE_FRONT, 0.98, 1000);
    List<DoubleSolution> population = List.of(solution(1.0, 1.0), solution(0.9, 1.0));

    assertThat(termination.isMet(status(population, 10))).isFalse();
  }

  @Test
  @DisplayName("given the limit of evaluations, when it is reached, then the run stops")
  void givenTheLimitOfEvaluations_whenItIsReached_thenTheRunStops() {
    var termination =
        new TerminationByQualityIndicator(new PISAHypervolume(), REFERENCE_FRONT, 0.98, 1000);
    List<DoubleSolution> population = List.of(solution(1.0, 1.0), solution(0.9, 1.0));

    assertThat(termination.isMet(status(population, 1000))).isTrue();
  }

  @Test
  @DisplayName("given an indicator that is better when lower, when created, then it is rejected")
  void givenAnIndicatorThatIsBetterWhenLower_whenCreated_thenItIsRejected() {
    assertThrows(
        InvalidConditionException.class,
        () ->
            new TerminationByQualityIndicator(
                new InvertedGenerationalDistancePlus(), REFERENCE_FRONT, 0.98, 1000));
  }
}
