package org.uma.jmetal.problem.multiobjective.maf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem MaF15
 */
@SuppressWarnings("serial")
public class MaF15 extends AbstractDoubleProblem {
  public int nk15;
  public int sublen15[], len15[];

  /**
   * Default constructor
   */
  public MaF15() {
    this(60, 3) ;
  }

  /**
   * Creates a MaF15 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF15(Integer numberOfVariables,
      Integer numberOfObjectives) {

    nk15 = 2;
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
      sublen[i] = (int) Math.ceil(Math.round(c[i] / sumc * numberOfVariables) / (double) nk15);
      len[i + 1] = len[i] + (nk15 * sublen[i]);
    }
    sublen15 = sublen;
    len15 = len;
    //re-update numberOfObjectives,numberOfVariables
    numberOfVariables = numberOfObjectives - 1 + len[numberOfObjectives];

    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF15");

    List<Double> lower = new ArrayList<>(numberOfVariables), upper = new ArrayList<>(
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

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public @NotNull DoubleSolution evaluate(DoubleSolution solution) {

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

    // change x
    for (var i = numberOfObjectives - 1; i < numberOfVariables; i++) {
      x[i] = (1 + Math.cos((i + 1) / (double) numberOfVariables * Math.PI / 2)) * x[i] - 10 * x[0];
    }
    // evaluate eta,g
    var g = new double[numberOfObjectives];
    double sub1;
    for (var i = 0; i < numberOfObjectives; i = i + 2) {
      var tx = new double[sublen15[i]];
      sub1 = 0;
      for (var j = 0; j < nk15; j++) {
        System
            .arraycopy(x, len15[i] + numberOfObjectives - 1 + j * sublen15[i], tx, 0, sublen15[i]);
        sub1 += Griewank(tx);
      }
      g[i] = sub1 / (nk15 * sublen15[i]);
    }

    for (var i = 1; i < numberOfObjectives; i = i + 2) {
      var tx = new double[sublen15[i]];
      sub1 = 0;
      for (var j = 0; j < nk15; j++) {
        System
            .arraycopy(x, len15[i] + numberOfObjectives - 1 + j * sublen15[i], tx, 0, sublen15[i]);
        sub1 += Sphere(tx);
      }
      g[i] = sub1 / (nk15 * sublen15[i]);
    }

    // evaluate fm,fm-1,...,2,f1
    double subf1 = 1;
    f[numberOfObjectives - 1] =
        (1 - Math.sin(Math.PI * x[0] / 2)) * (1 + g[numberOfObjectives - 1]);
    for (var i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= Math.cos(Math.PI * x[numberOfObjectives - i - 2] / 2);
      f[i] = (1 - subf1 * Math.sin(Math.PI * x[numberOfObjectives - i - 1] / 2)) * (1 + g[i] + g[i
          + 1]);
    }
    f[0] = (1 - subf1 * Math.cos(Math.PI * x[numberOfObjectives - 2] / 2)) * (1 + g[0] + g[1]);

    for (var i = 0; i < numberOfObjectives; i++) {
      solution.objectives()[i] = f[i];
    }

    return solution ;
  }

  public static double Griewank(double[] x) {
    double eta = 0, sub1 = 0, sub2 = 1;
    for (var i = 0; i < x.length; i++) {
      sub1 += (Math.pow(x[i], 2) / 4000);
      sub2 *= (Math.cos(x[i] / Math.sqrt(i + 1)));
    }
    eta = sub1 - sub2 + 1;
    return eta;
  }

  public static double Sphere(double[] x) {
    var eta = 0.0;
      for (var v : x) {
        var pow = Math.pow(v, 2);
          eta += pow;
      }
      return eta;
  }
}
