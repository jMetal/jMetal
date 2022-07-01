package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Rastrigin;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Rosenbrock;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem LSMOP3
 */

public class LSMOP3 extends AbstractLSMOP1_4 {

  /**
   * Creates a default LSMOP3 problem (7 variables and 3 objectives)
   */
  public LSMOP3() {
    this(5, 300, 3);
  }

  /**
   * Creates a LSMOP3 problem instance
   *
   * @param nk                 Number of subcomponents in each variable group
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */


  public LSMOP3(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
    super(nk, numberOfVariables, numberOfObjectives);
    setName("LSMOP3");
  }

  @Override
  protected Function getOddFunction() {
    return new Rastrigin();
  }

  @Override
  protected Function getEvenFunction() {
    return new Rosenbrock();
  }

}
