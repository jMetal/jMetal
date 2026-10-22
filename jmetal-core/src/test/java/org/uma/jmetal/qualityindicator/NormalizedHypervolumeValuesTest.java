package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for the values of class NormalizedHypervolume")
class NormalizedHypervolumeValuesTest {
  private static final double EPSILON = 1e-12;

  /** Its hypervolume with respect to (1, 1) is 0.28. */
  private static final double[][] TWO_POINTS_FRONT = {{0.2, 0.8}, {0.8, 0.2}};

  @Nested
  @DisplayName("When computing the indicator of a two-objective front")
  class WhenComputingTheIndicatorOfATwoObjectiveFront {
    private final NormalizedHypervolume indicator = new NormalizedHypervolume(TWO_POINTS_FRONT);

    @Test
    @DisplayName("given the reference front, then the indicator is zero")
    void givenTheReferenceFront_whenComputing_thenTheIndicatorIsZero() {
      // Act
      double value = indicator.compute(TWO_POINTS_FRONT);

      // Assert
      assertThat(value).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a subset of the reference front, then the indicator is the lost fraction")
    void givenASubsetOfTheReferenceFront_whenComputing_thenTheIndicatorIsTheLostFraction() {
      // Act
      double value = indicator.compute(new double[][] {{0.2, 0.8}});

      // Assert: the hypervolume of the point is 0.16, that of the reference front 0.28
      assertThat(value).isEqualTo(1.0 - 0.16 / 0.28, offset(EPSILON));
    }

    @Test
    @DisplayName("given a dominated front, then the indicator is between 0 and 1")
    void givenADominatedFront_whenComputing_thenTheIndicatorIsBetweenZeroAndOne() {
      // Act
      double value = indicator.compute(new double[][] {{0.5, 0.9}, {0.9, 0.5}});

      // Assert: the hypervolume of the front is 0.1 * 0.5 + 0.5 * 0.1 - 0.1 * 0.1 = 0.09
      assertThat(value).isEqualTo(1.0 - 0.09 / 0.28, offset(EPSILON));
      assertThat(value).isBetween(0.0, 1.0);
    }

    @Test
    @DisplayName("given a front better than the reference front, then the indicator is negative")
    void givenABetterFront_whenComputing_thenTheIndicatorIsNegative() {
      // Act
      double value = indicator.compute(new double[][] {{0.1, 0.1}});

      // Assert: the hypervolume of the point is 0.81
      assertThat(value).isEqualTo(1.0 - 0.81 / 0.28, offset(EPSILON));
      assertThat(value).isNegative();
    }

    @Test
    @DisplayName("given an empty front, then the indicator is one")
    void givenAnEmptyFront_whenComputing_thenTheIndicatorIsOne() {
      // Act
      double value = indicator.compute(new double[0][]);

      // Assert
      assertThat(value).isEqualTo(1.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front out of the unit box, then it adds no hypervolume")
    void givenAFrontOutOfTheUnitBox_whenComputing_thenItAddsNoHypervolume() {
      // Act
      double value = indicator.compute(new double[][] {{1.5, 1.5}});

      // Assert
      assertThat(value).isEqualTo(1.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front with a dominated point added, then the indicator does not change")
    void givenADominatedPointAdded_whenComputing_thenTheIndicatorDoesNotChange() {
      // Arrange
      double[][] front = {{0.2, 0.8}};
      double[][] withDominatedPoint = {{0.2, 0.8}, {0.9, 0.9}};

      // Act & Assert
      assertThat(indicator.compute(withDominatedPoint))
          .isEqualTo(indicator.compute(front), offset(EPSILON));
    }

    @Test
    @DisplayName("given a front with a non-dominated point added, then the indicator decreases")
    void givenANonDominatedPointAdded_whenComputing_thenTheIndicatorDecreases() {
      // Arrange
      double[][] front = {{0.2, 0.8}};
      double[][] withAnotherPoint = {{0.2, 0.8}, {0.8, 0.2}};

      // Act & Assert
      assertThat(indicator.compute(withAnotherPoint)).isLessThan(indicator.compute(front));
    }

    @Test
    @DisplayName("given the same front twice, then the indicator is the same")
    void givenTheSameFrontTwice_whenComputing_thenTheIndicatorIsTheSame() {
      // Arrange
      double[][] front = {{0.3, 0.7}};

      // Act & Assert
      assertThat(indicator.compute(front)).isEqualTo(indicator.compute(front));
    }
  }

  @Nested
  @DisplayName("When computing the indicator of a three-objective front")
  class WhenComputingTheIndicatorOfAThreeObjectiveFront {
    @Test
    @DisplayName("given a point with a fraction of the reference hypervolume, then it is 1 - f")
    void givenAPoint_whenComputing_thenTheIndicatorIsOneMinusTheFraction() {
      // Arrange: the hypervolumes are 0.5^3 = 0.125 and 0.25^3 = 0.015625
      var indicator = new NormalizedHypervolume(new double[][] {{0.5, 0.5, 0.5}});

      // Act
      double value = indicator.compute(new double[][] {{0.75, 0.75, 0.75}});

      // Assert
      assertThat(value).isEqualTo(1.0 - 0.015625 / 0.125, offset(EPSILON));
    }
  }

  @Nested
  @DisplayName("When the reference front has no hypervolume")
  class WhenTheReferenceFrontHasNoHypervolume {
    @Test
    @DisplayName("given a front with hypervolume, then the indicator is not a finite number")
    void givenAFrontWithHypervolume_whenComputing_thenTheIndicatorIsNotFinite() {
      // Arrange: the reference point (1, 1) is the only point of the reference front
      var indicator = new NormalizedHypervolume(new double[][] {{1.0, 1.0}});

      // Act
      double value = indicator.compute(new double[][] {{0.5, 0.5}});

      // Assert
      assertThat(Double.isFinite(value)).isFalse();
    }
  }

  @Nested
  @DisplayName("When setting the reference front")
  class WhenSettingTheReferenceFront {
    @Test
    @DisplayName("given a null reference front in the constructor, then it is rejected")
    void givenANullReferenceFront_whenConstructing_thenItIsRejected() {
      // Act
      Throwable thrown = catchThrowable(() -> new NormalizedHypervolume((double[][]) null));

      // Assert
      assertThat(thrown).isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given an indicator without reference front, when it is set, then it can compute")
    void givenAnIndicatorWithoutReferenceFront_whenItIsSet_thenItCanCompute() {
      // Arrange
      var indicator = new NormalizedHypervolume();

      // Act
      indicator.referenceFront(TWO_POINTS_FRONT);

      // Assert
      assertThat(indicator.referenceFront()).isSameAs(TWO_POINTS_FRONT);
      assertThat(indicator.compute(TWO_POINTS_FRONT)).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given a new reference front, then the indicator uses its hypervolume")
    void givenANewReferenceFront_whenComputing_thenTheIndicatorUsesItsHypervolume() {
      // Arrange
      var indicator = new NormalizedHypervolume(TWO_POINTS_FRONT);
      double[][] front = {{0.5, 0.5}};

      // Act
      double withTheFirstReference = indicator.compute(front);
      indicator.referenceFront(new double[][] {{0.5, 0.5}});
      double withTheSecondReference = indicator.compute(front);

      // Assert
      assertThat(withTheFirstReference).isEqualTo(1.0 - 0.25 / 0.28, offset(EPSILON));
      assertThat(withTheSecondReference).isEqualTo(0.0, offset(EPSILON));
    }
  }

  @Nested
  @DisplayName("When asking for its description")
  class WhenAskingForItsDescription {
    private final NormalizedHypervolume indicator = new NormalizedHypervolume();

    @Test
    @DisplayName("then it has a name and a description, and the lower the value the better")
    void thenItHasANameAndADescriptionAndTheLowerTheBetter() {
      // Assert
      assertThat(indicator.name()).isEqualTo("NHV");
      assertThat(indicator.description()).isEqualTo("Normalized hypervolume");
      assertThat(indicator.isTheLowerTheIndicatorValueTheBetter()).isTrue();
    }

    @Test
    @DisplayName("given an indicator with a reference front, then its new instance has none")
    void givenAnIndicatorWithAReferenceFront_whenCreatingAnInstance_thenItHasNone() {
      // Arrange
      var indicator = new NormalizedHypervolume(TWO_POINTS_FRONT);

      // Act
      QualityIndicator instance = indicator.newInstance();

      // Assert
      assertThat(instance).isInstanceOf(NormalizedHypervolume.class).isNotSameAs(indicator);
      assertThat(instance.referenceFront()).isNull();
    }
  }
}
