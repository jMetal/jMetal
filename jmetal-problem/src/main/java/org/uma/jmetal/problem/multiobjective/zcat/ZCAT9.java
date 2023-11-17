package org.uma.jmetal.problem.multiobjective.zcat;


import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F8;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F9;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G2;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G7;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ZCAT9 extends ZCAT1 {

  public ZCAT9(int numberOfObjectives, int numberOfVariables) {
    this(numberOfObjectives, numberOfVariables, true, 1, false, false);
  }

  public ZCAT9() {
    this(3, 30, true, 1, false, false);
  }

  public ZCAT9(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);

    paretoSetDimension = numberOfObjectives - 1;

    fFunction = new F9(numberOfObjectives);
    gFunction = complicatedParetoSet ? new G7(numberOfVariables, paretoSetDimension)
        : new G0(numberOfVariables, paretoSetDimension);
  }

  public static void main(String[] args) {
    DoubleProblem problem = new ZCAT9();

    DoubleSolution solution = problem.createSolution();
    Collections.fill(solution.variables(), 0.45);

    problem.evaluate(solution) ;
    System.out.println(solution) ;
  }
}

