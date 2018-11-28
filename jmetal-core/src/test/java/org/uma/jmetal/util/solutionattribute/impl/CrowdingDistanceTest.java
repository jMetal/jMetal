package org.uma.jmetal.util.solutionattribute.impl;

import org.junit.Test;
import org.uma.jmetal.util.point.PointSolution;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class CrowdingDistanceTest {
  private static final double EPSILON = 0.000000001 ;

  @Test
  public void shouldTheCrowdingDistanceOfAnEmptySetDoNothing() {
    CrowdingDistance<PointSolution> crowdingDistance = new CrowdingDistance<>();

    List<PointSolution> solutionList = new ArrayList<>() ;

    crowdingDistance.computeDensityEstimator(solutionList);
  }

  @Test
  public void shouldTheCrowdingDistanceOfASingleSolutionBeInfinity() {
    CrowdingDistance<PointSolution> crowdingDistance = new CrowdingDistance<>();

    List<PointSolution> solutionList = new ArrayList<>() ;
    solutionList.add(new PointSolution(3));

    crowdingDistance.computeDensityEstimator(solutionList);
    double value = crowdingDistance.getAttribute(solutionList.get(0)) ;

    assertEquals(Double.POSITIVE_INFINITY, value, EPSILON) ;
  }

  @Test
  public void shouldTheCrowdingDistanceOfTwoSolutionsBeInfinity() {
    CrowdingDistance<PointSolution> crowdingDistance = new CrowdingDistance<>();

    List<PointSolution> solutionList = new ArrayList<>() ;
    solutionList.add(new PointSolution(3));
    solutionList.add(new PointSolution(3));

    crowdingDistance.computeDensityEstimator(solutionList);

    assertEquals(Double.POSITIVE_INFINITY, (double)crowdingDistance.getAttribute(solutionList.get(0)), EPSILON) ;
    assertEquals(Double.POSITIVE_INFINITY, (double)crowdingDistance.getAttribute(solutionList.get(1)), EPSILON) ;
  }

  @Test
  public void shouldTheCrowdingDistanceOfThreeSolutionsCorrectlyAssigned() {
    CrowdingDistance<PointSolution> crowdingDistance = new CrowdingDistance<>();

    PointSolution solution1 = new PointSolution(2) ;
    PointSolution solution2 = new PointSolution(2) ;
    PointSolution solution3 = new PointSolution(2) ;

    solution1.setObjective(0, 0.0);
    solution1.setObjective(1, 1.0);
    solution2.setObjective(0, 1.0);
    solution2.setObjective(1, 0.0);
    solution3.setObjective(0, 0.5);
    solution3.setObjective(1, 0.5);

    List<PointSolution> solutionList = new ArrayList<>() ;
    solutionList.add(solution1);
    solutionList.add(solution2);
    solutionList.add(solution3);

    crowdingDistance.computeDensityEstimator(solutionList);

    assertEquals(Double.POSITIVE_INFINITY, (double)crowdingDistance.getAttribute(solutionList.get(0)), EPSILON) ;
    assertEquals(Double.POSITIVE_INFINITY, (double)crowdingDistance.getAttribute(solutionList.get(1)), EPSILON) ;
    assertEquals(2.0, (double)crowdingDistance.getAttribute(solutionList.get(2)), EPSILON) ;
  }
}