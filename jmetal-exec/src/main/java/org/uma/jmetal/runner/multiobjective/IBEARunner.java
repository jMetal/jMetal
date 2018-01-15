package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEABuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;

import java.util.List;

/**
 * Class for configuring and running the IBEA algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class IBEARunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws java.io.IOException
   * @throws SecurityException
   * @throws ClassNotFoundException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.IBEARunner problemName [referenceFront]
   */
  public static void main(String[] args) throws Exception {
    Problem<DoubleSolution> problem;
    IBEA<DoubleSolution> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    String problemName ;
    String referenceParetoFront = "" ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT1.pf" ;
    }

    problem = ProblemUtils.loadProblem(problemName);

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection<DoubleSolution>() ;

    algorithm = new IBEABuilder(problem)
      .setArchiveSize(100)
      .setPopulationSize(100)
      .setMaxEvaluations(25000)
      .setCrossover(crossover)
      .setMutation(mutation)
      .setSelection(selection)
      .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
      .execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
