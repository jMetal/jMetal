package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Unit tests for VectorUtils.readVectors")
class VectorUtilsReadVectorsTest {

  private static double[][] read(Path directory, String content, String separator)
      throws IOException {
    Path file = directory.resolve("front.csv");
    Files.writeString(file, content);
    return VectorUtils.readVectors(file.toString(), separator);
  }

  @Test
  @DisplayName("given values separated by spaces, when read, then they are the vectors")
  void givenSpaces_whenRead_thenTheyAreTheVectors(@TempDir Path directory) throws IOException {
    assertThat(read(directory, "1.0 2.0\n3.5   4.5\n", "\\s+"))
        .isDeepEqualTo(new double[][] {{1.0, 2.0}, {3.5, 4.5}});
  }

  @Test
  @DisplayName("given spaces at the beginning or the end of a line, when read, then they are ignored")
  void givenSpacesAtTheEnds_whenRead_thenTheyAreIgnored(@TempDir Path directory)
      throws IOException {
    assertThat(read(directory, "  1.0 2.0  \n\t3.0 4.0\t\n", "\\s+"))
        .isDeepEqualTo(new double[][] {{1.0, 2.0}, {3.0, 4.0}});
  }

  @Test
  @DisplayName("given blank lines, when read, then they are skipped")
  void givenBlankLines_whenRead_thenTheyAreSkipped(@TempDir Path directory) throws IOException {
    assertThat(read(directory, "1.0 2.0\n\n   \n3.0 4.0\n\n", "\\s+"))
        .isDeepEqualTo(new double[][] {{1.0, 2.0}, {3.0, 4.0}});
  }

  @Test
  @DisplayName("given commas and Windows line ends, when read, then they are the vectors")
  void givenCommasAndWindowsLineEnds_whenRead_thenTheyAreTheVectors(@TempDir Path directory)
      throws IOException {
    assertThat(read(directory, "1.0,2.0\r\n3.0, 4.0\r\n", ","))
        .isDeepEqualTo(new double[][] {{1.0, 2.0}, {3.0, 4.0}});
  }

  @Test
  @DisplayName("given an empty file, when read, then there are no vectors")
  void givenAnEmptyFile_whenRead_thenThereAreNoVectors(@TempDir Path directory)
      throws IOException {
    assertThat(read(directory, "\n\n", "\\s+")).isEmpty();
  }
}
