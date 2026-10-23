package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.lircmop.LIRCMOP5;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.constrainthandling.PushPullSearch;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run NSGA-II with a constraint-handling technique, push and pull search,
 * on the constrained problem LIR-CMOP5, whose feasible region is narrow and far from the
 * unconstrained front: push and pull search first ignores the constraints and then pulls the
 * population to the feasible region. An external archive keeps the feasible non-dominated
 * solutions found along the run, which are the result.
 *
 * <p>Other techniques of {@code org.uma.jmetal.util.constrainthandling} can be used in the same
 * way, e.g., {@code new EpsilonConstrained<>(0.2, 5.0, 0.5)} or {@code new
 * StochasticRanking<>(0.45)}.
 */
public class NSGAIIWithConstraintHandlingTechniqueExample {

  public static void main(String[] args) throws JMetalException, IOException {
    DoubleProblem problem = new LIRCMOP5();
    String referenceParetoFront = "resources/referenceFronts/LIRCMOP5.csv";

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;
    int offspringPopulationSize = 100;

    // The progress of the run, which push and pull search needs, comes from the termination
    Termination termination = new TerminationByEvaluations(25000);

    ConstraintHandlingTechnique<DoubleSolution> constraintHandlingTechnique =
        new PushPullSearch<>(0.001, 0.02, 0.8, 2.0, 0.95, 0.1);

    var archive = new NonDominatedSolutionListArchive<DoubleSolution>();

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .setConstraintHandlingTechnique(constraintHandlingTechnique)
        .setEvaluation(new SequentialEvaluationWithArchive<>(problem, archive))
        .build();

    nsgaii.run();

    List<DoubleSolution> front = archive.solutions();
    JMetalLogger.logger.info("Total execution time : " + nsgaii.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + nsgaii.numberOfEvaluations());
    JMetalLogger.logger.info(
        "Feasible solutions in the archive: "
            + Math.round(ConstraintHandling.feasibilityRatio(front) * front.size()));

    new SolutionListOutput(front)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    QualityIndicatorUtils.printQualityIndicators(
        VectorUtils.readVectors("FUN.csv", ","),
        VectorUtils.readVectors(referenceParetoFront, ","));
  }
}
