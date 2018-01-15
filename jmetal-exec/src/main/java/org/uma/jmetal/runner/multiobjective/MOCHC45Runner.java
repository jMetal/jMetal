package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mochc.MOCHC45;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * This class executes the algorithm described in:
 * A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo
 * "Optimal antenna placement using a new multi-objective chc algorithm".
 * GECCO '07: Proceedings of the 9th annual conference on Genetic and
 * evolutionary computation. London, England. July 2007.
 */
public class MOCHC45Runner extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws Exception {
    CrossoverOperator<BinarySolution> crossoverOperator;
    MutationOperator<BinarySolution> mutationOperator;
    SelectionOperator<List<BinarySolution>, BinarySolution> parentsSelection;
    SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection;
    Algorithm<List<BinarySolution>> algorithm ;

    BinaryProblem problem ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT5";
    }

    problem = (BinaryProblem) ProblemUtils.<BinarySolution> loadProblem(problemName);

    crossoverOperator = new HUXCrossover(1.0) ;
    parentsSelection = new RandomSelection<BinarySolution>() ;
    newGenerationSelection = new RankingAndCrowdingSelection<BinarySolution>(100) ;
    mutationOperator = new BitFlipMutation(0.35) ;

    algorithm = new MOCHC45(problem, 100, 25000, 3, 0.05, 0.25, crossoverOperator, mutationOperator,
        newGenerationSelection, parentsSelection, new SequentialSolutionListEvaluator<BinarySolution>()) ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    List<BinarySolution> population = (algorithm).getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    //if (!referenceParetoFront.equals("")) {
    //  printQualityIndicators(population, referenceParetoFront) ;
    //}
  }
}
