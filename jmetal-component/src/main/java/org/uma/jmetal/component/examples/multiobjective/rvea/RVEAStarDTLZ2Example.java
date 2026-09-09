package org.uma.jmetal.component.examples.multiobjective.rvea;

import java.io.IOException;
import org.uma.jmetal.component.algorithm.multiobjective.RVEAStarBuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Minimal RVEA* example on the regular three-objective DTLZ2 front. Run from the repository root.
 */
public class RVEAStarDTLZ2Example {
  public static void main(String[] args) throws IOException {
    JMetalRandom.getInstance().setSeed(42);
    var problem = new DTLZ2(12, 3);
    var crossover = new SBXCrossover(1.0, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    // With M=3 and H=12, the simplex lattice contains C(14, 2)=91 reference vectors.
    var algorithm =
        new RVEAStarBuilder<>(problem, 91, 25000, crossover, mutation, 2.0, 0.1, 12).build();
    RVEAExampleOutput.run(algorithm, problem);
  }
}
