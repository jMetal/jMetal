package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF07
 */
@SuppressWarnings("serial")
public class MaF07 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public MaF07() {
    this(22, 3) ;
  }

  /**
   * Creates a MaF07 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF07(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF07");

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
  public @NotNull DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    int numberOfVariables = solution.variables().size();
    int numberOfObjectives = solution.objectives().length;

    double[] x;
    double[] f;

      double @NotNull [] arr1 = new double[10];
      int count1 = 0;
      for (int i2 = 0; i2 < numberOfVariables; i2++) {
          double v3 = solution.variables().get(i2);
          if (arr1.length == count1) arr1 = Arrays.copyOf(arr1, count1 * 2);
          arr1[count1++] = v3;
      }
      arr1 = Arrays.copyOfRange(arr1, 0, count1);
      x = arr1;

    // evaluate g,h
    double g, h, sub1;
      double result = 0.0;
      for (int idx = numberOfObjectives - 1; idx < numberOfVariables; idx++) {
          double v2 = x[idx];
          result += v2;
      }
      g = result;
    g = 1 + 9 * g / (numberOfVariables - numberOfObjectives + 1);
    sub1 = 1 + g;
      double sum = 0.0;
      int bound = numberOfObjectives - 1;
      for (int i1 = 0; i1 < bound; i1++) {
          double v1 = (x[i1] * (1 + Math.sin(3 * Math.PI * x[i1])) / sub1);
          sum += v1;
      }
      h = sum;
    h = numberOfObjectives - h;
    // evaluate f1,...,m-1,m
      double[] arr = new double[10];
      int count = 0;
      for (int j = 0; j < numberOfObjectives; j++) {
          double v = x[j];
          if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
          arr[count++] = v;
      }
      arr = Arrays.copyOfRange(arr, 0, count);
      f = arr;
    f[numberOfObjectives - 1] = h * sub1;

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
