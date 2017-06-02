package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a Sphere problem.
 */
@SuppressWarnings("serial")
public class Sphere extends AbstractDoubleProblem {
  /** Constructor */
  public Sphere() {
    this(10) ;
  }

  /** Constructor */
  public Sphere(Integer numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(1);
    setName("Sphere");

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

    double sum = 0.0 ;
    for (int var = 0; var < numberOfVariables; var++) {
      double value = x[var];
      sum += value * value;
    }

    solution.setObjective(0, sum);
  }
}

