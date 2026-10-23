package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

@DisplayName("Unit tests for the selection of the builders that rank the population")
class RankingBuildersSelectionTest {
  private static final int OFFSPRING_POPULATION_SIZE = 20;

  private static DoubleProblem problem;

  @BeforeEach
  void setUp() {
    problem = new FakeDoubleProblem(2, 2, 0);
    JMetalRandom.getInstance().setSeed(3);
  }

  /** A ranking whose rank of a solution is its first objective, without computing anything. */
  private static final class FirstObjectiveRanking implements Ranking<DoubleSolution> {
    @Override
    public Ranking<DoubleSolution> compute(List<DoubleSolution> solutionList) {
      return this;
    }

    @Override
    public List<DoubleSolution> getSubFront(int rank) {
      return List.of();
    }

    @Override
    public int getNumberOfSubFronts() {
      return 0;
    }

    @Override
    public Integer getRank(DoubleSolution solution) {
      return (int) solution.objectives()[0];
    }

    @Override
    public Object getAttributedId() {
      return FirstObjectiveRanking.class;
    }
  }

  /** A density estimator that gives every solution the same density. */
  private static final class EqualDensity implements DensityEstimator<DoubleSolution> {
    @Override
    public void compute(List<DoubleSolution> solutionSet) {}

    @Override
    public Double value(DoubleSolution solution) {
      return 0.0;
    }

    @Override
    public Comparator<DoubleSolution> comparator() {
      return (solution1, solution2) -> 0;
    }
  }

  private static DoubleSolution solutionOfRank(int rank) {
    DoubleSolution solution = problem.createSolution();
    solution.objectives()[0] = rank;
    return solution;
  }

  @Nested
  @DisplayName("When the ranking is set")
  class WhenTheRankingIsSet {
    static Stream<Arguments> builders() {
      Function<Ranking<DoubleSolution>, EvolutionaryAlgorithm<DoubleSolution>> nsgaII =
          ranking ->
              new NSGAIIBuilder<>(
                      problem,
                      10,
                      OFFSPRING_POPULATION_SIZE,
                      new SBXCrossover(0.9, 20.0),
                      new PolynomialMutation(0.5, 20.0))
                  .setDensityEstimator(new EqualDensity())
                  .setRanking(ranking)
                  .build();
      Function<Ranking<DoubleSolution>, EvolutionaryAlgorithm<DoubleSolution>> rdsmoea =
          ranking ->
              new RDSMOEABuilder<>(
                      problem,
                      10,
                      OFFSPRING_POPULATION_SIZE,
                      new SBXCrossover(0.9, 20.0),
                      new PolynomialMutation(0.5, 20.0),
                      new FastNonDominatedSortRanking<>(),
                      new EqualDensity(),
                      RankingAndDensityEstimatorReplacement.RemovalPolicy.ONE_SHOT)
                  .setRanking(ranking)
                  .build();
      return Stream.of(Arguments.of("NSGA-II", nsgaII), Arguments.of("RDSMOEA", rdsmoea));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("builders")
    @DisplayName("given another ranking, when selecting, then the tournament compares its ranks")
    void givenAnotherRanking_whenSelecting_thenTheTournamentComparesItsRanks(
        String name,
        Function<Ranking<DoubleSolution>, EvolutionaryAlgorithm<DoubleSolution>> build) {
      // Arrange
      EvolutionaryAlgorithm<DoubleSolution> algorithm = build.apply(new FirstObjectiveRanking());
      DoubleSolution best = solutionOfRank(0);
      List<DoubleSolution> population = List.of(best, solutionOfRank(1));

      // Act
      List<DoubleSolution> matingPool = algorithm.selection().select(population);

      // Assert: each binary tournament compares both solutions, so the best one always wins
      assertThat(matingPool).hasSize(OFFSPRING_POPULATION_SIZE).containsOnly(best);
    }
  }

  @Nested
  @DisplayName("When the density estimator is set after the selection")
  class WhenTheDensityEstimatorIsSetAfterTheSelection {
    @Test
    @DisplayName("given a selection of the user, when building NSGA-II, then it is kept")
    void givenASelectionOfTheUser_whenBuilding_thenItIsKept() {
      // Arrange
      var selection = new RandomSelection<DoubleSolution>(OFFSPRING_POPULATION_SIZE);

      // Act
      EvolutionaryAlgorithm<DoubleSolution> algorithm =
          new NSGAIIBuilder<>(
                  problem,
                  10,
                  OFFSPRING_POPULATION_SIZE,
                  new SBXCrossover(0.9, 20.0),
                  new PolynomialMutation(0.5, 20.0))
              .setSelection(selection)
              .setDensityEstimator(new CrowdingDistanceDensityEstimator<>())
              .build();

      // Assert
      assertThat(algorithm.selection()).isSameAs(selection);
    }
  }
}
