package org.uma.jmetal.util.archive.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;

@DisplayName("Unit tests for the bounded archives")
class BoundedArchivesTest {

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  /** Five non-dominated solutions on the line f1 + f2 = 1; the middle ones are crowded. */
  private static List<DoubleSolution> line() {
    return List.of(
        solution(0.0, 1.0),
        solution(0.45, 0.55),
        solution(0.5, 0.5),
        solution(0.55, 0.45),
        solution(1.0, 0.0));
  }

  private static void addAll(BoundedArchive<DoubleSolution> archive, List<DoubleSolution> list) {
    list.forEach(archive::add);
  }

  @Nested
  @DisplayName("When more solutions than the maximum size are added")
  class WhenTheArchiveIsFull {
    @Test
    @DisplayName("given a crowding distance archive, then it keeps its size and the extremes")
    void givenACrowdingDistanceArchive_whenItOverflows_thenItKeepsTheExtremes() {
      // Arrange
      BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<>(3);
      List<DoubleSolution> line = line();

      // Act
      addAll(archive, line);

      // Assert
      assertThat(archive.size()).isEqualTo(3);
      assertThat(archive.solutions()).contains(line.get(0), line.get(4));
    }

    @Test
    @DisplayName("given a generic archive with crowding distance, then it keeps the extremes")
    void givenAGenericArchive_whenItOverflows_thenItKeepsTheExtremes() {
      // Arrange
      BoundedArchive<DoubleSolution> archive =
          new GenericBoundedArchive<>(3, new CrowdingDistanceDensityEstimator<>());
      List<DoubleSolution> line = line();

      // Act
      addAll(archive, line);

      // Assert
      assertThat(archive.size()).isEqualTo(3);
      assertThat(archive.solutions()).contains(line.get(0), line.get(4));
    }

    @Test
    @DisplayName("given a hypervolume archive, then it removes one of the crowded solutions")
    void givenAHypervolumeArchive_whenItOverflows_thenItKeepsTheExtremes() {
      // Arrange
      BoundedArchive<DoubleSolution> archive =
          new HypervolumeArchive<>(3, new PISAHypervolume<>(new double[] {1.0, 1.0}));
      List<DoubleSolution> line =
          List.of(
              solution(0.1, 0.9), solution(0.5, 0.5), solution(0.52, 0.48), solution(0.9, 0.1));

      // Act
      addAll(archive, line);

      // Assert
      assertThat(archive.size()).isEqualTo(3);
      assertThat(archive.solutions()).contains(line.get(0), line.get(3));
    }

    @Test
    @DisplayName("given a dominated solution, when added, then it is rejected and nothing is pruned")
    void givenADominatedSolution_whenAdded_thenItIsRejected() {
      // Arrange
      BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<>(3);
      archive.add(solution(0.2, 0.2));

      // Act
      boolean added = archive.add(solution(0.5, 0.5));

      // Assert
      assertThat(added).isFalse();
      assertThat(archive.size()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("When two archives are joined")
  class WhenTwoArchivesAreJoined {
    @Test
    @DisplayName("given another archive, when joined, then the result is this archive with the solutions")
    void givenAnotherArchive_whenJoined_thenTheResultIsThisArchiveWithTheSolutions() {
      // Arrange
      CrowdingDistanceArchive<DoubleSolution> archive = new CrowdingDistanceArchive<>(10);
      CrowdingDistanceArchive<DoubleSolution> other = new CrowdingDistanceArchive<>(10);
      archive.add(solution(0.0, 1.0));
      other.add(solution(1.0, 0.0));

      // Act
      var result = archive.join(other);

      // Assert
      assertThat(result).isSameAs(archive);
      assertThat(archive.size()).isEqualTo(2);
      assertThat(other.size()).isEqualTo(1);
    }
  }
}
