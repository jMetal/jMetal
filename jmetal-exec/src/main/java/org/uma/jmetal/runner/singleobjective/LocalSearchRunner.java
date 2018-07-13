package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.localsearch.BasicLocalSearch;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.BinarySolution;
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
        new BitFlipMutation(1.0 / problem.getNumberOfBits(0)) ;

    int improvementRounds = 10000 ;

    Comparator<BinarySolution> comparator = new DominanceComparator<>(0) ;

    LocalSearchOperator<BinarySolution> localSearch = new BasicLocalSearch<>(
            improvementRounds,
            mutationOperator,
            comparator,
            problem) ;

    BinarySolution solution = problem.createSolution() ;

    BinarySolution newSolution = localSearch.execute(solution) ;

    JMetalLogger.logger.info("Fitness: " + newSolution.getObjective(0)) ;
    JMetalLogger.logger.info("Solution: " + newSolution.getVariableValueString(0)) ;
  }
}
