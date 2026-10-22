package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.qualityindicator.impl.AverageHausdorffDistance;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for the values of class AverageHausdorffDistance")
class AverageHausdorffDistanceValuesTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] REFERENCE_FRONT = {{0.0, 1.0}, {1.0, 0.0}};

  private final AverageHausdorffDistance indicator = new AverageHausdorffDistance(REFERENCE_FRONT);

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
    @DisplayName("given a point at the same distance of every reference point, then it is d")
    void givenAPointAtTheSameDistance_whenComputing_thenItIsTheDistance() {
      // Act
      double value = indicator.compute(new double[][] {{0.5, 0.5}});

      // Assert: both parts are sqrt(0.5)
      assertThat(value).isEqualTo(Math.sqrt(0.5), offset(EPSILON));
    }

    @Test
    @DisplayName("given a front with one reference point, then the inverted part is the maximum")
    void givenOneReferencePoint_whenComputing_thenTheInvertedPartIsTheMaximum() {
      // Act: the generational part is 0 and the inverted one (0 + sqrt(2)) / 2
      double value = indicator.compute(new double[][] {{0.0, 1.0}});

      // Assert
      assertThat(value).isEqualTo(Math.sqrt(2.0) / 2.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given the reference front and an extra point, then the generational part is")
    void givenAnExtraPoint_whenComputing_thenTheGenerationalPartIsTheMaximum() {
      // Arrange: the inverted part is 0 and the generational one sqrt(0.5) / 3
      double[][] front = {{0.0, 1.0}, {1.0, 0.0}, {0.5, 0.5}};

      // Act
      double value = indicator.compute(front);

      // Assert
      assertThat(value).isEqualTo(Math.sqrt(0.5) / 3.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front, then it is the larger of the generational and inverted parts")
    void givenAFront_whenComputing_thenItIsTheLargerOfTheTwoParts() {
      // Arrange: (0.9, 0.9) is at sqrt(0.82) of both reference points, and (2, 2) at sqrt(5)
      double[][] front = {{0.0, 1.0}, {0.9, 0.9}, {2.0, 2.0}};
      double generational = (0.0 + Math.sqrt(0.82) + Math.sqrt(5.0)) / 3.0;
      double inverted = (0.0 + Math.sqrt(0.82)) / 2.0;

      // Act
      double value = indicator.compute(front);

      // Assert
      assertThat(generational).isGreaterThan(inverted);
      assertThat(value).isEqualTo(generational, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front far from the reference front, then the inverted part is larger")
    void givenAFarFront_whenComputing_thenTheInvertedPartIsLarger() {
      // Arrange: one point at (0, 1): generational 0, inverted (0 + sqrt(2)) / 2
      double[][] front = {{0.0, 1.0}, {0.0, 1.0}};

      // Act
      double value = indicator.compute(front);

      // Assert
      assertThat(value).isEqualTo(Math.sqrt(2.0) / 2.0, offset(EPSILON));
    }

    @ParameterizedTest(name = "translated by {0}")
    @ValueSource(doubles = {100.0, -100.0, -0.25})
    @DisplayName("given both fronts translated, then the indicator is the same")
    void givenTranslatedFronts_whenComputing_thenTheIndicatorIsTheSame(double shift) {
      // Arrange
      var translated =
          new AverageHausdorffDistance(new double[][] {{shift, 1 + shift}, {1 + shift, shift}});

      // Act
      double value = translated.compute(new double[][] {{0.5 + shift, 0.5 + shift}});

      // Assert
      assertThat(value).isEqualTo(Math.sqrt(0.5), offset(1e-9));
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
      Throwable thrown = catchThrowable(() -> new AverageHausdorffDistance().compute(front));

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
      assertThat(indicator.name()).isEqualTo("AHD");
      assertThat(indicator.description()).isNotBlank();
      assertThat(indicator.isTheLowerTheIndicatorValueTheBetter()).isTrue();
      assertThat(indicator.newInstance().referenceFront()).isNull();
    }
  }
}
