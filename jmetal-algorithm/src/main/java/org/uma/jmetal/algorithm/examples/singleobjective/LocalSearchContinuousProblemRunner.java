package org.uma.jmetal.algorithm.examples.singleobjective;

import java.util.Comparator;
import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;

/**
 * Class to configure and run a single objective local search. The target problem is Sphere.
 *
 * @author Antonio J. Nebro
 */
public class LocalSearchContinuousProblemRunner {
  public static void main(String[] args)  {
    var problem = new Sphere(20) ;

    var mutationOperator =
        new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0) ;

    int improvementRounds = 500000 ;

    Comparator<DoubleSolution> comparator = new DefaultDominanceComparator<>() ;

    DefaultLocalSearch<DoubleSolution> localSearch = new DefaultLocalSearch<>(
            improvementRounds,
            problem,
            mutationOperator,
            comparator) ;

    long startTime = System.currentTimeMillis() ;
    localSearch.run();
    long endTime = System.currentTimeMillis() ;

    DoubleSolution foundSolution = localSearch.getResult() ;

    String fitnessMessage = "Fitness: " + foundSolution.objectives()[0] ;
    JMetalLogger.logger.info(fitnessMessage) ;
    JMetalLogger.logger.info("Solution: " + foundSolution.variables().get(0)) ;
    JMetalLogger.logger.info("Computing time: " + (endTime - startTime)) ;
  }
}
