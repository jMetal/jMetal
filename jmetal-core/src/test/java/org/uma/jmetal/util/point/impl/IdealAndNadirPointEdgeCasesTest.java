package org.uma.jmetal.util.point.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the edge cases of classes IdealPoint and NadirPoint")
class IdealAndNadirPointEdgeCasesTest {
  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 0);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  @Nested
  @DisplayName("When the objective values are negative")
  class WhenTheObjectiveValuesAreNegative {
    @Test
    @DisplayName("given only negative values, then the ideal point is their minimum")
    void givenOnlyNegativeValues_whenUpdatingTheIdealPoint_thenItIsTheirMinimum() {
      // Arrange
      var ideal = new IdealPoint(2);

      // Act
      ideal.update(new double[] {-3.0, -1.0});
      ideal.update(new double[] {-2.0, -5.0});

      // Assert
      assertThat(ideal.values()).containsExactly(-3.0, -5.0);
    }

    @Test
    @DisplayName("given only negative values, then the nadir point is their maximum, not zero")
    void givenOnlyNegativeValues_whenUpdatingTheNadirPoint_thenItIsTheirMaximum() {
      // Arrange
      var nadir = new NadirPoint(2);

      // Act
      nadir.update(new double[] {-3.0, -1.0});
      nadir.update(new double[] {-2.0, -5.0});

      // Assert
      assertThat(nadir.values()).containsExactly(-2.0, -1.0);
    }

    @Test
    @DisplayName("given values of both signs, then both points are the extremes")
    void givenValuesOfBothSigns_whenUpdating_thenBothPointsAreTheExtremes() {
      // Arrange
      var ideal = new IdealPoint(1);
      var nadir = new NadirPoint(1);

      // Act
      for (double value : new double[] {0.0, -2.5, 4.0, 1.0}) {
        ideal.update(new double[] {value});
        nadir.update(new double[] {value});
      }

      // Assert
      assertThat(ideal.values()).containsExactly(-2.5);
      assertThat(nadir.values()).containsExactly(4.0);
    }
  }

  @Nested
  @DisplayName("When updating with a list of solutions")
  class WhenUpdatingWithAListOfSolutions {
    private final List<DoubleSolution> solutions =
        List.of(solution(-1.0, 7.0), solution(-4.0, 9.0), solution(-2.0, 3.0));

    @Test
    @DisplayName("given a list, then the ideal point is the minimum of each objective")
    void givenAList_whenUpdatingTheIdealPoint_thenItIsTheMinimumOfEachObjective() {
      // Arrange
      var ideal = new IdealPoint(2);

      // Act
      ideal.update(solutions);

      // Assert
      assertThat(ideal.values()).containsExactly(-4.0, 3.0);
    }

    @Test
    @DisplayName("given an empty list, then the points do not change")
    void givenAnEmptyList_whenUpdating_thenThePointsDoNotChange() {
      // Arrange
      var ideal = new IdealPoint(2);
      var nadir = new NadirPoint(2);
      ideal.update(new double[] {1.0, 2.0});
      nadir.update(new double[] {1.0, 2.0});

      // Act
      ideal.update(List.<DoubleSolution>of());
      nadir.update(List.<DoubleSolution>of());

      // Assert
      assertThat(ideal.values()).containsExactly(1.0, 2.0);
      assertThat(nadir.values()).containsExactly(1.0, 2.0);
    }

    @Test
    @DisplayName("given the same solutions in another order, then the points are the same")
    void givenAnotherOrder_whenUpdating_thenThePointsAreTheSame() {
      // Arrange
      var first = new NadirPoint(2);
      var second = new NadirPoint(2);

      // Act
      first.update(solutions);
      second.update(List.of(solutions.get(2), solutions.get(0), solutions.get(1)));

      // Assert
      assertThat(first.values()).containsExactly(second.values());
    }
  }

  @Nested
  @DisplayName("When the dimension of the point to update with is wrong")
  class WhenTheDimensionIsWrong {
    @Test
    @DisplayName("given a point of another dimension, then it is rejected")
    void givenAPointOfAnotherDimension_whenUpdating_thenItIsRejected() {
      // Arrange
      var ideal = new IdealPoint(2);
      var nadir = new NadirPoint(2);

      // Act
      Throwable idealThrown = catchThrowable(() -> ideal.update(new double[] {1.0, 2.0, 3.0}));
      Throwable nadirThrown = catchThrowable(() -> nadir.update(new double[] {1.0}));

      // Assert
      assertThat(idealThrown).isInstanceOf(InvalidConditionException.class);
      assertThat(nadirThrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a point of another dimension, then the message gives both dimensions")
    void givenAPointOfAnotherDimension_whenUpdating_thenTheMessageGivesBothDimensions() {
      // Arrange
      var ideal = new IdealPoint(2);

      // Act
      Throwable thrown = catchThrowable(() -> ideal.update(new double[] {1.0, 2.0, 3.0}));

      // Assert
      assertThat(thrown).hasMessageContaining("3").hasMessageContaining("2");
    }

    @Test
    @DisplayName("given a point of another dimension, then the point is not modified")
    void givenAPointOfAnotherDimension_whenUpdating_thenThePointIsNotModified() {
      // Arrange
      var ideal = new IdealPoint(2);
      ideal.update(new double[] {1.0, 2.0});

      // Act
      catchThrowable(() -> ideal.update(new double[] {0.0, 0.0, 0.0}));

      // Assert
      assertThat(ideal.values()).containsExactly(1.0, 2.0);
    }
  }
}
