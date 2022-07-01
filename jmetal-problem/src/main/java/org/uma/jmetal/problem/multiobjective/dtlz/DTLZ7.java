package org.uma.jmetal.problem.multiobjective.dtlz;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** Class representing problem DTLZ7 */
@SuppressWarnings("serial")
public class DTLZ7 extends AbstractDoubleProblem {
  /** Creates a default DTLZ7 problem (22 variables and 3 objectives) */
  public DTLZ7() {
    this(22, 3);
  }

  /**
   * Creates a DTLZ7 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ7(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ7");

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
    int numberOfObjectives = solution.objectives().length;

    double[] f = new double[numberOfObjectives];
    double[] x = new double[numberOfVariables];

    int k = getNumberOfVariables() - solution.objectives().length + 1;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.variables().get(i);
    }

    double g = 0.0;
    for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
      g += x[i];
    }

    g = 1 + (9.0 * g) / k;

    System.arraycopy(x, 0, f, 0, numberOfObjectives - 1);

    double h = 0.0;
    for (int i = 0; i < numberOfObjectives - 1; i++) {
      h += (f[i] / (1.0 + g)) * (1 + Math.sin(3.0 * Math.PI * f[i]));
    }

    h = numberOfObjectives - h;

    f[numberOfObjectives - 1] = (1 + g) * h;

    IntStream.range(0, numberOfObjectives).forEach(i -> solution.objectives()[i] = f[i]);

    return solution;
  }
}
