package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem Griewank
 */
public class Griewank extends AbstractDoubleProblem {

  /**
   * Constructor
   * Creates a default instance of the Griewank problem
   *
   * @param numberOfVariables Number of variables of the problem
   */
  public Griewank(Integer numberOfVariables)  {
    setNumberOfObjectives(1);
    setNumberOfConstraints(0) ;
    setName("Griewank");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-600.0);
      upperLimit.add(600.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] x = IntStream.range(0, numberOfVariables).mapToDouble(i -> solution.variables().get(i)).toArray();

      double sum = 0.0;
    double mult = 1.0;
    double d = 4000.0;
    for (int var = 0; var < numberOfVariables; var++) {
      sum += x[var] * x[var];
      mult *= Math.cos(x[var] / Math.sqrt(var + 1));
    }

    solution.objectives()[0] = 1.0 / d * sum - mult + 1.0;

    return solution ;
  }
}

