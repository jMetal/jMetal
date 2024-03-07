package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem DTLZ1
 */
@SuppressWarnings("serial")
public class DTLZ1 extends AbstractDoubleProblem {

  /**
   * Creates a default DTLZ1 problem (7 variables and 3 objectives)
   */
  public DTLZ1() {
    this(7, 3);
  }

  /**
   * Creates a DTLZ1 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ1(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    numberOfObjectives(numberOfObjectives);
    name("DTLZ1");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    IntStream.range(0, numberOfVariables).forEach(i -> {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    });

    variableBounds(lowerLimit, upperLimit);
  }

  /**
   * Evaluate() method
   */
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = numberOfVariables();
    int numberOfObjectives = solution.objectives().length;

    double[] f = new double[numberOfObjectives];
    double[] x = new double[numberOfVariables];

    int k = numberOfVariables() - solution.objectives().length + 1;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.variables().get(i);
    }

    double g = 0.0;
    for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
      g += (x[i] - 0.5) * (x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
    }

    g = 100 * (k + g);
    Arrays.fill(f, (1.0 + g) * 0.5);

    for (int i = 0; i < numberOfObjectives; i++) {
      for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
        f[i] *= x[j];
      }
      if (i != 0) {
        int aux = numberOfObjectives - (i + 1);
        /*
        1
        0
        */

        f[i] *= 1 - x[aux];
      }
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }

    return solution;
  }
}

