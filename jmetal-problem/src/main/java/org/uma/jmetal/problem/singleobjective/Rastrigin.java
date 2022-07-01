package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@SuppressWarnings("serial")
public class Rastrigin extends AbstractDoubleProblem {
  /**
   * Constructor
   * Creates a default instance of the Rastrigin problem
   *
   * @param numberOfVariables Number of variables of the problem
   */
  public Rastrigin(Integer numberOfVariables) {
    setNumberOfObjectives(1);
    setNumberOfConstraints(0) ;
    setName("Rastrigin");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-5.12);
      upperLimit.add(5.12);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] x = new double[numberOfVariables] ;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.variables().get(i) ;
    }

    double result = 0.0;
    double a = 10.0;
    double w = 2 * Math.PI;

    for (int i = 0; i < numberOfVariables; i++) {
      result += x[i] * x[i] - a * Math.cos(w * x[i]);
    }
    result += a * numberOfVariables;

    solution.objectives()[0] = result;

    return solution ;
  }
}

