package org.uma.jmetal.component.examples.multiobjective.rvea;

import org.uma.jmetal.component.algorithm.multiobjective.RVEABuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Binh2;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** Demonstrates automatic constraint support on Binh2; no files or reference fronts are written. */
public class ConstrainedRVEAExample {
  public static void main(String[] args) {
    JMetalRandom.getInstance().setSeed(42);
    var problem = new Binh2();
    var algorithm =
        new RVEABuilder<>(
                problem,
                21,
                2100,
                new SBXCrossover(1.0, 20.0),
                new PolynomialMutation(0.5, 20.0),
                2.0,
                0.1,
                20)
            .build();
    algorithm.run();
    var feasible = algorithm.result().stream().filter(ConstraintHandling::isFeasible).toList();
    System.out.printf(
        "Binh2: constraints=%d, evaluations=%d, survivors=%d, feasible=%d%n",
        problem.numberOfConstraints(),
        algorithm.numberOfEvaluations(),
        algorithm.result().size(),
        feasible.size());
    feasible.forEach(
        solution ->
            System.out.printf("%.12g,%.12g%n", solution.objectives()[0], solution.objectives()[1]));
  }
}
