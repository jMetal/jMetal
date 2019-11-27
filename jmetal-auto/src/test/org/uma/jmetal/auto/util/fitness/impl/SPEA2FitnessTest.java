package org.uma.jmetal.auto.util.fitness.impl;

import org.junit.Test;
import org.uma.jmetal.auto.util.fitness.Fitness;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SPEA2FitnessTest {

  private double EPSILON = 0.00000000000001;

  @Test
  public void shouldAllTheSolutionHaveAFitnessValueLowerThanOneIfAllOfThemAreNonDominated() {
    /*
         5 1
         4   2
         3     3
         2
         1         4
         0 1 2 3 4 5
    */
    DoubleProblem problem = new MockDoubleProblem(2);
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

    List<DoubleSolution> solutionList = Arrays.asList(solution1, solution2, solution3, solution4);

    Fitness<DoubleSolution> fitness = new SPEA2Fitness<>() ;
    fitness.computeFitness(solutionList);

    assertTrue((double) fitness.getFitness(solution1) < 1.0);
    assertTrue((double) fitness.getFitness(solution2) < 1.0);
    assertTrue((double) fitness.getFitness(solution3) < 1.0);
    assertTrue((double) fitness.getFitness(solution4) < 1.0);

    assertEquals(4, solutionList.size());
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