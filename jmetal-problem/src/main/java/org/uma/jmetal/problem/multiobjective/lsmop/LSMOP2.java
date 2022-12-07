package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Griewank;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Schwefel;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem LSMOP2
 */

public class LSMOP2 extends AbstractLSMOP1_4 {

  /**
   * Creates a default LSMOP2 problem (7 variables and 3 objectives)
   */
  public LSMOP2() {
    this(5, 300, 3);
  }

  /**
   * Creates a LSMOP2 problem instance
   *
   * @param nk                 Number of subcomponents in each variable group
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */


  public LSMOP2(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
    super(nk, numberOfVariables, numberOfObjectives);
    name("LSMOP2");
  }

  @Override
  protected Function getOddFunction() {
    return new Griewank();
  }

  @Override
  protected Function getEvenFunction() {
    return new Schwefel();
  }

}
