package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.IntegerSBXCrossover;
import org.uma.jmetal.operator.mutation.impl.IntegerPolynomialMutation;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.problem.multiobjective.OneZeroMax;
import org.uma.jmetal.problem.multiobjective.SimpleIntegerProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
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
 * Class to configure and run the NSGA-II algorithm configured with standard settings for solving
 * a binary problem ({@link OneZeroMax} is a multi-objective variant of OneMax).
 *
 * @author Antonio J. Nebro 
 */
public class NSGAIIIntegerProblemExample {
  public static void main(String[] args) throws JMetalException, IOException {

    IntegerProblem problem = new SimpleIntegerProblem() ;

    var crossover = new IntegerSBXCrossover(1.0, 20.0);
    var mutation = new IntegerPolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

    int populationSize = 100;
    int offspringPopulationSize = 100;

    Termination termination = new TerminationByEvaluations(2500);

    Ranking<IntegerSolution> ranking = new FastNonDominatedSortRanking<>(
            new DominanceWithConstraintsComparator<>(
                    new OverallConstraintViolationDegreeComparator<>()));

    EvolutionaryAlgorithm<IntegerSolution> nsgaii = new NSGAIIBuilder<>(
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    crossover,
                    mutation)
        .setTermination(termination).setRanking(ranking)

            .build();

    var chartObserver =
            new FrontPlotObserver<DoubleSolution>("NSGA-II", "F1", "F2", problem.name(), 1);

    nsgaii.observable().register(chartObserver);

    nsgaii.run();

    List<IntegerSolution> population = nsgaii.result();
    for (var solution: population) {
      solution.objectives()[0] *= -1.0 ;
    }

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
