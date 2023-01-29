package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;

/**
 * Class to configure and run the NSGA-II algorithm (variant with measures) getting the value of
 * quality indicators of each iteration.
 */
public class NSGAIIMeasuresWithQualityIndicatorRunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java
   *                           org.uma.jmetal.runner.multiobjective.nsgaii.NSGAIIMeasuresRunner
   *                           problemName [referenceFront]
   */
  public static void main(String[] args)
      throws JMetalException, InterruptedException, IOException {
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

    int populationSize = 100;
    int maxEvaluations = 25000;
    var algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, populationSize)
        .setSelectionOperator(selection)
        .setMaxEvaluations(maxEvaluations)
        .setVariant(NSGAIIBuilder.NSGAIIVariant.Measures)
        .build();

    ((NSGAIIMeasures<DoubleSolution>) algorithm).setReferenceFront(
        new ArrayFront(referenceParetoFront));

    /* Measure management */
    MeasureManager measureManager = ((NSGAIIMeasures<DoubleSolution>) algorithm).getMeasureManager();

    DurationMeasure currentComputingTime =
        (DurationMeasure) measureManager.<Long>getPullMeasure("currentExecutionTime");

    BasicMeasure<List<DoubleSolution>> solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
        .<List<DoubleSolution>>getPushMeasure("currentPopulation");

    solutionListMeasure.register(new Listener(referenceParetoFront));
    /* End of measure management */

    Thread algorithmThread = new Thread(algorithm);
    algorithmThread.start();

    algorithmThread.join();

    List<DoubleSolution> population = algorithm.result();
    long computingTime = currentComputingTime.get();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    QualityIndicatorUtils.printQualityIndicators(
        SolutionListUtils.getMatrixWithObjectiveValues(population),
        VectorUtils.readVectors(referenceParetoFront, ","));
  }

  private static class Listener implements MeasureListener<List<DoubleSolution>> {

    private static int counter = 1;

    double[][] referenceParetoFront;

    public Listener(String referenceParetoFrontFile) throws IOException {
      referenceParetoFront = VectorUtils.readVectors(referenceParetoFrontFile, ",");
    }

    @Override
    synchronized public void measureGenerated(List<DoubleSolution> solutionList) {
      double[][] front = getMatrixWithObjectiveValues(solutionList);

      double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceParetoFront);
      double[][] normalizedFront =
          NormalizeUtils.normalize(
              front,
              NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceParetoFront),
              NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceParetoFront));

      var epsilon = new Epsilon(normalizedReferenceFront);
      var hv = new PISAHypervolume(normalizedReferenceFront);

      System.out.println("Iteration: " + counter +
          ". Epsilon: " + epsilon.compute(normalizedFront) + ". Hypervolume: " + hv.compute(
          normalizedFront));
      counter++;
    }

  }
}
