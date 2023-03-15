package org.uma.jmetal.component.examples.singleobjective.geneticalgorithm;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.singleobjective.GeneticAlgorithmBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.problem.singleobjective.TSP;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.FitnessObserver;
import org.uma.jmetal.util.observer.impl.FitnessPlotObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure and run a genetic algorithm to solve an instance of the TSP
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GeneticAlgorithmTSPWithConstraintsExample extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws JMetalException, IOException {
    PermutationProblem<PermutationSolution<Integer>> problem;

    problem = new TSP("resources/tspInstances/kroA100.tsp") {

      @Override
      public int numberOfConstraints() {
        return 1;
      }

      // Example of constraint: city 17 must be the first one in the routes
      @Override
      public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
        super.evaluate(solution);

        int city = 17;
        int cityPosition = solution.variables().indexOf(city);

        int constraint = 0 - cityPosition;

        solution.constraints()[0] = constraint;

        return solution;
      }
    };

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    var crossover = new PMXCrossover(0.9);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    var mutation = new PermutationSwapMutation<Integer>(mutationProbability);

    Termination termination = new TerminationByEvaluations(150000);

    Replacement<PermutationSolution<Integer>> replacement = new MuPlusLambdaReplacement<>(
        new MultiComparator<>(List.of(new OverallConstraintViolationDegreeComparator<>(),
            new ObjectiveComparator<>(0))));

    EvolutionaryAlgorithm<PermutationSolution<Integer>> geneticAlgorithm = new GeneticAlgorithmBuilder<>(
        "GGA",
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .setReplacement(replacement)
        .build();

    geneticAlgorithm.observable().register(new FitnessObserver(10000));
    var chartObserver = new FitnessPlotObserver("Genetic algorithm", "Evaluations", "Fitness",
        "fitness", 10000);
    geneticAlgorithm.observable().register(chartObserver);

    geneticAlgorithm.run();

    List<PermutationSolution<Integer>> population = geneticAlgorithm.result();
    JMetalLogger.logger.info(
        "Total execution time : " + geneticAlgorithm.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + geneticAlgorithm.numberOfEvaluations());
    JMetalLogger.logger.info("Best found solution: " + population.get(0).objectives()[0]);

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
