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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.Check;

/** Class representing problem ZDT5 */
public class ZDT5 extends AbstractBinaryProblem {
  private List<Integer> bitsPerVariable;

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

    bitsPerVariable = new ArrayList<>(numberOfVariables);

    bitsPerVariable.add(30);
    for (int i = 1; i < numberOfVariables; i++) {
      bitsPerVariable.add(5);
    }
  }

  @Override
  public List<Integer> getListOfBitsPerVariable() {
    return bitsPerVariable;
  }

  @Override
  public int getBitsFromVariable(int index) {
    Check.valueIsInRange(index, 0, this.getNumberOfVariables());

    return bitsPerVariable.get(index);
  }

  /** Evaluate() method */
  public BinarySolution evaluate(BinarySolution solution) {
    double[] f = new double[solution.objectives().length];
    f[0] = 1 + u(solution.variables().get(0));
    double g = evalG(solution);
    double h = evalH(f[0], g);
    f[1] = h * g;

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    return solution;
  }

  /**
   * Returns the value of the ZDT5 function G.
   *
   * @param solution The solution.
   */
  public double evalG(BinarySolution solution) {
      double res = 0.0;
      int bound = solution.variables().size();
      for (int i = 1; i < bound; i++) {
          double v = evalV(u(solution.variables().get(i)));
          res += v;
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
    return bitset.cardinality();
  }
}
