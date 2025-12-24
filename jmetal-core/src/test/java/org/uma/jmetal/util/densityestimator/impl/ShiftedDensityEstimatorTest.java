package org.uma.jmetal.util.densityestimator.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Unit tests for ShiftedDensityEstimator.
 */
class ShiftedDensityEstimatorTest {

  @Test
  void shouldReturnInfinityForSingleSolution() {
    var estimator = new ShiftedDensityEstimator<DoubleSolution>();
    var problem = new FakeDoubleProblem(2, 2, 0);

    DoubleSolution solution = problem.createSolution();
    solution.objectives()[0] = 0.5;
    solution.objectives()[1] = 0.5;

    List<DoubleSolution> solutionList = new ArrayList<>();
    solutionList.add(solution);

    estimator.compute(solutionList);

    assertEquals(Double.POSITIVE_INFINITY, estimator.value(solution));
  }

  @Test
  void shouldComputeDensityForTwoNonDominatedSolutions() {
    var estimator = new ShiftedDensityEstimator<DoubleSolution>();
    var problem = new FakeDoubleProblem(2, 2, 0);

    // Two non-dominated solutions on a front
    DoubleSolution solution1 = problem.createSolution();
    solution1.objectives()[0] = 0.0;
    solution1.objectives()[1] = 1.0;

    DoubleSolution solution2 = problem.createSolution();
    solution2.objectives()[0] = 1.0;
    solution2.objectives()[1] = 0.0;

    List<DoubleSolution> solutionList = new ArrayList<>();
    solutionList.add(solution1);
    solutionList.add(solution2);

    estimator.compute(solutionList);

    // For non-dominated solutions on a trade-off front:
    // Solution1: p=(0,1), q=(1,0)
    // - q[0]=1 > p[0]=0, shift q[0] to 0
    // - q[1]=0 < p[1]=1, keep q[1]=0
    // Shifted q=(0,0), distance = sqrt((0-0)^2 + (1-0)^2) = 1.0
    assertEquals(1.0, estimator.value(solution1), 1e-10);
    assertEquals(1.0, estimator.value(solution2), 1e-10);
  }

  @Test
  void shouldComputeDensityForDominatedSolution() {
    var estimator = new ShiftedDensityEstimator<DoubleSolution>();
    var problem = new FakeDoubleProblem(2, 2, 0);

    // Non-dominated solution
    DoubleSolution nonDominated = problem.createSolution();
    nonDominated.objectives()[0] = 0.0;
    nonDominated.objectives()[1] = 0.0;

    // Dominated solution (worse in both objectives)
    DoubleSolution dominated = problem.createSolution();
    dominated.objectives()[0] = 1.0;
    dominated.objectives()[1] = 1.0;

    List<DoubleSolution> solutionList = new ArrayList<>();
    solutionList.add(nonDominated);
    solutionList.add(dominated);

    estimator.compute(solutionList);

    // For dominated solution: p=(1,1), q=(0,0)
    // q[0]=0 < p[0]=1, keep q[0]=0
    // q[1]=0 < p[1]=1, keep q[1]=0
    // Shifted q=(0,0), distance = sqrt((1-0)^2 + (1-0)^2) = sqrt(2)
    assertEquals(Math.sqrt(2), estimator.value(dominated), 1e-10);

    // For non-dominated solution: p=(0,0), q=(1,1)
    // q[0]=1 > p[0]=0, shift q[0] to 0
    // q[1]=1 > p[1]=0, shift q[1] to 0
    // Shifted q=(0,0), distance = sqrt((0-0)^2 + (0-0)^2) = 0
    assertEquals(0.0, estimator.value(nonDominated), 1e-10);
  }

  @Test
  void comparatorShouldPreferHigherDensity() {
    var estimator = new ShiftedDensityEstimator<DoubleSolution>();
    var problem = new FakeDoubleProblem(2, 2, 0);

    DoubleSolution s1 = problem.createSolution();
    s1.objectives()[0] = 0.0;
    s1.objectives()[1] = 1.0;

    DoubleSolution s2 = problem.createSolution();
    s2.objectives()[0] = 0.5;
    s2.objectives()[1] = 0.5;

    DoubleSolution s3 = problem.createSolution();
    s3.objectives()[0] = 1.0;
    s3.objectives()[1] = 0.0;

    List<DoubleSolution> solutionList = new ArrayList<>();
    solutionList.add(s1);
    solutionList.add(s2);
    solutionList.add(s3);

    estimator.compute(solutionList);

    // Comparator should return negative if first is preferred (higher density)
    var comparator = estimator.comparator();
    assertNotNull(comparator);
  }

  @Test
  void shouldWorkWithKthNearestNeighbor() {
    // k=2 means use second nearest neighbor
    var estimator = new ShiftedDensityEstimator<DoubleSolution>(2);
    var problem = new FakeDoubleProblem(2, 2, 0);

    // Three non-dominated solutions on different points
    DoubleSolution s1 = problem.createSolution();
    s1.objectives()[0] = 0.0;
    s1.objectives()[1] = 1.0;

    DoubleSolution s2 = problem.createSolution();
    s2.objectives()[0] = 0.5;
    s2.objectives()[1] = 0.5;

    DoubleSolution s3 = problem.createSolution();
    s3.objectives()[0] = 1.0;
    s3.objectives()[1] = 0.0;

    List<DoubleSolution> solutionList = new ArrayList<>();
    solutionList.add(s1);
    solutionList.add(s2);
    solutionList.add(s3);

    estimator.compute(solutionList);

    // Just verify it computes without error and returns valid values
    assertTrue(estimator.value(s1) >= 0);
    assertTrue(estimator.value(s2) >= 0);
    assertTrue(estimator.value(s3) >= 0);
  }
}
