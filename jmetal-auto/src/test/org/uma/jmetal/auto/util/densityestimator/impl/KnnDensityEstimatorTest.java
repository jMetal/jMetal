package org.uma.jmetal.auto.util.densityestimator.impl;

import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KnnDensityEstimatorTest {

  @Test
  public void test() {
    DoubleProblem problem = new MockDoubleProblem(2) ;
    KnnDensityEstimator<Solution<?>> densityEstimator = new KnnDensityEstimator<>() ;
    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    DoubleSolution solution3 = problem.createSolution() ;
    DoubleSolution solution4 = problem.createSolution() ;

    solution1.setObjective(0, 1.0);
    solution1.setObjective(1, 5.0);

    solution2.setObjective(0, 2.0);
    solution2.setObjective(1, 4.0);

    solution3.setObjective(0, 2.0);
    solution3.setObjective(1, 3.0);

    solution4.setObjective(0, 4.0);
    solution4.setObjective(1, 2.0);

    List<Solution<?>> solutionList = Arrays.asList(solution1, solution2, solution3, solution4) ;

    densityEstimator.computeDensityEstimator(solutionList);

    int a = 4 ;
  }

  /**
   * Mock class representing a double problem
   */
  @SuppressWarnings("serial")
  private class MockDoubleProblem extends AbstractDoubleProblem {

    /** Constructor */
    public MockDoubleProblem(Integer numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

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