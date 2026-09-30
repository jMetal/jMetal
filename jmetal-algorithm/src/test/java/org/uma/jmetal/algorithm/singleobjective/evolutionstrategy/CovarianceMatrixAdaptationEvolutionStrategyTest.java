package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.singleobjective.Sphere;

@DisplayName("Unit tests for class CovarianceMatrixAdaptationEvolutionStrategy")
class CovarianceMatrixAdaptationEvolutionStrategyTest {

  private CovarianceMatrixAdaptationEvolutionStrategy algorithm;

  @BeforeEach
  void setUp() {
    algorithm = new CovarianceMatrixAdaptationEvolutionStrategy.Builder(new Sphere(10))
        .setMaxEvaluations(5000)
        .build();
  }

  @Nested
  @DisplayName("When solving the sphere problem")
  class WhenSolvingTheSphereProblem {

    @Test
    @DisplayName("given 5000 evaluations, when running, then the best value is close to zero")
    void given5000Evaluations_whenRunning_thenTheBestValueIsCloseToZero() {
      // Act
      algorithm.run();

      // Assert
      double bestValue = algorithm.result().objectives()[0];
      assertTrue(bestValue < 1e-20, "Best value: " + bestValue);
    }
  }
}
