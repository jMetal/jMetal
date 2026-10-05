package org.uma.jmetal.util.densityestimator.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;

/**
 * All the density estimators give a solution whose density has not been computed the neutral value
 * of their convention, instead of failing: 0.0 (the worst, higher is better) for all but the
 * niche distance, which is minimized and gives the worst one, {@link Double#MAX_VALUE}.
 */
@DisplayName("Density estimators: value of a solution without computed density")
class DensityWithoutComputationTest {

  private static DoubleSolution newSolution() {
    return new FakeDoubleProblem(2, 2, 0).createSolution();
  }

  @Test
  @DisplayName("Given a solution without density, when asking for its value, then it is 0.0")
  void givenASolutionWithoutDensity_whenAskingForItsValue_thenItIsZero() {
    List<DensityEstimator<DoubleSolution>> estimators =
        List.of(
            new AngleDensityEstimator<>(),
            new CosineSimilarityDensityEstimator<>(),
            new CrowdingDistanceDensityEstimator<>(),
            new GridDensityEstimator<>(5, 2),
            new HypervolumeContributionDensityEstimator<>(new double[] {1.0, 1.0}),
            new KnnDensityEstimator<>(2),
            new ShiftedDensityEstimator<>(),
            new SpatialSpreadDeviationDensityEstimator<>(),
            new StrenghtRawFitnessDensityEstimator<>(2));

    for (var estimator : estimators) {
      assertEquals(0.0, estimator.value(newSolution()), estimator.getClass().getSimpleName());
    }
  }

  @Test
  @DisplayName("Given a solution without niche distance, then its value is the worst one")
  void givenASolutionWithoutNicheDistance_thenItsValueIsTheWorstOne() {
    var estimator =
        new ReferencePointNicheDistanceEstimator<DoubleSolution>(
            List.of(new double[] {0.5, 0.5}), 2);

    assertEquals(Double.MAX_VALUE, estimator.value(newSolution()));
  }
}
