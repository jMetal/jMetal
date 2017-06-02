package org.uma.jmetal.runner.multiobjective;

import java.io.FileNotFoundException;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIMeasures;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.chartcontainer.ChartContainer;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.front.imp.ArrayFront;

/**
 * Class to configure and run the NSGA-II algorithm (variant with measures)
 */
public class NSGAIIMeasuresWithChartsRunner extends AbstractAlgorithmRunner {
    /**
     * @param args
     *            Command line arguments.
     * @throws SecurityException
     *             Invoking command: java
     *             org.uma.jmetal.runner.multiobjective.NSGAIIMeasuresRunner
     *             problemName [referenceFront]
     */
    public static void main(String[] args) throws JMetalException, InterruptedException, FileNotFoundException {
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

        algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation).setSelectionOperator(selection)
                .setMaxEvaluations(maxEvaluations).setPopulationSize(populationSize)
                .setVariant(NSGAIIBuilder.NSGAIIVariant.Measures).build();

        ((NSGAIIMeasures<DoubleSolution>) algorithm).setReferenceFront(new ArrayFront(referenceParetoFront));

        /* Measure management */
        MeasureManager measureManager = ((NSGAIIMeasures<DoubleSolution>) algorithm).getMeasureManager();

        DurationMeasure currentComputingTime = (DurationMeasure) measureManager
                .<Long>getPullMeasure("currentExecutionTime");

        CountingMeasure iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentEvaluation");

        BasicMeasure<Double> hypervolumeMeasure = (BasicMeasure<Double>) measureManager
                .<Double>getPushMeasure("hypervolume");

        ChartContainer chart = new ChartContainer(algorithm.getName(), 200);
        chart.addIndicatorChart("Hypervolume");
        chart.initChart();

        iterationMeasure.register(new IterationListener(chart));
        hypervolumeMeasure.register(new IndicatorListener("Hypervolume", chart));

        /* End of measure management */

        Thread algorithmThread = new Thread(algorithm);
        algorithmThread.start();

        algorithmThread.join();

        List<DoubleSolution> population = algorithm.getResult();
        long computingTime = currentComputingTime.get();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront);
        }
    }

    private static class IterationListener implements MeasureListener<Long> {
        ChartContainer chart;

        public IterationListener(ChartContainer chart) {
            this.chart = chart;
            this.chart.getChart("Hypervolume").setTitle("Iteration: " + 0);
        }

        @Override
        synchronized public void measureGenerated(Long iteration) {
            if (this.chart != null) {
                this.chart.getChart("Hypervolume").setTitle("Iteration: " + iteration);
            }
        }
    }

    private static class IndicatorListener implements MeasureListener<Double> {
        ChartContainer chart;
        String indicator;

        public IndicatorListener(String indicator, ChartContainer chart) {
            this.chart = chart;
            this.indicator = indicator;
        }

        @Override
        synchronized public void measureGenerated(Double value) {
            if (this.chart != null) {
                this.chart.updateIndicatorChart(this.indicator, value);
                this.chart.refreshCharts();
            }
        }
    }
}
