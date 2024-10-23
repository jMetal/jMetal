package org.uma.jmetal.problem.multiobjective.zcat;

/**
 * Problem ZCAT12, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT17_2D extends ZCAT17 {


  public ZCAT17_2D() {
    super(2, 30, true, 1, false, false);
  }

  @Override
  public String name() {
    return "ZCAT17_2D";
  }
}

