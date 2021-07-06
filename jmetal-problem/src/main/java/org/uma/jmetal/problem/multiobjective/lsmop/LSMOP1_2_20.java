package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Sphere;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** Class representing problem LSMOP1 */
public class LSMOP1_2_20 extends LSMOP1 {

  /** Creates a default LSMOP1 problem (20 variables and 2 objectives) */
  public LSMOP1_2_20() {
    super(5, 20, 2);
  }
}
