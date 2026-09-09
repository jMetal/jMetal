package org.uma.jmetal.component.examples.multiobjective.rvea;

import java.io.IOException;
import java.util.Locale;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** Writes example results and reports IGD+ against the reference front shipped with jMetal. */
final class RVEAExampleOutput {
  private RVEAExampleOutput() {}

  static void run(EvolutionaryAlgorithm<DoubleSolution> algorithm, Problem<DoubleSolution> problem)
      throws IOException {
    String dimensionSuffix =
        problem.numberOfObjectives() == 2 ? "" : "." + problem.numberOfObjectives() + "D";
    double[][] referenceFront =
        VectorUtils.readVectors(
            "resources/referenceFrontsCSV/" + problem.name() + dimensionSuffix + ".csv", ",");
    algorithm.run();
    String suffix = algorithm.name().replace("*", "Star") + "." + problem.name() + ".csv";
    var front = SolutionListUtils.getNonDominatedSolutions(algorithm.result());
    new SolutionListOutput(front)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR." + suffix, ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN." + suffix, ","))
        .print();
    double igdPlus =
        new InvertedGenerationalDistancePlus(referenceFront)
            .compute(SolutionListUtils.getMatrixWithObjectiveValues(front));
    System.out.printf(
        Locale.US,
        "%s %s: seed=%d, evaluations=%d, survivors=%d, nondominated=%d, IGD+=%.6f, time=%d ms%n",
        algorithm.name(),
        problem.name(),
        JMetalRandom.getInstance().getSeed(),
        algorithm.numberOfEvaluations(),
        algorithm.result().size(),
        front.size(),
        igdPlus,
        algorithm.totalComputingTime());
    System.out.println("Saved FUN." + suffix + " and VAR." + suffix);
  }
}
