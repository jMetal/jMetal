package org.uma.jmetal.parallel.example;

import static java.lang.Math.sin;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.UniformCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.parallel.asynchronous.algorithm.impl.AsynchronousMultiThreadedGeneticAlgorithm;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.FitnessObserver;

public class AsynchronousMultiThreadedGeneticAlgorithmExample {
  public static void main(String[] args) {
    CrossoverOperator<BinarySolution> crossover;
    MutationOperator<BinarySolution> mutation;
    SelectionOperator<List<BinarySolution>, BinarySolution> selection ;
    Replacement<BinarySolution> replacement ;

    int populationSize = 100;
    int maxEvaluations = 25000;
    int numberOfCores = 16 ;

    @NotNull OneMax problem = new OneMax(1024) {
      @Override
      public BinarySolution evaluate (BinarySolution solution) {
        super.evaluate(solution) ;
        computingDelay();

        return solution ;
      }

      private void computingDelay() {
        for (long i = 0 ; i < 10000; i++)
          for (long j = 0; j < 100; j++) {
            double a = sin(i)*Math.cos(j) ;
          }
      }
    } ;

    double crossoverProbability = 0.9;
    crossover = new UniformCrossover(crossoverProbability);

    double mutationProbability = 1.0 / 1024;
    mutation = new BitFlipMutation(mutationProbability);

    selection = new BinaryTournamentSelection<>(new ObjectiveComparator<>(0)) ;

    replacement = new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0)) ;

    long initTime = System.currentTimeMillis();
    @NotNull AsynchronousMultiThreadedGeneticAlgorithm<BinarySolution> geneticAlgorithm =
        new AsynchronousMultiThreadedGeneticAlgorithm<>(
            numberOfCores, problem, populationSize, crossover, mutation, selection, replacement, new TerminationByEvaluations(maxEvaluations));

    @NotNull FitnessObserver printObjectivesObserver = new FitnessObserver(100) ;
    geneticAlgorithm.getObservable().register(printObjectivesObserver);

    geneticAlgorithm.run();

    long endTime = System.currentTimeMillis();

    List<BinarySolution> resultList = geneticAlgorithm.getResult();

    JMetalLogger.logger.info("Computing time: " + (endTime - initTime));
    new SolutionListOutput(resultList)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();
    System.exit(0);
  }
}
