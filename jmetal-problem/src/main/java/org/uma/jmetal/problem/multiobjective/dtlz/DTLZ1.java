package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
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
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ1");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables();
    int numberOfObjectives = solution.objectives().length ;

    double[] f = new double[numberOfObjectives];
    double[] x;

    int k = getNumberOfVariables() - solution.objectives().length + 1;

    double[] arr = new double[10];
    int count = 0;
    for (int i2 = 0; i2 < numberOfVariables; i2++) {
      double v1 = solution.variables().get(i2);
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = v1;
    }
    arr = Arrays.copyOfRange(arr, 0, count);
    x = arr;

    double g = 0.0;
    for (int i1 = numberOfVariables - k; i1 < numberOfVariables; i1++) {
      double v = (x[i1] - 0.5) * (x[i1] - 0.5) - Math.cos(20.0 * Math.PI * (x[i1] - 0.5));
      g += v;
    }

    g = 100 * (k + g);
    for (int i = 0; i < numberOfObjectives; i++) {
      f[i] = (1.0 + g) * 0.5;
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
        f[i] *= x[j];
      }
      if (i != 0) {
        int aux = numberOfObjectives - (i + 1);
        f[i] *= 1 - x[aux];
      }
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }

    return solution ;
  }
}

