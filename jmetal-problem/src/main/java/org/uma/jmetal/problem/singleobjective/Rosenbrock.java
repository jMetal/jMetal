package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class Rosenbrock extends AbstractDoubleProblem {

  /**
   * Constructor
   * Creates a default instance of the Rosenbrock problem
   *
   * @param numberOfVariables Number of variables of the problem
   */
  public Rosenbrock(Integer numberOfVariables) {
    setNumberOfObjectives(1);
    setNumberOfConstraints(0) ;
    setName("Rosenbrock");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-5.12);
      upperLimit.add(5.12);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    var numberOfVariables = getNumberOfVariables() ;

    var x = new double[10];
    var count = 0;
      for (var i1 = 0; i1 < numberOfVariables; i1++) {
          double v = solution.variables().get(i1);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v;
      }
      x = Arrays.copyOfRange(x, 0, count);

    var sum = 0.0;

    for (var i = 0; i < numberOfVariables - 1; i++) {
      var temp1 = (x[i] * x[i]) - x[i + 1];
      var temp2 = x[i] - 1.0;
      sum += (100.0 * temp1 * temp1) + (temp2 * temp2);
    }

    solution.objectives()[0] = sum;

    return solution ;
  }
}

