package org.uma.jmetal.component.examples.singleobjective.cellulargeneticalgorithm;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NeighborhoodSelection;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.C9;
import org.uma.jmetal.util.observer.impl.FitnessObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;


/**
 * Class to configure and run an asynchronous cellular genetic algorithm to solve a {@link DoubleProblem}
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AsynchronousCellularGeneticAlgorithmUsingAPermutationSequenceGeneratorExample {
  public static void main(String[] args) throws JMetalException {
    Problem<DoubleSolution> problem = new Sphere(20) ;

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
    @NotNull var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var populationSize = 100;
    var offspringPopulationSize = 1;

    var rows = 10 ;
    var columns = 10 ;
    @NotNull Neighborhood<DoubleSolution> neighborhood = new C9<>(rows, columns) ;

    SequenceGenerator<Integer> solutionIndexGenerator = new IntegerPermutationGenerator(populationSize);

    @NotNull var variation = new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation) ;

    var selection =
        new NeighborhoodSelection<>(
            variation.getMatingPoolSize(),
            solutionIndexGenerator,
            neighborhood,
            new NaryTournamentSelection<>(2, new ObjectiveComparator<>(0)),
            true);

    Termination termination = new TerminationByEvaluations(500000);

    var geneticAlgorithm = new GeneticAlgorithmBuilder<>(
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

    geneticAlgorithm.getObservable().register(new FitnessObserver(5000));

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
