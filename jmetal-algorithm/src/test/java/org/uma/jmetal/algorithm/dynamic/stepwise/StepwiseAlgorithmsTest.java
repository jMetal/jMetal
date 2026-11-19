package org.uma.jmetal.algorithm.dynamic.stepwise;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.uma.jmetal.algorithm.dynamic.InteractiveStepwiseAlgorithm;
import org.uma.jmetal.algorithm.dynamic.StepwiseAlgorithm;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.df.DF1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetal.util.restartstrategy.impl.DefaultRestartStrategy;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNRandomSolutions;

@DisplayName("Unit tests for the stepwise versions of NSGA-II, R-NSGA-II, WASF-GA and SMPSO/RP")
class StepwiseAlgorithmsTest {
  private static final int POPULATION_SIZE = 20;
  private static final List<Double> POINT = List.of(0.2, 0.6);

  private static StepwiseNSGAII<DoubleSolution> nsgaii(DF1 problem) {
    return new StepwiseNSGAII<>(
        problem, POPULATION_SIZE, POPULATION_SIZE, POPULATION_SIZE, new SBXCrossover(0.9, 20.0),
        new PolynomialMutation(0.1, 20.0), new BinaryTournamentSelection<>(),
        new SequentialSolutionListEvaluator<>());
  }

