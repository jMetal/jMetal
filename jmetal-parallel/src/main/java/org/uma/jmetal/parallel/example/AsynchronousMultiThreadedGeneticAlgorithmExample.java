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

    var populationSize = 100;
    var maxEvaluations = 25000;
    var numberOfCores = 16 ;

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
            var a = sin(i)*Math.cos(j) ;
          }
      }
    } ;

    var crossoverProbability = 0.9;
    CrossoverOperator<BinarySolution> crossover = new UniformCrossover(crossoverProbability);

    var mutationProbability = 1.0 / 1024;
    MutationOperator<BinarySolution> mutation = new BitFlipMutation(mutationProbability);

    SelectionOperator<List<BinarySolution>, BinarySolution> selection = new BinaryTournamentSelection<>(new ObjectiveComparator<>(0));

    Replacement<BinarySolution> replacement = new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0));

    var initTime = System.currentTimeMillis();
    @NotNull AsynchronousMultiThreadedGeneticAlgorithm<BinarySolution> geneticAlgorithm =
        new AsynchronousMultiThreadedGeneticAlgorithm<>(
            numberOfCores, problem, populationSize, crossover, mutation, selection, replacement, new TerminationByEvaluations(maxEvaluations));

    @NotNull FitnessObserver printObjectivesObserver = new FitnessObserver(100) ;
    geneticAlgorithm.getObservable().register(printObjectivesObserver);

    geneticAlgorithm.run();

    var endTime = System.currentTimeMillis();

    var resultList = geneticAlgorithm.getResult();

    JMetalLogger.logger.info("Computing time: " + (endTime - initTime));
    new SolutionListOutput(resultList)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();
    System.exit(0);
  }
}
