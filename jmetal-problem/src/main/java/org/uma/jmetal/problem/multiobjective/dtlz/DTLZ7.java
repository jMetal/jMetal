package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem DTLZ7
 */
@SuppressWarnings("serial")
public class DTLZ7 extends AbstractDoubleProblem {
  /**
   * Creates a default DTLZ7 problem (22 variables and 3 objectives)
   */
  public DTLZ7() {
    this(22, 3);
  }

  /**
   * Creates a DTLZ7 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ7(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("DTLZ7");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  public void evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables();
    int numberOfObjectives = getNumberOfObjectives() ;

    double[] f = new double[numberOfObjectives];
    double[] x = new double[numberOfVariables] ;

    int k = getNumberOfVariables() - getNumberOfObjectives() + 1;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i) ;
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

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}
