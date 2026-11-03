package org.uma.jmetal.util.archivewithreferencepoint;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;

@DisplayName("Unit tests for the archives with a reference point")
class ArchiveWithReferencePointTest {

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  @Test
  @DisplayName("given an archive without solutions, when the reference point changes, then nothing fails")
  void givenAnEmptyArchive_whenTheReferencePointChanges_thenNothingFails() {
    var archive = new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(5, List.of(0.5, 0.5));

    archive.changeReferencePoint(List.of(0.2, 0.2));

    assertThat(archive.size()).isZero();
  }

  @Test
  @DisplayName("given solutions on both sides of the reference point, when the reference point changes, then the incomparable ones are removed")
  void givenASolutionThatBecomesIncomparable_whenTheReferencePointChanges_thenItIsRemoved() {
    // Arrange: (0.3, 0.3) dominates (0.5, 0.5), and (0.6, 0.6) is dominated by it
    var archive = new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(5, List.of(0.5, 0.5));
    DoubleSolution dominating = solution(0.3, 0.3);
    assertThat(archive.add(dominating)).isTrue();

    // Act: with the new reference point (0.1, 0.9), (0.3, 0.3) is incomparable
    archive.changeReferencePoint(List.of(0.1, 0.9));

    // Assert
    assertThat(archive.solutions()).doesNotContain(dominating);
  }

  @Test
  @DisplayName("given a solution that dominates the reference point, when added, then it is kept")
  void givenASolutionThatDominatesTheReferencePoint_whenAdded_thenItIsKept() {
    var archive = new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(5, List.of(0.5, 0.5));
    DoubleSolution first = solution(0.6, 0.6);
    archive.add(first);

    boolean added = archive.add(solution(0.4, 0.4));

    assertThat(added).isTrue();
    assertThat(archive.solutions()).hasSize(1);
  }
}
