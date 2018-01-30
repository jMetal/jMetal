package org.uma.jmetal.runner.multiobjective;

import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.*;
import org.uma.jmetal.util.chartcontainer.ChartContainer;
import org.uma.jmetal.util.comparator.GDominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm (variant with measures) with the G-Dominance Comparator.
 */
public class GNSGAIIMeasuresWithChartsRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException Invoking command: java org.uma.jmetal.runner.multiobjective.NSGAIIMeasuresRunner
   *                           problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, InterruptedException, IOException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    String referenceParetoFront = "";

    String problemName;
    if (args.length == 2) {
      problemName = args[0];
      referenceParetoFront = args[1];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT1.pf";
    }

    problem = ProblemUtils.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    selection = new BinaryTournamentSelection<DoubleSolution>(
            new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    int maxEvaluations = 25000;
    int populationSize = 100;

    List<Double> referencePoint = Arrays.asList(0.5, 0.5) ;

    algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation)
            .setSelectionOperator(selection)
            .setMaxEvaluations(maxEvaluations)
            .setPopulationSize(populationSize)
            .setVariant(NSGAIIBuilder.NSGAIIVariant.Measures)
            .setDominanceComparator(new GDominanceComparator<>(referencePoint))
            .build();

    ((NSGAIIMeasures<DoubleSolution>) algorithm).setReferenceFront(new ArrayFront(referenceParetoFront));

    MeasureManager measureManager = ((NSGAIIMeasures<DoubleSolution>) algorithm).getMeasureManager();

        /* Measure management */
    BasicMeasure<List<DoubleSolution>> solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
            .<List<DoubleSolution>>getPushMeasure("currentPopulation");
    CountingMeasure iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentEvaluation");

    ChartContainer chart = new ChartContainer(algorithm.getName(), 100);
    chart.setFrontChart(0, 1, referenceParetoFront);
    chart.setReferencePoint(referencePoint);
    chart.initChart();

    solutionListMeasure.register(new ChartListener(chart));
   // iterationMeasure.register(new IterationListener(chart));
    /* End of measure management */

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
    chart.saveChart("./chart", BitmapFormat.PNG);

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }

    System.exit(0) ;
  }

  private static class ChartListener implements MeasureListener<List<DoubleSolution>> {
    private ChartContainer chart;
    private int iteration = 0;

    public ChartListener(ChartContainer chart) {
      this.chart = chart;
      this.chart.getFrontChart().setTitle("Iteration: " + this.iteration);
    }

    private void refreshChart(List<DoubleSolution> solutionList) {
      if (this.chart != null) {
        iteration++;
        this.chart.getFrontChart().setTitle("Iteration: " + this.iteration);
        this.chart.updateFrontCharts(solutionList);
        this.chart.refreshCharts();
      }
    }

    @Override
    synchronized public void measureGenerated(List<DoubleSolution> solutions) {
      refreshChart(solutions);
    }
  }

}
