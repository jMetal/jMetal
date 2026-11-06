package org.uma.jmetal.algorithm.multiobjective.top;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.OptionalInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for class ToP")
class ToPTest {
  private static final double EPSILON = 1e-12;

  /** A solution with the given objectives and constraint values (negative ones are violated). */
  private static DoubleSolution solution(double[] objectives, double... constraints) {
    DoubleSolution solution =
        new DefaultDoubleSolution(
            List.of(Bounds.create(0.0, 1.0)), objectives.length, constraints.length);
    System.arraycopy(objectives, 0, solution.objectives(), 0, objectives.length);
    System.arraycopy(constraints, 0, solution.constraints(), 0, constraints.length);
    return solution;
  }

  /** A solution with two objectives and one constraint whose violation is the given one. */
  private static DoubleSolution solution(double violation, double f1, double f2) {
    return solution(new double[] {f1, f2}, -violation);
  }

  /** A problem with two variables and objectives f1 = x1 and f2 = x2. */
  @SuppressWarnings("serial")
  private abstract static class TwoVariableProblem extends AbstractDoubleProblem {
    TwoVariableProblem(int numberOfConstraints) {
      numberOfObjectives(2);
      numberOfConstraints(numberOfConstraints);
      variableBounds(List.of(0.0, 0.0), List.of(1.0, 1.0));
    }
  }

  /** A problem whose only constraint is violated everywhere, so Pf is always zero. */
  @SuppressWarnings("serial")
  private static class InfeasibleProblem extends TwoVariableProblem {
    InfeasibleProblem() {
      super(1);
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      solution.objectives()[0] = solution.variables().get(0);
      solution.objectives()[1] = solution.variables().get(1);
      solution.constraints()[0] = -1.0 - solution.variables().get(0);
      return solution;
    }
  }

  /**
   * A problem without constraints whose objectives are f1 = x1 and f2 = 1 - x1, so the normalized
   * objectives of every solution add up to one and delta is (almost) zero.
   */
  @SuppressWarnings("serial")
  private static class LinearFrontProblem extends TwoVariableProblem {
    LinearFrontProblem() {
      super(0);
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      solution.objectives()[0] = solution.variables().get(0);
      solution.objectives()[1] = 1.0 - solution.variables().get(0);
      return solution;
    }
  }

  @Nested
  @DisplayName("When measuring a solution")
  class WhenMeasuringASolution {
    @Test
    @DisplayName("given violated constraints, when measuring, then the violation is positive")
    void givenViolatedConstraints_whenMeasuring_thenTheViolationIsTheSumOfTheViolations() {
      // Arrange: jMetal's convention, a negative constraint value is a violation
      DoubleSolution solution = solution(new double[] {1.0, 2.0}, -0.5, 0.2, -1.0);

      // Act
      double violation = ToP.constraintViolation(solution);

      // Assert
      assertEquals(1.5, violation, EPSILON);
    }

    @Test
    @DisplayName("given satisfied constraints, when measuring, then the violation is zero")
    void givenSatisfiedConstraints_whenMeasuring_thenTheViolationIsZero() {
      // Act
      double violation = ToP.constraintViolation(solution(new double[] {1.0, 2.0}, 0.0, 0.3));

      // Assert
      assertEquals(0.0, violation);
    }

    @Test
    @DisplayName("given three objectives, when computing the mean objective, then it is the mean")
    void givenThreeObjectives_whenComputingTheMeanObjective_thenItIsTheirMean() {
      // Act
      double mean = ToP.meanObjective(solution(new double[] {1.0, 2.0, 6.0}));

      // Assert
      assertEquals(3.0, mean, EPSILON);
    }

    @Test
    @DisplayName("given ties, when looking for the best of phase 1, then it is the first lowest")
    void givenTies_whenLookingForTheBest_thenItIsTheFirstLowestMeanObjective() {
      // Arrange: feasibility is ignored
      List<DoubleSolution> solutions =
          List.of(
              solution(0.0, 2.0, 2.0),
              solution(5.0, 1.0, 1.0),
              solution(0.0, 0.5, 1.5),
              solution(0.0, 3.0, 0.0));

      // Act
      int best = ToP.indexOfLowestMeanObjective(solutions);

      // Assert
      assertEquals(1, best);
    }
  }

