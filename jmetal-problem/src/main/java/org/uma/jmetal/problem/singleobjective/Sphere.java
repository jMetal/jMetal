package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double sum = 0.0;
    for (double v : solution.variables()) {
      sum += v * v;
    }

    solution.objectives()[0] =  sum;

    return solution ;
  }
}

