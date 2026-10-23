package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.constrainthandling.ConstrainedDominance;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.constrainthandling.PairwiseConstraintHandlingTechnique;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;

@DisplayName("Unit tests for the constraint-handling technique of the builders")
class ConstraintHandlingTechniqueBuildersTest {
  private static final int EVALUATIONS = 800;

  /** CDP that records its updates and the rankings it gives. */
  private static final class RecordingTechnique extends ConstrainedDominance<DoubleSolution> {
    private final List<Double> progresses = new ArrayList<>();
    private final List<Integer> sizes = new ArrayList<>();
    private int rankings;

    @Override
    public void update(List<DoubleSolution> population, double progress) {
      progresses.add(progress);
      sizes.add(population.size());
    }

    @Override
    public Ranking<DoubleSolution> ranking() {
      rankings++;
      return super.ranking();
    }
  }

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(5);
  }

  private static void assertTheTechniqueFollowsTheRun(RecordingTechnique technique) {
    assertThat(technique.progresses)
        .isNotEmpty()
        .allSatisfy(progress -> assertThat(progress).isBetween(0.0, 1.0));
    assertThat(technique.progresses).isSorted();
    assertThat(technique.progresses.getLast()).isGreaterThan(0.5);
    assertThat(technique.sizes).allSatisfy(size -> assertThat(size).isGreaterThan(1));
  }

  @Nested
  @DisplayName("When the algorithm ranks its population")
  class WhenTheAlgorithmRanksItsPopulation {
    static Stream<Arguments> algorithms() {
      return ConstraintHandlingBuilders.rankingAlgorithms(EVALUATIONS).entrySet().stream()
          .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @DisplayName("given a technique, when running, then it ranks and follows the run")
    void givenATechnique_whenRunning_thenItRanksAndFollowsTheRun(
        String name,
        BiFunction<DoubleProblem, ConstraintHandlingTechnique<DoubleSolution>,
            Algorithm<List<DoubleSolution>>> build) {
      // Arrange
      var technique = new RecordingTechnique();
      Algorithm<List<DoubleSolution>> algorithm = build.apply(new Srinivas(), technique);

      // Act
      algorithm.run();

      // Assert: the ranking of the technique is created once, for the selection and replacement
      assertThat(technique.rankings).isEqualTo(1);
      assertTheTechniqueFollowsTheRun(technique);
    }
  }

  @Nested
  @DisplayName("When the algorithm compares pairs of solutions")
  class WhenTheAlgorithmComparesPairsOfSolutions {
    static Stream<Arguments> algorithms() {
      return ConstraintHandlingBuilders.pairwiseAlgorithms(EVALUATIONS).entrySet().stream()
          .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("algorithms")
    @DisplayName("given a pairwise technique, when running, then it follows the run")
    void givenAPairwiseTechnique_whenRunning_thenItFollowsTheRun(
        String name,
        BiFunction<DoubleProblem, PairwiseConstraintHandlingTechnique<DoubleSolution>,
            Algorithm<List<DoubleSolution>>> build) {
      // Arrange
      var technique = new RecordingTechnique();
      Algorithm<List<DoubleSolution>> algorithm = build.apply(new Srinivas(), technique);

      // Act
      algorithm.run();

      // Assert
      assertTheTechniqueFollowsTheRun(technique);
    }
  }
}
