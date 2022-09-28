package org.uma.jmetal.algorithm.examples.singleobjective;

import java.util.Comparator;
import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/**
 * Class to configure and run a single objective local search. The target problem is OneMax.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class LocalSearchBinaryProblemRunner {
  public static void main(String[] args)  {
    BinaryProblem problem = new OneMax(1024) ;

    MutationOperator<BinarySolution> mutationOperator =
        new BitFlipMutation(1.0 / problem.getBitsFromVariable(0)) ;

    int improvementRounds = 10000 ;

    Comparator<BinarySolution> comparator = new DominanceWithConstraintsComparator<>() ;

    DefaultLocalSearch<BinarySolution> localSearch = new DefaultLocalSearch<>(
            improvementRounds,
            problem,
            mutationOperator,
            comparator) ;

    localSearch.run();

    BinarySolution foundSolution = localSearch.getResult() ;

    String fitnessMessage = "Fitness: " + foundSolution.objectives()[0] ;
    JMetalLogger.logger.info(fitnessMessage) ;
    JMetalLogger.logger.info("Solution: " + foundSolution.variables().get(0)) ;
  }
}
