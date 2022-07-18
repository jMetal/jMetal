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

      double[] x = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < numberOfVariables; i1++) {
          double v1 = solution.variables().get(i1);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v1;
      }
      x = Arrays.copyOfRange(x, 0, count);

      double result;
    double a = 10.0;
    double w = 2 * Math.PI;

      double sum = 0.0;
      for (int i = 0; i < numberOfVariables; i++) {
          double v = x[i] * x[i] - a * Math.cos(w * x[i]);
          sum += v;
      }
      result = sum;
    result += a * numberOfVariables;

    solution.objectives()[0] = result;

    return solution ;
  }
}

