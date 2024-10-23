package org.uma.jmetal.problem.multiobjective.zcat;

/**
 * Problem ZCAT12, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT13_2D extends ZCAT13 {


  public ZCAT13_2D() {
    super(2, 30, true, 1, false, false);
  }

  @Override
  public String name() {
    return "ZCAT13_2D";
  }
}

