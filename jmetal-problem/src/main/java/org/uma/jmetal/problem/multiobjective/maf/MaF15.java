package org.uma.jmetal.problem.multiobjective.maf;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem MaF15
 */
@SuppressWarnings("serial")
public class MaF15 extends AbstractDoubleProblem {
  public static int nk15;
  public static int sublen15[], len15[];

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
    double[] c = new double[numberOfObjectives];
    c[0] = 3.8 * 0.1 * (1 - 0.1);
    double sumc = 0;
    sumc += c[0];
    for (int i = 1; i < numberOfObjectives; i++) {
      c[i] = 3.8 * c[i - 1] * (1 - c[i - 1]);
      sumc += c[i];
    }

    int[] sublen = new int[numberOfObjectives];
    int[] len = new int[numberOfObjectives + 1];
    len[0] = 0;
    for (int i = 0; i < numberOfObjectives; i++) {
      sublen[i] = (int) Math.ceil(Math.round(c[i] / sumc * numberOfVariables) / (double) nk15);
      len[i + 1] = len[i] + (nk15 * sublen[i]);
    }
    sublen15 = sublen;
    len15 = len;
    //re-update numberOfObjectives,numberOfVariables
    numberOfVariables = numberOfObjectives - 1 + len[numberOfObjectives];

    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF15");

    List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(getNumberOfVariables());

    for (int var = 0; var < numberOfObjectives - 1; var++) {
        lower.add(0.0);
        upper.add(1.0);
    } //for
    for (int var = numberOfObjectives - 1; var < numberOfVariables; var++) {
        lower.add(0.0);
        upper.add(10.0);
    } //for

    setLowerLimit(lower);
    setUpperLimit(upper);
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   */
  @Override
  public void evaluate(DoubleSolution solution) {

    int numberOfVariables = solution.getNumberOfVariables();
    int numberOfObjectives = solution.getNumberOfObjectives();

    double[] x = new double[numberOfVariables];
    double[] f = new double[numberOfObjectives];

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i);
    }

    // change x
    for (int i = numberOfObjectives - 1; i < numberOfVariables; i++) {
      x[i] = (1 + Math.cos((i + 1) / (double) numberOfVariables * Math.PI / 2)) * x[i] - 10 * x[0];
    }
    // evaluate eta,g
    double[] g = new double[numberOfObjectives];
    double sub1;
    for (int i = 0; i < numberOfObjectives; i = i + 2) {
      double[] tx = new double[sublen15[i]];
      sub1 = 0;
      for (int j = 0; j < nk15; j++) {
        System
            .arraycopy(x, len15[i] + numberOfObjectives - 1 + j * sublen15[i], tx, 0, sublen15[i]);
        sub1 += Griewank(tx);
      }
      g[i] = sub1 / (nk15 * sublen15[i]);
    }

    for (int i = 1; i < numberOfObjectives; i = i + 2) {
      double[] tx = new double[sublen15[i]];
      sub1 = 0;
      for (int j = 0; j < nk15; j++) {
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
    for (int i = numberOfObjectives - 2; i > 0; i--) {
      subf1 *= Math.cos(Math.PI * x[numberOfObjectives - i - 2] / 2);
      f[i] = (1 - subf1 * Math.sin(Math.PI * x[numberOfObjectives - i - 1] / 2)) * (1 + g[i] + g[i
          + 1]);
    }
    f[0] = (1 - subf1 * Math.cos(Math.PI * x[numberOfObjectives - 2] / 2)) * (1 + g[0] + g[1]);

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, f[i]);
    }

  }

  public static double Griewank(double[] x) {
    double eta = 0, sub1 = 0, sub2 = 1;
    for (int i = 0; i < x.length; i++) {
      sub1 += (Math.pow(x[i], 2) / 4000);
      sub2 *= (Math.cos(x[i] / Math.sqrt(i + 1)));
    }
    eta = sub1 - sub2 + 1;
    return eta;
  }

  public static double Sphere(double[] x) {
    double eta = 0;
    for (int i = 0; i < x.length; i++) {
      eta += Math.pow(x[i], 2);
    }
    return eta;
  }
}
