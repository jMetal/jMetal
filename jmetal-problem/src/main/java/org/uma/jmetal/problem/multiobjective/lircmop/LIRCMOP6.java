package org.uma.jmetal.problem.multiobjective.lircmop;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem LIR-CMOP3, defined in: An Improved epsilon-constrained Method in
 * MOEA/D for CMOPs with Large Infeasible Regions Fan, Z., Li, W., Cai, X. et al. Soft Comput
 * (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP6 extends LIRCMOP5 {

  public LIRCMOP6() {
    this(30);
  }

  /** Constructor */
  public LIRCMOP6(int numberOfVariables) {
    super(numberOfVariables);
    setNumberOfConstraints(2);
    setName("LIRCMOP6");
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariable(i);
    }

    solution.setObjective(0, x[0] + 10 * g1(x) + 0.7057);
    solution.setObjective(1, 1 - x[0] * x[0] + 10 * g2(x) + 7057);

    evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  @Override
  public void evaluateConstraints(DoubleSolution solution) {
    double r = 0.1, theta = -0.25 * Math.PI;
    double[] a_array = new double[] {2.0, 2.0};
    double[] b_array = new double[] {8.0, 8.0};
    double[] xOffset = new double[] {1.8, 2.8};
    double[] yOffset = new double[] {1.8, 2.8};
    double f1 = solution.getObjective(0);
    double f2 = solution.getObjective(1);
    double[] constraint = new double[getNumberOfConstraints()];
    for (int i = 0; i < xOffset.length; i++) {
      constraint[i] =
          Math.pow(
                  ((f1 - xOffset[i]) * Math.cos(theta) - (f2 - yOffset[i]) * Math.sin(theta))
                      / a_array[i],
                  2)
              + Math.pow(
                  ((f1 - xOffset[i]) * Math.sin(theta) + (f2 - yOffset[i]) * Math.cos(theta))
                      / b_array[i],
                  2)
              - r;
    }

    solution.setConstraint(0, constraint[0]);
    solution.setConstraint(1, constraint[1]);
  }
}
