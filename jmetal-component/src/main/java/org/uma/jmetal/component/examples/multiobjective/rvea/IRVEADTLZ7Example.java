package org.uma.jmetal.component.examples.multiobjective.rvea;

import java.io.IOException;
import org.uma.jmetal.component.algorithm.multiobjective.IRVEABuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Minimal iRVEA example on the disconnected three-objective DTLZ7 front. Run from the repo root.
 */
public class IRVEADTLZ7Example {
  public static void main(String[] args) throws IOException {
    JMetalRandom.getInstance().setSeed(42);
    var problem = new DTLZ7(22, 3);
    var crossover = new SBXCrossover(1.0, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    var algorithm =
        new IRVEABuilder<>(problem, 91, 25000, crossover, mutation, 2.0, 0.1, 12)
            .setNumberOfSubregions(40)
            .build();
    RVEAExampleOutput.run(algorithm, problem);
  }
}
