package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.constrainthandling.ConstrainedDominance;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run NSGA-II with its own constraint-handling technique, Deb's
 * constrained-domination principle, on the constrained problem {@link Srinivas}: a feasible
 * solution is preferred to an infeasible one, two infeasible ones are compared by their overall
 * constraint violation and two feasible ones by Pareto dominance. The default ranking of {@link
 * NSGAIIBuilder} ignores the constraints, so the technique has to be set to solve a constrained
 * problem.
 *
 * <p>The same result is obtained by setting a ranking with a {@code
 * DominanceWithConstraintsComparator} (see {@link NSGAIISolvingConstrainedProblemExample}); with
 * a technique, it can be replaced by any other one of {@code
 * org.uma.jmetal.util.constrainthandling}, such as {@code new EpsilonConstrained<>(0.2, 5.0, 0.5)}.
 */
public class NSGAIIWithConstrainedDominanceExample {

  public static void main(String[] args) throws JMetalException, IOException {
    DoubleProblem problem = new Srinivas();
    String referenceParetoFront = "resources/referenceFronts/Srinivas.csv";

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;
    int offspringPopulationSize = 100;

    Termination termination = new TerminationByEvaluations(25000);

    ConstraintHandlingTechnique<DoubleSolution> constraintHandlingTechnique =
        new ConstrainedDominance<>();

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .setConstraintHandlingTechnique(constraintHandlingTechnique)
        .build();

    nsgaii.run();

    List<DoubleSolution> population = nsgaii.result();
    JMetalLogger.logger.info("Total execution time : " + nsgaii.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + nsgaii.numberOfEvaluations());
    JMetalLogger.logger.info(
        "Ratio of feasible solutions: " + ConstraintHandling.feasibilityRatio(population));

    new SolutionListOutput(population)
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
