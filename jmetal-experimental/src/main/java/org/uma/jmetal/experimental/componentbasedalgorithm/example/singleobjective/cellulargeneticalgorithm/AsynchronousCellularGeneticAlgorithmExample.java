package org.uma.jmetal.experimental.componentbasedalgorithm.example.singleobjective.cellulargeneticalgorithm;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.singleobjective.geneticalgorithm.AsynchronousCellularGeneticAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.Termination;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C9;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

/**
 * Class to configure and run the a generational genetic algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AsynchronousCellularGeneticAlgorithmExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException {
    Problem<DoubleSolution> problem;
    AsynchronousCellularGeneticAlgorithm<DoubleSolution> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    
    problem = new Sphere(20) ;

    int rows = 10 ;
    int columns = 10 ;
    Neighborhood<DoubleSolution> neighborhood = new C9<>(rows, columns) ;

    int populationSize = rows * columns ;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Termination termination = new TerminationByEvaluations(150000);

    algorithm =
            new AsynchronousCellularGeneticAlgorithm<>(
                    problem,
                    populationSize,
                    neighborhood,
                    crossover,
                    mutation,
                    termination);

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();
    JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    JMetalLogger.logger.info("Best found solution: " + population.get(0).getObjective(0)) ;
  }
}
