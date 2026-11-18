package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_VARIANT;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.cdtlz.C1_DTLZ1;
import org.uma.jmetal.problem.multiobjective.lircmop.LIRCMOP2;
import org.uma.jmetal.problem.multiobjective.mw.MW3;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Integration tests of CMOEA-MS on constrained problems, with 100 solutions and 25000 evaluations.
 * The thresholds of the IGD come from runs with 20 seeds, whose worst values are given in each
 * test, with a margin of at least 1.5.
 */
@DisplayName("Integration tests for class CMOEAMS")
class CMOEAMSIT {
  private static final int POPULATION_SIZE = 100;
  private static final int MAX_EVALUATIONS = 25000;

  @BeforeEach
  void setUp() {
    // The results of the runs are random: a fixed seed keeps the thresholds from failing by chance
    JMetalRandom.getInstance().setSeed(12);
  }

  @Test
  @DisplayName("given MW3 and the genetic operators, then the population is feasible and close to"
      + " the front")
  void givenMW3AndTheGeneticOperators_whenRunning_thenThePopulationIsFeasibleAndClose()
      throws IOException {
    // Arrange
    MW3 problem = new MW3();

    // Act
    List<DoubleSolution> population = run(problem, sbx());

    // Assert: IGD between 0.00018 and 0.0048 in 20 seeds
    assertFeasible(population);
    assertTrue(igd(population, "MW3.csv") < 0.01);
  }

  @Test
  @DisplayName("given LIRCMOP2 and the genetic operators, then the population is feasible and"
      + " close to the front")
  void givenLIRCMOP2AndTheGeneticOperators_whenRunning_thenThePopulationIsFeasibleAndClose()
      throws IOException {
    // Arrange
    LIRCMOP2 problem = new LIRCMOP2();

    // Act
    List<DoubleSolution> population = run(problem, sbx());

    // Assert: IGD between 0.017 and 0.030 in 20 seeds
    assertFeasible(population);
    assertTrue(igd(population, "LIRCMOP2.csv") < 0.045);
  }

  @Test
  @DisplayName("given C1_DTLZ1 with three objectives and the genetic operators, then the"
      + " population is feasible and close to the front")
  void givenC1DTLZ1AndTheGeneticOperators_whenRunning_thenThePopulationIsFeasibleAndClose()
      throws IOException {
    // Arrange
    C1_DTLZ1 problem = new C1_DTLZ1();

    // Act
    List<DoubleSolution> population = run(problem, sbx());

    // Assert: IGD between 0.00022 and 0.00027 in 20 seeds
    assertFeasible(population);
    assertTrue(igd(population, "C1_DTLZ1.3D.csv") < 0.0004);
  }

  @Test
  @DisplayName("given MW3 and differential evolution, then the population is feasible and close"
      + " to the front")
  void givenMW3AndDifferentialEvolution_whenRunning_thenThePopulationIsFeasibleAndClose()
      throws IOException {
    // Arrange
    MW3 problem = new MW3();
    var differentialEvolution = new DifferentialEvolutionCrossover(1.0, 0.5, DE_VARIANT.RAND_1_BIN);

    // Act
    List<DoubleSolution> population = run(problem, differentialEvolution);

    // Assert: IGD between 0.00020 and 0.00027 in 20 seeds
    assertFeasible(population);
    assertTrue(igd(population, "MW3.csv") < 0.0005);
  }

  private static CrossoverOperator<DoubleSolution> sbx() {
    return new SBXCrossover(1.0, 20.0);
  }

  private static List<DoubleSolution> run(
      Problem<DoubleSolution> problem, CrossoverOperator<DoubleSolution> crossover) {
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    CMOEAMS<DoubleSolution> algorithm =
        new CMOEAMSBuilder<>(problem, crossover, mutation)
            .setPopulationSize(POPULATION_SIZE)
            .setMaxEvaluations(MAX_EVALUATIONS)
            .build();

    algorithm.run();

    assertEquals(MAX_EVALUATIONS, algorithm.evaluations());
    return algorithm.result();
  }

  private static void assertFeasible(List<DoubleSolution> population) {
    assertEquals(POPULATION_SIZE, population.size());
    assertTrue(population.stream().allMatch(ConstraintHandling::isFeasible));
  }

  private static double igd(List<DoubleSolution> population, String referenceFrontFile)
      throws IOException {
    double[][] referenceFront =
        VectorUtils.readVectors("../resources/referenceFronts/" + referenceFrontFile, ",");
    double[][] front =
        SolutionListUtils.getMatrixWithObjectiveValues(
            SolutionListUtils.getNonDominatedSolutions(population));
    return new InvertedGenerationalDistance(referenceFront).compute(front);
  }
}
