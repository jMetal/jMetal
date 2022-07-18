package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.lab.visualization.plot.PlotFront;
import org.uma.jmetal.lab.visualization.plot.impl.Plot2D;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run the NSGA-II algorithm to solve a bi-objective problem and plotting
 * the result front with Plotli
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIWithPlotly2DChartExample {
  public static void main(String[] args) throws JMetalException, IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT3";
    @NotNull String referenceParetoFront = "resources/referenceFrontsCSV/ZDT3.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    @NotNull Termination termination = new TerminationByEvaluations(25000);

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    crossover,
                    mutation)
        .setTermination(termination)
        .build();

    nsgaii.run();

    List<DoubleSolution> population = nsgaii.getResult();
    JMetalLogger.logger.info("Total execution time : " + nsgaii.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + nsgaii.getNumberOfEvaluations());

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    QualityIndicatorUtils.printQualityIndicators(
        SolutionListUtils.getMatrixWithObjectiveValues(population),
        VectorUtils.readVectors(referenceParetoFront, ","));

    @NotNull PlotFront plot = new Plot2D(new ArrayFront(population).getMatrix(), problem.getName() + " (NSGA-II)");
    plot.plot();
  }
}
