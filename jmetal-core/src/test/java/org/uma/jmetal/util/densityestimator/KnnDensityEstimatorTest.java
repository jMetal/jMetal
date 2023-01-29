package org.uma.jmetal.util.densityestimator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.densityestimator.impl.KnnDensityEstimator;

class KnnDensityEstimatorTest {
  private double EPSILON = 0.00000000000001;

  @Test
  void shouldDensityEstimatorComputeTheRightDistancesCase1() {
    /*
         5 1
         4   2
         3     3
         2
         1         4
         0 1 2 3 4 5
    */
    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    KnnDensityEstimator<DoubleSolution> densityEstimator = new KnnDensityEstimator<>(1);
    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();
    DoubleSolution solution3 = problem.createSolution();
    DoubleSolution solution4 = problem.createSolution();

    solution1.objectives()[0] = 1.0;
    solution1.objectives()[1] = 5.0;

    solution2.objectives()[0] = 2.0;
    solution2.objectives()[1] = 4.0;

    solution3.objectives()[0] = 3.0;
    solution3.objectives()[1] = 3.0;

    solution4.objectives()[0] = 5.0;
    solution4.objectives()[1] = 1.0;

    List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2, solution3, solution4);

    densityEstimator.compute(solutionList);

    assertEquals(
        Math.sqrt(2), densityEstimator.value(solution1), EPSILON);
    assertEquals(
        Math.sqrt(2), densityEstimator.value(solution2), EPSILON);
    assertEquals(
        Math.sqrt(2), densityEstimator.value(solution3), EPSILON);
    assertEquals(
        Math.sqrt(2 * 2 + 2 * 2), densityEstimator.value(solution4), EPSILON);
  }

  @Test
  void shouldSortOrderTheSolutions() {
    /*
         5 1
         4   2
         3     3
         2     5
         1         4
         0 1 2 3 4 5

         List: 1,2,3,4,5
         Expected result: 4, 1, 2, 5, 3
    */

    DoubleProblem problem = new FakeDoubleProblem(3, 2, 0) ;

    KnnDensityEstimator<DoubleSolution> densityEstimator = new KnnDensityEstimator<>(1);
    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();
    DoubleSolution solution3 = problem.createSolution();
    DoubleSolution solution4 = problem.createSolution();
    DoubleSolution solution5 = problem.createSolution();

    solution1.objectives()[0] = 1.0;
    solution1.objectives()[1] = 5.0;

    solution2.objectives()[0] = 2.0;
    solution2.objectives()[1] = 4.0;

    solution3.objectives()[0] = 3.0;
    solution3.objectives()[1] = 3.0;

    solution4.objectives()[0] = 5.0;
    solution4.objectives()[1] = 1.0;

    solution5.objectives()[0] = 3.0;
    solution5.objectives()[1] = 2.0;

    List<DoubleSolution> solutionList =
        Arrays.asList(solution1, solution2, solution4, solution3, solution5);

    densityEstimator.compute(solutionList);
    solutionList.sort(Comparator.comparing(densityEstimator::value).reversed());

    assertEquals(solutionList.get(0), solution4);
    assertTrue((solutionList.get(1) == solution1) || (solutionList.get(1) == solution2)); ;
    assertTrue((solutionList.get(2) == solution1) || (solutionList.get(2) == solution2)); ;
    assertTrue((solutionList.get(3) == solution3) || (solutionList.get(3) == solution5)); ;
    assertTrue((solutionList.get(4) == solution3) || (solutionList.get(4) == solution5)); ;
  }
}
