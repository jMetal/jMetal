package org.uma.jmetal.component.example.singleobjective.cellulargeneticalgorithm;

import org.uma.jmetal.component.algorithm.singleobjective.geneticalgorithm.SynchronousCellularGeneticAlgorithm;
import org.uma.jmetal.component.catalogue.termination.Termination;
import org.uma.jmetal.component.catalogue.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.neighborhood.impl.C9;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

/**
 * Class to configure and run the a generational genetic algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SynchronousCellularGeneticAlgorithmBinaryExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws JMetalException {
    BinaryProblem problem;
    SynchronousCellularGeneticAlgorithm<BinarySolution> algorithm;
    CrossoverOperator<BinarySolution> crossover;
    MutationOperator<BinarySolution> mutation;
    
    problem = new OneMax(512) ;

    int rows = 10 ;
    int columns = 10 ;
    int populationSize = rows * columns ;

    double crossoverProbability = 0.9;
    crossover = new SinglePointCrossover(crossoverProbability) ;

    double mutationProbability = 1.0 / problem.getTotalNumberOfBits() ;
    mutation = new BitFlipMutation(mutationProbability);

    Termination termination = new TerminationByEvaluations(30000);

    algorithm =
            new SynchronousCellularGeneticAlgorithm<>(
                    problem,
                    populationSize,
                    new C9<>(rows, columns),
                    crossover,
                    mutation,
                    termination);

    algorithm.run();

    List<BinarySolution> population = algorithm.getResult();
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
