package org.uma.jmetal.runner.multiobjective;

import org.knowm.xchart.BitmapEncoder;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOMeasures;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOMeasuresNovelty;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSONovelty;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.lz09.LZ09F2Novelty;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.*;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.chartcontainer.ChartContainer;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class for configuring and running the SMPSO algorithm using an HypervolumeArchive, i.e, the
 * SMPSOhv algorithm described in:
 * A.J Nebro, J.J. Durillo, C.A. Coello Coello. Analysis of Leader Selection Strategies in a
 * Multi-Objective Particle Swarm Optimizer. 2013 IEEE Congress on Evolutionary Computation. June 2013
 * DOI: 10.1109/CEC.2013.6557955
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMPSOHvNoveltyMeasuresRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws SecurityException
   * Invoking command:
  java org.uma.jmetal.runner.multiobjective.NSGAIIMeasuresRunner problemName [referenceFront]
   */
  public static void main(String[] args)
          throws JMetalException, InterruptedException, IOException {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    MutationOperator<DoubleSolution> mutation;

    String referenceParetoFront = "" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT3";
      referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/LZ09_F2.pf" ;
    }

    problem = new LZ09F2Novelty() ;

    BoundedArchive<DoubleSolution> archive = new CrowdingDistanceArchive<DoubleSolution>(100) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    int maxIterations = 250 ;
    int swarmSize = 100 ;

    algorithm = new SMPSOMeasuresNovelty(problem, 100, archive,
            mutation, 1750, 0, 1, 0, 1, 1.5, 2.5, 1.5, 2.5,
            0.1, 0.1, -1, -1, new SequentialSolutionListEvaluator<>()) ;

    /* Measure management */
    MeasureManager measureManager = ((SMPSOMeasuresNovelty)algorithm).getMeasureManager() ;

    BasicMeasure<List<DoubleSolution>> solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
            .<List<DoubleSolution>>getPushMeasure("currentPopulation");
    CountingMeasure iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentIteration");

    ChartContainer chart = new ChartContainer(algorithm.getName(), 80);
    chart.setFrontChart(0, 1, referenceParetoFront);
    //chart.setVarChart(0, 1);
    chart.initChart();

    solutionListMeasure.register(new SMPSOHvNoveltyMeasuresRunner.ChartListener(chart));
    iterationMeasure.register(new SMPSOHvNoveltyMeasuresRunner.IterationListener(chart));

    /* End of measure management */

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
    chart.saveChart("./chart", BitmapEncoder.BitmapFormat.PNG);

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }

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