package org.uma.jmetal.algorithm.examples.multiobjective;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.agemoea.AGEMOEABuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.IOException;
import java.util.List;

/** Class to configure and run the AGEMOEA algorithm */
public class AGEMOEARunner extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws JMetalException, IOException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    problem = new DTLZ1();
    @NotNull String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ1.3D.csv";

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 30.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm =
        new AGEMOEABuilder<>(problem)
            .setCrossoverOperator(crossover)
            .setMutationOperator(mutation)
            .setMaxIterations(300)
            .setPopulationSize(200)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);

    if (!referenceParetoFront.equals("")) {
      QualityIndicatorUtils.printQualityIndicators(
              SolutionListUtils.getMatrixWithObjectiveValues(population),
              VectorUtils.readVectors(referenceParetoFront, ","));
    }
  }
}
