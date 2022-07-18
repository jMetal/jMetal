package org.uma.jmetal.component.examples.singleobjective.geneticalgorithm;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.FitnessObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run a generational genetic algorithm to solve a {@link BinaryProblem}
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerationalGeneticAlgorithmBinaryExample {
  public static void main(String[] args) throws JMetalException, IOException {
    BinaryProblem problem = new OneMax(512) ;

    var crossoverProbability = 0.9;
    var crossover = new SinglePointCrossover(crossoverProbability);

    var mutationProbability = 1.0 / problem.getTotalNumberOfBits() ;
    var mutation = new BitFlipMutation(mutationProbability);

    var populationSize = 100;
    var offspringPopulationSize = populationSize;

    @NotNull Termination termination = new TerminationByEvaluations(25000);

    var geneticAlgorithm = new GeneticAlgorithmBuilder<>(
        "GGA",
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    crossover,
                    mutation)
        .setTermination(termination)
        .build();

    geneticAlgorithm.getObservable().register(new FitnessObserver(1000));

    geneticAlgorithm.run();

    var population = geneticAlgorithm.getResult();
    JMetalLogger.logger.info("Total execution time : " + geneticAlgorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + geneticAlgorithm.getNumberOfEvaluations());
    JMetalLogger.logger.info("Best fitness: " + geneticAlgorithm.getResult().get(0).objectives()[0]);

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
