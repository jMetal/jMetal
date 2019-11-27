package org.uma.jmetal.problem.multiobjective.zdt;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem ZDT6 Difined in E. Zitzler, K. Deb, and L. Thiele, "Comparison of
 * Multiobjective Evolutionary Algorithms: Empirical Results," Evolutionary Computation, vol. 8, no.
 * 2, pp. 173-195, 2000, doi: 10.1162/106365600568202.
 */
@SuppressWarnings("serial")
public class ZDT6 extends ZDT1 {

  /** Constructor. Creates a default instance of problem ZDT6 (10 decision variables) */
  public ZDT6() {
    this(10);
  }

  /**
   * Creates a instance of problem ZDT6
   *
   * @param numberOfVariables Number of variables
   */
  public ZDT6(Integer numberOfVariables) {
    super(numberOfVariables);
    setName("ZDT6");
  }

  /** Evaluate() method */
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];

    double x1;
    x1 = solution.getVariable(0);
    f[0] = 1 - Math.exp(-4 * x1) * Math.pow(Math.sin(6 * Math.PI * x1), 6);
    double g = this.evalG(solution);
    double h = this.evalH(f[0], g);
    f[1] = h * g;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Returns the value of the ZDT6 function G.
   *
   * @param solution Solution
   */
  protected double evalG(DoubleSolution solution) {
    double g = 0.0;
    for (int var = 1; var < solution.getNumberOfVariables(); var++) {
      g += solution.getVariable(var);
    }
    g = g / (solution.getNumberOfVariables() - 1);
    g = Math.pow(g, 0.25);
    g = 9.0 * g;
    g = 1.0 + g;
    return g;
  }

  /**
   * Returns the value of the ZDT6 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  protected double evalH(double f, double g) {
    return 1.0 - Math.pow((f / g), 2.0);
  }
}
