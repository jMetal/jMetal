package org.uma.jmetal.util.constrainthandling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.ranking.Ranking;

@DisplayName("Unit tests for the constraint-handling techniques")
class ConstraintHandlingTechniquesTest {

  /** A solution with two objectives and the given constraint values (negative is a violation). */
  private static DoubleSolution solution(double f1, double f2, double... constraints) {
    var solution =
        new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, constraints.length);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    for (int i = 0; i < constraints.length; i++) {
      solution.constraints()[i] = constraints[i];
    }
    return solution;
  }

  /** Solutions with the same objectives and the given overall constraint violations. */
  private static List<DoubleSolution> populationWithViolations(double... violations) {
    List<DoubleSolution> population = new ArrayList<>();
    for (double violation : violations) {
      population.add(solution(0.5, 0.5, -violation));
    }
    return population;
  }

  /** A comparison of objectives that prefers the larger first objective, unlike dominance. */
  private static final Comparator<DoubleSolution> LARGER_FIRST_OBJECTIVE =
      (solution1, solution2) ->
          Double.compare(solution2.objectives()[0], solution1.objectives()[0]);

  @Nested
  @DisplayName("When using the constrained-domination principle")
  class WhenUsingTheConstrainedDominationPrinciple {

    @Test
    @DisplayName("given a feasible and an infeasible solution, then the feasible one is preferred")
    void givenFeasibleAndInfeasible_whenCompared_thenTheFeasibleOneIsPreferred() {
      // Arrange
      var comparator = new ConstrainedDominance<DoubleSolution>().comparator();
      var feasible = solution(1.0, 1.0, 0.0);
      var infeasible = solution(0.0, 0.0, -0.1);

      // Act
      int result = comparator.compare(feasible, infeasible);

      // Assert
      assertThat(result).isNegative();
      assertThat(comparator.compare(infeasible, feasible)).isPositive();
    }

    @Test
    @DisplayName("given two infeasible solutions, then the less violated one is preferred")
    void givenTwoInfeasible_whenCompared_thenTheLessViolatedOneIsPreferred() {
      // Arrange
      var comparator = new ConstrainedDominance<DoubleSolution>().comparator();

      // Act
      int result = comparator.compare(solution(1.0, 1.0, -0.5), solution(0.0, 0.0, -2.0));

      // Assert
      assertThat(result).isNegative();
    }

    @Test
    @DisplayName("given two feasible solutions, then Pareto dominance decides")
    void givenTwoFeasible_whenCompared_thenParetoDominanceDecides() {
      // Arrange
      var comparator = new ConstrainedDominance<DoubleSolution>().comparator();

      // Act & Assert
      assertThat(comparator.compare(solution(0.0, 0.0, 0.0), solution(1.0, 1.0, 0.0))).isNegative();
      assertThat(comparator.compare(solution(0.0, 1.0, 0.0), solution(1.0, 0.0, 0.0))).isZero();
    }

    @Test
    @DisplayName("given the number of violated constraints, then fewer violations win")
    void givenTheNumberOfViolatedConstraints_whenCompared_thenFewerViolationsWin() {
      // Arrange
      var comparator =
          new ConstrainedDominance<DoubleSolution>(
                  ConstrainedDominance.ViolationMeasure.NUMBER_OF_VIOLATED_CONSTRAINTS)
              .comparator();
      var oneLargeViolation = solution(1.0, 1.0, -5.0, 0.0);
      var twoSmallViolations = solution(1.0, 1.0, -0.1, -0.1);

      // Act
      int result = comparator.compare(oneLargeViolation, twoSmallViolations);

      // Assert
      assertThat(result).isNegative();
    }

    @Test
    @DisplayName("given another comparison of objectives, then it decides between feasible ones")
    void givenAnotherComparisonOfObjectives_whenComparingFeasibleOnes_thenItDecides() {
      // Arrange
      var technique = new ConstrainedDominance<DoubleSolution>();
      var smaller = solution(0.0, 0.0, 0.0);
      var larger = solution(1.0, 1.0, 0.0);
      var infeasible = solution(2.0, 2.0, -1.0);

      // Act & Assert: it decides between feasible solutions, not against an infeasible one
      assertThat(technique.compare(larger, smaller, LARGER_FIRST_OBJECTIVE)).isNegative();
      assertThat(technique.compare(infeasible, smaller, LARGER_FIRST_OBJECTIVE)).isPositive();
    }

    @Test
    @DisplayName("given no constraints, when ranking, then it is Pareto dominance")
    void givenNoConstraints_whenRanking_thenItIsParetoDominance() {
      // Arrange
      List<DoubleSolution> population =
          List.of(solution(0.0, 1.0), solution(1.0, 0.0), solution(1.0, 1.0));
      Ranking<DoubleSolution> ranking = new ConstrainedDominance<DoubleSolution>().ranking();

      // Act
      ranking.compute(population);

      // Assert
      assertThat(ranking.getNumberOfSubFronts()).isEqualTo(2);
      assertThat(ranking.getRank(population.get(2))).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("When using the epsilon-constrained method")
  class WhenUsingTheEpsilonConstrainedMethod {

    @Test
    @DisplayName("given the progress, then epsilon starts at the quantile and decreases to zero")
    void givenTheProgress_whenUpdated_thenEpsilonFollowsItsSchedule() {
      // Arrange: initial epsilon 2.0 (median of 0..4), linear decay to zero at the end of the run
      var technique = new EpsilonConstrained<DoubleSolution>(0.5, 1.0, 1.0);
      var population = populationWithViolations(0.0, 1.0, 2.0, 3.0, 4.0);

      // Act
      technique.update(population, 0.1);
      double atATenth = technique.epsilon();
      technique.update(populationWithViolations(9.0, 9.0), 0.5);
      double atTheMiddle = technique.epsilon();
      technique.update(population, 1.0);

      // Assert: the initial epsilon comes from the first population only
      assertThat(atATenth).isCloseTo(1.8, within(1e-12));
      assertThat(atTheMiddle).isCloseTo(1.0, within(1e-12));
      assertThat(technique.epsilon()).isZero();
    }

    @ParameterizedTest(name = "exponent {0}, Tc {1}, progress {2}")
    @CsvSource({
      "2.0, 1.0, 0.5, 0.5",
      "1.0, 0.5, 0.25, 1.0",
      "1.0, 0.5, 0.5, 0.0",
      "3.0, 0.5, 0.9, 0.0"
    })
    @DisplayName("given an exponent and Tc, then epsilon is epsilon(0) (1 - t / Tc)^exponent")
    void givenAnExponentAndTc_whenUpdated_thenEpsilonFollowsTheFormula(
        double exponent, double decayFraction, double progress, double expected) {
      // Arrange: initial epsilon 2.0
      var technique = new EpsilonConstrained<DoubleSolution>(1.0, exponent, decayFraction);

      // Act
      technique.update(populationWithViolations(0.0, 2.0), progress);

      // Assert
      assertThat(technique.epsilon()).isCloseTo(expected, within(1e-12));
    }

    @Test
    @DisplayName("given a solution within epsilon, then the objectives decide against a feasible")
    void givenASolutionWithinEpsilon_whenCompared_thenTheObjectivesDecide() {
      // Arrange
      var technique = new EpsilonConstrained<DoubleSolution>(0.5, 1.0, 1.0);
      technique.update(populationWithViolations(0.0, 1.0, 2.0, 3.0, 4.0), 0.1);
      var withinEpsilon = solution(0.0, 0.0, -1.5);
      var outsideEpsilon = solution(0.0, 0.0, -1.9);
      var feasible = solution(1.0, 1.0, 0.0);

      // Act & Assert: epsilon is 1.8
      assertThat(technique.comparator().compare(withinEpsilon, feasible)).isNegative();
      assertThat(technique.comparator().compare(outsideEpsilon, feasible)).isPositive();
      assertThat(technique.compare(withinEpsilon, feasible, LARGER_FIRST_OBJECTIVE)).isPositive();
    }

    @Test
    @DisplayName("given no update, when comparing, then it is the constrained-domination principle")
    void givenNoUpdate_whenComparing_thenItIsTheConstrainedDominationPrinciple() {
      // Arrange
      var technique = new EpsilonConstrained<DoubleSolution>(0.5, 1.0, 1.0);

      // Act
      int result = technique.comparator().compare(solution(0.0, 0.0, -0.1), solution(1, 1, 0.0));

      // Assert
      assertThat(technique.epsilon()).isZero();
      assertThat(result).isPositive();
    }

    @Test
    @DisplayName("given no progress, when updating, then an exception says what is missing")
    void givenNoProgress_whenUpdating_thenAnExceptionIsThrown() {
      // Arrange
      var technique = new EpsilonConstrained<DoubleSolution>(0.5, 1.0, 1.0);

      // Act & Assert
      assertThatThrownBy(() -> technique.update(populationWithViolations(1.0), Double.NaN))
          .isInstanceOf(InvalidConditionException.class)
          .hasMessageContaining("termination by evaluations or by computing time");
      assertThatThrownBy(() -> technique.update(populationWithViolations(1.0), 1.5))
          .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given invalid parameters, when creating it, then an exception is thrown")
    void givenInvalidParameters_whenCreatingIt_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThatThrownBy(() -> new EpsilonConstrained<DoubleSolution>(1.5, 1.0, 1.0))
          .isInstanceOf(RuntimeException.class);
      assertThatThrownBy(() -> new EpsilonConstrained<DoubleSolution>(0.5, 0.0, 1.0))
          .isInstanceOf(InvalidConditionException.class);
      assertThatThrownBy(() -> new EpsilonConstrained<DoubleSolution>(0.5, 1.0, 0.0))
          .isInstanceOf(InvalidConditionException.class);
    }
  }

  @Nested
  @DisplayName("When using push and pull search")
  class WhenUsingPushAndPullSearch {

    private final DoubleSolution feasible = solution(1.0, 1.0, 0.0);
    private final DoubleSolution infeasibleButBetter = solution(0.0, 0.0, -0.1);

    /** The defaults of the paper but the window, a tenth of the run. */
    private PushPullSearch<DoubleSolution> technique(double decayFraction) {
      return new PushPullSearch<>(1e-3, 0.1, decayFraction, 1.0, 0.95, 0.1);
    }

    @Test
    @DisplayName("given the push stage, when comparing, then the constraints are ignored")
    void givenThePushStage_whenCompared_thenTheConstraintsAreIgnored() {
      // Arrange
      var technique = technique(0.8);

      // Act
      int result = technique.comparator().compare(infeasibleButBetter, feasible);

      // Assert
      assertThat(technique.pushing()).isTrue();
      assertThat(result).isNegative();
    }

    @Test
    @DisplayName("given settled points, then epsilon starts at the largest violation")
    void givenSettledPoints_whenUpdated_thenThePullStageStarts() {
      // Arrange
      var technique = technique(0.8);
      var population = populationWithViolations(0.0, 1.0, 2.0, 3.0, 4.0);

      // Act
      technique.update(population, 0.1);
      boolean pushingAtFirst = technique.pushing();
      technique.update(population, 0.2);

      // Assert: 1 feasible solution in 5 is below alpha, so epsilon is reduced by tau
      assertThat(pushingAtFirst).isTrue();
      assertThat(technique.pushing()).isFalse();
      assertThat(technique.epsilon()).isCloseTo(0.9 * 4.0, within(1e-12));
    }

    @Test
    @DisplayName("given updates closer than the window, then the push stage goes on")
    void givenUpdatesCloserThanTheWindow_whenUpdated_thenThePushStageGoesOn() {
      // Arrange
      var technique = technique(0.8);
      var population = populationWithViolations(0.0, 1.0);

      // Act: the same points, but only 0.09 of the run apart
      technique.update(population, 0.01);
      technique.update(population, 0.05);
      technique.update(population, 0.10);
      boolean pushingBeforeTheWindow = technique.pushing();
      technique.update(population, 0.11);

      // Assert
      assertThat(pushingBeforeTheWindow).isTrue();
      assertThat(technique.pushing()).isFalse();
    }

    @Test
    @DisplayName("given points that keep changing, then the push stage lasts until Tc")
    void givenPointsThatKeepChanging_whenUpdated_thenThePushStageLastsUntilTc() {
      // Arrange
      var technique = technique(0.5);

      // Act
      for (int step = 1; step < 5; step++) {
        var population = List.of(solution(step, 1.0, -1.0), solution(1.0, step, 0.0));
        technique.update(population, step / 10.0);
      }
      boolean pushingBeforeTc = technique.pushing();
      technique.update(List.of(feasible, infeasibleButBetter), 0.5);

      // Assert
      assertThat(pushingBeforeTc).isTrue();
      assertThat(technique.pushing()).isFalse();
      assertThat(technique.epsilon()).isZero();
    }

    @Test
    @DisplayName("given enough feasible solutions, then epsilon follows its schedule")
    void givenMostlyFeasible_whenUpdated_thenEpsilonFollowsItsSchedule() {
      // Arrange: alpha 0.5, and epsilon is zero at the end of the run
      var technique = new PushPullSearch<DoubleSolution>(1e-3, 0.1, 1.0, 1.0, 0.5, 0.1);
      var population = populationWithViolations(0.0, 0.0, 0.0, 0.0, 4.0);

      // Act
      technique.update(population, 0.1);
      technique.update(population, 0.2);

      // Assert: epsilon(0) * (1 - 0.2)
      assertThat(technique.epsilon()).isCloseTo(4.0 * 0.8, within(1e-12));
    }

    @Test
    @DisplayName("given Tc, when reached, then it is the constrained-domination principle")
    void givenTc_whenReached_thenItIsTheConstrainedDominationPrinciple() {
      // Arrange: Tc at a fifth of the run, before the points can settle
      var technique = technique(0.2);

      // Act
      technique.update(populationWithViolations(0.0, 1.0), 0.1);
      technique.update(populationWithViolations(0.0, 3.0), 0.2);
      int result = technique.comparator().compare(infeasibleButBetter, feasible);

      // Assert
      assertThat(technique.pushing()).isFalse();
      assertThat(technique.epsilon()).isZero();
      assertThat(result).isPositive();
    }

    @Test
    @DisplayName("given no progress, when updating, then an exception is thrown")
    void givenNoProgress_whenUpdating_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThatThrownBy(() -> technique(0.8).update(populationWithViolations(1.0), Double.NaN))
          .isInstanceOf(InvalidConditionException.class)
          .hasMessageContaining("PushPullSearch");
    }
  }

  @Nested
  @DisplayName("When using stochastic ranking")
  class WhenUsingStochasticRanking {

    @Test
    @DisplayName("given pf = 0, when ranking, then a feasible solution comes first")
    void givenPfZero_whenRanked_thenTheFeasibleOneComesFirst() {
      // Arrange
      var feasible = solution(1.0, 1.0, 0.0);
      var infeasible = solution(0.0, 0.0, -0.1);
      Ranking<DoubleSolution> ranking = new StochasticRanking<DoubleSolution>(0.0).ranking();

      // Act
      ranking.compute(List.of(infeasible, feasible));

      // Assert
      assertThat(ranking.getRank(feasible)).isZero();
      assertThat(ranking.getRank(infeasible)).isEqualTo(1);
    }

    @Test
    @DisplayName("given pf = 1, when ranking, then the Pareto rank decides")
    void givenPfOne_whenRanked_thenTheDominatingOneComesFirst() {
      // Arrange
      var feasible = solution(1.0, 1.0, 0.0);
      var infeasible = solution(0.0, 0.0, -0.1);
      Ranking<DoubleSolution> ranking = new StochasticRanking<DoubleSolution>(1.0).ranking();

      // Act
      ranking.compute(List.of(feasible, infeasible));

      // Assert
      assertThat(ranking.getRank(infeasible)).isZero();
      assertThat(ranking.getRank(feasible)).isEqualTo(1);
    }

    @Test
    @DisplayName("given feasible non-dominated solutions, when ranking, then they share a subfront")
    void givenFeasibleNonDominatedSolutions_whenRanked_thenTheyShareASubfront() {
      // Arrange
      List<DoubleSolution> population =
          List.of(solution(0.0, 1.0, 0.0), solution(1.0, 0.0, 0.0), solution(0.5, 0.5, -1.0));
      Ranking<DoubleSolution> ranking = new StochasticRanking<DoubleSolution>(0.0).ranking();

      // Act
      ranking.compute(population);

      // Assert
      assertThat(ranking.getNumberOfSubFronts()).isEqualTo(2);
      assertThat(ranking.getSubFront(0)).hasSize(2);
    }

    @Test
    @DisplayName("given its comparator, then it is the constrained-domination principle")
    void givenItsComparator_whenComparing_thenItIsTheConstrainedDominationPrinciple() {
      // Act
      int result =
          new StochasticRanking<DoubleSolution>(1.0)
              .comparator()
              .compare(solution(0.0, 0.0, -0.1), solution(1.0, 1.0, 0.0));

      // Assert
      assertThat(result).isPositive();
    }
  }

  @Nested
  @DisplayName("When using the self-adaptive penalty function")
  class WhenUsingTheSelfAdaptivePenaltyFunction {

    @Test
    @DisplayName("given no feasible solutions, when ranking, then the less violated comes first")
    void givenNoFeasibleSolutions_whenRanked_thenTheLessViolatedOneComesFirst() {
      // Arrange
      var dominating = solution(0.0, 0.0, -2.0);
      var lessViolated = solution(1.0, 1.0, -1.0);
      Ranking<DoubleSolution> ranking = new SelfAdaptivePenalty<DoubleSolution>().ranking();

      // Act
      ranking.compute(List.of(dominating, lessViolated));

      // Assert
      assertThat(ranking.getRank(lessViolated)).isZero();
      assertThat(ranking.getRank(dominating)).isEqualTo(1);
    }

    @Test
    @DisplayName("given a slightly infeasible solution, then it ranks before a feasible one")
    void givenASlightlyInfeasibleSolution_whenRanked_thenItComesBeforeTheFeasibleOne() {
      // Arrange: the violation of 0.1 is 0.01 of the largest one in the population
      var feasible = solution(1.0, 1.0, 0.0);
      var slightlyInfeasible = solution(0.0, 0.0, -0.1);
      var veryInfeasible = solution(1.0, 1.0, -10.0);
      Ranking<DoubleSolution> ranking = new SelfAdaptivePenalty<DoubleSolution>().ranking();

      // Act
      ranking.compute(List.of(feasible, slightlyInfeasible, veryInfeasible));

      // Assert
      assertThat(ranking.getRank(slightlyInfeasible)).isZero();
      assertThat(ranking.getRank(feasible)).isEqualTo(1);
      assertThat(ranking.getRank(veryInfeasible)).isEqualTo(2);
    }

    @Test
    @DisplayName("given no constraints, when ranking, then it is Pareto dominance")
    void givenNoConstraints_whenRanked_thenItIsParetoDominance() {
      // Arrange
      List<DoubleSolution> population =
          List.of(solution(0.0, 1.0), solution(1.0, 0.0), solution(1.0, 1.0));
      Ranking<DoubleSolution> ranking = new SelfAdaptivePenalty<DoubleSolution>().ranking();

      // Act
      ranking.compute(population);

      // Assert
      assertThat(ranking.getNumberOfSubFronts()).isEqualTo(2);
      assertThat(ranking.getSubFront(0)).hasSize(2);
      assertThat(ranking.getRank(population.get(2))).isEqualTo(1);
    }
  }
}
