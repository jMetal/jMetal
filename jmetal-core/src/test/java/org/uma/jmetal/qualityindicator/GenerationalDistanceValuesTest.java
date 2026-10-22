package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for the values of class GenerationalDistance")
class GenerationalDistanceValuesTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] REFERENCE_FRONT = {{0.0, 1.0}, {1.0, 0.0}};

  private final GenerationalDistance indicator = new GenerationalDistance(REFERENCE_FRONT);

  @Nested
  @DisplayName("When computing the indicator")
  class WhenComputingTheIndicator {
    @Test
    @DisplayName("given points of the reference front, then the indicator is zero")
    void givenPointsOfTheReferenceFront_whenComputing_thenTheIndicatorIsZero() {
      // Act & Assert
      assertThat(indicator.compute(REFERENCE_FRONT)).isEqualTo(0.0, offset(EPSILON));
      assertThat(indicator.compute(new double[][] {{0.0, 1.0}})).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a point at a distance of the reference front, then it is that distance")
    void givenAPointAtADistance_whenComputing_thenItIsThatDistance() {
      // Act: the point (0.5, 0.5) is at sqrt(0.5) of both reference points
      double value = indicator.compute(new double[][] {{0.5, 0.5}});

      // Assert
      assertThat(value).isEqualTo(Math.sqrt(0.5), offset(EPSILON));
    }

    @Test
    @DisplayName("given several points, then it is the root of the sum of squares over the points")
    void givenSeveralPoints_whenComputing_thenItIsTheRootOfTheSumOfSquaresOverThePoints() {
      // Act: the distances are 0 and sqrt(0.5) and 1
      double value = indicator.compute(new double[][] {{0.0, 1.0}, {0.5, 0.5}, {1.0, 1.0}});

      // Assert: sqrt(0 + 0.5 + 1) / 3
      assertThat(value).isEqualTo(Math.sqrt(1.5) / 3.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given points on the front and a bad one, then the bad one worsens the indicator")
    void givenABadPoint_whenComputing_thenTheIndicatorIsWorse() {
      // Arrange
      double[][] good = {{0.0, 1.0}, {1.0, 0.0}};
      double[][] withABadPoint = {{0.0, 1.0}, {1.0, 0.0}, {2.0, 2.0}};

      // Act & Assert
      assertThat(indicator.compute(withABadPoint)).isGreaterThan(indicator.compute(good));
    }

    @Test
    @DisplayName("given the points in another order, then the indicator is the same")
    void givenAnotherOrder_whenComputing_thenTheIndicatorIsTheSame() {
      // Act & Assert
      assertThat(indicator.compute(new double[][] {{1.0, 1.0}, {0.0, 1.0}, {0.5, 0.5}}))
          .isEqualTo(indicator.compute(new double[][] {{0.0, 1.0}, {0.5, 0.5}, {1.0, 1.0}}),
              offset(EPSILON));
    }

    @ParameterizedTest(name = "translated by {0}")
    @ValueSource(doubles = {100.0, -100.0, -0.25})
    @DisplayName("given both fronts translated, then the indicator is the same")
    void givenTranslatedFronts_whenComputing_thenTheIndicatorIsTheSame(double shift) {
      // Arrange
      var translated =
          new GenerationalDistance(new double[][] {{shift, 1 + shift}, {1 + shift, shift}});

      // Act
      double value = translated.compute(new double[][] {{0.5 + shift, 0.5 + shift}});

      // Assert
      assertThat(value).isEqualTo(Math.sqrt(0.5), offset(1e-9));
    }

    @Test
    @DisplayName("given a front, then it is not modified")
    void givenAFront_whenComputing_thenItIsNotModified() {
      // Arrange
      double[][] front = {{0.5, 0.5}, {2.0, 2.0}};

      // Act
      indicator.compute(front);

      // Assert
      assertThat(front).isDeepEqualTo(new double[][] {{0.5, 0.5}, {2.0, 2.0}});
    }
  }

  @Nested
  @DisplayName("When the arguments are not valid")
  class WhenTheArgumentsAreNotValid {
    @Test
    @DisplayName("given a null or empty front, then it is rejected, not a NaN")
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
      Throwable thrown = catchThrowable(() -> new GenerationalDistance().compute(front));

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
      assertThat(indicator.name()).isEqualTo("GD");
      assertThat(indicator.description()).isNotBlank();
      assertThat(indicator.isTheLowerTheIndicatorValueTheBetter()).isTrue();
      assertThat(indicator.newInstance().referenceFront()).isNull();
    }
  }
}
