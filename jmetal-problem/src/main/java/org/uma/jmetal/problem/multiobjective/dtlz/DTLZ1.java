package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
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
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ1");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    IntStream.range(0, numberOfVariables).forEach(i -> {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    });

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables();
    int numberOfObjectives = solution.objectives().length ;

    double[] f = new double[numberOfObjectives];
    double[] x;

    int k = getNumberOfVariables() - solution.objectives().length + 1;

      x = IntStream.range(0, numberOfVariables).mapToDouble(i -> solution.variables().get(i)).toArray();

    double g = IntStream.range(numberOfVariables - k, numberOfVariables).mapToDouble(i -> (x[i] - 0.5) * (x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5))).sum();

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

    IntStream.range(0, numberOfObjectives).forEach(i -> solution.objectives()[i] = f[i]);

    return solution ;
  }
}

