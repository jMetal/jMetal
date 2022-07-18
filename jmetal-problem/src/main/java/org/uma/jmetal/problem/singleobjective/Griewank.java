package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.Arrays;
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

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-600.0);
      upperLimit.add(600.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    var numberOfVariables = getNumberOfVariables() ;

    var x = new double[10];
    var count = 0;
      for (var i = 0; i < numberOfVariables; i++) {
          double v = solution.variables().get(i);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v;
      }
      x = Arrays.copyOfRange(x, 0, count);

    var sum = 0.0;
    var mult = 1.0;
    var d = 4000.0;
    for (var var = 0; var < numberOfVariables; var++) {
      sum += x[var] * x[var];
      mult *= Math.cos(x[var] / Math.sqrt(var + 1));
    }

    solution.objectives()[0] = 1.0 / d * sum - mult + 1.0;

    return solution ;
  }
}

