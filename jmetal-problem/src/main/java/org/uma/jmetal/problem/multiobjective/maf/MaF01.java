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

      for (var i = 0; i < numberOfVariables; i++) {
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
    var numberOfVariables = solution.variables().size();
    var numberOfObjectives = solution.objectives().length;

    var f = new double[numberOfObjectives];

    var arr = new double[10];
    var count = 0;
      for (var i1 = 0; i1 < numberOfVariables; i1++) {
          double v = solution.variables().get(i1);
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
    var x = arr;

    double subf1 = 1;
    var sum = 0.0;
      for (var j = numberOfObjectives - 1; j < numberOfVariables; j++) {
        var pow = (Math.pow(x[j] - 0.5, 2));
          sum += pow;
      }
    var g = sum;
    var subf3 = 1 + g;

    f[numberOfObjectives - 1] = x[0] * subf3;
    for (var i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= x[numberOfObjectives - i - 2];
      f[i] = subf3 * (1 - subf1 * (1 - x[numberOfObjectives - i - 1]));
    }
    f[0] = (1 - subf1 * x[numberOfObjectives - 2]) * subf3;

    for (var i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
