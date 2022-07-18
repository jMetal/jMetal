package org.uma.jmetal.problem.multiobjective.lircmop;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem LIR-CMOP3, defined in: An Improved epsilon-constrained Method in
 * MOEA/D for CMOPs with Large Infeasible Regions Fan, Z., Li, W., Cai, X. et al. Soft Comput
 * (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP7 extends LIRCMOP5 {

  /** Constructor */
  public LIRCMOP7() {
    this(30);
  }

  /** Constructor */
  public LIRCMOP7(int numberOfVariables) {
    super(numberOfVariables);
    setNumberOfConstraints(2);
    setName("LIRCMOP7");
  }

  /** EvaluateConstraints() method */
  @Override
  public void evaluateConstraints(DoubleSolution solution) {
    double r = 0.1, theta = -0.25 * Math.PI;
    var a_array = new double[] {2.0, 2.5, 2.5};
    var b_array = new double[] {6.0, 12.0, 10.0};
    var xOffset = new double[] {1.2, 2.25, 3.5};
    var yOffset = new double[] {1.2, 2.25, 3.5};
    var f1 = solution.objectives()[0];
    var f2 = solution.objectives()[1];
    var constraint = new double[getNumberOfConstraints()];
    for (var i = 0; i < xOffset.length; i++) {
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

    solution.constraints()[0] = constraint[0];
    solution.constraints()[1] = constraint[1];
  }
}
