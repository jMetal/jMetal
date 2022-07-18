package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem DTLZ6
 */
@SuppressWarnings("serial")
public class DTLZ6 extends AbstractDoubleProblem {
  /**
   * Creates a default DTLZ6 problem (12 variables and 3 objectives)
   */
  public DTLZ6() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ6 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ6(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ6");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables();
    int numberOfObjectives = solution.objectives().length ;
    double[] theta = new double[numberOfObjectives - 1];

    double[] f;
    double[] x;

    int k = getNumberOfVariables() - solution.objectives().length + 1;

      x = IntStream.range(0, numberOfVariables).mapToDouble(i -> solution.variables().get(i)).toArray();

    double g = IntStream.range(numberOfVariables - k, numberOfVariables).mapToDouble(i -> Math.pow(x[i], 0.1)).sum();

      double t = java.lang.Math.PI / (4.0 * (1.0 + g));
    theta[0] = x[0] * java.lang.Math.PI / 2;
    for (int i = 1; i < (numberOfObjectives - 1); i++) {
      theta[i] = t * (1.0 + 2.0 * g * x[i]);
    }

    f = IntStream.range(0, numberOfObjectives).mapToDouble(i -> 1.0 + g).toArray();

    for (int i = 0; i < numberOfObjectives; i++) {
      for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
        f[i] *= java.lang.Math.cos(theta[j]);
      }
      if (i != 0) {
        int aux = numberOfObjectives - (i + 1);
        f[i] *= java.lang.Math.sin(theta[aux]);
      }
    }

    IntStream.range(0, numberOfObjectives).forEach(i -> solution.objectives()[i] = f[i]);

    return solution ;
  }
}
