package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.function.Function;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F19;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G6;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ZCAT9, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT19 extends ZCAT1 {

  public ZCAT19() {
    this(
        DefaultZCATSettings.numberOfObjectives,
        DefaultZCATSettings.numberOfVariables,
        DefaultZCATSettings.complicatedParetoSet,
        DefaultZCATSettings.level,
        DefaultZCATSettings.bias,
        DefaultZCATSettings.imbalance);
  }

  public ZCAT19(
      int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias,
      boolean imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);
    name("ZCAT19");

    fFunction = new F19(numberOfObjectives);
    gFunction =
        complicatedParetoSet
            ? new G6(numberOfVariables, paretoSetDimension)
            : new G0(numberOfVariables, paretoSetDimension);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] normalizedVariables = zcatGetY(solution.variables());
    // Local, not the inherited paretoSetDimension/gFunction fields: this value is
    // solution-dependent (recomputed per evaluate() call), so mutating the shared instance
    // fields is not thread-safe when the same problem instance is evaluated concurrently
    // (e.g. Evolver's async multi-threaded meta-optimizer reuses one Problem instance across
    // worker threads).
    int localParetoSetDimension =
        (zcatValueIn(normalizedVariables[0], 0.0, 0.2)
                || zcatValueIn(normalizedVariables[0], 0.4, 0.6))
            ? 1
            : numberOfObjectives - 1;

    Function<double[], double[]> localGFunction =
        complicatedParetoSet
            ? new G6(numberOfVariables(), localParetoSetDimension)
            : new G0(numberOfVariables(), localParetoSetDimension);

    double[] alpha = zcatGetAlpha(normalizedVariables, numberOfObjectives(), fFunction);
    double[] beta =
        zcatGetBeta(
            normalizedVariables,
            numberOfVariables(),
            localParetoSetDimension,
            bias,
            imbalance,
            level,
            localGFunction);

    double[] f = zcatMopDefinition(alpha, beta, numberOfObjectives);
    IntStream.range(0, numberOfObjectives).forEach(i -> solution.objectives()[i] = f[i]);

    return solution;
  }
}
