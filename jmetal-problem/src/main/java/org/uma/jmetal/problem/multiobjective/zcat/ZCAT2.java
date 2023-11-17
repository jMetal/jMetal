package org.uma.jmetal.problem.multiobjective.zcat;


import java.util.Collections;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F1;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F2;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G4;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G5;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ZCAT2 extends ZCAT1 {

  public ZCAT2(int numberOfObjectives, int numberOfVariables) {
    this(numberOfObjectives, numberOfVariables, true, 1, false, false);
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

