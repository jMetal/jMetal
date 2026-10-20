package org.uma.jmetal.util.archive.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

@DisplayName("Unit tests of the default dominance comparator of the bounded archives")
class BoundedArchiveConstraintsTest {
  private static final int MAX_SIZE = 10;

  static Stream<Arguments> archives() {
    return Stream.of(
        archive("CrowdingDistanceArchive", () -> new CrowdingDistanceArchive<>(MAX_SIZE)),
        archive("AngleArchive", () -> new AngleArchive<>(MAX_SIZE)),
        archive("CosineSimilarityArchive", () -> new CosineSimilarityArchive<>(MAX_SIZE)),
        archive(
            "SpatialSpreadDeviationArchive", () -> new SpatialSpreadDeviationArchive<>(MAX_SIZE)),
        archive("KNNDistanceArchive", () -> new KNNDistanceArchive<>(MAX_SIZE, 2)),
        archive(
            "HypervolumeArchive",
            () -> new HypervolumeArchive<>(MAX_SIZE, new PISAHypervolume<>())));
  }

  private static Arguments archive(String name, Supplier<BoundedArchive<DoubleSolution>> archive) {
    return Arguments.of(name, archive);
  }

  private static DoubleSolution solution(double f1, double f2, double constraint) {
    DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 1);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = constraint;
    return solution;
  }

  @Nested
  @DisplayName("When a solution violates the constraints")
  class WhenASolutionViolatesTheConstraints {
    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.util.archive.impl.BoundedArchiveConstraintsTest#archives")
    @DisplayName(
        "given a feasible solution, when an infeasible one with better objectives is added, then"
            + " the feasible one stays")
    void givenAFeasibleSolution_whenABetterInfeasibleOneIsAdded_thenTheFeasibleOneStays(
        String name, Supplier<BoundedArchive<DoubleSolution>> archiveSupplier) {
      // Arrange
      BoundedArchive<DoubleSolution> archive = archiveSupplier.get();
      DoubleSolution feasible = solution(1.0, 1.0, 0.0);
      archive.add(feasible);

      // Act
      archive.add(solution(0.0, 0.0, -1.0));

      // Assert
      assertThat(archive.solutions()).containsExactly(feasible);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.util.archive.impl.BoundedArchiveConstraintsTest#archives")
    @DisplayName(
        "given an infeasible solution, when a feasible one with worse objectives is added, then it"
            + " replaces the infeasible one")
    void givenAnInfeasibleSolution_whenAFeasibleOneIsAdded_thenItReplacesIt(
        String name, Supplier<BoundedArchive<DoubleSolution>> archiveSupplier) {
      // Arrange
      BoundedArchive<DoubleSolution> archive = archiveSupplier.get();
      archive.add(solution(0.0, 0.0, -1.0));
      DoubleSolution feasible = solution(1.0, 1.0, 0.0);

      // Act
      archive.add(feasible);

      // Assert
      assertThat(archive.solutions()).containsExactly(feasible);
    }
  }

  @Nested
  @DisplayName("When every solution is feasible")
  class WhenEverySolutionIsFeasible {
    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.util.archive.impl.BoundedArchiveConstraintsTest#archives")
    @DisplayName(
        "given feasible solutions, when they are added, then the non-dominated ones are kept, as"
            + " with plain Pareto dominance")
    void givenFeasibleSolutions_whenAdded_thenTheNonDominatedOnesAreKept(
        String name, Supplier<BoundedArchive<DoubleSolution>> archiveSupplier) {
      // Arrange
      BoundedArchive<DoubleSolution> archive = archiveSupplier.get();

      // Act
      archive.add(solution(0.0, 1.0, 0.0));
      archive.add(solution(1.0, 0.0, 0.0));
      archive.add(solution(1.0, 1.0, 0.0));

      // Assert
      assertThat(archive.size()).isEqualTo(2);
    }
  }
}
