package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
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
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure a steady-state version of NSGA-II
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIISteadyStateExample {
  public static void main(String[] args) throws JMetalException, IOException {
    var problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    var referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    @NotNull var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 100;
    var offspringPopulationSize = 1;

    Termination termination = new TerminationByEvaluations(20000);

    var nsgaii = new NSGAIIBuilder<>(
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    crossover,
                    mutation)
        .setTermination(termination)
        .build();

    nsgaii.run();

    var population = nsgaii.getResult();
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
  }
}
