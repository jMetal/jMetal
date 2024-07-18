package org.uma.jmetal.problem.multiobjective.zcat;

/**
 * Problem ZCAT12, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT16_2D extends ZCAT16 {


  public ZCAT16_2D() {
    super(2, 30, true, 1, false, false);
  }

  @Override
  public String name() {
    return "ZCAT16_2D";
  }
}

