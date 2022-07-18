package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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
          double v1 = solution.variables().get(i1);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v1;
      }
      x = Arrays.copyOfRange(x, 0, count);

      double result;
    var a = 10.0;
    var w = 2 * Math.PI;

    var sum = 0.0;
      for (var i = 0; i < numberOfVariables; i++) {
        var v = x[i] * x[i] - a * Math.cos(w * x[i]);
          sum += v;
      }
      result = sum;
    result += a * numberOfVariables;

    solution.objectives()[0] = result;

    return solution ;
  }
}

