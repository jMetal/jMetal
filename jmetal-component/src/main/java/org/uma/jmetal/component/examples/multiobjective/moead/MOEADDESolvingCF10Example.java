package org.uma.jmetal.component.examples.multiobjective.moead;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADDEBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.ImprovedEpsilonCriterion;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.cf.CF10;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

/**
 * Class to configure and run MOEA/D-DE on the three-objective constrained problem CF10 (Xiang et
 * al., "Constrained Multiobjective Optimization: Test Problem Construction and Performance
 * Evaluations", IEEE TEVC, 2021). Constraints are handled with the improved epsilon criterion of
 * Fan et al., and every evaluated solution is stored in an unbounded external archive; the result
 * is a diverse subset of the feasible non-dominated solutions found, selected by distance-based
 * subset selection.
 *
 * @author Antonio J. Nebro
 */
public class MOEADDESolvingCF10Example {

  public static void main(String[] args) throws JMetalException, IOException {
    Problem<DoubleSolution> problem = new CF10();
    String referenceParetoFront = "resources/referenceFrontsRSG/CF10.csv";

    double cr = 1.0;
    double f = 0.5;

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 91;
    int maximumNumberOfEvaluations = 50000;

    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    String weightVectorDirectory = "resources/weightVectorFiles/moead";

    SequenceGenerator<Integer> sequenceGenerator = new RandomPermutationCycle(populationSize);
    boolean normalizeObjectives = false;

    int tc = (int) (0.8 * maximumNumberOfEvaluations / populationSize);

    Archive<DoubleSolution> externalArchive =
        new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), populationSize);

    EvolutionaryAlgorithm<DoubleSolution> moead = new MOEADDEBuilder(
        problem,
        populationSize,
        cr,
        f,
        mutation,
        weightVectorDirectory,
        sequenceGenerator,
        normalizeObjectives)
        .setTermination(termination)
        .setSubproblemUpdateCriterion(new ImprovedEpsilonCriterion<>(tc))
        .setEvaluation(new SequentialEvaluationWithArchive<>(problem, externalArchive))
        .build();

    moead.run();

    List<DoubleSolution> result = externalArchive.solutions();
    JMetalLogger.logger.info("Total execution time : " + moead.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + moead.numberOfEvaluations());
    JMetalLogger.logger.info("Archive size: " + result.size());
    JMetalLogger.logger.info(
        "Feasibility ratio of the result: " + ConstraintHandling.feasibilityRatio(result));

    new SolutionListOutput(result)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    QualityIndicatorUtils.printQualityIndicators(
        SolutionListUtils.getMatrixWithObjectiveValues(result),
        VectorUtils.readVectors(referenceParetoFront, ","));
  }
}
