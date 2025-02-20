package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.multiobjective.multiobjectiveknapsack.MultiObjectiveKnapsack;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.FrontPlotObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Class to configure and run the NSGA-II algorithm to solve a bi-objective TSP.
 *
 * @author Antonio J. Nebro
 */
public class NSGAIIBiobjectiveKnapsackExample {
  public static void main(String[] args) throws JMetalException, IOException {

    var problem =
        MultiObjectiveKnapsack.readMKP("resources/mokpInstances/1B-B/2KP50-1B.dat");

    CrossoverOperator<BinarySolution> crossover = new SinglePointCrossover<>(0.9);

    double mutationProbability = 1.0 / problem.totalNumberOfBits();
    MutationOperator<BinarySolution> mutation = new BitFlipMutation<>(mutationProbability);

    int populationSize = 100;
    int offspringPopulationSize = 100;

    Termination termination = new TerminationByEvaluations(100000);

    Ranking<BinarySolution> ranking =
        new FastNonDominatedSortRanking<>(
            new DominanceWithConstraintsComparator<>(
                new OverallConstraintViolationDegreeComparator<>()));

    EvolutionaryAlgorithm<BinarySolution> nsgaii =
        new NSGAIIBuilder<>(problem, populationSize, offspringPopulationSize, crossover, mutation)
            .setTermination(termination)
            .setRanking(ranking)
            .build();

    var chartObserver =
        new FrontPlotObserver<DoubleSolution>("NSGA-II", "F1", "F2", problem.name(), 10000);

    nsgaii.observable().register(chartObserver);

    nsgaii.run();

    List<BinarySolution> population = nsgaii.result();
    JMetalLogger.logger.info("Total execution time : " + nsgaii.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + nsgaii.numberOfEvaluations());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
