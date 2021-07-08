package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Griewank;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Schwefel;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** Class representing problem LSMOP2 */
public class LSMOP2_2_20 extends LSMOP2 {

  /** Creates a default LSMOP2 problem (20 variables and 2 objectives) */
  public LSMOP2_2_20() {
    super(5, 20, 2);
  }
}
