package org.uma.jmetal.experimental.componentbasedalgorithm.example.multiobjective.smpsorp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.smpso.SMPSORP;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

public class SMPSORPWithMultipleReferencePointsAndRealTimeChartExample {
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
  public static void main(String[] args) throws JMetalException {

    var problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    var referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv";

    var problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

    List<List<Double>> referencePoints = new ArrayList<>();
    referencePoints.add(List.of(0.2, 0.8));
    referencePoints.add(List.of(0.7, 0.4));

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var maxEvaluations = 25000;
    var swarmSize = 100;

      @NotNull List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints = new ArrayList<>();
      for (var referencePoint : referencePoints) {
        var doubleSolutionCrowdingDistanceArchiveWithReferencePoint = new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(
                  swarmSize / referencePoints.size(), referencePoint);
          archivesWithReferencePoints.add(doubleSolutionCrowdingDistanceArchiveWithReferencePoint);
      }

      Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);
    Termination termination = new TerminationByEvaluations(maxEvaluations);

    var algorithm = new SMPSORP(problem,
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

    @NotNull var evaluationObserver = new EvaluationObserver(100);
    var runTimeChartObserver = new RunTimeChartObserver<>("SMPSORP", 80, referenceParetoFront);
    runTimeChartObserver.setReferencePointList(referencePoints);

    algorithm.getObservable().register(evaluationObserver);
    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();

    JMetalLogger.logger.info("Total execution time : " + algorithm.getTotalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + algorithm.getEvaluations());

    new SolutionListOutput(algorithm.getResult())
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    System.exit(0);
  }
}
