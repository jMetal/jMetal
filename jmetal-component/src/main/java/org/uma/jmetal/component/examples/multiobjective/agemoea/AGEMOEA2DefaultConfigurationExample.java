package org.uma.jmetal.component.examples.multiobjective.agemoea;

import java.io.IOException;
import java.util.List;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.AGEMOEABuilder;
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
 * Class to configure and run the AGE-MOEA-II algorithm using the components architecture.
 *
 * @author Annibale Panichella
 */
public class AGEMOEA2DefaultConfigurationExample {
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

    int populationSize = 100;
    int offspringPopulationSize = 100;

    Termination termination = new TerminationByEvaluations(40000);

    // Notice the Variant.AGEMOEAII argument used here
    EvolutionaryAlgorithm<DoubleSolution> agemoea2 =
        new AGEMOEABuilder<>(problem, populationSize, offspringPopulationSize, crossover, mutation, AGEMOEABuilder.Variant.AGEMOEAII)
            .setTermination(termination)
            .build();

    agemoea2.run();

    List<DoubleSolution> population = agemoea2.result();
    JMetalLogger.logger.info("Total execution time : " + agemoea2.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + agemoea2.numberOfEvaluations());

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
