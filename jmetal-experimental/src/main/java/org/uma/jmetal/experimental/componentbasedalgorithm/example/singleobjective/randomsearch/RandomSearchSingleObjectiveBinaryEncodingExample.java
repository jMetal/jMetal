package org.uma.jmetal.experimental.componentbasedalgorithm.example.singleobjective.randomsearch;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.ComponentBasedRandomSearchAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.Termination;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

/**
 * Class to configure and run the a random search.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class RandomSearchSingleObjectiveBinaryEncodingExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException {
    BinaryProblem problem = new OneMax(512) ;
    Termination termination = new TerminationByEvaluations(50000);
    Evaluation<BinarySolution> evaluation = new SequentialEvaluation<>(problem) ;
    SolutionsCreation<BinarySolution> solutionsCreation = new RandomSolutionsCreation<>(problem, 1) ;

    var algorithm =
            new ComponentBasedRandomSearchAlgorithm<>(
          "Random Search", solutionsCreation, evaluation,
                    termination);

    algorithm.run();

    List<BinarySolution> population = algorithm.getResult();
    JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    JMetalLogger.logger.info("Best found solution: " + population.get(0).getObjective(0)) ;
  }
}
