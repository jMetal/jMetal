package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F11;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G3;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ZCAT11, defined in: "Challenging test problems for multi-and many-objective
 * optimization", DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT11 extends ZCAT1 {

  public ZCAT11() {
    this(
        DefaultZCATSettings.numberOfObjectives,
        DefaultZCATSettings.numberOfVariables,
        DefaultZCATSettings.complicatedParetoSet,
        DefaultZCATSettings.level,
        DefaultZCATSettings.bias,
        DefaultZCATSettings.imbalance);
  }

  public ZCAT11(
      int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias,
      boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);
    name("ZCAT11");

    paretoSetDimension = numberOfObjectives - 1;

    fFunction = new F11(numberOfObjectives);
    gFunction =
        complicatedParetoSet
            ? new G3(numberOfVariables, paretoSetDimension)
            : new G0(numberOfVariables, paretoSetDimension);
  }
}
