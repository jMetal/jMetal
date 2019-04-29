//  ZDT3.java
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

/**
 * Class representing problem ZDT3
 */
@SuppressWarnings("serial")
public class ZDT3 extends ZDT1 {
  /** Constructor. Creates default instance of problem ZDT3 (30 decision variables) */
  public ZDT3() {
    this(30);
  }

  /**
   * Constructor.
   * Creates a instance of ZDT3 problem.
   *
   * @param numberOfVariables Number of variables.
   */
  public ZDT3(Integer numberOfVariables) {
    super(numberOfVariables) ;
    setName("ZDT3");
  }

  /**
   * Returns the value of the ZDT3 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  protected double evalH(double f, double g) {
    double h ;
    h = 1.0 - Math.sqrt(f / g)
      - (f / g) * Math.sin(10.0 * Math.PI * f);
    return h;
  }
}
