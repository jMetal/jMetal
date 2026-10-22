package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@DisplayName("Unit tests for the method printQualityIndicators of class QualityIndicatorUtils")
class QualityIndicatorUtilsPrintTest {
  private final List<String> messages = new ArrayList<>();
  private Handler handler;

  @BeforeEach
  void setUp() {
    handler =
        new Handler() {
          @Override
          public void publish(LogRecord record) {
            messages.add(record.getMessage() == null ? "" : record.getMessage());
          }

          @Override
          public void flush() {}

          @Override
          public void close() {}
        };
    JMetalLogger.logger.addHandler(handler);
  }

  @AfterEach
  void tearDown() {
    JMetalLogger.logger.removeHandler(handler);
  }

  private static List<String> indicatorNames() {
    return QualityIndicatorUtils.getAvailableIndicators().stream()
        .map(QualityIndicator::name)
        .toList();
  }

  private double printedValue(String name) {
    String prefix = name + ": ";
    String message =
        messages.stream().filter(m -> m.startsWith(prefix)).findFirst().orElseThrow();
    return Double.parseDouble(message.substring(prefix.length()));
  }

  @Nested
  @DisplayName("When printing the indicators of a front")
  class WhenPrintingTheIndicatorsOfAFront {
    private final double[][] referenceFront = {{0.0, 10.0}, {5.0, 5.0}, {10.0, 0.0}};

    @Test
    @DisplayName("given a front, then one line is printed for each available indicator")
    void givenAFront_whenPrinting_thenOneLineIsPrintedForEachIndicator() {
      // Act
      QualityIndicatorUtils.printQualityIndicators(referenceFront, referenceFront);

      // Assert
      List<String> indicatorMessages =
          messages.stream().filter(m -> m.matches("[A-Za-z0-9+-]+: .*")).toList();
      assertThat(indicatorMessages).hasSameSizeAs(indicatorNames());
      for (String name : indicatorNames()) {
        assertThat(indicatorMessages).anyMatch(m -> m.startsWith(name + ": "));
      }
    }

    @Test
    @DisplayName("given the reference front itself, then the distance indicators are zero")
    void givenTheReferenceFront_whenPrinting_thenTheDistanceIndicatorsAreZero() {
      // Act
      QualityIndicatorUtils.printQualityIndicators(referenceFront, referenceFront);

      // Assert
      assertThat(printedValue("EP")).isZero();
      assertThat(printedValue("NHV")).isZero();
      assertThat(printedValue("IGD")).isZero();
      assertThat(printedValue("IGD+")).isZero();
      assertThat(printedValue("GD")).isZero();
    }

    @Test
    @DisplayName("given a front far from the reference front, then the indicators are normalized")
    void givenAFarFront_whenPrinting_thenTheIndicatorsAreNormalized() {
      // Arrange: with the bounds of the reference front the point (10, 10) is (1, 1)
      double[][] front = {{10.0, 10.0}};

      // Act
      QualityIndicatorUtils.printQualityIndicators(front, referenceFront);

      // Assert: the additive epsilon is 1 - 0 for the points (0, 1) and (1, 0) of the reference
      assertThat(printedValue("EP")).isEqualTo(1.0);
      assertThat(printedValue("NHV")).isEqualTo(1.0);
    }

    @Test
    @DisplayName("given a reference front with negative objectives, then its range is used")
    void givenNegativeObjectives_whenPrinting_thenTheFrontIsNormalizedWithTheirRange() {
      // Arrange: the same shapes as above, shifted by -100
      double[][] negativeReference = {{-100.0, -90.0}, {-95.0, -95.0}, {-90.0, -100.0}};
      double[][] negativeFront = {{-90.0, -90.0}};

      // Act
      QualityIndicatorUtils.printQualityIndicators(negativeFront, negativeReference);

      // Assert: the same values as with the reference front of the positive objectives
      assertThat(printedValue("EP")).isEqualTo(1.0);
      assertThat(printedValue("NHV")).isEqualTo(1.0);
    }
  }

  @Nested
  @DisplayName("When printing the indicators of invalid fronts")
  class WhenPrintingTheIndicatorsOfInvalidFronts {
    @Test
    @DisplayName("given a null front or reference front, then it is rejected")
    void givenANullFront_whenPrinting_thenItIsRejected() {
      // Arrange
      double[][] front = {{1.0, 2.0}};

      // Act
      Throwable nullFront =
          catchThrowable(() -> QualityIndicatorUtils.printQualityIndicators(null, front));
      Throwable nullReference =
          catchThrowable(() -> QualityIndicatorUtils.printQualityIndicators(front, null));

      // Assert
      assertThat(nullFront).isInstanceOf(NullParameterException.class);
      assertThat(nullReference).isInstanceOf(NullParameterException.class);
    }

    @Test
    @DisplayName("given an empty reference front, then it is rejected")
    void givenAnEmptyReferenceFront_whenPrinting_thenItIsRejected() {
      // Arrange
      double[][] front = {{1.0}};
      double[][] emptyReference = new double[0][];

      // Act
      Throwable thrown =
          catchThrowable(
              () -> QualityIndicatorUtils.printQualityIndicators(front, emptyReference));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given a front with an infinite value, then it is rejected")
    void givenAnInfiniteValue_whenPrinting_thenItIsRejected() {
      // Arrange
      double[][] reference = {{0.0, 1.0}, {1.0, 0.0}};
      double[][] front = {{Double.POSITIVE_INFINITY, 0.5}};

      // Act
      Throwable thrown =
          catchThrowable(() -> QualityIndicatorUtils.printQualityIndicators(front, reference));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }
  }
}
