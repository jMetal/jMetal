package org.uma.jmetal.component.examples.multiobjective.rvea;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.RVEABuilder;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.CarSideImpact;
import org.uma.jmetal.problem.multiobjective.ElectricMotor;
import org.uma.jmetal.problem.multiobjective.GAA;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

import java.util.List;

/**
 * Single-run RVEA experiment on the three real-world benchmark problems:
 * CarSideImpact (3 obj, 10 constraints), GAA (10 obj, 1 constraint),
 * and ElectricMotor (20 obj, 60 constraints).
 *
 * <p>Population sizes are derived from the simplex lattice formula C(h+M-1, M-1):
 * <ul>
 *   <li>CarSideImpact: M=3, h=12 → 91 vectors</li>
 *   <li>GAA:           M=10, h=3 → 220 vectors</li>
 *   <li>ElectricMotor: M=20, h=2 → 210 vectors</li>
 * </ul>
 */
public class RVEARealWorldProblemsExample {

  record ProblemConfig(
      Problem<DoubleSolution> problem,
      String funFileName,
      int h,
      int maxEvaluations) {}

  public static void main(String[] args) {
    var configs = List.of(
        new ProblemConfig(new CarSideImpact(), "FUN_CarSideImpact.csv", 12, 10000),
        new ProblemConfig(new GAA(), "FUN_GAA.csv", 3, 50000),
        new ProblemConfig(new ElectricMotor(), "FUN_ElectricMotor.csv", 2, 50000));

    for (var config : configs) {
      runRVEA(config);
    }
  }

  private static void runRVEA(ProblemConfig config) {
    Problem<DoubleSolution> problem = config.problem();
    int nObj = problem.numberOfObjectives();
    int h = config.h();
    int populationSize = ReferencePointGenerator.calculateNumberOfReferencePoints(nObj, h);
    int maxEvaluations = config.maxEvaluations();

    JMetalLogger.logger.info(
        "Running RVEA on " + problem.name() + " | objectives=" + nObj
            + " | constraints=" + problem.numberOfConstraints()
            + " | populationSize=" + populationSize
            + " | maxEvaluations=" + maxEvaluations);

    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

    EvolutionaryAlgorithm<DoubleSolution> rvea =
        new RVEABuilder<>(problem, populationSize, maxEvaluations,
            crossover, mutation, 2.0, 0.1, h)
            .setTermination(new TerminationByEvaluations(maxEvaluations))
            .build();

    rvea.run();

    List<DoubleSolution> population = rvea.result();
    JMetalLogger.logger.info(
        problem.name() + " done in " + rvea.totalComputingTime() + "ms"
            + " | evaluations=" + rvea.numberOfEvaluations()
            + " | result size=" + population.size());

    new SolutionListOutput(population)
        .setFunFileOutputContext(new DefaultFileOutputContext(config.funFileName(), ","))
        .print();

    JMetalLogger.logger.info("Objectives written to " + config.funFileName());
  }
}
