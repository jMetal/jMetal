package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF05
 */
@SuppressWarnings("serial")
public class MaF05 extends AbstractDoubleProblem {

  public double const5[];

  /**
   * Default constructor
   */
  public MaF05() {
    this(12, 3);
  }

  /**
   * Creates a MaF05 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF05(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF05");

    @NotNull List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

      for (var i1 = 0; i1 < numberOfVariables; i1++) {
          lower.add(0.0);
          upper.add(1.0);
      }

      setVariableBounds(lower, upper);

    //other constants during the whole process once M&D are defined
    var c5 = new double[10];
    var count = 0;
      for (var i = 0; i < numberOfObjectives; i++) {
        var pow = Math.pow(2, i + 1);
          if (c5.length == count) c5 = Arrays.copyOf(c5, count * 2);
          c5[count++] = pow;
      }
      c5 = Arrays.copyOfRange(c5, 0, count);
      const5 = c5;
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {

    var numberOfVariables = solution.variables().size();
    var numberOfObjectives = solution.objectives().length;

    var f = new double[numberOfObjectives];

    var arr = new double[10];
    var count = 0;
      for (var i2 = 0; i2 < numberOfVariables; i2++) {
          double v = solution.variables().get(i2);
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
    var x = arr;
    var g = 0.0;
      for (var i1 = numberOfObjectives - 1; i1 < numberOfVariables; i1++) {
        var pow = Math.pow(x[i1] - 0.5, 2);
          g += pow;
      }
      // evaluate g
      double subf1 = 1, subf3 = 1 + g;
    // evaluate fm,fm-1,...2,f1
    f[numberOfObjectives - 1] =
        2 * Math.pow(Math.sin(Math.PI * Math.pow(x[0], 100) / 2) * subf3, 1);
    // fi=2^i*(subf1*subf2)*(subf3)
    for (var i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= Math.cos(Math.PI * Math.pow(x[numberOfObjectives - i - 2], 100) / 2);
      f[i] = const5[numberOfObjectives - i - 1] * Math.pow(
          subf1 * Math.sin(Math.PI * Math.pow(x[numberOfObjectives - i - 1], 100) / 2) * subf3, 1);
    }
    f[0] = const5[numberOfObjectives - 1] * Math
        .pow(subf1 * (Math.cos(Math.PI * Math.pow(x[numberOfObjectives - 2], 100) / 2)) * subf3,
            1);

    for (var i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
