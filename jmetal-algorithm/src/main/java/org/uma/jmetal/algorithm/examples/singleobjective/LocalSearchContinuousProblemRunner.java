package org.uma.jmetal.algorithm.examples.singleobjective;

import static org.uma.jmetal.util.AbstractAlgorithmRunner.printFinalSolutionSet;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.algorithm.singleobjective.localsearch.BasicLocalSearch;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

/**
 * Class to configure and run a single objective local search. The target problem is Sphere.
 *
 * @author Antonio J. Nebro
 */
public class LocalSearchContinuousProblemRunner {
  public static void main(String[] args)  {
    var problem = new Sphere(20) ;

    var mutationOperator =
        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0) ;

    int improvementRounds = 500000 ;

    Comparator<DoubleSolution> comparator = new ObjectiveComparator<>(0) ;

    DoubleSolution initialSolution = problem.createSolution() ;
    problem.evaluate(initialSolution );

    BasicLocalSearch<DoubleSolution> localSearch = new BasicLocalSearch<>(initialSolution,
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
    JMetalLogger.logger.info("Computing time: " + (endTime - startTime)) ;
    printFinalSolutionSet(List.of(foundSolution));
  }
}
