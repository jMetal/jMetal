//  ZDT5.java
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

import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.JMetalException;

import java.util.BitSet;

/**
 * Class representing problem ZDT5
 */
@SuppressWarnings("serial")
public class ZDT5 extends AbstractBinaryProblem {
	private int[] bitsPerVariable ;

  /** Creates a default instance of problem ZDT5 (11 decision variables) */
  public ZDT5() {
    this(11);
  }

  /**
   * Creates a instance of problem ZDT5
   *
   * @param numberOfVariables Number of variables.
   */
  public ZDT5(Integer numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(2);
    setName("ZDT5");

    bitsPerVariable = new int[numberOfVariables] ;

    bitsPerVariable[0] = 30;
    for (int var = 1; var < numberOfVariables; var++) {
      bitsPerVariable[var] = 5;
    }
  }
  
  @Override
  protected int getBitsPerVariable(int index) {
  	if ((index <0) || (index >= this.getNumberOfVariables())) {
  		throw new JMetalException("Index value is incorrect: " + index) ;
  	}
  	return bitsPerVariable[index] ;
  }

  /** Evaluate() method */
  public void evaluate(BinarySolution solution) {
    double[] f = new double[solution.getNumberOfObjectives()];
    f[0] = 1 + u(solution.getVariableValue(0));
    double g = evalG(solution);
    double h = evalH(f[0], g);
    f[1] = h * g;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Returns the value of the ZDT5 function G.
   *
   * @param solution The solution.
   */
  public double evalG(BinarySolution solution) {
    double res = 0.0;
    for (int i = 1; i < solution.getNumberOfVariables(); i++) {
      res += evalV(u(solution.getVariableValue(i)));
    }

    return res;
  }

  /**
   * Returns the value of the ZDT5 function V.
   *
   * @param value The parameter of V function.
   */
  public double evalV(double value) {
    if (value < 5.0) {
      return 2.0 + value;
    } else {
      return 1.0;
    }
  }

  /**
   * Returns the value of the ZDT5 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    return 1 / f;
  }

  /**
   * Returns the u value defined in ZDT5 for a solution.
   *
   * @param bitset A bitset variable
   */
  private double u(BitSet bitset) {
    return bitset.cardinality() ;
  }
}