  private static StepwiseRNSGAII<DoubleSolution> rnsgaii(DF1 problem) {
    return new StepwiseRNSGAII<>(
        problem, POPULATION_SIZE, POPULATION_SIZE, POPULATION_SIZE, new SBXCrossover(0.9, 20.0),
        new PolynomialMutation(0.1, 20.0),
        new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()),
        new SequentialSolutionListEvaluator<>(), List.of(POINT), 0.001);
  }

  private static StepwiseWASFGA<DoubleSolution> wasfga(DF1 problem) {
    return new StepwiseWASFGA<>(
        problem, POPULATION_SIZE, new SBXCrossover(0.9, 20.0), new PolynomialMutation(0.1, 20.0),
        new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()),
        new SequentialSolutionListEvaluator<>(), 0.01, POINT);
  }

  private static StepwiseSMPSORP smpsorp(DF1 problem) {
    List<ArchiveWithReferencePoint<DoubleSolution>> archives = new ArrayList<>();
    archives.add(new CrowdingDistanceArchiveWithReferencePoint<>(POPULATION_SIZE, POINT));
    return new StepwiseSMPSORP(
        problem, POPULATION_SIZE, archives, List.of(POINT), new PolynomialMutation(0.1, 20.0), 250,
        0.0, 1.0, 0.0, 1.0, 2.5, 1.5, 2.5, 1.5, 0.1, 0.1, -1.0, -1.0,
        new DefaultDominanceComparator<>(), new SequentialSolutionListEvaluator<>());
  }

  static Stream<Arguments> algorithms() {
    return Stream.of(
        Arguments.of("NSGA-II", (Function<DF1, StepwiseAlgorithm<DoubleSolution>>) StepwiseAlgorithmsTest::nsgaii),
        Arguments.of("R-NSGA-II", (Function<DF1, StepwiseAlgorithm<DoubleSolution>>) StepwiseAlgorithmsTest::rnsgaii),
        Arguments.of("WASF-GA", (Function<DF1, StepwiseAlgorithm<DoubleSolution>>) StepwiseAlgorithmsTest::wasfga),
        Arguments.of("SMPSO/RP", (Function<DF1, StepwiseAlgorithm<DoubleSolution>>) StepwiseAlgorithmsTest::smpsorp));
  }

  private DF1 problem;

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(1);
    problem = new DF1(10, 1, 10);
  }

  /** True if the objectives of the solution are those of the problem at its current time. */
  private boolean isEvaluatedWithTheCurrentProblem(DoubleSolution solution) {
    DoubleSolution copy = (DoubleSolution) solution.copy();
    problem.evaluate(copy);
    return Arrays.equals(copy.objectives(), solution.objectives());
  }

  @Nested
  @DisplayName("When it runs step by step")
  class WhenItRunsStepByStep {
    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.algorithm.dynamic.stepwise.StepwiseAlgorithmsTest#algorithms")
    @DisplayName("given an algorithm, then each step keeps the population size and evaluates a population")
    void givenAnAlgorithm_thenEachStepKeepsThePopulationSize(
        String name, Function<DF1, StepwiseAlgorithm<DoubleSolution>> factory) {
      StepwiseAlgorithm<DoubleSolution> algorithm = factory.apply(problem);

      assertThat(algorithm.initialize()).isEqualTo(POPULATION_SIZE);
      for (int i = 0; i < 5; i++) {
        assertThat(algorithm.step()).isEqualTo(POPULATION_SIZE);
        assertThat(algorithm.population()).hasSize(POPULATION_SIZE);
      }
      assertThat(algorithm.result()).isNotEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("org.uma.jmetal.algorithm.dynamic.stepwise.StepwiseAlgorithmsTest#algorithms")
    @DisplayName("given the problem changes, when the search is restarted, then the whole population is evaluated with the new problem")
    void givenTheProblemChanges_whenTheSearchIsRestarted_thenThePopulationIsEvaluatedAgain(
        String name, Function<DF1, StepwiseAlgorithm<DoubleSolution>> factory) {
      StepwiseAlgorithm<DoubleSolution> algorithm = factory.apply(problem);
      algorithm.initialize();
      algorithm.step();
      problem.update(5);

      int evaluations =
          algorithm.restart(
              new DefaultRestartStrategy<>(
                  new RemoveNRandomSolutions<>(POPULATION_SIZE / 2), new CreateNRandomSolutions<>()),
              problem);

      assertThat(evaluations).isEqualTo(POPULATION_SIZE);
      assertThat(algorithm.population())
          .hasSize(POPULATION_SIZE)
          .allMatch(StepwiseAlgorithmsTest.this::isEvaluatedWithTheCurrentProblem);
      assertThat(algorithm.result())
          .allMatch(StepwiseAlgorithmsTest.this::isEvaluatedWithTheCurrentProblem);
      assertThat(algorithm.step()).isEqualTo(POPULATION_SIZE);
    }
  }

  @Nested
  @DisplayName("When the reference points change")
  class WhenTheReferencePointsChange {
    @Test
    @DisplayName("given R-NSGA-II, then it accepts several points")
    void givenRNSGAII_thenItAcceptsSeveralPoints() {
      InteractiveStepwiseAlgorithm<DoubleSolution> algorithm = rnsgaii(problem);
      List<List<Double>> points = List.of(List.of(0.1, 0.1), List.of(0.5, 0.5));

      algorithm.updateReferencePoints(points);

      assertThat(algorithm.referencePoints()).isEqualTo(points);
    }

    @Test
    @DisplayName("given WASF-GA, then it accepts one point and rejects two")
    void givenWASFGA_thenItAcceptsOnePointAndRejectsTwo() {
      InteractiveStepwiseAlgorithm<DoubleSolution> algorithm = wasfga(problem);
      algorithm.initialize();

      algorithm.updateReferencePoints(List.of(List.of(0.5, 0.5)));

      assertThat(algorithm.referencePoints()).containsExactly(List.of(0.5, 0.5));
      assertThatThrownBy(
              () -> algorithm.updateReferencePoints(List.of(List.of(0.1, 0.1), List.of(0.5, 0.5))))
          .isInstanceOf(InvalidConditionException.class);
    }

    @Test
    @DisplayName("given SMPSO/RP with one archive, then it rejects two points")
    void givenSMPSORPWithOneArchive_thenItRejectsTwoPoints() {
      InteractiveStepwiseAlgorithm<DoubleSolution> algorithm = smpsorp(problem);

      assertThat(algorithm.acceptsNumberOfReferencePoints(1)).isTrue();
      assertThat(algorithm.acceptsNumberOfReferencePoints(2)).isFalse();
    }
  }
}
