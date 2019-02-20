//  ZDT2.java
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

/** Class representing problem ZDT2 */
@SuppressWarnings("serial")
public class ZDT2 extends ZDT1 {

  /** Constructor. Creates default instance of problem ZDT2 (30 decision variables) */
  public ZDT2()  {
    this(30);
  }

  /**
   * Constructor.
   * Creates a new ZDT2 problem instance.
   *
   * @param numberOfVariables Number of variables
   */
  public ZDT2(Integer numberOfVariables) {
    super(numberOfVariables) ;
    setName("ZDT2");
  }

  /**
   * Returns the value of the ZDT2 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    return 1.0 - Math.pow(f / g, 2.0);
  }
}
