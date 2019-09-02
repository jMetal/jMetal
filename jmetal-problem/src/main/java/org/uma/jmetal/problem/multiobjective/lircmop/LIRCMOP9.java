package org.uma.jmetal.problem.multiobjective.lircmop;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem LIR-CMOP9, defined in: An Improved epsilon-constrained Method in
 * MOEA/D for CMOPs with Large Infeasible Regions Fan, Z., Li, W., Cai, X. et al. Soft Comput
 * (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP9 extends LIRCMOP8 {

  /** Constructor */
  public LIRCMOP9() {
    this(30);
  }

  /** Constructor */
  public LIRCMOP9(int numberOfVariables) {
    super(numberOfVariables);
    setName("LIRCMOP9");
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariable(i);
    }

    solution.setObjective(0, 1.7057 * x[0] * (10 * g1(x) + 1));
    solution.setObjective(1, 1.7957 * (1 - x[0] * x[0]) * (10 * g2(x) + 1));

    evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double f0 = solution.getObjective(0);
    double f1 = solution.getObjective(1);
    double N = 4.0, theta = -0.25 * Math.PI;
    double[] constraint = new double[getNumberOfConstraints()];
    constraint[0] =
        f0 * Math.sin(theta)
            + f1 * Math.cos(theta)
            - Math.sin(N * Math.PI * (f0 * Math.cos(theta) - f1 * Math.sin(theta)))
            - 2;
    double xOffset = 1.40, yOffset = 1.40, a = 1.5, b = 6.0, r = 0.1;
    constraint[1] =
        Math.pow(((f0 - xOffset) * Math.cos(theta) - (f1 - yOffset) * Math.sin(theta)) / a, 2)
            + Math.pow(
                ((f0 - xOffset) * Math.sin(theta) + (f1 - yOffset) * Math.cos(theta)) / b, 2)
            - r;

    solution.setConstraint(0, constraint[0]);
    solution.setConstraint(1, constraint[1]);
  }
}
