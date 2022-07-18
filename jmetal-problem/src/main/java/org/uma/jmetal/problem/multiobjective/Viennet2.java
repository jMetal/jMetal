package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem Viennet2 */
@SuppressWarnings("serial")
public class Viennet2 extends AbstractDoubleProblem {

  /** Constructor. Creates a default instance of the Viennet2 problem */
  public Viennet2() {
    int numberOfVariables = 2 ;
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("Viennet2");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(-4.0);
      upperLimit.add(4.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables();

    double[] f = new double[solution.objectives().length];
      double[] x = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < numberOfVariables; i1++) {
          double v = solution.variables().get(i1);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v;
      }
      x = Arrays.copyOfRange(x, 0, count);

      // First function
    f[0] = (x[0] - 2) * (x[0] - 2) / 2.0 + (x[1] + 1) * (x[1] + 1) / 13.0 + 3.0;

    // Second function
    f[1] =
        (x[0] + x[1] - 3.0) * (x[0] + x[1] - 3.0) / 36.0
            + (-x[0] + x[1] + 2.0) * (-x[0] + x[1] + 2.0) / 8.0
            - 17.0;

    // Third function
    f[2] =
        (x[0] + 2 * x[1] - 1) * (x[0] + 2 * x[1] - 1) / 175.0
            + (2 * x[1] - x[0]) * (2 * x[1] - x[0]) / 17.0
            - 13.0;

    for (int i = 0; i < solution.objectives().length; i++) solution.objectives()[i] = f[i];

    return solution;
  }
}
