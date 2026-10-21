package org.uma.jmetal.component.examples.multiobjective.rvea;

import java.util.Locale;
import org.uma.jmetal.component.algorithm.multiobjective.RVEABuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Demonstrates the constraint handling of RVEA (C-RVEA) on Srinivas, writing the feasible solutions
 * found to VAR.csv and FUN.csv.
 */
public class ConstrainedRVEAExample {
  public static void main(String[] args) {
    JMetalRandom.getInstance().setSeed(42);
    var problem = new Srinivas();
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
    var feasibleSolutions =
        algorithm.result().stream().filter(ConstraintHandling::isFeasible).toList();
    System.out.printf(
        Locale.ROOT,
        "%s: constraints=%d, evaluations=%d, survivors=%d, feasible=%d%n",
        problem.name(),
        problem.numberOfConstraints(),
        algorithm.numberOfEvaluations(),
        algorithm.result().size(),
        feasibleSolutions.size());
    feasibleSolutions.forEach(
        solution ->
            System.out.printf(
                Locale.ROOT,
                "%.12g,%.12g%n",
                solution.objectives()[0],
                solution.objectives()[1]));

    new SolutionListOutput(feasibleSolutions)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();
  }
}
