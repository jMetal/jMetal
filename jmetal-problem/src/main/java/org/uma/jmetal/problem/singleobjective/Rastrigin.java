package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Rastrigin extends AbstractDoubleProblem {
  /**
   * Constructor
   * Creates a default instance of the Rastrigin problem
   *
   * @param numberOfVariables Number of variables of the problem
   */
  public Rastrigin(Integer numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(1);
    setNumberOfConstraints(0) ;
    setName("Rastrigin");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-5.12);
      upperLimit.add(5.12);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] x = new double[numberOfVariables] ;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i) ;
    }

    double result = 0.0;
    double a = 10.0;
    double w = 2 * Math.PI;

    for (int i = 0; i < numberOfVariables; i++) {
      result += x[i] * x[i] - a * Math.cos(w * x[i]);
    }
    result += a * numberOfVariables;

    solution.setObjective(0, result);
  }
}

