package org.uma.jmetal.component.examples.multiobjective.moead;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByQualityIndicator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.aggregationfunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

import static org.uma.jmetal.util.VectorUtils.readVectors;

/**
 * Class to configure and run the NSGA-II algorithm configured with standard settings.
 *
 * @author Antonio J. Nebro
 */
public class MOEADStoppingByQualityIndicatorExample {
  public static void main(String[] args) throws JMetalException, IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1";
    String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ1.3D.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;

    TerminationByQualityIndicator termination = new TerminationByQualityIndicator(
            new PISAHypervolume(),
            readVectors(referenceParetoFront, ","),
            0.95, 200000);

    String weightVectorDirectory = "resources/weightVectorFiles/moead";
    SequenceGenerator<Integer> sequenceGenerator = new RandomPermutationCycle(populationSize);
    boolean normalizeObjectives = false;

    EvolutionaryAlgorithm<DoubleSolution> moead =
        new MOEADBuilder<>(
                problem,
                populationSize,
                crossover,
                mutation,
                weightVectorDirectory,
                sequenceGenerator,
                normalizeObjectives)
            .setTermination(termination)
            .setAggregationFunction(new PenaltyBoundaryIntersection(5.0, normalizeObjectives))
            .build();

    moead.run();

    List<DoubleSolution> population = moead.result();
    JMetalLogger.logger.info("Total execution time : " + moead.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + moead.numberOfEvaluations());

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
