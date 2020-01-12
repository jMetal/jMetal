package org.uma.jmetal.example.singleobjective;

import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;

/**
 * Class to configure and run a single objective local search. The target problem is OneMax.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class LocalSearchRunner {
  /**
   * Usage: java org.uma.jmetal.runner.singleobjective.LocalSearchRunner
   */
  public static void main(String[] args) throws Exception {
    BinaryProblem problem = new OneMax(1024) ;

    MutationOperator<BinarySolution> mutationOperator =
        new BitFlipMutation(1.0 / problem.getBitsFromVariable(0)) ;

    int improvementRounds = 10000 ;

    Comparator<BinarySolution> comparator = new DominanceComparator<>() ;

    DefaultLocalSearch<BinarySolution> localSearch = new DefaultLocalSearch<BinarySolution>(
            improvementRounds,
            problem,
            mutationOperator,
            comparator) ;

    localSearch.run();

    BinarySolution newSolution = localSearch.getResult() ;

    JMetalLogger.logger.info("Fitness: " + newSolution.getObjective(0)) ;
    JMetalLogger.logger.info("Solution: " + newSolution.getVariable(0)) ;
  }
}
