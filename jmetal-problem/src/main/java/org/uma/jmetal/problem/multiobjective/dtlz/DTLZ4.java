package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem DTLZ4
 */
@SuppressWarnings("serial")
public class DTLZ4 extends AbstractDoubleProblem {
  /**
   * Creates a default DTLZ4 problem (12 variables and 3 objectives)
   */
  public DTLZ4() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ4 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ4(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ4");

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
    double alpha = 100.0;

    double[] f;
    double[] x;

    int k = getNumberOfVariables() - solution.objectives().length + 1;

      x = IntStream.range(0, numberOfVariables).mapToDouble(i -> solution.variables().get(i)).toArray();

    double g = IntStream.range(numberOfVariables - k, numberOfVariables).mapToDouble(i -> (x[i] - 0.5) * (x[i] - 0.5)).sum();

    f = IntStream.range(0, numberOfObjectives).mapToDouble(i -> 1.0 + g).toArray();

    for (int i = 0; i < numberOfObjectives; i++) {
      for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
        f[i] *= java.lang.Math.cos(java.lang.Math.pow(x[j], alpha) * (java.lang.Math.PI / 2.0));
      }
      if (i != 0) {
        int aux = numberOfObjectives - (i + 1);
        f[i] *= java.lang.Math.sin(java.lang.Math.pow(x[aux], alpha) * (java.lang.Math.PI / 2.0));
      }
    }

    IntStream.range(0, numberOfObjectives).forEach(i -> solution.objectives()[i] = f[i]);

    return solution ;
  }
}
