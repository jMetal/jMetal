package org.uma.jmetal.algorithm.examples.multiobjective.mochc;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.mochc.MOCHC45;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.HUXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.RandomSelection;
import org.uma.jmetal.operator.selection.impl.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * This class executes the algorithm described in:
 * A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo
 * "Optimal antenna placement using a new multi-objective chc algorithm".
 * GECCO '07: Proceedings of the 9th annual conference on Genetic and
 * evolutionary computation. London, England. July 2007.
 */
public class MOCHC45Runner extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws Exception {

      String problemName ;
    if (args.length == 1) {
      problemName = args[0] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT5";
    }

    var problem = (BinaryProblem) ProblemFactory.<BinarySolution>loadProblem(problemName);

      CrossoverOperator<BinarySolution> crossoverOperator = new HUXCrossover(1.0);
      SelectionOperator<List<BinarySolution>, BinarySolution> parentsSelection = new RandomSelection<BinarySolution>();
      SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection = new RankingAndCrowdingSelection<BinarySolution>(100);
      MutationOperator<BinarySolution> mutationOperator = new BitFlipMutation(0.35);

      Algorithm<List<BinarySolution>> algorithm = new MOCHC45(problem, 100, 25000, 3, 0.05, 0.25, crossoverOperator, mutationOperator,
              newGenerationSelection, parentsSelection, new SequentialSolutionListEvaluator<BinarySolution>());

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    var population = (algorithm).getResult() ;
    var computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    //if (!referenceParetoFront.equals("")) {
    //  printQualityIndicators(population, referenceParetoFront) ;
    //}
  }
}
