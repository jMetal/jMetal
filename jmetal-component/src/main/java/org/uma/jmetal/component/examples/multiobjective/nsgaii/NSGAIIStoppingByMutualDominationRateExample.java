package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByAnyOf;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByMutualDominationRate;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class to configure and run the NSGA-II algorithm with the adaptive stopping criterion MGBM
 * ({@link TerminationByMutualDominationRate}), which stops the search when the population no longer
 * improves, combined with a maximum number of evaluations in case it does not stop before.
 *
 * @author Antonio J. Nebro
 */
public class NSGAIIStoppingByMutualDominationRateExample {

  public static void main(String[] args) throws JMetalException, IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    String referenceParetoFront = "resources/referenceFronts/DTLZ2.3D.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    double threshold = 0.05;
    double observationNoise = 0.05;
    double processNoise = 0.0;
    double epsilon = 0.01;
    var mutualDominationRate =
        new TerminationByMutualDominationRate(threshold, observationNoise, processNoise, epsilon);
    int maximumNumberOfEvaluations = 100000;
    Termination termination =
        new TerminationByAnyOf(
            mutualDominationRate, new TerminationByEvaluations(maximumNumberOfEvaluations));

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .build();

    nsgaii.run();

    List<DoubleSolution> population = nsgaii.result();
    boolean stoppedByTheCriterion = nsgaii.numberOfEvaluations() < maximumNumberOfEvaluations;
    JMetalLogger.logger.info(
        () ->
            (stoppedByTheCriterion
                    ? "Stopped by the mutual domination rate"
                    : "Stopped by the maximum number of evaluations")
                + ": "
                + nsgaii.numberOfEvaluations()
                + " evaluations, estimate of the MDR "
                + mutualDominationRate.estimate());
    JMetalLogger.logger.info("Total execution time : " + nsgaii.totalComputingTime() + "ms");

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    QualityIndicatorUtils.printQualityIndicators(
        SolutionListUtils.getMatrixWithObjectiveValues(population),
        VectorUtils.readVectors(referenceParetoFront, ","));
  }
}
