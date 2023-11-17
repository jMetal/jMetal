package org.uma.jmetal.problem.multiobjective.zcat;


import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F5;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F6;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G4;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G9;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ZCAT6 extends ZCAT1 {

  public ZCAT6(int numberOfObjectives, int numberOfVariables) {
    this(numberOfObjectives, numberOfVariables, true, 1, false, false);
  }

  public ZCAT6() {
    this(3, 30, true, 1, false, false);
  }

  public ZCAT6(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);

    paretoSetDimension = numberOfObjectives - 1;

    fFunction = new F6(numberOfObjectives);
    gFunction = complicatedParetoSet ? new G4(numberOfVariables, paretoSetDimension)
        : new G0(numberOfVariables, paretoSetDimension);
  }

  public static void main(String[] args) {
    DoubleProblem problem = new ZCAT6();

    DoubleSolution solution = problem.createSolution();
    Collections.fill(solution.variables(), 0.45);

    problem.evaluate(solution) ;
    System.out.println(solution) ;
  }
}

