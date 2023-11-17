package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F16;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F19;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G10;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G6;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ZCAT19 extends ZCAT1 {

  public ZCAT19(int numberOfObjectives, int numberOfVariables) {
    this(numberOfObjectives, numberOfVariables, true, 1, false, false);
  }

  public ZCAT19() {
    this(3, 30, true, 1, false, false);
  }

  public ZCAT19(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);

    fFunction = new F19(numberOfObjectives);
    gFunction = complicatedParetoSet ? new G6(numberOfVariables, paretoSetDimension)
        : new G0(numberOfVariables, paretoSetDimension);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] normalizedVariables = zcatGetY(solution.variables());
    paretoSetDimension =
        (zcatValueIn(normalizedVariables[0], 0.0, 0.2) || zcatValueIn(normalizedVariables[0], 0.4,
            0.6)) ? 1 : numberOfObjectives - 1;

    gFunction = complicatedParetoSet ? new G6(numberOfVariables(), paretoSetDimension)
        : new G0(numberOfVariables(), paretoSetDimension);

    double[] alpha = zcatGetAlpha(normalizedVariables, numberOfObjectives(), fFunction);
    double[] beta = zcatGetBeta(normalizedVariables, numberOfVariables(), paretoSetDimension, bias,
        imbalance, level, gFunction);

    double[] f = zcatMopDefinition(alpha, beta, numberOfObjectives);
    IntStream.range(0, numberOfObjectives).forEach(i -> solution.objectives()[i] = f[i]);

    return solution;
  }

  public static void main(String[] args) {
    DoubleProblem problem = new ZCAT19();

    DoubleSolution solution = problem.createSolution();
    Collections.fill(solution.variables(), 0.45);

    problem.evaluate(solution);
    System.out.println(solution);
  }
}