  @Nested
  @DisplayName("When replacing one to one in phase 1")
  class WhenReplacingOneToOne {
    @ParameterizedTest(name = "offspring (cv {0}, mean {1}) vs parent (cv {2}, mean {3}): {4}")
    @CsvSource({
      "0.1, 9.0, 0.2, 1.0, true",
      "0.0, 9.0, 0.2, 1.0, true",
      "0.3, 1.0, 0.2, 9.0, false",
      "0.2, 9.0, 0.0, 1.0, false",
      "0.0, 1.0, 0.0, 2.0, true",
      "0.0, 2.0, 0.0, 1.0, false",
      "0.0, 1.0, 0.0, 1.0, false",
      "0.5, 1.0, 0.5, 2.0, true",
      "0.5, 2.0, 0.5, 1.0, false"
    })
    @DisplayName("given an offspring and its parent, when comparing, then the feasibility rule")
    void givenAnOffspringAndItsParent_whenComparing_thenTheFeasibilityRuleDecides(
        double offspringViolation,
        double offspringMean,
        double parentViolation,
        double parentMean,
        boolean expected) {
      // Arrange
      DoubleSolution offspring = solution(offspringViolation, offspringMean, offspringMean);
      DoubleSolution parent = solution(parentViolation, parentMean, parentMean);

      // Act
      boolean replaces = ToP.offspringReplacesParent(offspring, parent);

      // Assert
      assertEquals(expected, replaces);
    }

    @Test
    @DisplayName("given a population, when replacing, then each offspring faces its own parent")
    void givenAPopulation_whenReplacing_thenEachOffspringFacesTheParentAtItsPosition() {
      // Arrange
      List<DoubleSolution> population =
          List.of(solution(0.0, 1.0, 1.0), solution(0.0, 1.0, 1.0), solution(0.4, 0.0, 0.0));
      List<DoubleSolution> offspring =
          List.of(solution(0.0, 0.5, 0.5), solution(0.0, 2.0, 2.0), solution(0.1, 9.0, 9.0));

      // Act
      List<DoubleSolution> next = ToP.oneToOneReplacement(population, offspring);

      // Assert
      assertEquals(3, next.size());
      assertSame(offspring.get(0), next.get(0));
      assertSame(population.get(1), next.get(1));
      assertSame(offspring.get(2), next.get(2));
    }
  }

  @Nested
  @DisplayName("When updating Pf and delta")
  class WhenUpdatingPfAndDelta {
    @Test
    @DisplayName("given a population, when computing Pf, then it is the feasible ratio")
    void givenAPopulation_whenComputingPf_thenItIsTheRatioOfFeasibleSolutions() {
      // Arrange
      List<DoubleSolution> population =
          List.of(
              solution(0.0, 1.0, 1.0),
              solution(0.1, 1.0, 1.0),
              solution(0.0, 2.0, 2.0),
              solution(3.0, 1.0, 1.0));

      // Act
      double feasibleRatio = ToP.feasibleRatio(population);

      // Assert
      assertEquals(0.5, feasibleRatio);
    }

    @Test
    @DisplayName("given two feasible solutions, when updating delta, then it keeps its value")
    void givenTwoFeasibleSolutions_whenUpdatingDelta_thenItKeepsItsValue() {
      // Arrange
      List<DoubleSolution> population =
          List.of(
              solution(0.0, 0.0, 1.0),
              solution(0.0, 1.0, 0.0),
              solution(0.2, 0.5, 0.5),
              solution(0.3, 0.2, 0.2));

      // Act
      double delta = ToP.updatedDelta(population, 0.7);

      // Assert
      assertEquals(0.7, delta);
    }

    @Test
    @DisplayName("given six feasible solutions, when updating delta, then 2nd minus 1st sum")
    void givenSixFeasibleSolutions_whenUpdatingDelta_thenItIsTheSecondMinusTheFirstSum() {
      // Arrange: normalized with (0, 0) and (4, 4), the sums are 0, 0.5, 1.5, 1.5, 1 and 2; the
      // infeasible solution is ignored, although it would change the minimum
      List<DoubleSolution> population =
          List.of(
              solution(0.0, 0.0, 0.0),
              solution(0.0, 1.0, 1.0),
              solution(0.0, 2.0, 4.0),
              solution(0.0, 4.0, 2.0),
              solution(0.0, 2.0, 2.0),
              solution(0.0, 4.0, 4.0),
              solution(0.5, -10.0, -10.0));

      // Act
      double delta = ToP.updatedDelta(population, 1.0);

      // Assert: floor(6 / 3) = 2, so delta is the second smallest sum minus the smallest one
      assertEquals(0.5, delta, EPSILON);
    }

    @Test
    @DisplayName("given three feasible solutions, when updating delta, then it is zero")
    void givenThreeFeasibleSolutions_whenUpdatingDelta_thenItIsZero() {
      // Arrange
      List<DoubleSolution> population =
          List.of(solution(0.0, 0.0, 4.0), solution(0.0, 1.0, 1.0), solution(0.0, 4.0, 0.0));

      // Act
      double delta = ToP.updatedDelta(population, 1.0);

      // Assert: floor(3 / 3) = 1, the smallest sum minus itself
      assertEquals(0.0, delta);
    }

