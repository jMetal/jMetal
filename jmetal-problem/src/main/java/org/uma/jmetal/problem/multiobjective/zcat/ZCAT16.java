package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F16;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G10;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ZCAT16, defined in: "Challenging test problems for multi-and many-objective
 * optimization", DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT16 extends ZCAT1 {

  public ZCAT16() {
    this(
        DefaultZCATSettings.numberOfObjectives,
        DefaultZCATSettings.numberOfVariables,
        DefaultZCATSettings.complicatedParetoSet,
        DefaultZCATSettings.level,
        DefaultZCATSettings.bias,
        DefaultZCATSettings.imbalance);
  }

  public ZCAT16(
      int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias,
      boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);
    name("ZCAT16");

    paretoSetDimension = 1;

    fFunction = new F16(numberOfObjectives);
    gFunction =
        complicatedParetoSet
            ? new G10(numberOfVariables, paretoSetDimension)
            : new G0(numberOfVariables, paretoSetDimension);
  }
}
