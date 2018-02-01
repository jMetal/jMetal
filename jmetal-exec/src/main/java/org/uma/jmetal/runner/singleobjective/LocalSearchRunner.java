



//




// 



package org.uma.jmetal.runner.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.EvolutionStrategyBuilder;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.impl.localsearch.BasicLocalSearch;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Class to configure and run an elitist (mu + lambda) evolution strategy. The target problem is
 * OneMax.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class LocalSearchRunner {
  /**
   * Usage: java org.uma.jmetal.runner.singleobjective.LocalSearchRunner
   */
  public static void main(String[] args) throws Exception {
    BinaryProblem problem = new OneMax(128) ;

    MutationOperator<BinarySolution> mutationOperator =
        new BitFlipMutation(1.0 / problem.getNumberOfBits(0)) ;

    int improvementRounds = 1000 ;

    Comparator<BinarySolution> comparator = new DominanceComparator<>(0) ;

    LocalSearchOperator<BinarySolution> localSearch = new BasicLocalSearch(
            improvementRounds,
            mutationOperator,
            new ObjectiveComparator(0),
            problem) ;

    BinarySolution solution = problem.createSolution() ;

    BinarySolution newSolution = localSearch.execute(solution) ;

    System.out.println("Fitness: " + newSolution.getObjective(0)) ;
    System.out.println("Solution: " + newSolution.getVariableValueString(0)) ;
  }
}
