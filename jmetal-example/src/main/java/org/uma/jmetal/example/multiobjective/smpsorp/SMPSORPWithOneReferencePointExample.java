package org.uma.jmetal.example.multiobjective.smpsorp;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSORP;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SMPSORPWithOneReferencePointExample {
  /**
   * Program to run the SMPSORP algorithm with one reference point. SMPSORP is described in
   *  "Extending the Speed-constrained Multi-Objective PSO (SMPSO) With Reference Point Based Preference
   *  * Articulation. Antonio J. Nebro, Juan J. Durillo, José García-Nieto, Cristóbal Barba-González,
   *  * Javier Del Ser, Carlos A. Coello Coello, Antonio Benítez-Hidalgo, José F. Aldana-Montes.
   *  * Parallel Problem Solving from Nature -- PPSN XV. Lecture Notes In Computer Science, Vol. 11101,
   *  * pp. 298-310. 2018
   *
   * @author Antonio J. Nebro
   */
  public static void main(String[] args) throws JMetalException {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    MutationOperator<DoubleSolution> mutation;

    String problemName;
    if (args.length == 1) {
      problemName = args[0];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    }
    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    List<List<Double>> referencePoints;
    referencePoints = new ArrayList<>();
    referencePoints.add(List.of(0.2, 0.8));

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

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;
    Termination termination = new TerminationByEvaluations(maxEvaluations) ;

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

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    System.exit(0);
  }
}
