package org.uma.jmetal.component.examples.multiobjective.moead;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADDEBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.ImprovedEpsilonCriterion;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.Tanaka;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

/**
 * Class to configure and run MOEA/D-DE on the constrained problem Tanaka, using the improved
 * epsilon constraint-handling method of Fan et al. as subproblem update criterion.
 *
 * @author Antonio J. Nebro
 */
public class MOEADDEWithImprovedEpsilonExample {

  public static void main(String[] args) throws JMetalException, IOException {
    Problem<DoubleSolution> problem = new Tanaka();
    String referenceParetoFront = "resources/referenceFrontsCSV/Tanaka.csv";

    double cr = 1.0;
    double f = 0.5;

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int populationSize = 100;
    int maximumNumberOfEvaluations = 30000;

    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    String weightVectorDirectory = "resources/weightVectorFiles/moead";

    SequenceGenerator<Integer> sequenceGenerator = new RandomPermutationCycle(populationSize);
    boolean normalizeObjectives = false;

    int tc = (int) (0.8 * maximumNumberOfEvaluations / populationSize);

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
