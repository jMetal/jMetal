package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;

/**
 * Class to configure and run the NSGA-II algorithm (variant with measures)
 */
public class NSGAIIMeasuresRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws InterruptedException {
    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    String referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var selection = new BinaryTournamentSelection<>(
        new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    int populationSize = 100 ;
    int maxEvaluations = 25000 ;
    var algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, populationSize)
        .setSelectionOperator(selection)
        .setMaxEvaluations(maxEvaluations)
        .setVariant(NSGAIIBuilder.NSGAIIVariant.Measures)
        .build() ;

    /* Measure management */
    MeasureManager measureManager = ((NSGAIIMeasures<DoubleSolution>)algorithm).getMeasureManager() ;

    CountingMeasure currentEvalution =
        (CountingMeasure) measureManager.<Long>getPullMeasure("currentEvaluation");
    DurationMeasure currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");
    BasicMeasure<Integer> nonDominatedSolutions =
        (BasicMeasure<Integer>) measureManager.<Integer>getPullMeasure("numberOfNonDominatedSolutionsInPopulation");

    BasicMeasure<List<DoubleSolution>> solutionListMeasure =
        (BasicMeasure<List<DoubleSolution>>) measureManager.<List<DoubleSolution>> getPushMeasure("currentPopulation");
    CountingMeasure iteration2 =
        (CountingMeasure) measureManager.<Long>getPushMeasure("currentEvaluation");

    solutionListMeasure.register(new Listener());
    iteration2.register(new Listener2());
    /* End of measure management */

    Thread algorithmThread = new Thread(algorithm) ;
    algorithmThread.start();

    /* Using the measures */
    int i = 0 ;
    while(currentEvalution.get() < maxEvaluations) {
      TimeUnit.SECONDS.sleep(5);
      System.out.println("Evaluations (" + i + ")                     : " + currentEvalution.get()) ;
      System.out.println("Computing time (" + i + ")                  : " + currentComputingTime.get()) ;
      System.out.println("Number of Nondominated solutions (" + i + "): " + nonDominatedSolutions.get()) ;
      i++ ;
    }

    algorithmThread.join();

    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = currentComputingTime.get() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }

    System.exit(0) ;
  }

  private static class Listener implements MeasureListener<List<DoubleSolution>> {
    private int counter = 0 ;

    @Override synchronized public void measureGenerated(List<DoubleSolution> solutions) {
      if ((counter % 10 == 0)) {
        System.out.println("PUSH MEASURE. Counter = " + counter+ " First solution: " + solutions.get(0)) ;
      }
      counter ++ ;
    }
  }

  private static class Listener2 implements MeasureListener<Long> {
    @Override synchronized public void measureGenerated(Long value) {
      if ((value % 50 == 0)) {
        System.out.println("PUSH MEASURE. Iteration: " + value) ;
      }
    }
  }
}
