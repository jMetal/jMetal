package org.uma.jmetal.solution.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Integration test to compare the performance of {@link ArrayDoubleSolution} against
 * {@link DefaultDoubleSolution}
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DoubleSolutionComparisonIT {
  @Ignore
  @Test
  public void compareDoubleSolutionImplementationsWhenCreatingSolutions() {
    DoubleProblem problem1 = new MockedDoubleProblem1(500) ;
    DoubleProblem problem2 = new MockedDoubleProblem2(500) ;

    long startTime ;

    startTime = System.currentTimeMillis() ;
    for (int i = 0 ; i < 150000; i++) {
      problem1.createSolution() ;
    }
    long timeToCreateObjectsWithProblem1 = System.currentTimeMillis() - startTime ;

    startTime = System.currentTimeMillis() ;
    for (int i = 0 ; i < 150000; i++) {
      problem2.createSolution() ;
    }
    long timeToCreateObjectsWithProblem2 = System.currentTimeMillis() - startTime ;

    JMetalLogger.logger.info("Time to create objects with ArrayDoubleSolution: "
            + timeToCreateObjectsWithProblem1);
    JMetalLogger.logger.info("Time to create objects with DefaultDoubleSolution: "
            + timeToCreateObjectsWithProblem2);

    assertTrue(timeToCreateObjectsWithProblem1 < timeToCreateObjectsWithProblem2) ;
  }

  @Ignore
  @Test
  public void compareDoubleSolutionImplementationsWhenEvaluatingSolutions() {
    DoubleProblem problem1 = new MockedDoubleProblem1(20000) ;
    DoubleProblem problem2 = new MockedDoubleProblem2(20000) ;

    long startTime ;

    DoubleSolution solution ;
    solution = problem1.createSolution() ;
    startTime = System.currentTimeMillis() ;
    for (int i = 0 ; i < 1500000; i++) {
      problem1.evaluate(solution);
    }
    long timeToCreateObjectsWithProblem1 = System.currentTimeMillis() - startTime ;

    solution = problem2.createSolution() ;
    startTime = System.currentTimeMillis() ;
    for (int i = 0 ; i < 1500000; i++) {
      problem2.evaluate(solution) ;
    }
    long timeToCreateObjectsWithProblem2 = System.currentTimeMillis() - startTime ;

    JMetalLogger.logger.info("Time to runAlgorithm solutions with ArrayDoubleSolution: "
            + timeToCreateObjectsWithProblem1);
    JMetalLogger.logger.info("Time to runAlgorithm solutions with DefaultDoubleSolution: "
            + timeToCreateObjectsWithProblem2);

    assertTrue(timeToCreateObjectsWithProblem1 < timeToCreateObjectsWithProblem2) ;
  }


  @SuppressWarnings("serial")
  private class MockedDoubleProblem1 extends AbstractDoubleProblem {
    public MockedDoubleProblem1(int numberOfVariables) {
      setNumberOfVariables(numberOfVariables);
      setNumberOfObjectives(2);
      setNumberOfConstraints(0);

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

      for (int i = 0 ; i < getNumberOfVariables(); i++) {
        lowerLimit.add(0.0);
        upperLimit.add(1.0);
      }

      setLowerLimit(lowerLimit);
      setUpperLimit(upperLimit);
    }

    public void evaluate(DoubleSolution solution) {
      double[] f = new double[getNumberOfObjectives()];

      f[0] = solution.getVariableValue(0)+0.0;
      double g = this.evalG(solution);
      double h = this.evalH(f[0], g);
      f[1] = h * g;

      solution.setObjective(0, f[0]);
      solution.setObjective(1, f[1]);
    }

    /**
     * Returns the value of the ZDT1 function G.
     *
     * @param solution Solution
     */
    private double evalG(DoubleSolution solution) {
      double g = 0.0;
      for (int i = 1; i < solution.getNumberOfVariables(); i++) {
        g += solution.getVariableValue(i);
      }
      double constant = 9.0 / (solution.getNumberOfVariables() - 1.0);
      g = constant * g;
      g = g + 1.0;
      return g;
    }

    /**
     * Returns the value of the ZDT1 function H.
     *
     * @param f First argument of the function H.
     * @param g Second argument of the function H.
     */
    public double evalH(double f, double g) {
      double h ;
      h = 1.0 - Math.sqrt(f / g);
      return h;
    }

    @Override
    public DoubleSolution createSolution() {
      return new ArrayDoubleSolution(this)  ;
    }
  }

  @SuppressWarnings("serial")
  private class MockedDoubleProblem2 extends MockedDoubleProblem1 {
    public MockedDoubleProblem2(int numberOfVariables) {
      super(numberOfVariables) ;
    }

    @Override
    public DoubleSolution createSolution() {
      return new DefaultDoubleSolution(this)  ;
    }
  }
}
