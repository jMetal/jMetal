package org.uma.jmetal.util.neighborhood.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("Unit tests for the weight vectors of WeightVectorNeighborhood")
class WeightVectorNeighborhoodFilesTest {
  private static final String WEIGHT_DIRECTORY = "../resources/weightVectorFiles/moead";
  private static final Pattern FILE_NAME = Pattern.compile("W(\\d+)D_(\\d+)\\.dat");

  static Stream<String> weightFiles() {
    File[] files = new File(WEIGHT_DIRECTORY).listFiles((dir, name) -> FILE_NAME.matcher(name).matches());
    assumeTrue(files != null && files.length > 0, "The weight vector files are not available");
    return Arrays.stream(files).map(File::getName).sorted();
  }

  @Nested
  @DisplayName("When the weight vectors are read from the files of jMetal")
  class WhenTheFilesOfJMetalAreRead {
    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhoodFilesTest#weightFiles")
    @DisplayName("given a file of jMetal, when read, then all its vectors are valid weights")
    void givenAFileOfJMetal_whenRead_thenAllItsVectorsAreValidWeights(String fileName)
        throws IOException {
      // Arrange
      Matcher name = FILE_NAME.matcher(fileName);
      assertThat(name.matches()).isTrue();
      int size = Integer.parseInt(name.group(1));
      int vectors = Integer.parseInt(name.group(2));

      // Act
      WeightVectorNeighborhood<DoubleSolution> neighborhood =
          new WeightVectorNeighborhood<>(vectors, size, Math.min(20, vectors), WEIGHT_DIRECTORY);

      // Assert
      double[][] weights = neighborhood.getWeightVector();
      assertThat(weights).hasDimensions(vectors, size);
      for (int i = 0; i < vectors; i++) {
        assertThat(Arrays.stream(weights[i]).sum()).as("sum of vector %d", i).isCloseTo(1.0, org.assertj.core.data.Offset.offset(1e-4));
        assertThat(neighborhood.getNeighborhood()[i]).as("neighbors of %d", i).contains(i);
      }
    }
  }

  @Nested
  @DisplayName("When the arguments or the file are not valid")
  class WhenTheArgumentsOrTheFileAreNotValid {
    @Test
    @DisplayName("given a neighborhood larger than the number of vectors, then it is rejected")
    void givenALargeNeighborhood_whenCreated_thenItIsRejected() {
      assertThrows(InvalidConditionException.class, () -> new WeightVectorNeighborhood<>(10, 11));
    }

    @Test
    @DisplayName("given a single weight vector, then it is rejected")
    void givenASingleWeightVector_whenCreated_thenItIsRejected() {
      assertThrows(InvalidConditionException.class, () -> new WeightVectorNeighborhood<>(1, 1));
    }

    @Test
    @DisplayName("given a neighborhood of size zero, then it is rejected")
    void givenAnEmptyNeighborhood_whenCreated_thenItIsRejected() {
      assertThrows(InvalidConditionException.class, () -> new WeightVectorNeighborhood<>(10, 0));
    }

    private void assertTheFileIsRejected(Path directory, String content) throws IOException {
      Files.writeString(directory.resolve("W2D_3.dat"), content);
      assertThrows(
          InvalidConditionException.class,
          () -> new WeightVectorNeighborhood<>(3, 2, 2, directory.toString()));
    }

    @Test
    @DisplayName("given a file with fewer vectors than expected, then it is rejected")
    void givenAShortFile_whenRead_thenItIsRejected(@TempDir Path directory) throws IOException {
      assertTheFileIsRejected(directory, "1 0\n0 1\n");
    }

    @Test
    @DisplayName("given a file with more vectors than expected, then it is rejected")
    void givenALongFile_whenRead_thenItIsRejected(@TempDir Path directory) throws IOException {
      assertTheFileIsRejected(directory, "1 0\n0.5 0.5\n0 1\n0.2 0.8\n");
    }

    @Test
    @DisplayName("given a file with a vector of another size, then it is rejected")
    void givenAVectorOfAnotherSize_whenRead_thenItIsRejected(@TempDir Path directory)
        throws IOException {
      assertTheFileIsRejected(directory, "1 0\n0.5 0.3 0.2\n0 1\n");
    }

    @Test
    @DisplayName("given values separated by commas and blank lines, when read, then they are accepted")
    void givenCommasAndBlankLines_whenRead_thenTheyAreAccepted(@TempDir Path directory)
        throws IOException {
      // Arrange
      Files.writeString(directory.resolve("W2D_3.dat"), "1.0,0.0\r\n\r\n0.5,0.5\r\n0.0,1.0\r\n\r\n");

      // Act
      WeightVectorNeighborhood<DoubleSolution> neighborhood =
          new WeightVectorNeighborhood<>(3, 2, 2, directory.toString());

      // Assert
      assertThat(neighborhood.getWeightVector())
          .isDeepEqualTo(new double[][] {{1.0, 0.0}, {0.5, 0.5}, {0.0, 1.0}});
    }
  }
}
