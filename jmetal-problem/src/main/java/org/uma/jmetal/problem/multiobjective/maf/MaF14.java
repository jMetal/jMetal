package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF14
 */
@SuppressWarnings("serial")
public class MaF14 extends AbstractDoubleProblem {
  public int nk14;
  public int sublen14[], len14[];

  /**
   * Default constructor
   */
  public MaF14() {
    this(60, 3) ;
  }

  /**
   * Creates a MaF14 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF14(Integer numberOfVariables,
      Integer numberOfObjectives) {

    //evaluate sublen14,len14
    nk14 = 2;
    var c = new double[numberOfObjectives];
    c[0] = 3.8 * 0.1 * (1 - 0.1);
    double sumc = 0;
    sumc += c[0];
    for (var i = 1; i < numberOfObjectives; i++) {
      c[i] = 3.8 * c[i - 1] * (1 - c[i - 1]);
      sumc += c[i];
    }

    var sublen = new int[numberOfObjectives];
    var len = new int[numberOfObjectives + 1];
    len[0] = 0;
    for (var i = 0; i < numberOfObjectives; i++) {
      sublen[i] = (int) Math.ceil(Math.round(c[i] / sumc * numberOfVariables) / (double) nk14);
      len[i + 1] = len[i] + (nk14 * sublen[i]);
    }
    sublen14 = sublen;
    len14 = len;
    //re-update numberOfObjectives,numberOfVariables
    numberOfVariables = numberOfObjectives - 1 + len[numberOfObjectives];

    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF14");

    @NotNull List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
        numberOfVariables);

    for (var i = 0; i < numberOfObjectives - 1; i++) {
        lower.add(0.0);
        upper.add(1.0);
    }
    for (var i = numberOfObjectives - 1; i < numberOfVariables; i++) {
        lower.add(0.0);
        upper.add(10.0);
    }

    setVariableBounds(lower, upper);
  }

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

    //change x
    for (var i = numberOfObjectives - 1; i < numberOfVariables; i++) {
      x[i] = (1 + (i + 1) / (double) numberOfVariables) * x[i] - 10 * x[0];
    }
    // evaluate eta,g
    var g = new double[numberOfObjectives];
    double sub1;
    for (var i = 0; i < numberOfObjectives; i = i + 2) {
      var tx = new double[sublen14[i]];
      sub1 = 0;
      for (var j = 0; j < nk14; j++) {
        System
            .arraycopy(x, len14[i] + numberOfObjectives - 1 + j * sublen14[i], tx, 0, sublen14[i]);
        sub1 += Rastrigin(tx);
      }
      g[i] = sub1 / (nk14 * sublen14[i]);
    }

    for (var i = 1; i < numberOfObjectives; i = i + 2) {
      var tx = new double[sublen14[i]];
      sub1 = 0;
      for (var j = 0; j < nk14; j++) {
        System
            .arraycopy(x, len14[i] + numberOfObjectives - 1 + j * sublen14[i], tx, 0, sublen14[i]);
        sub1 += Rosenbrock(tx);
      }
      g[i] = sub1 / (nk14 * sublen14[i]);
    }

    // evaluate fm,fm-1,...,2,f1
    double subf1 = 1;
    f[numberOfObjectives - 1] = (1 - x[0]) * (1 + g[numberOfObjectives - 1]);
    for (var i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= x[numberOfObjectives - i - 2];
      f[i] = subf1 * (1 - x[numberOfObjectives - i - 1]) * (1 + g[i]);
    }
    f[0] = subf1 * x[numberOfObjectives - 2] * (1 + g[0]);

    for (var i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }
    return solution ;
  }

  public static double Rastrigin(double @NotNull [] x) {
    var eta = 0.0;
      for (var v : x) {
        var v1 = (Math.pow(v, 2) - 10 * Math.cos(2 * Math.PI * v) + 10);
          eta += v1;
      }
      return eta;
  }

  public static double Rosenbrock(double[] x) {
    var eta = 0.0;
    var bound = x.length - 1;
      for (var i = 0; i < bound; i++) {
        var v = (100 * Math.pow(Math.pow(x[i], 2) - x[i + 1], 2) + Math.pow((x[i] - 1), 2));
          eta += v;
      }
      return eta;
  }
}
