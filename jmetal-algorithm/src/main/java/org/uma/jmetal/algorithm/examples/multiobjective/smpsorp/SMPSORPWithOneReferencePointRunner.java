package org.uma.jmetal.algorithm.examples.multiobjective.smpsorp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSORP;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

public class SMPSORPWithOneReferencePointRunner {
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

    String problemName;
    if (args.length == 1) {
      problemName = args[0];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    }
    var problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

    List<List<Double>> referencePoints = new ArrayList<>();
    referencePoints.add(Arrays.asList(0.2, 0.8));

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var maxIterations = 250;
    var swarmSize = 100;

      List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints = new ArrayList<>();
      for (var referencePoint : referencePoints) {
          @NotNull CrowdingDistanceArchiveWithReferencePoint<DoubleSolution> doubleSolutionCrowdingDistanceArchiveWithReferencePoint = new CrowdingDistanceArchiveWithReferencePoint<>(
                  swarmSize / referencePoints.size(), referencePoint);
          archivesWithReferencePoints.add(doubleSolutionCrowdingDistanceArchiveWithReferencePoint);
      }

    Algorithm<List<DoubleSolution>> algorithm = new SMPSORP(problem,
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
            new DefaultDominanceComparator<>(),
            new SequentialSolutionListEvaluator<>());

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute();

    var population = algorithm.getResult();
    var computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    System.exit(0);
  }
}
