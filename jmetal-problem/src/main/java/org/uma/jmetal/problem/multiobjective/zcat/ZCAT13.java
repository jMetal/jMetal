package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F12;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F13;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G1;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G10;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ZCAT13 extends ZCAT1 {

  public ZCAT13(int numberOfObjectives, int numberOfVariables) {
    this(numberOfObjectives, numberOfVariables, true, 1, false, false);
  }

  public ZCAT13() {
    this(3, 30, true, 1, false, false);
  }
  public ZCAT13(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);

    paretoSetDimension = numberOfObjectives - 1;

    fFunction = new F13(numberOfObjectives);
    gFunction = complicatedParetoSet ? new G1(numberOfVariables, paretoSetDimension)
        : new G0(numberOfVariables, paretoSetDimension);
  }

  public static void main(String[] args) {
    DoubleProblem problem = new ZCAT13();

    DoubleSolution solution = problem.createSolution();
    Collections.fill(solution.variables(), 0.45);

    problem.evaluate(solution) ;
    System.out.println(solution) ;
  }
}

