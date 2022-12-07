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
    name("LIRCMOP2");
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] fx = new double[solution.objectives().length];
    double[] x = new double[numberOfVariables()];
    for (int i = 0; i < numberOfVariables(); i++) {
      x[i] = solution.variables().get(i);
    }

    fx[0] = x[0] + g1(x);
    fx[1] = 1 - Math.sqrt(x[0]) + g2(x);

    solution.objectives()[0] = fx[0];
    solution.objectives()[1] = fx[1];

    evaluateConstraints(solution);
    return solution ;
  }
}
