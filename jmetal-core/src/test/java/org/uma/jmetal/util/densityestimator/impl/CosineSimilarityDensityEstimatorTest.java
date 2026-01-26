package org.uma.jmetal.util.densityestimator.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Class CosineSimilarityDensityEstimator")
class CosineSimilarityDensityEstimatorTest {

  @Nested
  @DisplayName("When compute() is called")
  class ComputeMethod {

    @Test
    @DisplayName("With an empty list, it should not throw an exception")
    void computeWithEmptyListDoesNotThrowException() {
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, false);

      List<DoubleSolution> solutionList = new ArrayList<>();
      densityEstimator.compute(solutionList);

      assertEquals(0, solutionList.size());
    }

    @Test
    @DisplayName("With a single solution, density should be POSITIVE_INFINITY (protected as extreme)")
    void computeWithSingleSolutionAssignsInfinityDensity() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, false);

      DoubleSolution solution = problem.createSolution();
      solution.objectives()[0] = 1.0;
      solution.objectives()[1] = 1.0;

      List<DoubleSolution> solutionList = Arrays.asList(solution);
      densityEstimator.compute(solutionList);

      // Single solution is both extreme in all objectives, so it gets POSITIVE_INFINITY
      assertEquals(Double.POSITIVE_INFINITY, densityEstimator.value(solution));
    }

    @Test
    @DisplayName("With solutions equal to number of objectives, all densities should be 0.0")
    void computeWithSolutionsEqualToObjectivesAssignsZeroDensity() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, false);

      DoubleSolution solution1 = problem.createSolution();
      DoubleSolution solution2 = problem.createSolution();

      solution1.objectives()[0] = 1.0;
      solution1.objectives()[1] = 0.0;

      solution2.objectives()[0] = 0.0;
      solution2.objectives()[1] = 1.0;

      List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2);
      densityEstimator.compute(solutionList);

      assertEquals(0.0, densityEstimator.value(solution1));
      assertEquals(0.0, densityEstimator.value(solution2));
    }

    @Test
    @DisplayName("With multiple solutions, it should assign non-null density values")
    void computeWithMultipleSolutionsAssignsDensityValues() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, false);

      DoubleSolution solution1 = problem.createSolution();
      DoubleSolution solution2 = problem.createSolution();
      DoubleSolution solution3 = problem.createSolution();

      solution1.objectives()[0] = 0.0;
      solution1.objectives()[1] = 1.0;

      solution2.objectives()[0] = 0.5;
      solution2.objectives()[1] = 0.5;

      solution3.objectives()[0] = 1.0;
      solution3.objectives()[1] = 0.0;

      List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2, solution3);
      densityEstimator.compute(solutionList);

      assertNotNull(densityEstimator.value(solution1));
      assertNotNull(densityEstimator.value(solution2));
      assertNotNull(densityEstimator.value(solution3));
    }

    @Test
    @DisplayName("Extreme solutions should have density POSITIVE_INFINITY (protected)")
    void extremeSolutionsShouldHaveInfinityDensity() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, false);

      DoubleSolution solution1 = problem.createSolution();
      DoubleSolution solution2 = problem.createSolution();
      DoubleSolution solution3 = problem.createSolution();
      DoubleSolution solution4 = problem.createSolution();

      // Extreme in objective 0
      solution1.objectives()[0] = 0.0;
      solution1.objectives()[1] = 1.0;

      // Middle solutions
      solution2.objectives()[0] = 0.3;
      solution2.objectives()[1] = 0.7;

      solution3.objectives()[0] = 0.7;
      solution3.objectives()[1] = 0.3;

      // Extreme in objective 1
      solution4.objectives()[0] = 1.0;
      solution4.objectives()[1] = 0.0;

      List<DoubleSolution> solutionList = new ArrayList<>(Arrays.asList(solution1, solution2, solution3, solution4));
      densityEstimator.compute(solutionList);

      // Extreme solutions should have density POSITIVE_INFINITY (protected from pruning)
      assertEquals(Double.POSITIVE_INFINITY, densityEstimator.value(solution1));
      assertEquals(Double.POSITIVE_INFINITY, densityEstimator.value(solution4));
    }

    @Test
    @DisplayName("With normalization enabled, it should compute without errors")
    void computeWithNormalizationEnabled() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, true);

      DoubleSolution solution1 = problem.createSolution();
      DoubleSolution solution2 = problem.createSolution();
      DoubleSolution solution3 = problem.createSolution();

      solution1.objectives()[0] = 10.0;
      solution1.objectives()[1] = 100.0;

      solution2.objectives()[0] = 50.0;
      solution2.objectives()[1] = 50.0;

      solution3.objectives()[0] = 100.0;
      solution3.objectives()[1] = 10.0;

      List<DoubleSolution> solutionList = new ArrayList<>(Arrays.asList(solution1, solution2, solution3));
      densityEstimator.compute(solutionList);

      assertNotNull(densityEstimator.value(solution1));
      assertNotNull(densityEstimator.value(solution2));
      assertNotNull(densityEstimator.value(solution3));
    }
  }

  @Nested
  @DisplayName("When value() is called")
  class ValueMethod {

    @Test
    @DisplayName("With a solution without computed density, it should return 0.0")
    void valueWithoutComputedDensityReturnsZero() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, false);

      DoubleSolution solution = problem.createSolution();

      assertEquals(0.0, densityEstimator.value(solution));
    }
  }

  @Nested
  @DisplayName("When comparator() is called")
  class ComparatorMethod {

    @Test
    @DisplayName("It should return a non-null comparator")
    void comparatorReturnsNonNull() {
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, false);

      assertNotNull(densityEstimator.comparator());
    }

    @Test
    @DisplayName("Comparator should order solutions by density value")
    void comparatorOrdersSolutionsByDensity() {
      DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
      double[] referencePoint = {0.0, 0.0};
      CosineSimilarityDensityEstimator<DoubleSolution> densityEstimator =
          new CosineSimilarityDensityEstimator<>(referencePoint, false);

      DoubleSolution solution1 = problem.createSolution();
      DoubleSolution solution2 = problem.createSolution();
      DoubleSolution solution3 = problem.createSolution();
      DoubleSolution solution4 = problem.createSolution();

      solution1.objectives()[0] = 0.0;
      solution1.objectives()[1] = 1.0;

      solution2.objectives()[0] = 0.25;
      solution2.objectives()[1] = 0.75;

      solution3.objectives()[0] = 0.75;
      solution3.objectives()[1] = 0.25;

      solution4.objectives()[0] = 1.0;
      solution4.objectives()[1] = 0.0;

      List<DoubleSolution> solutionList = new ArrayList<>(Arrays.asList(solution1, solution2, solution3, solution4));
      densityEstimator.compute(solutionList);

      solutionList.sort(densityEstimator.comparator());

      // Solutions should be sorted by density (ascending order)
      // Middle solutions have finite density, extreme solutions have POSITIVE_INFINITY
      // So middle solutions (with lower density) should be first
      assertTrue(densityEstimator.value(solutionList.get(0)) < Double.POSITIVE_INFINITY);
    }
  }
}
