package org.uma.jmetal.component.examples.multiobjective.moead;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADBuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

/**
 * Class to configure and run the NSGA-II algorithm configured with standard settings.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class MOEADWithUnboundedArchiveExample {
  public static void main(String[] args) throws JMetalException, IOException {
    var problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2Minus";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    @NotNull var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 91;

    @NotNull Termination termination = new TerminationByEvaluations(40000);

    var weightVectorDirectory = "resources/weightVectorFiles/moead";

    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(populationSize) ;

    Archive<DoubleSolution> externalArchive = new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), populationSize) ;

    var moead = new MOEADBuilder<>(
        problem,
        populationSize,
        crossover,
        mutation,
        weightVectorDirectory,
        sequenceGenerator)
        .setTermination(termination)
        .setEvaluation(new SequentialEvaluationWithArchive<DoubleSolution>(problem, externalArchive))
        .build();

    moead.run();

    var population = externalArchive.getSolutionList();
    JMetalLogger.logger.info("Total execution time : " + moead.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + moead.getNumberOfEvaluations());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
