package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Rastrigin;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Rosenbrock;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** Class representing problem LSMOP3 */
public class LSMOP3_2_20 extends LSMOP3 {
  /** Creates a default LSMOP3 problem (20 variables and 2 objectives) */
  public LSMOP3_2_20() {
    super(5, 20, 2);
  }
}
