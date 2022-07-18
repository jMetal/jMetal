package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF01
 */
@SuppressWarnings("serial")
public class MaF01 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public MaF01() {
    this(12, 3) ;
  }

  /**
   * Creates a MaF01 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF01(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF01");

    @NotNull List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

      for (int i = 0; i < numberOfVariables; i++) {
          lower.add(0.0);
          upper.add(1.0);
      }

      setVariableBounds(lower, upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    int numberOfVariables = solution.variables().size();
    int numberOfObjectives = solution.objectives().length;

    double[] x;
    double[] f = new double[numberOfObjectives];

      double[] arr = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < numberOfVariables; i1++) {
          double v = solution.variables().get(i1);
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
      x = arr;

    double g, subf1 = 1, subf3;
      double sum = 0.0;
      for (int j = numberOfObjectives - 1; j < numberOfVariables; j++) {
          double pow = (Math.pow(x[j] - 0.5, 2));
          sum += pow;
      }
      g = sum;
    subf3 = 1 + g;

    f[numberOfObjectives - 1] = x[0] * subf3;
    for (int i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= x[numberOfObjectives - i - 2];
      f[i] = subf3 * (1 - subf1 * (1 - x[numberOfObjectives - i - 1]));
    }
    f[0] = (1 - subf1 * x[numberOfObjectives - 2]) * subf3;

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
