package org.uma.jmetal.problem.multiobjective.maf;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

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
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(0);
    setName("MaF13");

    List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(getNumberOfVariables());

    for (int var = 0; var < 2; var++) {
        lower.add(0.0);
        upper.add(1.0);
    } //for
    for (int var = 2; var < numberOfVariables; var++) {
        lower.add(-2.0);
        upper.add(2.0);
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

    int numberOfVariables_ = solution.getNumberOfVariables();
    int numberOfObjectives_ = solution.getNumberOfObjectives();

    double[] x = new double[numberOfVariables_];
    double[] f = new double[numberOfObjectives_];

    for (int i = 0; i < numberOfVariables_; i++) {
      x[i] = solution.getVariableValue(i);
    }

    // evaluate J,y,sub1,sub2,sub3,sub4
    double[] y = new double[numberOfVariables_];
    for (int i = 0; i < numberOfVariables_; i++) {
      y[i] =
          x[i] - 2 * x[1] * Math.sin(2 * Math.PI * x[0] + (i + 1) * Math.PI / numberOfVariables_);
    }
    ArrayList<Integer> J1 = new ArrayList<Integer>();
    ArrayList<Integer> J2 = new ArrayList<Integer>();
    ArrayList<Integer> J3 = new ArrayList<Integer>();
    ArrayList<Integer> J4 = new ArrayList<Integer>();
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
      solution.setObjective(i, f[i]);
    }
  }
}
