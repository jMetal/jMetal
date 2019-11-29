package org.uma.jmetal.component.densityestimator;

import org.junit.Test;
import org.uma.jmetal.component.densityestimator.impl.KnnDensityEstimator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class KnnDensityEstimatorTest {
  private double EPSILON = 0.00000000000001;

  @Test
  public void shouldDensityEstimatorComputeTheRightDistancesCase1() {
    /*
         5 1
         4   2
         3     3
         2
         1         4
         0 1 2 3 4 5
    */
    DoubleProblem problem = new MockDoubleProblem(2);
    KnnDensityEstimator<Solution<?>> densityEstimator = new KnnDensityEstimator<>(1);
    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();
    DoubleSolution solution3 = problem.createSolution();
    DoubleSolution solution4 = problem.createSolution();

    solution1.setObjective(0, 1.0);
    solution1.setObjective(1, 5.0);

    solution2.setObjective(0, 2.0);
    solution2.setObjective(1, 4.0);

    solution3.setObjective(0, 3.0);
    solution3.setObjective(1, 3.0);

    solution4.setObjective(0, 5.0);
    solution4.setObjective(1, 1.0);

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2, solution3, solution4);

    densityEstimator.computeDensityEstimator(solutionList);

    assertEquals(
        Math.sqrt(2), (double) solution1.getAttribute(densityEstimator.getAttributeId()), EPSILON);
    assertEquals(
        Math.sqrt(2), (double) solution2.getAttribute(densityEstimator.getAttributeId()), EPSILON);
    assertEquals(
        Math.sqrt(2), (double) solution3.getAttribute(densityEstimator.getAttributeId()), EPSILON);
    assertEquals(
        Math.sqrt(2 * 2 + 2 * 2),
        (double) solution4.getAttribute(densityEstimator.getAttributeId()),
        EPSILON);
  }

  @Test
  public void shouldSortOrderTheSolutions() {
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
    DoubleProblem problem = new MockDoubleProblem(2);
    KnnDensityEstimator<DoubleSolution> densityEstimator = new KnnDensityEstimator<>(1);

    DoubleSolution solution1 = problem.createSolution();
    DoubleSolution solution2 = problem.createSolution();
    DoubleSolution solution3 = problem.createSolution();
    DoubleSolution solution4 = problem.createSolution();
    DoubleSolution solution5 = problem.createSolution();

    solution1.setObjective(0, 1.0);
    solution1.setObjective(1, 5.0);

    solution2.setObjective(0, 2.0);
    solution2.setObjective(1, 4.0);

    solution3.setObjective(0, 3.0);
    solution3.setObjective(1, 3.0);

    solution4.setObjective(0, 5.0);
    solution4.setObjective(1, 1.0);

    solution5.setObjective(0, 3);
    solution5.setObjective(1, 2);

    List<DoubleSolution> solutionList =
        Arrays.asList(solution1, solution2, solution4, solution3, solution5);

    densityEstimator.computeDensityEstimator(solutionList);
    densityEstimator.sort(solutionList);

    assertEquals(solutionList.get(0), solution4);
    assertEquals(solutionList.get(1), solution1);
    assertEquals(solutionList.get(2), solution2);
    assertEquals(solutionList.get(3), solution5);
  }

  /** Mock class representing a double problem */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(0.0);
        upperLimit.add(5.0);
      }

      setVariableBounds(lowerLimit, upperLimit);
    }

    /** Evaluate() method */
    @Override
    public void evaluate(DoubleSolution solution) {
      solution.setObjective(0, 0.0);
      solution.setObjective(1, 1.0);
    }
  }
}
