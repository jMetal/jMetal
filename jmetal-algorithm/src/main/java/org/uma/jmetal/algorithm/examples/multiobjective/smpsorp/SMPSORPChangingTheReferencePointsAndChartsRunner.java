package org.uma.jmetal.algorithm.examples.multiobjective.smpsorp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.BitmapEncoder;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSORP;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.ebes.Ebes;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.chartcontainer.ChartContainerWithReferencePoints;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;

public class SMPSORPChangingTheReferencePointsAndChartsRunner {

  /**
   * Program to run the SMPSORP algorithm allowing to change a reference point interactively.
   * SMPSORP is described in "Extending the Speed-constrained Multi-Objective PSO (SMPSO) With Reference Point Based Preference
   * Articulation. Antonio J. Nebro, Juan J. Durillo, José García-Nieto, Cristóbal Barba-González,
   * Javier Del Ser, Carlos A. Coello Coello, Antonio Benítez-Hidalgo, José F. Aldana-Montes.
   * Parallel Problem Solving from Nature -- PPSN XV. Lecture Notes In Computer Science, Vol. 11101,
   *  pp. 298-310. 2018." This runner is the one used in the use case included in the paper.
   *
   * In the current implementation, only one reference point can be modified interactively.
   *
   * @author Antonio J. Nebro
   */
  public static void main(String[] args) throws JMetalException, IOException, InterruptedException {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    MutationOperator<DoubleSolution> mutation;
    String referenceParetoFront ;

    problem = new Ebes("ebes/Mobile_Bridge_25N_35B_8G_16OrdZXY.ebe", new String[]{"W", "D"}) ;
    referenceParetoFront = null ;
    List<List<Double>> referencePoints;
    referencePoints = new ArrayList<>();

    referencePoints.add(Arrays.asList(0.0, 0.0));

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int maxIterations = 2500;
    int swarmSize = 100;

      List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints = new ArrayList<>();
      for (List<Double> referencePoint : referencePoints) {
          @NotNull CrowdingDistanceArchiveWithReferencePoint<DoubleSolution> doubleSolutionCrowdingDistanceArchiveWithReferencePoint = new CrowdingDistanceArchiveWithReferencePoint<>(
                  swarmSize / referencePoints.size(), referencePoint);
          archivesWithReferencePoints.add(doubleSolutionCrowdingDistanceArchiveWithReferencePoint);
      }

      algorithm = new SMPSORP(problem,
        swarmSize,
        archivesWithReferencePoints,
        referencePoints,
        mutation,
        maxIterations,
        0.0, 1.0,
        0.0, 1.0,
        2.5, 1.5,
        2.5, 1.5,
        0.1, 0.1,
        -1.0, -1.0,
        new DominanceWithConstraintsComparator<>(new OverallConstraintViolationDegreeComparator<>()),
        new SequentialSolutionListEvaluator<>());

    /* Measure management */
    MeasureManager measureManager = ((SMPSORP) algorithm).getMeasureManager();

    BasicMeasure<List<DoubleSolution>> solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
        .<List<DoubleSolution>>getPushMeasure("currentPopulation");
    CountingMeasure iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure(
        "currentIteration");

    @NotNull ChartContainerWithReferencePoints chart = new ChartContainerWithReferencePoints(algorithm.getName(), 300);
    chart.setFrontChart(0, 1, referenceParetoFront);
    chart.setReferencePoint(referencePoints);
    chart.initChart();

    solutionListMeasure.register(new ChartListener(chart));
    iterationMeasure.register(new IterationListener(chart));

    /* End of measure management */

    Thread algorithmThread = new Thread(algorithm);
    ChangeReferencePoint changeReferencePoint = new ChangeReferencePoint(algorithm, referencePoints, archivesWithReferencePoints, chart) ;

    Thread changePointsThread = new Thread(changeReferencePoint) ;

    algorithmThread.start();
    changePointsThread.start();

    algorithmThread.join();

    chart.saveChart("RSMPSO", BitmapEncoder.BitmapFormat.PNG);
    List<DoubleSolution> population = algorithm.getResult();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    for (int i = 0; i < archivesWithReferencePoints.size(); i++) {
      new SolutionListOutput(archivesWithReferencePoints.get(i).getSolutionList())
          .setVarFileOutputContext(new DefaultFileOutputContext("VAR" + i + ".tsv"))
          .setFunFileOutputContext(new DefaultFileOutputContext("FUN" + i + ".tsv"))
          .print();
    }

    System.out.println("FINISH") ;
    System.exit(0);
  }

  private static class ChartListener implements MeasureListener<List<DoubleSolution>> {
    private ChartContainerWithReferencePoints chart;
    private int iteration = 0;

    public ChartListener(ChartContainerWithReferencePoints chart) {
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
    synchronized public void measureGenerated(@NotNull List<DoubleSolution> solutions) {
      refreshChart(solutions);
    }
  }

  private static class IterationListener implements MeasureListener<Long> {
    ChartContainerWithReferencePoints chart;

    public IterationListener(ChartContainerWithReferencePoints chart) {
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

  private static class ChangeReferencePoint implements Runnable {
    ChartContainerWithReferencePoints chart ;
    List<List<Double>> referencePoints;
    SMPSORP algorithm ;

    public ChangeReferencePoint(
            Algorithm<List<DoubleSolution>> algorithm,
        List<List<Double>> referencePoints,
        List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints,
            ChartContainerWithReferencePoints chart)
        throws InterruptedException {
      this.referencePoints = referencePoints;
      this.chart = chart ;
      this.algorithm = (SMPSORP) algorithm ;
    }

    @Override
    public void run() {
      try (Scanner scanner = new Scanner(System.in)) {
        double v1 ;
        double v2 ;
        
        while (true) {
          System.out.println("Introduce the new reference point (between commas):");
          String s = scanner.nextLine() ;
          
          try (Scanner sl = new Scanner(s)) {
            sl.useDelimiter(",");
            
            for (int i = 0; i < referencePoints.size(); i++) {
              try {
                v1 = Double.parseDouble(sl.next());
                v2 = Double.parseDouble(sl.next());
              } catch (Exception e) {//any problem
                v1 = 0;
                v2 = 0;
              }
              
              referencePoints.get(i).set(0, v1);
              referencePoints.get(i).set(1, v2);
            }
          }
          
          chart.updateReferencePoint(referencePoints);
          
          algorithm.changeReferencePoints(referencePoints);
        }
      }
    }
  }
}
