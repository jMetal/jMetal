package org.uma.jmetal.problem.multiobjective.maf;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem MaF10
 */
@SuppressWarnings("serial")
public class MaF10 extends AbstractDoubleProblem {
  public static int K10;

  /**
   * Default constructor
   */
  public MaF10() {
    this(12, 3) ;
  }

  /**
   * Creates a MaF10 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF10(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF10");
    K10 = numberOfObjectives - 1;

    List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(
        getNumberOfVariables());

    for (int var = 0; var < numberOfVariables; var++) {
      lower.add(0.0);
      upper.add(2.0 * (var + 1));
    }

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

    int numberOfVariables_ = solution.getNumberOfVariables();
    int numberOfObjectives_ = solution.getNumberOfObjectives();

    double[] x = new double[numberOfVariables_];
    double[] f = new double[numberOfObjectives_];

    for (int i = 0; i < numberOfVariables_; i++) {
      x[i] = solution.getVariableValue(i);
    }

    // evaluate zi,t1i,t2i,t3i,t4i,yi
    double[] z = new double[numberOfVariables_];
    double[] t1 = new double[numberOfVariables_];
    double[] t2 = new double[numberOfVariables_];
    double[] t3 = new double[numberOfVariables_];
    double[] t4 = new double[numberOfObjectives_];
    double[] y = new double[numberOfObjectives_];
    double sub1 = 0, sub2 = 0;
    int lb = 0, ub = 0;
    for (int i = 0; i < K10; i++) {
      z[i] = x[i] / (2 * i + 2);
      t1[i] = z[i];
      t2[i] = t1[i];
      t3[i] = Math.pow(t2[i], 0.02);
    }
    for (int i = K10; i < numberOfVariables_; i++) {
      z[i] = x[i] / (2 * i + 2);
      t1[i] = Math.abs(z[i] - 0.35) / (Math.abs(Math.floor(0.35 - z[i]) + 0.35));
      t2[i] = 0.8 + 0.8 * (0.75 - t1[i]) * Math.min(0, Math.floor(t1[i] - 0.75)) / 0.75
          - 0.2 * (t1[i] - 0.85) * Math.min(0, Math.floor(0.85 - t1[i])) / 0.15;
      t2[i] = Math.round(t2[i] * 1000000) / 1000000.0;
      t3[i] = Math.pow(t2[i], 0.02);
    }
    for (int i = 0; i < numberOfObjectives_ - 1; i++) {
      sub1 = 0;
      sub2 = 0;
      lb = i * K10 / (numberOfObjectives_ - 1) + 1;
      ub = (i + 1) * K10 / (numberOfObjectives_ - 1);
      for (int j = lb - 1; j < ub; j++) {
        sub1 += (2 * (j + 1) * t3[j]);
        sub2 += (2 * (j + 1));
      }
      t4[i] = sub1 / sub2;
    }
    lb = K10 + 1;
    ub = numberOfVariables_;
    sub1 = 0;
    sub2 = 0;
    for (int j = lb - 1; j < ub; j++) {
      sub1 += (2 * (j + 1) * t3[j]);
      sub2 += (2 * (j + 1));
    }
    t4[numberOfObjectives_ - 1] = sub1 / sub2;
    for (int i = 0; i < numberOfObjectives_ - 1; i++) {
      y[i] = (t4[i] - 0.5) * Math.max(1, t4[numberOfObjectives_ - 1]) + 0.5;
    }
    y[numberOfObjectives_ - 1] = t4[numberOfObjectives_ - 1];

    // evaluate fm,fm-1,...,2,f1
    double subf1 = 1;
    f[numberOfObjectives_ - 1] = y[numberOfObjectives_ - 1] + 2 * numberOfObjectives_ * (1 - y[0]
        - Math.cos(10 * Math.PI * y[0] + Math.PI / 2) / (10 * Math.PI));
    for (int i = numberOfObjectives_ - 2; i > 0; i--) {
      subf1 *= (1 - Math.cos(Math.PI * y[numberOfObjectives_ - i - 2] / 2));
      f[i] = y[numberOfObjectives_ - 1] + 2 * (i + 1) * subf1 * (1 - Math
          .sin(Math.PI * y[numberOfObjectives_ - i - 1] / 2));
    }
    f[0] = y[numberOfObjectives_ - 1] + 2 * subf1 * (1 - Math
        .cos(Math.PI * y[numberOfObjectives_ - 2] / 2));

    for (int i = 0; i < numberOfObjectives_; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}
