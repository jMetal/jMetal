package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem Griewank
 */
@SuppressWarnings("serial")
public class Griewank extends AbstractDoubleProblem {

  /**
   * Constructor
   * Creates a default instance of the Griewank problem
   *
   * @param numberOfVariables Number of variables of the problem
   */
  public Griewank(Integer numberOfVariables)  {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(1);
    setNumberOfConstraints(0) ;
    setName("Griewank");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-600.0);
      upperLimit.add(600.0);
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

    double sum = 0.0;
    double mult = 1.0;
    double d = 4000.0;
    for (int var = 0; var < numberOfVariables; var++) {
      sum += x[var] * x[var];
      mult *= Math.cos(x[var] / Math.sqrt(var + 1));
    }

    solution.setObjective(0, 1.0 / d * sum - mult + 1.0);
  }
}

