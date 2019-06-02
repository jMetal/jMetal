package org.uma.jmetal.problem.multiobjective.maf;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem MaF12
 */
@SuppressWarnings("serial")
public class MaF12 extends AbstractDoubleProblem {

  public static int K12, L12;

  /**
   * Default constructor
   */
  public MaF12() {
    this(12, 3) ;
  }

  /**
   * Creates a MaF12 problem instance
   *
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public MaF12(Integer numberOfVariables,
      Integer numberOfObjectives) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF12");
    K12 = numberOfObjectives - 1;
    L12 = numberOfVariables - K12;

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

    double subf1 = 1;
    // evaluate zi,t1i,t2i,t3i,t4i,yi
    double[] z = new double[numberOfVariables_];
    double[] t1 = new double[numberOfVariables_];
    double[] t2 = new double[numberOfVariables_];
    double[] t3 = new double[numberOfObjectives_];
    double[] y = new double[numberOfObjectives_];
    double sub1, sub2 = 0;
    int lb1 = 0, ub1 = 0, lb2 = 0, ub2 = 0;
    for (int i = 0; i < numberOfVariables_; i++) {
      z[i] = x[i] / (2 * i + 2);
    }
    for (int i = 0; i < numberOfVariables_ - 1; i++) {
      sub1 = 0;
      for (int j = i + 1; j < numberOfVariables_; j++) {
        sub1 += z[j];
      }
      sub2 = sub1 / (numberOfVariables_ - i - 1);
      t1[i] = Math.pow(z[i], 0.02 + 49.98 * (0.98 / 49.98 - (1 - 2 * sub2) * Math
          .abs(Math.floor(0.5 - sub2) + 0.98 / 49.98)));
    }
    t1[numberOfVariables_ - 1] = z[numberOfVariables_ - 1];

    for (int i = 0; i < K12; i++) {
      t2[i] = 1 + (Math.abs(t1[i] - 0.35) - 0.001) * (349.95 * Math.floor(t1[i] - 0.349) / 0.349
          + 649.95 * Math.floor(0.351 - t1[i]) / 0.649 + 1000);
    }
    for (int i = K12; i < numberOfVariables_; i++) {
      t2[i] = 1.0 / 97 * (1 + Math.cos(
          122 * Math.PI * (0.5 - Math.abs(t1[i] - 0.35) * 0.5 / (Math.floor(0.35 - t1[i]) + 0.35)))
          + 380 * Math.pow(Math.abs(t1[i] - 0.35) * 0.5 / (Math.floor(0.35 - t1[i]) + 0.35), 2));
    }
    int p = 0, h = 0;
    double sub3 = 0, sub4 = 0;
    sub1 =
        Math.ceil(0.5 * K12 / (numberOfObjectives_ - 1)) * (1 + 2 * K12 / (numberOfObjectives_ - 1)
            - 2 * Math.ceil(0.5 * K12 / (numberOfObjectives_ - 1)));
    sub2 = Math.ceil(L12 / 2.0) * (1 + 2 * (L12) - 2 * Math.ceil(L12 / 2.0));
    lb2 = 0;
    ub2 = K12 / (numberOfObjectives_ - 1) - 2;
    for (int i = 0; i < numberOfObjectives_ - 1; i++) {
      sub4 = 0;
      lb1 = i * K12 / (numberOfObjectives_ - 1) + 1;
      ub1 = (i + 1) * K12 / (numberOfObjectives_ - 1);
      for (int j = lb1 - 1; j < ub1; j++) {
        h = lb2;
        sub3 = 0;
        while (h <= ub2) {
          p = lb1 + (j - lb1 + 1 + h) % (K12 / (numberOfObjectives_ - 1));
          sub3 += Math.abs(t2[j] - t2[p - 1]);
          h++;
        }
        sub4 += t2[j] + sub3;
      }
      t3[i] = sub4 / sub1;
    }
    lb1 = K12 + 1;
    ub1 = numberOfVariables_;
    lb2 = 0;
    ub2 = L12 - 2;
    sub3 = 0;
    sub4 = 0;
    for (int j = lb1 - 1; j < ub1; j++) {
      for (h = j + 1; h < K12 + L12; h++) {
        sub3 += Math.abs(t2[j] - t2[h]);
      }
      sub4 += t2[j];
    }

    sub4 += (sub3 * 2);
    t3[numberOfObjectives_ - 1] = sub4 / sub2;
    for (int i = 0; i < numberOfObjectives_ - 1; i++) {
      y[i] = (t3[i] - 0.5) * Math.max(1, t3[numberOfObjectives_ - 1]) + 0.5;
    }
    y[numberOfObjectives_ - 1] = t3[numberOfObjectives_ - 1];

    // evaluate fm,fm-1,...,2,f1
    f[numberOfObjectives_ - 1] =
        y[numberOfObjectives_ - 1] + 2 * numberOfObjectives_ * Math.cos(Math.PI * y[0] / 2);
    for (int i = numberOfObjectives_ - 2; i > 0; i--) {
      subf1 *= Math.sin(Math.PI * y[numberOfObjectives_ - i - 2] / 2);
      f[i] = y[numberOfObjectives_ - 1] + 2 * (i + 1) * subf1 * Math
          .cos(Math.PI * y[numberOfObjectives_ - i - 1] / 2);
    }
    f[0] =
        y[numberOfObjectives_ - 1] + 2 * subf1 * Math.sin(Math.PI * y[numberOfObjectives_ - 2] / 2);

    for (int i = 0; i < numberOfObjectives_; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}
