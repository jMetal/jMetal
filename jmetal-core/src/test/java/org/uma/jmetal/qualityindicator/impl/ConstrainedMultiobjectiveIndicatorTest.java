package org.uma.jmetal.qualityindicator.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class ConstrainedMultiobjectiveIndicator")
class ConstrainedMultiobjectiveIndicatorTest {

  private static final double EPSILON = 1e-12;

  /** Ideal point (0, 0) and nadir point (1, 1). */
  private static final double[][] REFERENCE_FRONT = {{0.0, 1.0}, {1.0, 0.0}};

  private ConstrainedMultiobjectiveIndicator indicator;

  @BeforeEach
  void setUp() {
    indicator = new ConstrainedMultiobjectiveIndicator(REFERENCE_FRONT);
  }

  private static DoubleSolution solution(double f1, double f2, double constraint) {
    DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 1);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = constraint;
    return solution;
  }

  @Nested
  @DisplayName("When all the solutions are feasible")
  class FeasibleSolutions {

    @Test
    @DisplayName("given a point that dominates the nadir point, then the value is minus the hypervolume")
    void givenAPointDominatingTheNadir_whenComputed_thenItIsMinusTheHypervolume() {
      // Act
      double value = indicator.compute(new double[][] {{0.5, 0.5}});

      // Assert
      assertEquals(-0.25, value, EPSILON);
    }

    @Test
    @DisplayName("given no point that dominates the nadir point, then the value is the distance to the region of interest")
    void givenNoPointDominatingTheNadir_whenComputed_thenItIsTheDistanceToTheRegionOfInterest() {
      // Act
      double value = indicator.compute(new double[][] {{1.5, 0.5}, {1.3, 1.4}});

      // Assert
      assertEquals(0.5, value, EPSILON);
    }

    @Test
    @DisplayName("given a front far from the region of interest, then the value is capped at the threshold")
    void givenAFarFront_whenComputed_thenTheValueIsTheThreshold() {
      // Act
      double value = indicator.compute(new double[][] {{3.0, 3.0}});

      // Assert
      assertEquals(ConstrainedMultiobjectiveIndicator.DEFAULT_THRESHOLD, value, EPSILON);
    }

    @Test
    @DisplayName("given a reference front with other bounds, then the objectives are normalized with them")
    void givenAScaledReferenceFront_whenComputed_thenTheObjectivesAreNormalized() {
      // Arrange
      var scaled = new ConstrainedMultiobjectiveIndicator(new double[][] {{0.0, 10.0}, {10.0, 0.0}});

      // Act
      double value = scaled.compute(new double[][] {{5.0, 5.0}});

      // Assert
      assertEquals(-0.25, value, EPSILON);
    }
  }

  @Nested
  @DisplayName("When some solutions are infeasible")
  class InfeasibleSolutions {

    @Test
    @DisplayName("given feasible and infeasible points, then only the feasible ones count")
    void givenFeasibleAndInfeasiblePoints_whenComputed_thenOnlyTheFeasibleOnesCount() {
      // Act: the infeasible point would dominate the whole region of interest
      double value =
          indicator.compute(new double[][] {{0.5, 0.5}, {0.0, 0.0}}, new double[] {0.0, 0.3});

      // Assert
      assertEquals(-0.25, value, EPSILON);
    }

    @Test
    @DisplayName("given only infeasible points, then the value is the least violation plus the threshold")
    void givenOnlyInfeasiblePoints_whenComputed_thenItIsTheLeastViolationPlusTheThreshold() {
      // Act
      double value =
          indicator.compute(new double[][] {{0.0, 0.0}, {0.5, 0.5}}, new double[] {0.7, 0.3});

      // Assert
      assertEquals(1.3, value, EPSILON);
    }

    @Test
    @DisplayName("given any feasible and any infeasible set, then the feasible one is better")
    void givenAFeasibleAndAnInfeasibleSet_whenCompared_thenTheFeasibleOneIsBetter() {
      // Act
      double worstFeasible = indicator.compute(new double[][] {{100.0, 100.0}});
      double bestInfeasible = indicator.compute(new double[][] {{0.0, 0.0}}, new double[] {1e-9});

      // Assert: a feasible set is at most the threshold, an infeasible one above it
      assertEquals(ConstrainedMultiobjectiveIndicator.DEFAULT_THRESHOLD, worstFeasible, EPSILON);
      assertTrue(worstFeasible < bestInfeasible);
    }

    @Test
    @DisplayName("given solutions, then their constraints give their violations")
    void givenSolutions_whenComputed_thenTheirConstraintsAreUsed() {
      // Arrange: jMetal's constraints are violated when negative
      List<DoubleSolution> solutions = List.of(solution(0.0, 0.0, -0.4), solution(0.2, 0.2, -0.1));

      // Act
      double value = indicator.compute(solutions);

      // Assert
      assertEquals(1.1, value, EPSILON);
    }
  }

  @Nested
  @DisplayName("When the indicator is configured")
  class Configuration {

    @Test
    @DisplayName("given a new instance, then it keeps the threshold and takes a reference front later")
    void givenAnIndicator_whenANewInstanceIsCreated_thenItKeepsTheThreshold() {
      // Arrange
      var custom = new ConstrainedMultiobjectiveIndicator(REFERENCE_FRONT, 2.0);

      // Act
      var copy = (ConstrainedMultiobjectiveIndicator) custom.newInstance();
      copy.referenceFront(REFERENCE_FRONT);

      // Assert
      assertEquals(2.0, copy.threshold(), EPSILON);
      assertEquals(2.5, copy.compute(new double[][] {{0.5, 0.5}}, new double[] {0.5}), EPSILON);
    }

    @Test
    @DisplayName("given no reference front, then computing the indicator fails")
    void givenNoReferenceFront_whenComputed_thenItFails() {
      // Act and Assert
      assertThrows(
          InvalidConditionException.class,
          () -> new ConstrainedMultiobjectiveIndicator().compute(new double[][] {{0.5, 0.5}}));
    }

    @Test
    @DisplayName("given a non-positive threshold, then the indicator is rejected")
    void givenANonPositiveThreshold_whenCreated_thenItFails() {
      // Act and Assert
      assertThrows(
          InvalidConditionException.class,
          () -> new ConstrainedMultiobjectiveIndicator(REFERENCE_FRONT, 0.0));
    }
  }
}
