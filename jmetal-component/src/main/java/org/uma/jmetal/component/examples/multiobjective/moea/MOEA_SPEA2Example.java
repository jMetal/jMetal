package org.uma.jmetal.component.examples.multiobjective.moea;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.RDSMOEABuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.KnnDensityEstimator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.StrengthRanking;

/**
 * Class to configure and run the NSGA-II algorithm configured with standard settings.
 *
 * @author Antonio J. Nebro
 */
public class MOEA_SPEA2Example {
  public static void main(String[] args) throws JMetalException, IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
    String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ2.3D.csv";

    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;
    int offspringPopulationSize = 100;

    Termination termination = new TerminationByEvaluations(50000);

    Ranking<DoubleSolution> ranking = new StrengthRanking<>();
    DensityEstimator<DoubleSolution> densityEstimator = new KnnDensityEstimator<>(1, false);

    RankingAndDensityEstimatorReplacement.RemovalPolicy removalPolicy = RankingAndDensityEstimatorReplacement.RemovalPolicy.SEQUENTIAL ;

    EvolutionaryAlgorithm<DoubleSolution> spea2 =
        new RDSMOEABuilder<>(
                problem,
                populationSize,
                offspringPopulationSize,
                crossover,
                mutation,
                ranking,
                densityEstimator,
                removalPolicy)
            .setTermination(termination)
            .build();

    spea2.run();

    List<DoubleSolution> population = spea2.result();
    JMetalLogger.logger.info("Total execution time : " + spea2.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + spea2.numberOfEvaluations());

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
