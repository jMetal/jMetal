package org.uma.jmetal.algorithm.examples.multiobjective.wasfga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGAMeasures;
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
import org.uma.jmetal.util.chartcontainer.ChartContainer;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;

public class WASFGAMeasuresRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
 * @throws IOException 
   */
  public static void main(String[] args) throws JMetalException, IOException {
      var referenceParetoFront = "" ;

      String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv" ;
    }

      var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

      List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(0.6);
    referencePoint.add(0.4);

      var crossoverProbability = 0.9 ;
      var crossoverDistributionIndex = 20.0 ;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

      var mutationProbability = 1.0 / problem.getNumberOfVariables() ;
      var mutationDistributionIndex = 20.0 ;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

      var epsilon = 0.01 ;
      Algorithm<List<DoubleSolution>> algorithm = new WASFGAMeasures<DoubleSolution>(
              problem,
              100,
              250,
              crossover, mutation, selection, new SequentialSolutionListEvaluator<DoubleSolution>(),
              epsilon,
              referencePoint);
    
    /* Measure management */
      var measureManager = ((WASFGAMeasures<DoubleSolution>) algorithm).getMeasureManager();

      var solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
            .<List<DoubleSolution>>getPushMeasure("currentPopulation");
      var iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentEvaluation");

      var chart = new ChartContainer(algorithm.getName(), 200);
    chart.setFrontChart(0, 1, referenceParetoFront);
    chart.setReferencePoint(referencePoint);
    chart.setVarChart(0, 1);
    chart.initChart();

    solutionListMeasure.register(new ChartListener(chart));
    iterationMeasure.register(new IterationListener(chart));

    /* End of measure management */

      var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    chart.saveChart("WASFGA", BitmapFormat.PNG);
      var population = algorithm.getResult() ;
      var computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
  
  private static class ChartListener implements MeasureListener<List<DoubleSolution>> {
      private ChartContainer chart;
      private int iteration = 0;

      public ChartListener(ChartContainer chart) {
          this.chart = chart;
          this.chart.getFrontChart().setTitle("Iteration: " + this.iteration);
      }

      private void refreshChart(@NotNull List<DoubleSolution> solutionList) {
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

  private static class IterationListener implements MeasureListener<Long> {
      ChartContainer chart;

      public IterationListener(ChartContainer chart) {
          this.chart = chart;
          this.chart.getFrontChart().setTitle("Iteration: " + 0);
      }

      @Override
      synchronized public void measureGenerated(Long iteration) {
          if (this.chart != null) {
              this.chart.getFrontChart().setTitle("Iteration: " + iteration);
          }
      }
  }
}
