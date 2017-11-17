package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADBuilder;
import org.uma.jmetal.problem.multiobjective.wfg.WFG7;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;

/**
 * Class for configuring and running the MOEA/DD algorithm
 *
 * @author
 */
public class MOEADDRunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   * org.uma.jmetal.runner.multiobjective.MOEADRunner problemName
   * [referenceFront]
   */
  public static void main(String[] args) throws FileNotFoundException {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    String problemName;
    String referenceParetoFront = "";

//    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    int m = 10;
    int k = 2 * (m - 1);
    problem = new WFG7(k, 20, m);
        /**/
    MutationOperator<DoubleSolution> mutation;
    CrossoverOperator<DoubleSolution> crossover;

    double crossoverProbability = 1.0;
    double crossoverDistributionIndex = 30.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    MOEADBuilder builder =  new MOEADBuilder(problem, MOEADBuilder.Variant.MOEADD);
    builder.setCrossover(crossover)
            .setMutation(mutation)
            .setMaxEvaluations(3000 * 275)
            .setPopulationSize(275)
            .setResultPopulationSize(275)
            .setNeighborhoodSelectionProbability(0.9)
            .setMaximumNumberOfReplacedSolutions(1)
            .setNeighborSize(20)
            .setFunctionType(AbstractMOEAD.FunctionType.PBI)
            .setDataDirectory("MOEAD_Weights");
    algorithm = builder.build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    
  }
}
