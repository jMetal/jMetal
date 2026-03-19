package org.uma.jmetal.component.examples.multiobjective.smsemoa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.SMSEMOABuilder;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class SMSEMOABenchmarkRunner {
  public static void main(String[] args) throws IOException {
    Check.that(
        args.length >= 5,
        "Usage: <problem> <populationSize> <maxEvaluations> <seed> <frontOutputPath>");

    String problemKey = args[0].trim().toLowerCase();
    int populationSize = Integer.parseInt(args[1]);
    int maxEvaluations = Integer.parseInt(args[2]);
    long seed = Long.parseLong(args[3]);
    Path frontOutputPath = Path.of(args[4]);

    Problem<DoubleSolution> problem = createProblem(problemKey);
    double mutationProbability = 1.0 / problem.numberOfVariables();

    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(mutationProbability, 20.0);

    JMetalRandom.getInstance().setSeed(seed);

    EvolutionaryAlgorithm<DoubleSolution> algorithm =
        new SMSEMOABuilder<>(problem, populationSize, crossover, mutation)
            .setTermination(new TerminationByEvaluations(maxEvaluations))
            .build();

    algorithm.run();

    List<DoubleSolution> nonDominatedFront =
        SolutionListUtils.getNonDominatedSolutions(algorithm.result());
    writeFront(frontOutputPath, nonDominatedFront);

    System.out.printf(
        "runtime_ms=%d evaluations=%d solutions=%d%n",
        algorithm.totalComputingTime(),
        algorithm.numberOfEvaluations(),
        nonDominatedFront.size());
  }

  private static Problem<DoubleSolution> createProblem(String problemKey) {
    if ("zdt1".equals(problemKey)) {
      return new ZDT1();
    } else if ("zdt4".equals(problemKey)) {
      return new ZDT4();
    } else if ("dtlz1".equals(problemKey)
        || "dtlz1-3".equals(problemKey)
        || "dtlz1_3".equals(problemKey)
        || "dtlz1:3".equals(problemKey)) {
      return new DTLZ1(7, 3);
    } else if ("dtlz2-3".equals(problemKey)
        || "dtlz2_3".equals(problemKey)
        || "dtlz2:3".equals(problemKey)) {
      return new DTLZ2(12, 3);
    } else if ("dtlz2-5".equals(problemKey)
        || "dtlz2_5".equals(problemKey)
        || "dtlz2:5".equals(problemKey)) {
      return new DTLZ2(14, 5);
    }

    throw new IllegalArgumentException("Unsupported problem: " + problemKey);
  }

  private static void writeFront(Path outputPath, List<DoubleSolution> front) throws IOException {
    Path parent = outputPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      for (DoubleSolution solution : front) {
        double[] objectives = solution.objectives();
        for (int index = 0; index < objectives.length; index++) {
          if (index > 0) {
            writer.write(',');
          }
          writer.write(Double.toString(objectives[index]));
        }
        writer.newLine();
      }
    }
  }
}
