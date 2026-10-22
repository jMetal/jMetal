package org.uma.jmetal.utilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.offset;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for class CommandLineQualityIndicatorTool")
class CommandLineQualityIndicatorToolTest {
  private static final double EPSILON = 1e-9;

  @TempDir Path directory;

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

  private String file(String name, String content) throws IOException {
    Path path = directory.resolve(name);
    Files.writeString(path, content);
    return path.toString();
  }

  /** The reference front of the examples; normalized, it is (0, 1), (0.5, 0.5) and (1, 0). */
  private String referenceFront() throws IOException {
    return file("reference.csv", "0.0,10.0\n5.0,5.0\n10.0,0.0\n");
  }

  private double lastPrintedValue() {
    return Double.parseDouble(messages.get(messages.size() - 1));
  }

  private double printedValue(String prefix) {
    String message = messages.stream().filter(m -> m.startsWith(prefix)).findFirst().orElseThrow();
    return Double.parseDouble(message.substring(prefix.length()).trim());
  }

  @Nested
  @DisplayName("When asking for one indicator")
  class WhenAskingForOneIndicator {
    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource({"EP, 1.0", "NHV, 1.0", "GD, 0.7071067811865476", "IGD, 0.5270462766947299"})
    @DisplayName("given a front far from the reference front, then the normalized value is printed")
    void givenAFarFront_whenAskingForAnIndicator_thenTheNormalizedValueIsPrinted(
        String indicator, double expected) throws Exception {
      // Arrange: with the bounds of the reference front the point (10, 10) is (1, 1)
      String front = file("front.csv", "10.0,10.0\n");

      // Act
      CommandLineQualityIndicatorTool.main(new String[] {indicator, referenceFront(), front});

      // Assert
      assertThat(lastPrintedValue()).isEqualTo(expected, offset(EPSILON));
    }

    @Test
    @DisplayName("given the reference front itself, then the distance indicators are zero")
    void givenTheReferenceFront_whenAskingForAnIndicator_thenTheDistanceIsZero() throws Exception {
      // Arrange
      String reference = referenceFront();

      // Act
      CommandLineQualityIndicatorTool.main(new String[] {"IGD", reference, reference});

      // Assert
      assertThat(lastPrintedValue()).isZero();
    }

    @Test
    @DisplayName("given a reference front with negative objectives, then its range is used")
    void givenNegativeObjectives_whenAskingForAnIndicator_thenItsRangeIsUsed() throws Exception {
      // Arrange: the same fronts as above, shifted by -100
      String reference =
          file("negative-reference.csv", "-100.0,-90.0\n-95.0,-95.0\n-90.0,-100.0\n");
      String front = file("negative-front.csv", "-90.0,-90.0\n");

      // Act
      CommandLineQualityIndicatorTool.main(new String[] {"EP", reference, front});

      // Assert
      assertThat(lastPrintedValue()).isEqualTo(1.0);
    }
  }

  @Nested
  @DisplayName("When asking for the set coverage")
  class WhenAskingForTheSetCoverage {
    @Test
    @DisplayName("given SC, then the coverage in both directions is printed")
    void givenSC_whenAskingForTheIndicator_thenTheCoverageInBothDirectionsIsPrinted()
        throws Exception {
      // Arrange: the point (1, 1) is dominated by (0.5, 0.5) and dominates no reference point
      String front = file("front.csv", "10.0,10.0\n");

      // Act
      CommandLineQualityIndicatorTool.main(new String[] {"SC", referenceFront(), front});

      // Assert
      assertThat(printedValue("SC(refFront, front):")).isEqualTo(1.0);
      assertThat(printedValue("SC(front, refFront):")).isEqualTo(0.0);
    }
  }

  @Nested
  @DisplayName("When asking for all the indicators")
  class WhenAskingForAllTheIndicators {
    @Test
    @DisplayName("given ALL, then every indicator and the coverage are printed")
    void givenALL_whenAskingForTheIndicators_thenEveryIndicatorAndTheCoverageArePrinted()
        throws Exception {
      // Arrange
      String front = file("front.csv", "10.0,10.0\n");
      List<String> names =
          List.of("EP", "HV", "NHV", "ER", "GD", "IGD", "IGD+", "SP", "GSPREAD", "AHD", "R2");

      // Act
      CommandLineQualityIndicatorTool.main(new String[] {"ALL", referenceFront(), front});

      // Assert
      for (String name : names) {
        assertThat(messages).anyMatch(message -> message.startsWith(name + ": "));
      }
      assertThat(printedValue("EP:")).isEqualTo(1.0);
      assertThat(printedValue("SC(refFront, front):")).isEqualTo(1.0);
      assertThat(printedValue("SC(front, refFront):")).isEqualTo(0.0);
    }
  }

  @Nested
  @DisplayName("When the arguments are not valid")
  class WhenTheArgumentsAreNotValid {
    @Test
    @DisplayName("given a wrong number of arguments, then it is rejected")
    void givenAWrongNumberOfArguments_whenRunning_thenItIsRejected() {
      // Act
      Throwable tooFew =
          catchThrowable(() -> CommandLineQualityIndicatorTool.main(new String[] {"EP"}));
      Throwable tooMany =
          catchThrowable(
              () -> CommandLineQualityIndicatorTool.main(new String[] {"EP", "a", "b", "c"}));

      // Assert
      assertThat(tooFew).isInstanceOf(JMetalException.class);
      assertThat(tooMany).isInstanceOf(JMetalException.class);
    }

    @Test
    @DisplayName("given an indicator that does not exist, then it is rejected")
    void givenAnUnknownIndicator_whenRunning_thenItIsRejected() throws Exception {
      // Arrange
      String front = file("front.csv", "10.0,10.0\n");
      String reference = referenceFront();

      // Act
      Throwable thrown =
          catchThrowable(
              () -> CommandLineQualityIndicatorTool.main(new String[] {"NOPE", reference, front}));

      // Assert
      assertThat(thrown).isInstanceOf(JMetalException.class).hasMessageContaining("NOPE");
    }

    @Test
    @DisplayName("given a file that does not exist, then it is rejected")
    void givenAMissingFile_whenRunning_thenItIsRejected() throws Exception {
      // Arrange
      String reference = referenceFront();
      String missing = directory.resolve("missing.csv").toString();

      // Act
      Throwable thrown =
          catchThrowable(
              () -> CommandLineQualityIndicatorTool.main(new String[] {"EP", reference, missing}));

      // Assert
      assertThat(thrown).isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("given a front with an infinite value, then it is rejected")
    void givenAFrontWithAnInfiniteValue_whenRunning_thenItIsRejected() throws Exception {
      // Arrange
      String front = file("infinite.csv", "Infinity,5.0\n");
      String reference = referenceFront();

      // Act
      Throwable thrown =
          catchThrowable(
              () -> CommandLineQualityIndicatorTool.main(new String[] {"EP", reference, front}));

      // Assert
      assertThat(thrown).isInstanceOf(InvalidConditionException.class);
    }
  }
}
