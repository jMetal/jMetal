package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Ackley;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Griewank;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** Class representing problem LSMOP4 */
public class LSMOP4_2_20 extends LSMOP4 {
  /** Creates a default LSMOP4 problem (20 variables and 2 objectives) */
  public LSMOP4_2_20() {
    super(5, 20, 2);
  }
}
