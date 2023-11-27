package org.uma.jmetal.problem.multiobjective.zcat;

import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F2;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G5;

/**
 * Problem ZCAT2, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT2 extends ZCAT1 {

  public ZCAT2(int numberOfObjectives, int numberOfVariables) {
    this(numberOfObjectives, numberOfVariables, false, 1, false, false);
  }

  public ZCAT2() {
    this(3, 30, true, 1, false, false);
  }

  public ZCAT2(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);

    paretoSetDimension = numberOfObjectives - 1;

    fFunction = new F2(numberOfObjectives);
    gFunction = complicatedParetoSet ? new G5(numberOfVariables, paretoSetDimension)
        : new G0(numberOfVariables, paretoSetDimension);
  }
}

