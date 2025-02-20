package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F17;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ZCAT17, defined in: "Challenging test problems for multi-and many-objective
 * optimization", DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT17 extends ZCAT1 {

  public ZCAT17() {
    this(
        DefaultZCATSettings.numberOfObjectives,
        DefaultZCATSettings.numberOfVariables,
        DefaultZCATSettings.complicatedParetoSet,
        DefaultZCATSettings.level,
        DefaultZCATSettings.bias,
        DefaultZCATSettings.imbalance);
  }

  public ZCAT17(
      int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias,
      boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);
    name("ZCAT17");

    paretoSetDimension = numberOfObjectives - 1;

    fFunction = new F17(numberOfObjectives);
    gFunction =
        complicatedParetoSet
            ? new G1(numberOfVariables, paretoSetDimension)
            : new G0(numberOfVariables, paretoSetDimension);
  }
}
