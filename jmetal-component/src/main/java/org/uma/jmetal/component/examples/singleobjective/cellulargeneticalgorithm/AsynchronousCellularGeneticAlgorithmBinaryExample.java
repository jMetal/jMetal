package org.uma.jmetal.component.examples.singleobjective.cellulargeneticalgorithm;

import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NeighborhoodSelection;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C25;
import org.uma.jmetal.util.observer.impl.FitnessObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerBoundedSequenceGenerator;


/**
 * Class to configure and run an asynchronous cellular genetic algorithm to solve a {@link DoubleProblem}
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AsynchronousCellularGeneticAlgorithmBinaryExample {
  public static void main(String[] args) throws JMetalException {
    BinaryProblem problem = new OneMax(1024) ;

    double crossoverProbability = 0.9;
    var crossover = new SinglePointCrossover(crossoverProbability);

    double mutationProbability = 1.0 / problem.totalNumberOfBits() ;
    var mutation = new BitFlipMutation(mutationProbability);

    int populationSize = 100;
    int offspringPopulationSize = 1;

    int rows = 10 ;
    int columns = 10 ;
    Neighborhood<BinarySolution> neighborhood = new C25<>(rows, columns) ;

    SequenceGenerator<Integer> solutionIndexGenerator = new IntegerBoundedSequenceGenerator(populationSize);

    var variation = new CrossoverAndMutationVariation<BinarySolution>(offspringPopulationSize, crossover, mutation) ;

    var selection =
        new NeighborhoodSelection<BinarySolution>(
            variation.getMatingPoolSize(),
            solutionIndexGenerator,
            neighborhood,
            new NaryTournamentSelection<>(2, new ObjectiveComparator<>(0)),
            true);

    Termination termination = new TerminationByEvaluations(40000);

    EvolutionaryAlgorithm<BinarySolution> geneticAlgorithm = new GeneticAlgorithmBuilder<>(
        "acGA",
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    crossover,
                    mutation)
        .setTermination(termination)
        .setVariation(variation)
        .setSelection(selection)
        .build();

    geneticAlgorithm.getObservable().register(new FitnessObserver(1000));

    geneticAlgorithm.run();

    List<BinarySolution> population = geneticAlgorithm.result();
    JMetalLogger.logger.info("Total execution time : " + geneticAlgorithm.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + geneticAlgorithm.numberOfEvaluations());
    JMetalLogger.logger.info("Best fitness: " + geneticAlgorithm.result().get(0).objectives()[0]);

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
