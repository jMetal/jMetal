package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for the values of class Spread")
class SpreadValuesTest {
  private static final double EPSILON = 1e-12;
  private static final double[][] REFERENCE_FRONT = {{0.0, 1.0}, {1.0, 0.0}};

  private final Spread spread = new Spread(REFERENCE_FRONT);

  @Nested
  @DisplayName("When computing the indicator of a front")
  class WhenComputingTheIndicator {
    @Test
    @DisplayName("given a front uniformly spread between the extremes, then the indicator is zero")
    void givenAUniformFront_whenComputing_thenTheIndicatorIsZero() {
      // Act & Assert
      assertThat(spread.compute(new double[][] {{0, 1}, {0.5, 0.5}, {1, 0}}))
          .isEqualTo(0.0, offset(EPSILON));
      double[][] five = {{0, 1}, {0.25, 0.75}, {0.5, 0.5}, {0.75, 0.25}, {1, 0}};
      assertThat(spread.compute(five)).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given the reference front itself, then the indicator is zero")
    void givenTheReferenceFront_whenComputing_thenTheIndicatorIsZero() {
      // Act & Assert
      assertThat(spread.compute(REFERENCE_FRONT)).isEqualTo(0.0, offset(EPSILON));
    }

    @Test
    @DisplayName("given an unevenly spread front, then the indicator is the one of Deb's formula")
    void givenAnUnevenFront_whenComputing_thenTheIndicatorIsTheOneOfTheFormula() {
      // Arrange: the distances between consecutive points are 0.1414 and 1.2728, df = dl = 0
      double d1 = Math.sqrt(0.02);
      double d2 = Math.sqrt(1.62);
      double mean = (d1 + d2) / 2.0;
      double expected = (Math.abs(d1 - mean) + Math.abs(d2 - mean)) / (2.0 * mean);

      // Act
      double value = spread.compute(new double[][] {{0, 1}, {0.1, 0.9}, {1, 0}});

      // Assert
      assertThat(value).isEqualTo(expected, offset(EPSILON));
      assertThat(value).isEqualTo(0.8, offset(EPSILON));
    }

    @Test
    @DisplayName("given a front that lacks an extreme, then the distance to it is penalized")
    void givenAFrontWithoutAnExtreme_whenComputing_thenTheDistanceToItIsPenalized() {
      // Arrange: dl = 0.7071 to the extreme (1, 0), and the points are evenly spread
      double dl = Math.sqrt(0.5);
      double mean = Math.sqrt(0.125);
      double expected = dl / (dl + 2.0 * mean);

      // Act
      double value = spread.compute(new double[][] {{0, 1}, {0.25, 0.75}, {0.5, 0.5}});

      // Assert
      assertThat(value).isEqualTo(expected, offset(EPSILON));
      assertThat(value).isEqualTo(0.5, offset(EPSILON));
    }

    @Test
    @DisplayName("given the points in another order, then the indicator is the same")
    void givenAnotherOrder_whenComputing_thenTheIndicatorIsTheSame() {
      // Act
      double ordered = spread.compute(new double[][] {{0, 1}, {0.1, 0.9}, {1, 0}});
      double shuffled = spread.compute(new double[][] {{1, 0}, {0, 1}, {0.1, 0.9}});

      // Assert
      assertThat(shuffled).isEqualTo(ordered, offset(EPSILON));
    }

    @ParameterizedTest(name = "translated by {0}")
    @ValueSource(doubles = {100.0, -100.0, -0.5})
    @DisplayName("given both fronts translated, then the indicator is the same")
    void givenTranslatedFronts_whenComputing_thenTheIndicatorIsTheSame(double shift) {
      // Arrange
      var translated =
          new Spread(new double[][] {{shift, 1 + shift}, {1 + shift, shift}});

      // Act
      double[][] front = {{shift, 1 + shift}, {0.1 + shift, 0.9 + shift}, {1 + shift, shift}};
      double value = translated.compute(front);

      // Assert
      assertThat(value).isEqualTo(0.8, offset(1e-9));
    }

    @Test
    @DisplayName("given both fronts scaled by the same factor, then the indicator is the same")
    void givenScaledFronts_whenComputing_thenTheIndicatorIsTheSame() {
      // Arrange
      var scaled = new Spread(new double[][] {{0, 1000}, {1000, 0}});

      // Act
      double value = scaled.compute(new double[][] {{0, 1000}, {100, 900}, {1000, 0}});

      // Assert
      assertThat(value).isEqualTo(0.8, offset(1e-9));
    }

    @Test
    @DisplayName("given a single point or identical points, then the indicator is the worst, one")
    void givenASinglePoint_whenComputing_thenTheIndicatorIsOne() {
      // Act & Assert
      assertThat(spread.compute(new double[][] {{0.5, 0.5}})).isEqualTo(1.0);
      assertThat(spread.compute(new double[][] {{0.5, 0.5}, {0.5, 0.5}})).isEqualTo(1.0);
    }

    @Test
    @DisplayName("given a front, then the front and the reference front are not modified")
    void givenAFront_whenComputing_thenNothingIsModified() {
      // Arrange
      double[][] front = {{1, 0}, {0, 1}, {0.1, 0.9}};

      // Act
      spread.compute(front);

      // Assert
      assertThat(front).isDeepEqualTo(new double[][] {{1, 0}, {0, 1}, {0.1, 0.9}});
      assertThat(REFERENCE_FRONT).isDeepEqualTo(new double[][] {{0, 1}, {1, 0}});
    }
  }

  @Nested
  @DisplayName("When the arguments are not valid")
  class WhenTheArgumentsAreNotValid {
    @Test
    @DisplayName("given a null or empty front, then it is rejected")
    void givenANullOrEmptyFront_whenComputing_thenItIsRejected() {
      // Act
      Throwable nullFront = catchThrowable(() -> spread.compute(null));
      Throwable emptyFront = catchThrowable(() -> spread.compute(new double[0][]));

      // Assert
      assertThat(nullFront).isInstanceOf(NullParameterException.class);
      assertThat(emptyFront).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given no reference front, then it is rejected")
    void givenNoReferenceFront_whenComputing_thenItIsRejected() {
      // Act
      double[][] front = {{0, 1}, {1, 0}};
      Throwable thrown = catchThrowable(() -> new Spread().compute(front));

      // Assert
      assertThat(thrown).isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given a front with another number of objectives, then it is rejected")
    void givenAFrontOfAnotherDimension_whenComputing_thenItIsRejected() {
      // Act
      double[][] threeObjectives = {{0, 1, 0}, {1, 0, 0}};
      Throwable thrown = catchThrowable(() -> spread.compute(threeObjectives));

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
      assertThat(spread.name()).isEqualTo("SP");
      assertThat(spread.description()).isNotBlank();
      assertThat(spread.isTheLowerTheIndicatorValueTheBetter()).isTrue();
      assertThat(spread.newInstance()).isInstanceOf(Spread.class);
    }
  }
}
