//  ZDT6.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//




//




// 



package org.uma.jmetal.problem.multiobjective.zdt;

import org.uma.jmetal.solution.DoubleSolution;

/**
 * Class representing problem ZDT6
 */
@SuppressWarnings("serial")
public class ZDT6 extends ZDT1 {

  /** Constructor. Creates a default instance of problem ZDT6 (10 decision variables) */
  public ZDT6()  {
    this(10);
  }

  /**
   * Creates a instance of problem ZDT6
   *
   * @param numberOfVariables Number of variables
   */
  public ZDT6(Integer numberOfVariables) {
    super(numberOfVariables) ;
    setName("ZDT6");
  }

  /**
   * Returns the value of the ZDT6 function G.
   *
   * @param solution Solution
   */
  protected double evalG(DoubleSolution solution) {
    double g = 0.0;
    for (int var = 1; var < solution.getNumberOfVariables(); var++) {
      g += solution.getVariableValue(var);
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
