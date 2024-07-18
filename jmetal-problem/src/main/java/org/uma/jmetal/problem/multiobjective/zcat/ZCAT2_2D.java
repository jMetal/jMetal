package org.uma.jmetal.problem.multiobjective.zcat;

/**
 * Problem ZCAT1_2D (configured with 2 objectives), defined in: "Challenging test problems for
 * multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT2_2D extends ZCAT2 {
  public ZCAT2_2D() {
    super(2, 30, true, 1, false, false);
  }

  @Override
  public String name() {
    return "ZCAT2_2D";
  }
}
