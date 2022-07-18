package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF11
 */
@SuppressWarnings("serial")
public class MaF11 extends AbstractDoubleProblem {

  public int K11, L11;

  /**
   * Default constructor
   */
  public MaF11() {
    this(12, 3) ;
  }

  /**
   * Creates a MaF11 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF11(Integer numberOfVariables,
      Integer numberOfObjectives) {
    numberOfVariables = ((int) (
        Math.ceil((numberOfVariables - numberOfObjectives + 1) / 2.0) * 2 + numberOfObjectives
            - 1));
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF11");

    K11 = numberOfObjectives - 1;
    L11 = numberOfVariables - K11;

    List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lower.add(0.0);
      upper.add(2.0 * (i + 1));
    }

    setVariableBounds(lower, upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public @NotNull DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    int numberOfVariables = solution.variables().size();
    int numberOfObjectives = solution.objectives().length;

    double[] x;
    double @NotNull [] f = new double[numberOfObjectives];

      double[] arr = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < numberOfVariables; i1++) {
          double v1 = solution.variables().get(i1);
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v1;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
      x = arr;

    // evaluate zi,t1i,t2i,t3i,t4i,yi
    double[] z = new double[numberOfVariables];
    double[] t1 = new double[numberOfVariables];
    double[] t2 = new double[(numberOfVariables + K11) / 2];
    double[] t3 = new double[numberOfObjectives];
    double[] y = new double[numberOfObjectives];
    double sub1 = 0, sub2 = 0;
    int lb = 0, ub = 0;
    for (int i = 0; i < K11; i++) {
      z[i] = x[i] / (2 * i + 2);
      t1[i] = z[i];
      t2[i] = t1[i];
    }
    for (int i = K11; i < numberOfVariables; i++) {
      z[i] = x[i] / (2 * i + 2);
      t1[i] = Math.abs(z[i] - 0.35) / (Math.abs(Math.floor(0.35 - z[i]) + 0.35));
    }
    for (int i = K11; i < t2.length; i++) {
      t2[i] = (t1[2 * i - K11] + t1[2 * i - K11 + 1] + 2 * Math
          .abs(t1[2 * i - K11] - t1[2 * i - K11 + 1])) / 3;
    }
    sub2 = K11 / (numberOfObjectives - 1);
    for (int i = 0; i < numberOfObjectives - 1; i++) {
      sub1 = 0;
      lb = i * K11 / (numberOfObjectives - 1) + 1;
      ub = (i + 1) * K11 / (numberOfObjectives - 1);
        double sum = 0.0;
        for (int j = lb - 1; j < ub; j++) {
            double v = t2[j];
            sum += v;
        }
        sub1 += sum;
      t3[i] = sub1 / sub2;
    }
    lb = K11 + 1;
    ub = (numberOfVariables + K11) / 2;
    sub1 = 0;
    sub2 = (numberOfVariables - K11) / 2;
      double sum = 0.0;
      for (int j = lb - 1; j < ub; j++) {
          double v = t2[j];
          sum += v;
      }
      sub1 += sum;
    t3[numberOfObjectives - 1] = sub1 / sub2;
    for (int i = 0; i < numberOfObjectives - 1; i++) {
      y[i] = (t3[i] - 0.5) * Math.max(1, t3[numberOfObjectives - 1]) + 0.5;
    }
    y[numberOfObjectives - 1] = t3[numberOfObjectives - 1];

    // evaluate fm,fm-1,...,2,f1
    double subf1 = 1;
    f[numberOfObjectives - 1] =
        y[numberOfObjectives - 1] + 2 * numberOfObjectives * (1 - y[0] * Math
            .pow(Math.cos(5 * Math.PI * y[0]), 2));
    for (int i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= (1 - Math.cos(Math.PI * y[numberOfObjectives - i - 2] / 2));
      f[i] = y[numberOfObjectives - 1] + 2 * (i + 1) * subf1 * (1 - Math
          .sin(Math.PI * y[numberOfObjectives - i - 1] / 2));
    }
    f[0] = y[numberOfObjectives - 1] + 2 * subf1 * (1 - Math
        .cos(Math.PI * y[numberOfObjectives - 2] / 2));

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
