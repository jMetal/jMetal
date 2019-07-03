package org.uma.jmetal.problem.multiobjective.lircmop;

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
    super();
    setNumberOfConstraints(2);
    setName("LIRCMOP7");
  }

  /** EvaluateConstraints() method */
  @Override
  public void evaluateConstraints(DoubleSolution solution) {
    double r = 0.1, theta = -0.25 * Math.PI;
    double[] a_array = new double[]{2.0,2.5,2.5};
    double[] b_array = new double[]{6.0,12.0,10.0};
    double[] xOffset = new double[]{1.2,2.25,3.5};
    double[] yOffset = new double[]{1.2,2.25,3.5};
    double f1 = solution.getObjective(0);
    double f2 = solution.getObjective(1);
    double[] constraint = new double[getNumberOfConstraints()];
    for(int i = 0; i < xOffset.length; i++){
      constraint[i] = Math.pow(((f1 - xOffset[i]) * Math.cos(theta) - (f2 - yOffset[i]) * Math.sin(theta))/a_array[i] ,2)
          + Math.pow(((f1 - xOffset[i]) * Math.sin(theta) + (f2 - yOffset[i]) * Math.cos(theta))/b_array[i],2)
          - r;
    }

    solution.setConstraint(0, constraint[0]);
    solution.setConstraint(1, constraint[1]);
  }

  protected double g1(double[] x) {
    double result = 0.0;
    for (int i = 2; i < x.length; i += 2) {
      double tmp = Math.pow(x[i] - Math.sin(0.5 * i / x.length * Math.PI * x[0]), 2.0);
      result += tmp * tmp;
    }
    return result;
  }

  protected double g2(double[] x) {
    double result = 0.0;
    for (int i = 1; i < (getNumberOfVariables() - 1); i += 2) {
      double tmp = Math.pow(x[i] - Math.cos(0.5 * i / x.length * Math.PI * x[0]), 2.0);
      result += tmp * tmp;
    }

    return result;
  }
}
