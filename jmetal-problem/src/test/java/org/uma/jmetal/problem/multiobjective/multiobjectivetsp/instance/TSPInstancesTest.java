package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Checks the multi-objective TSP instances without instantiating them: their constructors read the
 * distance files with paths relative to the working directory of the program (the root of the
 * repository), which is not the one of the tests (the module), so each file that a class names is
 * looked for in {@code ../resources/tspInstances}.
 */
@DisplayName("Unit tests for the multi-objective TSP instances")
class TSPInstancesTest {

  private static final Path SOURCE_DIRECTORY =
      Path.of(
          "src/main/java/org/uma/jmetal/problem/multiobjective/multiobjectivetsp/instance");
  private static final Path INSTANCES_DIRECTORY = Path.of("../resources/tspInstances");
  private static final Pattern FILE = Pattern.compile("resources/tspInstances/([\\w.\\-]+\\.tsp)");

  @Test
  @DisplayName("given every instance class, when its distance files are looked for, then all exist")
  void givenEveryInstanceClass_whenItsFilesAreLookedFor_thenAllExist() throws IOException {
    // Arrange
    List<String> missing = new ArrayList<>();
    int files = 0;

    // Act
    try (Stream<Path> sources = Files.list(SOURCE_DIRECTORY)) {
      for (Path source : sources.filter(path -> path.toString().endsWith(".java")).toList()) {
        Matcher matcher = FILE.matcher(Files.readString(source));
        while (matcher.find()) {
          files++;
          if (!Files.exists(INSTANCES_DIRECTORY.resolve(matcher.group(1)))) {
            missing.add(source.getFileName() + " reads " + matcher.group(1));
          }
        }
      }
    }

    // Assert
    assertTrue(files > 20, "only " + files + " distance files found");
    assertEquals(List.of(), missing);
  }
}
