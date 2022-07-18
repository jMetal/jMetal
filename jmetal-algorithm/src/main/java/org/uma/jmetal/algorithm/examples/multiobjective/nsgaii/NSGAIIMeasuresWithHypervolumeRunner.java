package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;

/**
 * Class to configure and run the NSGA-II algorithm (variant with measures)
 */
public class NSGAIIMeasuresWithHypervolumeRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.nsgaii.NSGAIIMeasuresRunner problemName [referenceFront]
   */
  public static void main(String[] args)
      throws JMetalException, InterruptedException, FileNotFoundException {
    var referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv" ;
    }

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9 ;
    var crossoverDistributionIndex = 20.0 ;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    var mutationDistributionIndex = 20.0 ;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    var maxEvaluations = 25000 ;
    var populationSize = 100 ;

      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, populationSize)
              .setSelectionOperator(selection)
              .setMaxEvaluations(maxEvaluations)
              .setVariant(NSGAIIBuilder.NSGAIIVariant.Measures)
              .build();

    ((NSGAIIMeasures<DoubleSolution>)algorithm).setReferenceFront(new ArrayFront(referenceParetoFront));

    /* Measure management */
    var measureManager = ((NSGAIIMeasures<DoubleSolution>)algorithm).getMeasureManager() ;

    var currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");

    var hypervolumeMeasure =
            (BasicMeasure<Double>) measureManager.<Double>getPushMeasure("hypervolume");

    hypervolumeMeasure.register(new Listener());
    /* End of measure management */

    var algorithmThread = new Thread(algorithm) ;
    algorithmThread.start();

    algorithmThread.join();

    var population = algorithm.getResult() ;
    long computingTime = currentComputingTime.get() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }

  private static class Listener implements MeasureListener<Double> {
    private static int counter = 0 ;
    @Override synchronized public void measureGenerated(Double value) {
      if ((counter++ % 10 == 0)) {
        System.out.println("Hypervolume: " + value) ;
      }
    }
  }
}
