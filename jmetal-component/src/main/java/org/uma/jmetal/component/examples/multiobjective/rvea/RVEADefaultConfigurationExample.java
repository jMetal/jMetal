package org.uma.jmetal.component.examples.multiobjective.rvea;

import java.io.IOException;
import java.util.List;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.RVEABuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
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
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run the RVEA algorithm using the components architecture.
 *
 * @author 
 */
public class RVEADefaultConfigurationExample {
  public static void main(String[] args) throws JMetalException, IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3";
    String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ3.3D.csv";

    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 30.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 91; // H=12 in 3D gives 91 reference vectors
    int maxEvaluations = 40000;
    int h = 12; // Divisions for standard RVEA 3D
    double alpha = 2.0;
    double fr = 0.1;

    Termination termination = new TerminationByEvaluations(maxEvaluations);

    EvolutionaryAlgorithm<DoubleSolution> rvea =
        new RVEABuilder<>(problem, populationSize, maxEvaluations, crossover, mutation, alpha, fr, h)
            .setTermination(termination)
            .build();

    rvea.run();

    List<DoubleSolution> population = rvea.result();
    JMetalLogger.logger.info("Total execution time : " + rvea.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + rvea.numberOfEvaluations());

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
