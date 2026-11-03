package org.uma.jmetal.util.densityestimator.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class StrenghtRawFitnessDensityEstimator (SPEA2 fitness)")
class StrenghtRawFitnessDensityEstimatorTest {

  private static List<DoubleSolution> front(double[]... objectives) {
    List<DoubleSolution> front = new ArrayList<>();
    for (double[] values : objectives) {
      DoubleSolution solution = new FakeDoubleProblem(2, values.length, 0).createSolution();
      System.arraycopy(values, 0, solution.objectives(), 0, values.length);
      front.add(solution);
    }
    return front;
  }

  @Nested
  @DisplayName("When the fitness is computed")
  class WhenTheFitnessIsComputed {
    @Test
    @DisplayName("given a chain of dominated solutions, when computed, then fitness is raw fitness plus density")
    void givenAChainOfSolutions_whenComputed_thenTheFitnessIsTheRawFitnessPlusTheDensity() {
      // Arrange: a dominates b and c, and b dominates c; the strengths are 2, 1 and 0, so the
      // raw fitness is 0 for a, 2 for b (a) and 3 for c (a + b). With k = 1, the distance to the
      // nearest neighbor is sqrt(2) for the three of them
      List<DoubleSolution> solutions =
          front(new double[] {1.0, 1.0}, new double[] {2.0, 2.0}, new double[] {3.0, 3.0});
      var subject = new StrenghtRawFitnessDensityEstimator<DoubleSolution>(1);
      double density = 1.0 / (Math.sqrt(2.0) + 2.0);

      // Act
      subject.compute(solutions);

      // Assert
      assertThat(subject.value(solutions.get(0))).isCloseTo(0.0 + density, org.assertj.core.data.Offset.offset(1e-12));
      assertThat(subject.value(solutions.get(1))).isCloseTo(2.0 + density, org.assertj.core.data.Offset.offset(1e-12));
      assertThat(subject.value(solutions.get(2))).isCloseTo(3.0 + density, org.assertj.core.data.Offset.offset(1e-12));
    }

    @Test
    @DisplayName("given a non-dominated front, when computed, then the isolated solution has the lowest fitness")
    void givenANonDominatedFront_whenComputed_thenTheIsolatedSolutionIsTheBest() {
      // Arrange: the raw fitness is 0 for all, so only the density counts
      List<DoubleSolution> solutions =
          front(new double[] {0.0, 1.0}, new double[] {0.5, 0.5}, new double[] {0.52, 0.48});
      var subject = new StrenghtRawFitnessDensityEstimator<DoubleSolution>(1);

      // Act
      subject.compute(solutions);

      // Assert: lower is better, and the isolated (0, 1) is the best
      assertThat(subject.value(solutions.get(0))).isLessThan(subject.value(solutions.get(1)));
      assertThat(subject.value(solutions.get(0))).isLessThan(subject.value(solutions.get(2)));
    }

    @Test
    @DisplayName("given as many solutions as k, when computed, then an exception names the problem")
    void givenTooFewSolutions_whenComputed_thenAnExceptionIsRaised() {
      List<DoubleSolution> solutions = front(new double[] {0.0, 1.0}, new double[] {1.0, 0.0});
      var subject = new StrenghtRawFitnessDensityEstimator<DoubleSolution>(2);

      assertThrows(InvalidConditionException.class, () -> subject.compute(solutions));
    }
  }
}
