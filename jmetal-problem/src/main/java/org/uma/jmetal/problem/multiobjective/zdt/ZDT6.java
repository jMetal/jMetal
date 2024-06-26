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
    name("ZDT6");
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] f = new double[solution.objectives().length];

    double x1;
    x1 = solution.variables().get(0);
    f[0] = 1 - Math.exp(-4 * x1) * Math.pow(Math.sin(6 * Math.PI * x1), 6);
    double g = this.evalG(solution);
    double h = this.evalH(f[0], g);
    f[1] = h * g;

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    return solution ;
  }

  /**
   * Returns the value of the ZDT6 function G.
   *
   * @param solution Solution
   */
  @Override
  protected double evalG(DoubleSolution solution) {
    double g = 0.0;
    for (int i = 1; i < solution.variables().size(); i++) {
      g += solution.variables().get(i);
    }
    g = g / (solution.variables().size() - 1);
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
  @Override
  protected double evalH(double f, double g) {
    return 1.0 - Math.pow((f / g), 2.0);
  }
}
