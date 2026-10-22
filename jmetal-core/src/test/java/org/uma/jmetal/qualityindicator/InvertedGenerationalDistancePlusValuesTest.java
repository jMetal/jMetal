package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for the values of class InvertedGenerationalDistancePlus")
class InvertedGenerationalDistancePlusValuesTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] REFERENCE_FRONT = {{0.0, 1.0}, {1.0, 0.0}};

  private final InvertedGenerationalDistancePlus indicator =
      new InvertedGenerationalDistancePlus(REFERENCE_FRONT);

  @Nested
  @DisplayName("When computing the indicator")
  class WhenComputingTheIndicator {
    @Test
    @DisplayName("given the reference front, then the indicator is zero")
    void givenTheReferenceFront_whenComputing_thenTheIndicatorIsZero() {
      // Act & Assert
      assertThat(indicator.compute(REFERENCE_FRONT)).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a point that is worse in one objective, then only that objective counts")
    void givenAPointWorseInOneObjective_whenComputing_thenOnlyThatObjectiveCounts() {
      // Act: for (0, 1) the distance is max(0.5 - 0, 0) = 0.5, as the point is better in the second
      double value = indicator.compute(new double[][] {{0.5, 0.5}});

      // Assert: both reference points are at 0.5 of (0.5, 0.5) in the dominance distance
      assertThat(value).isEqualTo(0.5, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front with one reference point, then only the other one counts")
    void givenOneReferencePoint_whenComputing_thenOnlyTheOtherOneCounts() {
      // Act: the distances are 0 and 1, as (0, 1) is worse than (1, 0) by 1 in the second
      double value = indicator.compute(new double[][] {{0.0, 1.0}});

      // Assert
      assertThat(value).isEqualTo(0.5, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front that dominates the reference front, then the indicator is zero")
    void givenAFrontThatDominatesTheReference_whenComputing_thenTheIndicatorIsZero() {
      // Arrange: IGD+ does not penalize being better than the reference, but IGD does
      double[][] better = {{0.0, 0.0}};

      // Act & Assert
      assertThat(indicator.compute(better)).isEqualTo(0.0, offset(EPSILON));
      assertThat(new InvertedGenerationalDistance(REFERENCE_FRONT).compute(better)).isPositive();
    }

    @Test
    @DisplayName("given a point that is dominated by the reference front, then it is the distance")
    void givenADominatedPoint_whenComputing_thenItIsTheDistance() {
      // Act: for both reference points, the point (2, 3) is worse by (2, 2) and (1, 3)
      double value = indicator.compute(new double[][] {{2.0, 3.0}});

      // Assert
      assertThat(value)
          .isEqualTo((Math.sqrt(4.0 + 4.0) + Math.sqrt(1.0 + 9.0)) / 2.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front with an extra point, then the indicator does not increase")
    void givenAnExtraPoint_whenComputing_thenTheIndicatorDoesNotIncrease() {
      // Arrange
      double[][] front = {{0.5, 0.5}};
      double[][] withAnotherPoint = {{0.5, 0.5}, {5.0, 5.0}};

      // Act & Assert
      assertThat(indicator.compute(withAnotherPoint))
          .isLessThanOrEqualTo(indicator.compute(front) + EPSILON);
    }

    @ParameterizedTest(name = "translated by {0}")
    @ValueSource(doubles = {100.0, -100.0, -0.25})
    @DisplayName("given both fronts translated, then the indicator is the same")
    void givenTranslatedFronts_whenComputing_thenTheIndicatorIsTheSame(double shift) {
      // Arrange
      var translated =
          new InvertedGenerationalDistancePlus(
              new double[][] {{shift, 1 + shift}, {1 + shift, shift}});

      // Act
      double value = translated.compute(new double[][] {{0.5 + shift, 0.5 + shift}});

      // Assert
      assertThat(value).isEqualTo(0.5, offset(1e-9));
    }
  }

  @Nested
  @DisplayName("When the arguments are not valid")
  class WhenTheArgumentsAreNotValid {
    @Test
    @DisplayName("given a null or empty front, then it is rejected")
    void givenANullOrEmptyFront_whenComputing_thenItIsRejected() {
      // Act
      Throwable nullFront = catchThrowable(() -> indicator.compute(null));
      Throwable emptyFront = catchThrowable(() -> indicator.compute(new double[0][]));

      // Assert
      assertThat(nullFront).isInstanceOf(NullParameterException.class);
      assertThat(emptyFront).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given no reference front, then it is rejected")
    void givenNoReferenceFront_whenComputing_thenItIsRejected() {
      // Arrange
      double[][] front = {{0.5, 0.5}};

      // Act
      Throwable thrown =
          catchThrowable(() -> new InvertedGenerationalDistancePlus().compute(front));

      // Assert
      assertThat(thrown).isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given a front with another number of objectives, then it is rejected")
    void givenAFrontOfAnotherDimension_whenComputing_thenItIsRejected() {
      // Arrange
      double[][] front = {{0.5, 0.5, 0.5}};

      // Act
      Throwable thrown = catchThrowable(() -> indicator.compute(front));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }
  }

  @Nested
  @DisplayName("When asking for its description")
  class WhenAskingForItsDescription {
    @Test
    @DisplayName("then it has a name and a description, and the lower the value the better")
    void thenItHasANameAndADescriptionAndTheLowerTheBetter() {
      // Assert
      assertThat(indicator.name()).isEqualTo("IGD+");
      assertThat(indicator.description()).isNotBlank();
      assertThat(indicator.isTheLowerTheIndicatorValueTheBetter()).isTrue();
      assertThat(indicator.newInstance().referenceFront()).isNull();
    }
  }
}
