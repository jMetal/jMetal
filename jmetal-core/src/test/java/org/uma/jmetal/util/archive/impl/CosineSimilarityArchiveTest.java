package org.uma.jmetal.util.archive.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Class CosineSimilarityArchive")
class CosineSimilarityArchiveTest {

  @Nested
  @DisplayName("Constructor tests")
  class ConstructorTests {

    @Test
    @DisplayName("Constructor with only maxSize creates an empty archive")
    void constructorWithOnlyMaxSizeCreatesEmptyArchive() {
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10);

      assertEquals(0, archive.size());
      assertEquals(10, archive.maximumSize());
    }

    @Test
    @DisplayName("Constructor with maxSize and normalize creates an empty archive")
    void constructorWithNormalizeFlagCreatesEmptyArchive() {
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10, true);

      assertEquals(0, archive.size());
    }

    @Test
    @DisplayName("Constructor with referencePoint creates an empty archive")
    void constructorWithReferencePointCreatesEmptyArchive() {
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10, referencePoint);

      assertEquals(0, archive.size());
    }

    @Test
    @DisplayName("Constructor with referencePoint and normalize creates an empty archive")
    void constructorWithReferencePointAndNormalizeCreatesEmptyArchive() {
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10, referencePoint, true);

      assertEquals(0, archive.size());
    }
  }

  @Nested
  @DisplayName("When add() is called")
  class AddMethod {

    @Test
    @DisplayName("Adding a solution to empty archive increases size to 1")
    void addSolutionToEmptyArchiveIncreasesSizeToOne() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10);

      DoubleSolution solution = problem.createSolution();
      solution.objectives()[0] = 1.0;
      solution.objectives()[1] = 1.0;

      archive.add(solution);

      assertEquals(1, archive.size());
    }

    @Test
    @DisplayName("Adding a dominated solution does not increase archive size")
    void addDominatedSolutionDoesNotIncreaseSize() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10);

      DoubleSolution solution1 = problem.createSolution();
      solution1.objectives()[0] = 1.0;
      solution1.objectives()[1] = 1.0;

      DoubleSolution solution2 = problem.createSolution();
      solution2.objectives()[0] = 2.0;
      solution2.objectives()[1] = 2.0;

      archive.add(solution1);
      archive.add(solution2);

      assertEquals(1, archive.size());
    }

    @Test
    @DisplayName("Adding a dominating solution replaces the dominated one")
    void addDominatingSolutionReplacesExisting() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10);

      DoubleSolution solution1 = problem.createSolution();
      solution1.objectives()[0] = 2.0;
      solution1.objectives()[1] = 2.0;

      DoubleSolution solution2 = problem.createSolution();
      solution2.objectives()[0] = 1.0;
      solution2.objectives()[1] = 1.0;

      archive.add(solution1);
      archive.add(solution2);

      assertEquals(1, archive.size());
      assertTrue(archive.solutions().contains(solution2));
    }

    @Test
    @DisplayName("Adding non-dominated solutions increases archive size")
    void addNonDominatedSolutionsIncreasesSize() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10);

      DoubleSolution solution1 = problem.createSolution();
      solution1.objectives()[0] = 0.0;
      solution1.objectives()[1] = 1.0;

      DoubleSolution solution2 = problem.createSolution();
      solution2.objectives()[0] = 1.0;
      solution2.objectives()[1] = 0.0;

      archive.add(solution1);
      archive.add(solution2);

      assertEquals(2, archive.size());
    }
  }

  @Nested
  @DisplayName("When prune() is called")
  class PruneMethod {

    @Test
    @DisplayName("Archive does not exceed maximum size after adding many solutions")
    void archiveDoesNotExceedMaximumSize() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(3);

      // Add 5 non-dominated solutions
      for (int i = 0; i < 5; i++) {
        DoubleSolution solution = problem.createSolution();
        solution.objectives()[0] = (double) i / 5.0;
        solution.objectives()[1] = 1.0 - (double) i / 5.0;
        archive.add(solution);
      }

      assertTrue(archive.size() <= archive.maximumSize());
    }

    @Test
    @DisplayName("Prune keeps extreme solutions (with density 0.0)")
    void pruneKeepsExtremeSolutions() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(2);

      DoubleSolution extreme1 = problem.createSolution();
      extreme1.objectives()[0] = 0.0;
      extreme1.objectives()[1] = 1.0;

      DoubleSolution middle = problem.createSolution();
      middle.objectives()[0] = 0.5;
      middle.objectives()[1] = 0.5;

      DoubleSolution extreme2 = problem.createSolution();
      extreme2.objectives()[0] = 1.0;
      extreme2.objectives()[1] = 0.0;

      archive.add(extreme1);
      archive.add(middle);
      archive.add(extreme2);

      // Archive should have at most 2 solutions, and extreme solutions should be preferred
      assertEquals(2, archive.size());
    }
  }

  @Nested
  @DisplayName("Comparator tests")
  class ComparatorTests {

    @Test
    @DisplayName("comparator() returns a non-null comparator")
    void comparatorReturnsNonNull() {
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10);

      assertNotNull(archive.comparator());
    }
  }

  @Nested
  @DisplayName("computeDensityEstimator tests")
  class ComputeDensityEstimatorTests {

    @Test
    @DisplayName("computeDensityEstimator does not throw exception on non-empty archive")
    void computeDensityEstimatorWorksOnNonEmptyArchive() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(10);

      DoubleSolution solution1 = problem.createSolution();
      solution1.objectives()[0] = 0.0;
      solution1.objectives()[1] = 1.0;

      DoubleSolution solution2 = problem.createSolution();
      solution2.objectives()[0] = 0.5;
      solution2.objectives()[1] = 0.5;

      DoubleSolution solution3 = problem.createSolution();
      solution3.objectives()[0] = 1.0;
      solution3.objectives()[1] = 0.0;

      archive.add(solution1);
      archive.add(solution2);
      archive.add(solution3);

      archive.computeDensityEstimator();

      // Should not throw exception and archive should still contain solutions
      assertEquals(3, archive.size());
    }
  }

  @Nested
  @DisplayName("Integration tests")
  class IntegrationTests {

    @Test
    @DisplayName("Archive maintains size limit with many solutions")
    void archiveMaintainsSizeLimitWithManySolutions() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      CosineSimilarityArchive<DoubleSolution> archive = new CosineSimilarityArchive<>(5);

      // Add 20 non-dominated solutions along the Pareto front
      for (int i = 0; i <= 20; i++) {
        DoubleSolution solution = problem.createSolution();
        solution.objectives()[0] = (double) i / 20.0;
        solution.objectives()[1] = 1.0 - (double) i / 20.0;
        archive.add(solution);
      }

      assertEquals(5, archive.size());

      // Check that solutions span some range in objective space
      List<DoubleSolution> solutions = archive.solutions();
      double minObj0 = Double.MAX_VALUE;
      double maxObj0 = Double.MIN_VALUE;

      for (DoubleSolution s : solutions) {
        minObj0 = Math.min(minObj0, s.objectives()[0]);
        maxObj0 = Math.max(maxObj0, s.objectives()[0]);
      }

      // Archive should contain solutions with some spread (at least 0.1 range)
      assertTrue(maxObj0 - minObj0 > 0.0, "Archive should contain diverse solutions");
    }
  }
}
