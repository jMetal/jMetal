package org.uma.jmetal.component.examples.multiobjective.rvea;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.multiobjective.IRVEABuilder;
import org.uma.jmetal.component.algorithm.multiobjective.RVEABuilder;
import org.uma.jmetal.component.algorithm.multiobjective.RVEAStarBuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ5;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT2;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT3;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * Runs RVEA, RVEA*, and iRVEA with the same seed and budget on a DTLZ or ZDT problem. Optional
 * arguments: {@code DTLZ2|DTLZ5|DTLZ7|ZDT1|ZDT2|ZDT3 maxEvaluations seed}; defaults: DTLZ5 25000
 * 42. DTLZ uses three objectives and 91 vectors; ZDT uses two objectives, 30 decision variables,
 * and 100 vectors.
 */
public class RVEAVariantsExample {
  public static void main(String[] args) throws IOException {
    String problemName = args.length > 0 ? args[0] : "DTLZ5";
    int budget = args.length > 1 ? Integer.parseInt(args[1]) : 25000;
    long seed = args.length > 2 ? Long.parseLong(args[2]) : 42;
    Problem<DoubleSolution> problem =
        switch (problemName) {
          case "DTLZ2" -> new DTLZ2(12, 3);
          case "DTLZ5" -> new DTLZ5(12, 3);
          case "DTLZ7" -> new DTLZ7(22, 3);
          case "ZDT1" -> new ZDT1();
          case "ZDT2" -> new ZDT2();
          case "ZDT3" -> new ZDT3();
          default ->
              throw new IllegalArgumentException("Choose DTLZ2, DTLZ5, DTLZ7, ZDT1, ZDT2, or ZDT3");
        };
    int divisions = problem.numberOfObjectives() == 2 ? 99 : 12;
    int populationSize =
        ReferencePointGenerator.calculateNumberOfReferencePoints(
            problem.numberOfObjectives(), divisions);
    for (String variant : List.of("RVEA", "RVEA*", "iRVEA")) {
      JMetalRandom.getInstance().setSeed(seed);
      var crossover = new SBXCrossover(1.0, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
      RVEABuilder<DoubleSolution> builder =
          switch (variant) {
            case "RVEA" ->
                new RVEABuilder<>(
                    problem, populationSize, budget, crossover, mutation, 2, 0.1, divisions);
            case "RVEA*" ->
                new RVEAStarBuilder<>(
                    problem, populationSize, budget, crossover, mutation, 2, 0.1, divisions);
            case "iRVEA" ->
                new IRVEABuilder<>(
                    problem, populationSize, budget, crossover, mutation, 2, 0.1, divisions);
            default -> throw new IllegalArgumentException(variant);
          };
      RVEAExampleOutput.run(builder.build(), problem);
    }
  }
}
