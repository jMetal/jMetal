package org.uma.jmetal.problem.multiobjective.lircmop;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem LIR-CMOP2, defined in: An Improved epsilon-constrained Method in
 * MOEA/D for CMOPs with Large Infeasible Regions Fan, Z., Li, W., Cai, X. et al. Soft Comput
 * (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP2 extends LIRCMOP1 {

  /** Constructor */
  public LIRCMOP2() {
    super();
    setName("LIRCMOP2");
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double[] fx = new double[getNumberOfObjectives()];
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariable(i);
    }

    fx[0] = x[0] + g1(x);
    fx[1] = 1 - Math.sqrt(x[0]) + g2(x);

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);

    evaluateConstraints(solution);
  }
}
