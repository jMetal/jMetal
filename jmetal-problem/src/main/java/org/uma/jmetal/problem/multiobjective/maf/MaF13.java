package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF13
 */
@SuppressWarnings("serial")
public class MaF13 extends AbstractDoubleProblem {

  /**
   * Default constructor
   */
  public MaF13() {
    this(5, 3) ;
  }

  /**
   * Creates a MaF13 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF13(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF13");

    List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

    for (int i = 0; i < 2; i++) {
        lower.add(0.0);
        upper.add(1.0);
    }
    for (int i = 2; i < numberOfVariables; i++) {
        lower.add(-2.0);
        upper.add(2.0);
    }

    setVariableBounds(lower, upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {

    int numberOfVariables_ = solution.variables().size();
    int numberOfObjectives_ = solution.objectives().length;

    double[] x;
    double[] f = new double[numberOfObjectives_];

      double[] arr = new double[10];
      int count1 = 0;
      for (int i2 = 0; i2 < numberOfVariables_; i2++) {
          double v1 = solution.variables().get(i2);
          if (arr.length == count1) arr = Arrays.copyOf(arr, count1 * 2);
          arr[count1++] = v1;
      }
      arr = Arrays.copyOfRange(arr, 0, count1);
      x = arr;

    // evaluate J,y,sub1,sub2,sub3,sub4
      double @NotNull [] y = new double[10];
      int count = 0;
      for (int i1 = 0; i1 < numberOfVariables_; i1++) {
          double v = x[i1] - 2 * x[1] * Math.sin(2 * Math.PI * x[0] + (i1 + 1) * Math.PI / numberOfVariables_);
          if (y.length == count) y = Arrays.copyOf(y, count * 2);
          y[count++] = v;
      }
      y = Arrays.copyOfRange(y, 0, count);
      @NotNull ArrayList<Integer> J1 = new ArrayList<Integer>();
    @NotNull ArrayList<Integer> J2 = new ArrayList<Integer>();
    ArrayList<Integer> J3 = new ArrayList<Integer>();
    @NotNull ArrayList<Integer> J4 = new ArrayList<Integer>();
    double sub1 = 0, sub2 = 0, sub3 = 0, sub4 = 0;
    for (int i = 4; i <= numberOfVariables_; i = i + 3) {
      J1.add(i);
      sub1 += Math.pow(y[i - 1], 2);
    }
    sub1 = 2 * sub1 / J1.size();
    for (int i = 5; i <= numberOfVariables_; i = i + 3) {
      J2.add(i);
      sub2 += Math.pow(y[i - 1], 2);
    }
    sub2 = 2 * sub2 / J2.size();
    for (int i = 3; i <= numberOfVariables_; i = i + 3) {
      J3.add(i);
      sub3 += Math.pow(y[i - 1], 2);
    }
    sub3 = 2 * sub3 / J3.size();
    for (int i = 4; i <= numberOfVariables_; i++) {
      J4.add(i);
      sub4 += Math.pow(y[i - 1], 2);
    }
    sub4 = 2 * sub4 / J4.size();
    // evaluate f1,f2,f3,f4,...m
    f[0] = Math.sin(Math.PI * x[0] / 2) + sub1;
    f[1] = Math.cos(Math.PI * x[0] / 2) * Math.sin(Math.PI * x[1] / 2) + sub2;
    f[2] = Math.cos(Math.PI * x[0] / 2) * Math.cos(Math.PI * x[1] / 2) + sub3;
    for (int i = 3; i < numberOfObjectives_; i++) {
      f[i] = Math.pow(f[0], 2) + Math.pow(f[1], 10) + Math.pow(f[2], 10) + sub4;
    }

    for (int i = 0; i < numberOfObjectives_; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }
}