    @Test
    @DisplayName("given an objective with a single value, when updating delta, then it is NaN")
    void givenAnObjectiveWithASingleValue_whenUpdatingDelta_thenItIsNaN() {
      // Arrange
      List<DoubleSolution> population =
          List.of(solution(0.0, 1.0, 0.0), solution(0.0, 1.0, 1.0), solution(0.0, 1.0, 2.0));

      // Act
      double delta = ToP.updatedDelta(population, 1.0);

      // Assert
      assertTrue(Double.isNaN(delta));
    }
  }

  @Nested
  @DisplayName("When deciding the phase")
  class WhenDecidingThePhase {
    @ParameterizedTest(name = "delta {0}, Pf {1}, {2} of {3} evaluations: phase 1 is {4}")
    @CsvSource({
      "0.2, 1.0, 100, 1000, true",
      "0.19, 1.0, 100, 1000, false",
      "0.0, 0.3333333333333333, 100, 1000, true",
      "0.0, 0.34, 100, 1000, false",
      "1.0, 0.0, 900, 1000, true",
      "1.0, 0.0, 901, 1000, false",
      "NaN, 1.0, 100, 1000, false",
      "NaN, 0.0, 100, 1000, true"
    })
    @DisplayName("given delta, Pf and the evaluations, when deciding, then PlatEMO's rule")
    void givenDeltaPfAndTheEvaluations_whenDeciding_thenTheConditionOfPlatEMOIsApplied(
        double delta, double feasibleRatio, int evaluations, int maxEvaluations, boolean expected) {
      // Act
      boolean phaseOne = ToP.phaseOneContinues(delta, feasibleRatio, evaluations, maxEvaluations);

      // Assert
      assertEquals(expected, phaseOne);
    }
  }

  @Nested
  @DisplayName("When running")
  class WhenRunning {
    @BeforeEach
    void setUp() {
      JMetalRandom.getInstance().setSeed(1);
    }

    private ToP top(AbstractDoubleProblem problem, int maxEvaluations) {
      return new ToPBuilder(problem)
          .setPopulationSize(10)
          .setMaxEvaluations(maxEvaluations)
          .build();
    }

    @Test
    @DisplayName("given no feasible solution, when running, then phase 2 starts after 90%")
    void givenNoFeasibleSolution_whenRunning_thenPhaseTwoStartsAfterNinetyPercent() {
      // Arrange
      ToP algorithm = top(new InfeasibleProblem(), 200);

      // Act
      algorithm.run();

      // Assert: phase 1 runs at 10, 20, ..., 180 evaluations (180 <= 0.9 * 200)
      assertEquals(OptionalInt.of(190), algorithm.phaseTwoStart());
      assertEquals(200, algorithm.evaluations());
      assertEquals(10, algorithm.result().size());
    }

    @Test
    @DisplayName("given a small budget, when running, then phase 2 is not reached")
    void givenASmallBudgetAndNoFeasibleSolution_whenRunning_thenPhaseTwoIsNotReached() {
      // Arrange
      ToP algorithm = top(new InfeasibleProblem(), 50);

      // Act
      algorithm.run();

      // Assert
      assertEquals(OptionalInt.empty(), algorithm.phaseTwoStart());
      assertEquals(50, algorithm.evaluations());
    }

    @Test
    @DisplayName("given a small delta and Pf = 1, when running, then phase 2 starts at once")
    void givenASmallDeltaAndAllFeasible_whenRunning_thenPhaseTwoStartsAfterOneGeneration() {
      // Arrange: delta starts at 1, so the first generation is of phase 1
      ToP algorithm = top(new LinearFrontProblem(), 200);

      // Act
      algorithm.run();

      // Assert
      assertEquals(OptionalInt.of(20), algorithm.phaseTwoStart());
      assertEquals(10, algorithm.result().size());
    }

    @Test
    @DisplayName("given a budget not multiple of N, when running, then the last generation ends")
    void givenABudgetThatIsNotAMultipleOfN_whenRunning_thenTheLastGenerationIsCompleted() {
      // Arrange
      ToP algorithm = top(new LinearFrontProblem(), 95);

      // Act
      algorithm.run();

      // Assert
      assertEquals(100, algorithm.evaluations());
    }

    @Test
    @DisplayName("given an algorithm, when not run yet, then the result is empty")
    void givenAnAlgorithm_whenNotRunYet_thenTheResultIsEmpty() {
      // Act
      ToP algorithm = top(new LinearFrontProblem(), 100);

      // Assert
      assertTrue(algorithm.result().isEmpty());
      assertFalse(algorithm.phaseTwoStart().isPresent());
      assertEquals("ToP", algorithm.name());
    }
  }
}
