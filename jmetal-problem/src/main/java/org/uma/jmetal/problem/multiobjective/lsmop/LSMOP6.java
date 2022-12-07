package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Rosenbrock;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Schwefel;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class LSMOP6 extends AbstractLSMOP5_8 {

  /**
   * Creates a default LSMOP6 problem (7 variables and 3 objectives)
   */
  public LSMOP6() {
    this(5, 300, 3);
  }

  /**
   * Creates a LSMOP6 problem instance
   *
   * @param nk                 Number of subcomponents in each variable group
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */


  public LSMOP6(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
    super(nk, numberOfVariables, numberOfObjectives);
    name("LSMOP6");
  }

  @Override
  protected Function getOddFunction() {
    return new Rosenbrock();
  }

  @Override
  protected Function getEvenFunction() {
    return new Schwefel();
  }

}
