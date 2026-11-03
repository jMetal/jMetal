package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

@DisplayName("Unit tests for StoredSolutionsUtils.readSolutionsFromFile")
class StoredSolutionsUtilsTest {

  private static List<PointSolution> read(Path directory, String content, int objectives)
      throws IOException {
    Path file = directory.resolve("solutions.csv");
    Files.writeString(file, content);
    return StoredSolutionsUtils.readSolutionsFromFile(file.toString(), objectives);
  }

  @Test
  @DisplayName("given lines with the objectives, when read, then they are the objectives of the solutions")
  void givenLines_whenRead_thenTheyAreTheObjectives(@TempDir Path directory) throws IOException {
    List<PointSolution> solutions = read(directory, "1.0 2.0\n3.5,4.5\n", 2);

    assertThat(solutions).hasSize(2);
    assertThat(solutions.get(0).objectives()).containsExactly(1.0, 2.0);
    assertThat(solutions.get(1).objectives()).containsExactly(3.5, 4.5);
  }

  @Test
  @DisplayName("given more values than objectives in a line, when read, then the rest is ignored")
  void givenMoreValuesThanObjectives_whenRead_thenTheRestIsIgnored(@TempDir Path directory)
      throws IOException {
    List<PointSolution> solutions = read(directory, "1.0 2.0 3.0 4.0 variables\n", 2);

    assertThat(solutions.get(0).objectives()).containsExactly(1.0, 2.0);
  }

  @Test
  @DisplayName("given blank lines and spaces at the beginning of a line, when read, then they are ignored")
  void givenBlankLinesAndLeadingSpaces_whenRead_thenTheyAreIgnored(@TempDir Path directory)
      throws IOException {
    List<PointSolution> solutions = read(directory, "  1.0 2.0\n\n   \n3.0 4.0\n\n", 2);

    assertThat(solutions).hasSize(2);
    assertThat(solutions.get(0).objectives()).containsExactly(1.0, 2.0);
    assertThat(solutions.get(1).objectives()).containsExactly(3.0, 4.0);
  }

  @Test
  @DisplayName("given a file that does not exist, when read, then an exception is raised")
  void givenAMissingFile_whenRead_thenAnExceptionIsRaised(@TempDir Path directory) {
    assertThrows(
        JMetalException.class,
        () -> StoredSolutionsUtils.readSolutionsFromFile(directory.resolve("none.csv").toString(), 2));
  }
}
