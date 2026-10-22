package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for class InvertedGenerationalDistance")
class InvertedGenerationalDistanceTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] REFERENCE_FRONT = {{0.0, 1.0}, {1.0, 0.0}};

  @Nested
  @DisplayName("When computing the indicator")
  class WhenComputingTheIndicator {
    private final InvertedGenerationalDistance indicator =
        new InvertedGenerationalDistance(REFERENCE_FRONT);

    @Test
    @DisplayName("given the reference front, then the indicator is zero")
    void givenTheReferenceFront_whenComputing_thenTheIndicatorIsZero() {
      // Act & Assert
      assertThat(indicator.compute(REFERENCE_FRONT)).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a point at the same distance from each reference point, then it is d")
    void givenAPointAtTheSameDistance_whenComputing_thenTheIndicatorIsTheRootOfTheSumOverN() {
      // Act: the distance from (0.5, 0.5) to each reference point is sqrt(0.5)
      double value = indicator.compute(new double[][] {{0.5, 0.5}});

      // Assert: sqrt(0.5 + 0.5) / 2
      assertThat(value).isEqualTo(0.5, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front with one of the reference points, then only the other counts")
    void givenOneReferencePoint_whenComputing_thenOnlyTheOtherCounts() {
      // Act: the distances are 0 and sqrt(2)
      double value = indicator.compute(new double[][] {{0.0, 1.0}});

      // Assert
      assertThat(value).isEqualTo(Math.sqrt(2.0) / 2.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front with an extra point, then the indicator does not increase")
    void givenAnExtraPoint_whenComputing_thenTheIndicatorDoesNotIncrease() {
      // Arrange
      double[][] front = {{0.0, 1.0}};
      double[][] withAnotherPoint = {{0.0, 1.0}, {5.0, 5.0}};

      // Act & Assert
      assertThat(indicator.compute(withAnotherPoint))
          .isLessThanOrEqualTo(indicator.compute(front) + EPSILON);
    }

    @Test
    @DisplayName("given both fronts translated by the same vector, then the indicator is the same")
    void givenTranslatedFronts_whenComputing_thenTheIndicatorIsTheSame() {
      // Arrange
      var translated =
          new InvertedGenerationalDistance(new double[][] {{-100.0, -99.0}, {-99.0, -100.0}});

      // Act
      double value = translated.compute(new double[][] {{-99.5, -99.5}});

      // Assert
      assertThat(value).isEqualTo(0.5, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front, then it is not modified")
    void givenAFront_whenComputing_thenItIsNotModified() {
      // Arrange
      double[][] front = {{0.25, 0.75}};

      // Act
      indicator.compute(front);

      // Assert
      assertThat(front).isDeepEqualTo(new double[][] {{0.25, 0.75}});
    }
  }

  @Nested
  @DisplayName("When using another power")
  class WhenUsingAnotherPower {
    @Test
    @DisplayName("given the power 1, then it is the mean of the distances")
    void givenThePowerOne_whenComputing_thenItIsTheMeanOfTheDistances() {
      // Arrange
      var indicator = new InvertedGenerationalDistance(REFERENCE_FRONT, 1.0);

      // Act & Assert: the distances to (0, 1) are 0 and sqrt(2)
      assertThat(indicator.compute(new double[][] {{0.0, 1.0}}))
          .isEqualTo(Math.sqrt(2.0) / 2.0, offset(EPSILON));
      assertThat(indicator.compute(new double[][] {{0.5, 0.5}}))
          .isEqualTo(Math.sqrt(0.5), offset(EPSILON));
    }

    @Test
    @DisplayName("given the power 3, then it is the cubic mean over n")
    void givenThePowerThree_whenComputing_thenItIsTheCubicMeanOverN() {
      // Arrange
      var indicator = new InvertedGenerationalDistance(REFERENCE_FRONT, 3.0);

      // Act
      double value = indicator.compute(new double[][] {{0.5, 0.5}});

      // Assert: (2 * sqrt(0.5)^3)^(1/3) / 2
      double expected = Math.cbrt(2.0 * Math.pow(Math.sqrt(0.5), 3.0)) / 2.0;
      assertThat(value).isEqualTo(expected, offset(EPSILON));
    }
  }

  @Nested
  @DisplayName("When the front is not valid")
  class WhenTheFrontIsNotValid {
    private final InvertedGenerationalDistance indicator =
        new InvertedGenerationalDistance(REFERENCE_FRONT);

    @Test
    @DisplayName("given a null front, then it is rejected")
    void givenANullFront_whenComputing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> indicator.compute(null));

      // Assert
      assertThat(thrown).isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given an empty front, then it is rejected")
    void givenAnEmptyFront_whenComputing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> indicator.compute(new double[0][]));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a front with another number of objectives, then it is rejected")
    void givenAFrontOfAnotherDimension_whenComputing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> indicator.compute(new double[][] {{0.5, 0.5, 0.5}}));

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
      // Arrange
      var indicator = new InvertedGenerationalDistance();

      // Assert
      assertThat(indicator.name()).isEqualTo("IGD");
      assertThat(indicator.description()).isNotBlank();
      assertThat(indicator.isTheLowerTheIndicatorValueTheBetter()).isTrue();
    }

    @Test
    @DisplayName("given an indicator with a reference front, then its new instance has none")
    void givenAReferenceFront_whenCreatingAnInstance_thenItHasNone() {
      // Act
      QualityIndicator instance = new InvertedGenerationalDistance(REFERENCE_FRONT).newInstance();

      // Assert
      assertThat(instance).isInstanceOf(InvertedGenerationalDistance.class);
      assertThat(instance.referenceFront()).isNull();
    }

    @Test
    @DisplayName("given a null reference front, then it is rejected")
    void givenANullReferenceFront_whenConstructing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> new InvertedGenerationalDistance(null));

      // Assert
      assertThat(thrown).isInstanceOf(NullParameterException.class);
    }
  }
}
