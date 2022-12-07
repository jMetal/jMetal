package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Sphere;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem LSMOP1
 */

public class LSMOP1 extends AbstractLSMOP1_4 {

  /**
   * Creates a default LSMOP1 problem (7 variables and 3 objectives)
   */
  public LSMOP1() {
    this(5, 300, 3);
  }

  /**
   * Creates a LSMOP1 problem instance
   *
   * @param nk                 Number of subcomponents in each variable group
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */


  public LSMOP1(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
    super(nk, numberOfVariables, numberOfObjectives);
    name("LSMOP1");

  }

  @Override
  protected Function getOddFunction() {
    return new Sphere();
  }

  @Override
  protected Function getEvenFunction() {
    return new Sphere();
  }


}
