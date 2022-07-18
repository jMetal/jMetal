package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem DTLZ3
 */
@SuppressWarnings("serial")
public class DTLZ3 extends AbstractDoubleProblem {
  /**
   * Creates a default DTLZ3 problem (12 variables and 3 objectives)
   */
  public DTLZ3() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ3 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ3(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ3");

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
    int numberOfObjectives = getNumberOfObjectives();
    double[] f = new double[numberOfObjectives];
    double[] x = IntStream.range(0, numberOfVariables).mapToDouble(i -> solution.variables().get(i)).toArray();

      int k = getNumberOfVariables() - getNumberOfObjectives() + 1;

    double g = IntStream.range(numberOfVariables - k, numberOfVariables).mapToDouble(i -> (x[i] - 0.5) * (x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5))).sum();

      g = 100.0 * (k + g);
    for (int i = 0; i < numberOfObjectives; i++) {
      f[i] = 1.0 + g;
    }

    for (int i = 0; i < numberOfObjectives; i++) {
      for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
        f[i] *= java.lang.Math.cos(x[j] * 0.5 * java.lang.Math.PI);
      }
      if (i != 0) {
        int aux = numberOfObjectives - (i + 1);
        f[i] *= java.lang.Math.sin(x[aux] * 0.5 * java.lang.Math.PI);
      }
    }

    IntStream.range(0, numberOfObjectives).forEach(i -> solution.objectives()[i] = f[i]);

    return solution ;
  }
}
