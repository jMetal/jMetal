package org.uma.jmetal.problem.multiobjective.lircmop;

import static java.lang.Math.sqrt;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem LIR-CMOP9, defined in: An Improved epsilon-constrained Method in
 * MOEA/D for CMOPs with Large Infeasible Regions Fan, Z., Li, W., Cai, X. et al. Soft Comput
 * (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP10 extends LIRCMOP8 {

  /** Constructor */
  public LIRCMOP10() {
    this(30);
  }

  /** Constructor */
  public LIRCMOP10(int numberOfVariables) {
    super(numberOfVariables);
    name("LIRCMOP10");
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = new double[numberOfVariables()];
    for (int i = 0; i < numberOfVariables(); i++) {
      x[i] = solution.variables().get(i);
    }

    solution.objectives()[0] = 1.7057 * x[0] * (10 * g1(x) + 1);
    solution.objectives()[1] = 1.7957 * (1 - sqrt(x[0])) * (10 * g2(x) + 1);

    evaluateConstraints(solution);

    return solution ;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double f0 = solution.objectives()[0];
    double f1 = solution.objectives()[1];
    double N = 4.0, theta = 0.25 * Math.PI;
    double[] constraint = new double[numberOfConstraints()];
    constraint[0] =
        f0 * Math.sin(theta)
            + f1 * Math.cos(theta)
            - Math.sin(N * Math.PI * (f0 * Math.cos(theta) - f1 * Math.sin(theta)))
            - 1;
    double xOffset = 1.1, yOffset = 1.2, a = 2, b = 4.0, r = 0.1;
    constraint[1] =
        Math.pow(((f0 - xOffset) * Math.cos(-theta) - (f1 - yOffset) * Math.sin(-theta)) / a, 2)
            + Math.pow(
                ((f0 - xOffset) * Math.sin(-theta) + (f1 - yOffset) * Math.cos(-theta)) / b, 2)
            - r;

    solution.constraints()[0] = constraint[0];
    solution.constraints()[1] = constraint[1];
  }
}
