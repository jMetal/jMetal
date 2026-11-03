package org.uma.jmetal.util.densityestimator.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class SpatialSpreadDeviationDensityEstimator")
class SpatialSpreadDeviationDensityEstimatorTest {
  private SpatialSpreadDeviationDensityEstimator<DoubleSolution> subject;

  @BeforeEach
  void setUp() {
    subject = new SpatialSpreadDeviationDensityEstimator<>();
  }

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
  @DisplayName("When the list has few solutions")
  class WhenTheListHasFewSolutions {
    @Test
    @DisplayName("given an empty list, when computed, then nothing happens")
    void givenAnEmptyList_whenComputed_thenNothingHappens() {
      subject.compute(new ArrayList<>());
    }

    @Test
    @DisplayName("given as many solutions as objectives, when computed, then all are isolated")
    void givenAsManySolutionsAsObjectives_whenComputed_thenAllAreIsolated() {
      List<DoubleSolution> front = front(new double[] {0.0, 1.0}, new double[] {1.0, 0.0});

      subject.compute(front);

      assertThat(front).allSatisfy(s -> assertThat(subject.value(s)).isEqualTo(Double.POSITIVE_INFINITY));
    }
  }

  @Nested
  @DisplayName("When the list has degenerate fronts")
  class WhenTheListHasDegenerateFronts {
    @Test
    @DisplayName("given a front, when computed, then the extremes are infinite and the rest finite")
    void givenAFront_whenComputed_thenTheExtremesAreInfiniteAndTheRestFinite() {
      List<DoubleSolution> front =
          front(
              new double[] {0.0, 1.0},
              new double[] {0.25, 0.7},
              new double[] {0.5, 0.45},
              new double[] {0.75, 0.2},
              new double[] {1.0, 0.0});

      subject.compute(front);

      assertThat(subject.value(front.get(0))).isEqualTo(Double.POSITIVE_INFINITY);
      assertThat(subject.value(front.get(4))).isEqualTo(Double.POSITIVE_INFINITY);
      for (int i = 1; i < 4; i++) {
        assertThat(Double.isFinite(subject.value(front.get(i)))).as("solution %d", i).isTrue();
      }
    }

    @Test
    @DisplayName("given duplicated solutions, when computed, then no value is NaN")
    void givenDuplicatedSolutions_whenComputed_thenNoValueIsNaN() {
      List<DoubleSolution> front =
          front(
              new double[] {0.0, 1.0},
              new double[] {0.5, 0.5},
              new double[] {0.5, 0.5},
              new double[] {1.0, 0.0});

      subject.compute(front);

      assertThat(front).allSatisfy(s -> assertThat(subject.value(s)).isNotNaN());
    }

    @Test
    @DisplayName("given an objective that is constant, when computed, then no value is NaN")
    void givenAConstantObjective_whenComputed_thenNoValueIsNaN() {
      List<DoubleSolution> front =
          front(
              new double[] {0.0, 1.0},
              new double[] {0.3, 1.0},
              new double[] {0.6, 1.0},
              new double[] {1.0, 1.0});

      subject.compute(front);

      assertThat(front).allSatisfy(s -> assertThat(subject.value(s)).isNotNaN());
    }
  }
}
