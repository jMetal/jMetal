package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F20;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G3;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G6;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ZCAT20, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT20 extends ZCAT1 {

  public ZCAT20(int numberOfObjectives, int numberOfVariables) {
    this(numberOfObjectives, numberOfVariables, false, 1, false, false);
  }

  public ZCAT20() {
    this(3, 30, false, 1, false, false);
  }

  public ZCAT20(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);

    fFunction = new F20(numberOfObjectives);
    gFunction = complicatedParetoSet ? new G6(numberOfVariables, paretoSetDimension)
        : new G0(numberOfVariables, paretoSetDimension);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] normalizedVariables = zcatGetY(solution.variables());
    paretoSetDimension =
        (zcatValueIn(normalizedVariables[0], 0.1, 0.4) || zcatValueIn(normalizedVariables[0], 0.6,
            0.9)) ? 1 : numberOfObjectives - 1;

    gFunction = complicatedParetoSet ? new G3(numberOfVariables(), paretoSetDimension)
        : new G0(numberOfVariables(), paretoSetDimension);

    double[] alpha = zcatGetAlpha(normalizedVariables, numberOfObjectives(), fFunction);
    double[] beta = zcatGetBeta(normalizedVariables, numberOfVariables(), paretoSetDimension, bias,
        imbalance, level, gFunction);

    double[] f = zcatMopDefinition(alpha, beta, numberOfObjectives);
    IntStream.range(0, numberOfObjectives).forEach(i -> solution.objectives()[i] = f[i]);

    return solution;
  }

  public static void main(String[] args) {
    DoubleProblem problem = new ZCAT20();

    DoubleSolution solution = problem.createSolution();
    Collections.fill(solution.variables(), 0.45);

    problem.evaluate(solution);
    System.out.println(solution);
  }
}

