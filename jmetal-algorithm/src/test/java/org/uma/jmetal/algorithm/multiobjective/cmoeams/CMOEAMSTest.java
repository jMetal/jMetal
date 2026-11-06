package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_VARIANT;
import org.uma.jmetal.operator.crossover.impl.NullCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for class CMOEAMS")
class CMOEAMSTest {
  private Srinivas problem;

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(1);
    problem = new Srinivas();
  }

  @Nested
  @DisplayName("When deciding the stage")
  class WhenDecidingTheStage {
    private final double[] threeOfFourFeasible = {0.0, 0.0, 0.0, 0.5};

    @Test
    @DisplayName("given a feasible ratio above lambda and 10% of the evaluations, then it is stage"
        + " B")
    void givenAFeasibleRatioAboveLambdaAndTenPercent_whenDeciding_thenItIsStageB() {
      // Act & Assert
      assertTrue(CMOEAMS.isStageB(threeOfFourFeasible, 0.5, 1000, 10000));
    }

    @Test
    @DisplayName("given less than 10% of the evaluations, then it is stage A")
    void givenLessThanTenPercentOfTheEvaluations_whenDeciding_thenItIsStageA() {
      // Act & Assert
      assertFalse(CMOEAMS.isStageB(threeOfFourFeasible, 0.5, 999, 10000));
    }

    @Test
    @DisplayName("given a feasible ratio equal to lambda, then it is stage A")
    void givenAFeasibleRatioEqualToLambda_whenDeciding_thenItIsStageA() {
      // Arrange
      double[] halfFeasible = {0.0, 0.0, 0.1, 0.2};

      // Act & Assert
      assertFalse(CMOEAMS.isStageB(halfFeasible, 0.5, 5000, 10000));
    }

    @Test
    @DisplayName("given a feasible ratio below lambda, then it is stage A")
    void givenAFeasibleRatioBelowLambda_whenDeciding_thenItIsStageA() {
      // Act & Assert
      assertFalse(CMOEAMS.isStageB(threeOfFourFeasible, 0.8, 5000, 10000));
    }

    @Test
    @DisplayName("given a NaN violation, then the solution is not feasible")
    void givenANaNViolation_whenDeciding_thenTheSolutionIsNotFeasible() {
      // Arrange
      double[] withNaN = {0.0, 0.0, Double.NaN, 0.5};

      // Act & Assert
      assertFalse(CMOEAMS.isStageB(withNaN, 0.5, 5000, 10000));
    }
  }

  @Nested
  @DisplayName("When running")
  class WhenRunning {
    @Test
    @DisplayName("given a budget that is a multiple of N, then it is used exactly")
    void givenABudgetThatIsAMultipleOfN_whenRunning_thenItIsUsedExactly() {
      // Arrange
      CMOEAMS<DoubleSolution> algorithm = geneticAlgorithm(10, 100);

      // Act
      algorithm.run();

      // Assert
      assertEquals(100, algorithm.evaluations());
      assertEquals(10, algorithm.result().size());
      assertEquals(10, algorithm.fitness().length);
    }

    @Test
    @DisplayName("given an odd N, then each generation has N - 1 children and the last one can"
        + " exceed the budget")
    void givenAnOddN_whenRunning_thenEachGenerationHasNMinusOneChildren() {
      // Arrange
      CMOEAMS<DoubleSolution> algorithm = geneticAlgorithm(5, 20);

      // Act
      algorithm.run();

      // Assert: 5 + 4 + 4 + 4 + 4 evaluations
      assertEquals(21, algorithm.evaluations());
      assertEquals(5, algorithm.result().size());
    }

    @Test
    @DisplayName("given a budget of N evaluations, then the fitness is that of stage A of the"
        + " initial population")
    void givenABudgetOfNEvaluations_whenRunning_thenTheFitnessIsThatOfStageA() {
      // Arrange
      CMOEAMS<DoubleSolution> algorithm = geneticAlgorithm(10, 10);

      // Act
      algorithm.run();

      // Assert
      List<DoubleSolution> population = algorithm.result();
      double[][] objectives =
          population.stream().map(DoubleSolution::objectives).toArray(double[][]::new);
      double[][] constraints =
          population.stream().map(DoubleSolution::constraints).toArray(double[][]::new);
      double[] expected =
          CMOEAMSFitness.stageAFitness(
              objectives, CMOEAMSFitness.constraintViolation(constraints));
      assertEquals(10, algorithm.evaluations());
      assertArrayEquals(expected, algorithm.fitness(), 0.0);
    }

    @Test
    @DisplayName("given the genetic operators, then parent i is paired with parent i + N/2 and"
        + " their children go to positions i and i + N/2")
    void givenTheGeneticOperators_whenRunning_thenTheParentsArePairedAsInPlatEMO() {
      // Arrange
      List<List<DoubleSolution>> pairs = new ArrayList<>();
      CrossoverOperator<DoubleSolution> recording =
          new NullCrossover<>() {
            @Override
            public List<DoubleSolution> execute(List<DoubleSolution> parents) {
              pairs.add(List.copyOf(parents));
              return super.execute(parents);
            }
          };
      RecordingEvaluator evaluator = new RecordingEvaluator();
      CMOEAMS<DoubleSolution> algorithm =
          new CMOEAMS<>(problem, 6, 12, 0.5, recording, new NullMutation<>(), evaluator);

      // Act
      algorithm.run();

      // Assert
      List<DoubleSolution> offspring = evaluator.evaluated.get(1);
      assertEquals(3, pairs.size());
      assertEquals(6, offspring.size());
      for (int i = 0; i < 3; i++) {
        assertEquals(pairs.get(i).get(0).variables(), offspring.get(i).variables());
        assertEquals(pairs.get(i).get(1).variables(), offspring.get(i + 3).variables());
        assertTrue(evaluator.evaluated.get(0).contains(pairs.get(i).get(0)));
        assertTrue(evaluator.evaluated.get(0).contains(pairs.get(i).get(1)));
      }
    }

    @Test
    @DisplayName("given differential evolution, then the child of solution i is x_i + F (x_r1 -"
        + " x_r2) within the bounds")
    void givenDifferentialEvolution_whenRunning_thenTheChildOfSolutionIIsBasedOnIt() {
      // Arrange
      var crossover = new DifferentialEvolutionCrossover(1.0, 0.5, DE_VARIANT.RAND_1_BIN);
      RecordingEvaluator evaluator = new RecordingEvaluator();
      CMOEAMS<DoubleSolution> algorithm =
          new CMOEAMS<>(problem, 6, 12, 0.5, crossover, new NullMutation<>(), evaluator);

      // Act
      algorithm.run();

      // Assert
      List<DoubleSolution> population = evaluator.evaluated.get(0);
      List<DoubleSolution> offspring = evaluator.evaluated.get(1);
      assertEquals(12, algorithm.evaluations());
      assertEquals(6, offspring.size());
      for (int i = 0; i < offspring.size(); i++) {
        assertTrue(isDifferentialEvolutionChild(offspring.get(i), population.get(i), population));
      }
    }

    @Test
    @DisplayName("given differential evolution and polynomial mutation, then the run uses the"
        + " budget")
    void givenDifferentialEvolutionAndPolynomialMutation_whenRunning_thenTheRunUsesTheBudget() {
      // Arrange
      var crossover = new DifferentialEvolutionCrossover(1.0, 0.5, DE_VARIANT.RAND_1_BIN);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
      CMOEAMS<DoubleSolution> algorithm =
          new CMOEAMSBuilder<>(problem, crossover, mutation)
              .setPopulationSize(10)
              .setMaxEvaluations(200)
              .build();

      // Act
      algorithm.run();

      // Assert
      assertEquals(200, algorithm.evaluations());
      assertEquals(10, algorithm.result().size());
    }

    @Test
    @DisplayName("given the algorithm, then its name is CMOEA-MS")
    void givenTheAlgorithm_whenAskingItsName_thenItIsCMOEAMS() {
      // Act
      CMOEAMS<DoubleSolution> algorithm = geneticAlgorithm(10, 100);

      // Assert
      assertEquals("CMOEA-MS", algorithm.name());
      assertFalse(algorithm.description().isBlank());
    }

    private CMOEAMS<DoubleSolution> geneticAlgorithm(int populationSize, int maxEvaluations) {
      return new CMOEAMSBuilder<>(
              problem,
              new SBXCrossover(1.0, 20.0),
              new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0))
          .setPopulationSize(populationSize)
          .setMaxEvaluations(maxEvaluations)
          .build();
    }

    private boolean isDifferentialEvolutionChild(
        DoubleSolution child, DoubleSolution base, List<DoubleSolution> population) {
      for (DoubleSolution first : population) {
        for (DoubleSolution second : population) {
          boolean matches = true;
          for (int j = 0; j < child.variables().size(); j++) {
            double value =
                base.variables().get(j)
                    + 0.5 * (first.variables().get(j) - second.variables().get(j));
            double clamped =
                Math.min(
                    Math.max(value, child.getBounds(j).getLowerBound()),
                    child.getBounds(j).getUpperBound());
            matches &= Math.abs(clamped - child.variables().get(j)) < 1e-12;
          }
          if (matches) {
            return true;
          }
        }
      }
      return false;
    }
  }

  /** A sequential evaluator that keeps the lists it evaluates. */
  @SuppressWarnings("serial")
  private static class RecordingEvaluator implements SolutionListEvaluator<DoubleSolution> {
    private final List<List<DoubleSolution>> evaluated = new ArrayList<>();

    @Override
    public List<DoubleSolution> evaluate(
        List<DoubleSolution> solutionList, Problem<DoubleSolution> problem) {
      solutionList.forEach(problem::evaluate);
      evaluated.add(List.copyOf(solutionList));
      return solutionList;
    }

    @Override
    public void shutdown() {}
  }
}
