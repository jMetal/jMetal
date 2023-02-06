package org.uma.jmetal.algorithm.examples.singleobjective;

import java.util.Comparator;
import org.uma.jmetal.algorithm.singleobjective.localsearch.BasicLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

/**
 * Class to configure and run a single objective local search. The target problem is OneMax.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class LocalSearchBinaryProblemRunner {
  public static void main(String[] args)  {
    BinaryProblem problem = new OneMax(512) ;

    MutationOperator<BinarySolution> mutationOperator =
        new BitFlipMutation(1.0 / problem.bitsFromVariable(0)) ;

    int improvementRounds = 5000 ;

    Comparator<BinarySolution> comparator = new ObjectiveComparator<>(0) ;

    BinarySolution initialSolution = problem.createSolution() ;
    problem.evaluate(initialSolution );

    BasicLocalSearch<BinarySolution> localSearch = new BasicLocalSearch<>(initialSolution,
        improvementRounds,
            problem,
            mutationOperator,
            comparator) ;

    localSearch.run();

    BinarySolution foundSolution = localSearch.result() ;

    String fitnessMessage = "Fitness: " + foundSolution.objectives()[0] ;
    JMetalLogger.logger.info(fitnessMessage) ;
    JMetalLogger.logger.info("Solution: " + foundSolution.variables().get(0)) ;
  }
}
