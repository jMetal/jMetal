package org.uma.jmetal.problem.multiobjective.lircmop;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem LIR-CMOP3, defined in: An Improved epsilon-constrained Method in
 * MOEA/D for CMOPs with Large Infeasible Regions Fan, Z., Li, W., Cai, X. et al. Soft Comput
 * (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP4 extends LIRCMOP2 {

  /** Constructor */
  public LIRCMOP4() {
    super();
    setNumberOfConstraints(3);
    setName("LIRCMOP4");
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariable(i);
    }

    final double a = 0.51;
    final double b = 0.5;
    final double c = 20.0;

    solution.setConstraint(0, (a - g1(x)) * (g1(x) - b));
    solution.setConstraint(1, (a - g2(x)) * (g2(x) - b));
    solution.setConstraint(2, Math.sin(c * Math.PI * x[0]) - 0.5);
  }
}
