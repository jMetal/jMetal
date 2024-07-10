package org.uma.jmetal.util.densityestimator;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;

class CrowdingDistanceDensityEstimatorTest {
  private static final double EPSILON = 0.000000001;

  @Test
  void shouldTheCrowdingDistanceOfASingleSolutionBeInfinity() {
    DensityEstimator<PointSolution> crowdingDistance = new CrowdingDistanceDensityEstimator<>();

    List<PointSolution> solutionList = new ArrayList<>();
    solutionList.add(new PointSolution(3));

    crowdingDistance.compute(solutionList);
    double value = crowdingDistance.value(solutionList.get(0));

    Assertions.assertEquals(Double.POSITIVE_INFINITY, value, EPSILON);
  }

  @Test
  void shouldTheCrowdingDistanceOfTwoSolutionsBeInfinity() {
    DensityEstimator<PointSolution> crowdingDistance = new CrowdingDistanceDensityEstimator<>();

    List<PointSolution> solutionList = new ArrayList<>();
    solutionList.add(new PointSolution(3));
    solutionList.add(new PointSolution(3));

    crowdingDistance.compute(solutionList);

    Assertions.assertEquals(Double.POSITIVE_INFINITY, crowdingDistance.value(solutionList.get(0)),
        EPSILON);
    Assertions.assertEquals(Double.POSITIVE_INFINITY, crowdingDistance.value(solutionList.get(1)),
        EPSILON);
  }

  @Test
  void shouldTheCrowdingDistanceOfThreeSolutionsCorrectlyAssigned() {
    DensityEstimator<PointSolution> crowdingDistance = new CrowdingDistanceDensityEstimator<>();

    PointSolution solution1 = new PointSolution(2);
    PointSolution solution2 = new PointSolution(2);
    PointSolution solution3 = new PointSolution(2);

    solution1.objectives()[0] = 0.0;
    solution1.objectives()[1] = 1.0;
    solution2.objectives()[0] = 1.0;
    solution2.objectives()[1] = 0.0;
    solution3.objectives()[0] = 0.5;
    solution3.objectives()[1] = 0.5;

    List<PointSolution> solutionList = new ArrayList<>();
    solutionList.add(solution1);
    solutionList.add(solution2);
    solutionList.add(solution3);

    crowdingDistance.compute(solutionList);

    Assertions.assertEquals(Double.POSITIVE_INFINITY, crowdingDistance.value(solution1), EPSILON);
    Assertions.assertEquals(Double.POSITIVE_INFINITY, crowdingDistance.value(solution2), EPSILON);
    Assertions.assertEquals(2.0, crowdingDistance.value(solution3), EPSILON);
  }
}
