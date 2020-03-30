package org.uma.jmetal.example.multiobjective.smpsorp;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSORP;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.example.multiobjective.smpsorp.jmetal5version.SMPSORPChangingTheReferencePointsAndChartsRunnerZDT1;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.chartcontainer.ChartContainer;
import org.uma.jmetal.util.chartcontainer.ChartContainerWithReferencePoints;
import org.uma.jmetal.util.chartcontainer.GenericChartContainer;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SMPSORPChangingTheReferencePointsAndRealTimeChartExample {
  /**
   * Program to run the SMPSORP algorithm with one reference point. SMPSORP is described in
   * "Extending the Speed-constrained Multi-Objective PSO (SMPSO) With Reference Point Based Preference
   * * Articulation. Antonio J. Nebro, Juan J. Durillo, José García-Nieto, Cristóbal Barba-González,
   * * Javier Del Ser, Carlos A. Coello Coello, Antonio Benítez-Hidalgo, José F. Aldana-Montes.
   * * Parallel Problem Solving from Nature -- PPSN XV. Lecture Notes In Computer Science, Vol. 11101,
   * * pp. 298-310. 2018
   *
   * @author Antonio J. Nebro
   */
  public static void main(String[] args) throws JMetalException, InterruptedException {
    DoubleProblem problem;
    SMPSORP algorithm;
    MutationOperator<DoubleSolution> mutation;

    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    String referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.pf";

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    List<List<Double>> referencePoints;
    referencePoints = new ArrayList<>();
    referencePoints.add(Arrays.asList(0.2, 0.8));
    referencePoints.add(Arrays.asList(0.7, 0.4));

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int maxEvaluations = 25000;
    int swarmSize = 100;

    List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints = new ArrayList<>();

    for (int i = 0; i < referencePoints.size(); i++) {
      archivesWithReferencePoints.add(
              new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(
                      swarmSize / referencePoints.size(), referencePoints.get(i)));
    }

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);
    Termination termination = new TerminationByEvaluations(maxEvaluations);

    algorithm = new SMPSORP(problem,
            swarmSize,
            archivesWithReferencePoints,
            referencePoints,
            mutation,
            0.0, 1.0,
            0.0, 1.0,
            2.5, 1.5,
            2.5, 1.5,
            0.1, 0.1,
            -1.0, -1.0,
            evaluation, termination);

    var runTimeChartObserver = new RunTimeChartObserver<>("SMPSORP", 300, referenceParetoFront);
    runTimeChartObserver.setReferencePointList(referencePoints);

    algorithm.getObservable().register(runTimeChartObserver);

    Thread algorithmThread = new Thread(algorithm);
    ChangeReferencePoint changeReferencePoint = new ChangeReferencePoint(algorithm, referencePoints, archivesWithReferencePoints, runTimeChartObserver.getChart()) ;

    Thread changePointsThread = new Thread(changeReferencePoint) ;

    algorithmThread.start();
    changePointsThread.start();

    algorithmThread.join();

    JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

    new SolutionListOutput(algorithm.getResult())
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    System.exit(0);
  }

  private static class ChangeReferencePoint implements Runnable {
    GenericChartContainer chart ;
    List<List<Double>> referencePoints;
    SMPSORP algorithm ;

    public ChangeReferencePoint(
            Algorithm<List<DoubleSolution>> algorithm,
            List<List<Double>> referencePoints,
            List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints,
            GenericChartContainer chart) {
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
              } catch (Exception e) {
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
