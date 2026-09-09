package org.uma.jmetal.component.examples.multiobjective.rvea;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.multiobjective.IRVEABuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Minimal iRVEA example on the disconnected three-objective DTLZ7 front. Run from the repo root.
 */
public class IRVEADTLZ7Example {
  public static void main(String[] args) throws IOException {
    JMetalRandom.getInstance().setSeed(42);

    String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ7.3D.csv";
    var problem = new DTLZ7(22, 3);
    var crossover = new SBXCrossover(1.0, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    var rvea =
        new IRVEABuilder<>(problem, 91, 25000, crossover, mutation, 2.0, 0.1, 12)
            .setNumberOfSubregions(40)
            .build();

    rvea.run();

    List<DoubleSolution> population = rvea.result();
    List<DoubleSolution> paretoFront = SolutionListUtils.getNonDominatedSolutions(population);
    JMetalLogger.logger.info("Total execution time : " + rvea.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + rvea.numberOfEvaluations());
    JMetalLogger.logger.info("Number of non-dominated solutions: " + paretoFront.size());

    String suffix = rvea.name().replace("*", "Star") + "." + problem.name() + ".csv";
    new SolutionListOutput(paretoFront)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR." + suffix, ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN." + suffix, ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());

    QualityIndicatorUtils.printQualityIndicators(
        SolutionListUtils.getMatrixWithObjectiveValues(paretoFront),
        VectorUtils.readVectors(referenceParetoFront, ","));
  }
}
