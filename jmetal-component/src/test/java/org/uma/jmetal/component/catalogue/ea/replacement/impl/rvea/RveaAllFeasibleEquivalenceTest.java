package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Original RVEA-family full-state trajectory equivalence")
class RveaAllFeasibleEquivalenceTest {
  static Stream<Arguments> cases() throws IOException {
    // This oracle was generated with the unmodified upstream 438afd3 implementation.
    try (var input =
            RveaAllFeasibleEquivalenceTest.class.getResourceAsStream(
                "/rvea/unconstrained-438afd3.csv");
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
      Map<String, List<String[]>> groups =
          reader
              .lines()
              .skip(1)
              .map(line -> line.split(","))
              .collect(
                  Collectors.groupingBy(row -> String.join(",", row[0], row[1], row[2], row[3])));
      return groups.entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .flatMap(
              entry -> {
                String[] key = entry.getKey().split(",");
                List<String> expected = entry.getValue().stream().map(row -> row[5]).toList();
                return Stream.of(0, 3)
                    .map(
                        constraints ->
                            Arguments.of(
                                key[0],
                                Integer.parseInt(key[1]),
                                key[2],
                                Long.parseLong(key[3]),
                                constraints,
                                expected));
              })
          .toList()
          .stream();
    }
  }

  @ParameterizedTest(name = "{0}, M={1}, {2}, seed={3}, constraints={4}")
  @MethodSource("cases")
  @DisplayName("given feasible inputs, when evolving 24 generations, then match original states")
  void givenFeasibleInputs_whenEvolving_thenMatchOriginalStates(
      String variant,
      int objectives,
      String shape,
      long seed,
      int constraints,
      List<String> expected) {
    // Arrange: immutable oracle covers IDs, objective bits, counts, vectors, archive, and final
    // RNG.
    // Act
    var actual = RveaTrajectoryFixtures.trajectory(variant, objectives, shape, seed, constraints);

    // Assert
    assertEquals(expected, actual);
  }

  @ParameterizedTest(name = "mixed {0}, M={1}, {2}, seed={3}, constraints={4}")
  @MethodSource("cases")
  @DisplayName(
      "given infeasible ideals, when evolving, then preserve feasible geometry and archives")
  void givenInfeasibleIdeals_whenEvolving_thenPreserveFeasibleGeometryAndArchives(
      String variant,
      int objectives,
      String shape,
      long seed,
      int constraints,
      List<String> expected) {
    // Arrange: the same feasible inputs plus an infeasible point dominating every objective.
    // Act: inspect the feasible survivors, all vectors, the entire archive, and final RNG state.
    var actual =
        RveaTrajectoryFixtures.trajectory(variant, objectives, shape, seed, constraints, true);

    // Assert
    assertEquals(expected, actual);
  }
}
