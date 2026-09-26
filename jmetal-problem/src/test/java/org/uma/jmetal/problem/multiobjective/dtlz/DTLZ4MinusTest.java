package org.uma.jmetal.problem.multiobjective.dtlz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class DTLZ4Minus")
class DTLZ4MinusTest {

  @Nested
  @DisplayName("When constructing the problem")
  class WhenConstructing {

    @Test
    @DisplayName("given the default constructor, then the instance matches plain DTLZ4's shape")
    void givenDefaultConstructor_whenCreated_thenInstanceMatchesPlainDTLZ4Shape() {
      DoubleProblem problem = new DTLZ4Minus();

      assertThat(problem.numberOfVariables()).isEqualTo(12);
      assertThat(problem.numberOfObjectives()).isEqualTo(3);
      assertThat(problem.numberOfConstraints()).isZero();
      assertThat(problem.name()).isEqualTo("DTLZ4Minus");
    }
  }

  @Nested
  @DisplayName("When evaluating solutions")
  class WhenEvaluating {

    @Test
    @DisplayName(
        "given a solution on the DTLZ4 front, when evaluated, then objectives equal one minus the"
            + " plain DTLZ4 objectives")
    void givenASolutionOnTheFront_whenEvaluated_thenObjectivesEqualOneMinusPlainDTLZ4() {
      DTLZ4 plain = new DTLZ4(12, 3);
      DTLZ4Minus minus = new DTLZ4Minus(12, 3);
      double[] positionVariables = {0.2, 0.7};

      DoubleSolution plainSolution = solutionOnTheFront(plain, positionVariables);
      DoubleSolution minusSolution = solutionOnTheFront(minus, positionVariables);

      plain.evaluate(plainSolution);
      minus.evaluate(minusSolution);

      for (int i = 0; i < 3; i++) {
        assertThat(minusSolution.objectives()[i])
            .isCloseTo(1.0 - plainSolution.objectives()[i], within(1.0e-9));
      }
    }

    @Test
    @DisplayName(
        "given two solutions sharing the same position variables, when one has g(x_m) = 0 and the"
            + " other g(x_m) > 0, then the g(x_m) = 0 solution dominates")
    void
        givenSharedPositionWithDifferentDistanceValues_whenEvaluated_thenTheUndistortedSolutionDominates() {
      DTLZ4Minus problem = new DTLZ4Minus(12, 3);
      double[] positionVariables = {0.3, 0.6};

      DoubleSolution onTheFront = solutionOnTheFront(problem, positionVariables);
      DoubleSolution awayFromTheFront = problem.createSolution();
      for (int i = 0; i < positionVariables.length; i++) {
        awayFromTheFront.variables().set(i, positionVariables[i]);
      }
      for (int i = positionVariables.length; i < problem.numberOfVariables(); i++) {
        awayFromTheFront.variables().set(i, 0.0);
      }

      problem.evaluate(onTheFront);
      problem.evaluate(awayFromTheFront);

      for (int i = 0; i < problem.numberOfObjectives(); i++) {
        assertThat(onTheFront.objectives()[i]).isLessThanOrEqualTo(awayFromTheFront.objectives()[i]);
      }
      assertThat(onTheFront.objectives()).isNotEqualTo(awayFromTheFront.objectives());
    }

    @Test
    @DisplayName(
        "given the position variables at their extremes, when evaluated, then the objectives stay"
            + " within the (0, ..., 0)-(1, ..., 1) ideal/nadir bounds")
    void givenExtremePositionVariables_whenEvaluated_thenObjectivesStayWithinIdealNadirBounds() {
      DTLZ4Minus problem = new DTLZ4Minus(12, 3);

      for (double[] positionVariables : new double[][] {{0.0, 0.0}, {1.0, 1.0}, {0.0, 1.0}}) {
        DoubleSolution solution = solutionOnTheFront(problem, positionVariables);
        problem.evaluate(solution);

        for (double objective : solution.objectives()) {
          assertThat(objective).isBetween(-1.0e-9, 1.0 + 1.0e-9);
        }
      }
    }
  }

  /** Places the distance variables at 0.5 (g(x_m) = 0) and the given position variables. */
  private static DoubleSolution solutionOnTheFront(DoubleProblem problem, double[] positionVariables) {
    DoubleSolution solution = problem.createSolution();
    for (int i = 0; i < positionVariables.length; i++) {
      solution.variables().set(i, positionVariables[i]);
    }
    for (int i = positionVariables.length; i < problem.numberOfVariables(); i++) {
      solution.variables().set(i, 0.5);
    }
    return solution;
  }
}
