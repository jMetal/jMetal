package org.uma.jmetal.algorithm.singleobjective;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization.CoralReefsOptimization;
import org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization.CoralReefsOptimizationBuilder;
import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.CovarianceMatrixAdaptationEvolutionStrategy;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Reproducibility of single-objective algorithms with JMetalRandom's seed")
class ReproducibilityTest {

  private static <T> T runWithSeed(long seed, Supplier<T> run) {
    JMetalRandom.getInstance().setSeed(seed);
    return run.get();
  }

  @Nested
  @DisplayName("When running CovarianceMatrixAdaptationEvolutionStrategy")
  class WhenRunningCovarianceMatrixAdaptationEvolutionStrategy {

    private List<Double> run() {
      CovarianceMatrixAdaptationEvolutionStrategy algorithm =
          new CovarianceMatrixAdaptationEvolutionStrategy.Builder(new Sphere(10))
              .setMaxEvaluations(1000)
              .build();
      algorithm.run();
      DoubleSolution best = algorithm.result();
      return best.variables();
    }

    @Test
    @DisplayName("given the same seed, when running twice, then the results are equal")
    void givenTheSameSeed_whenRunningTwice_thenTheResultsAreEqual() {
      // Act
      List<Double> first = runWithSeed(1, this::run);
      List<Double> second = runWithSeed(1, this::run);

      // Assert
      assertEquals(first, second);
    }
  }

  @Nested
  @DisplayName("When running CoralReefsOptimization")
  class WhenRunningCoralReefsOptimization {

    private List<String> run() {
      OneMax problem = new OneMax(64);
      CoralReefsOptimization<BinarySolution> algorithm =
          new CoralReefsOptimizationBuilder<BinarySolution>(
                  problem,
                  new NaryTournamentSelection<>(1, new ObjectiveComparator<>(0)),
                  new SinglePointCrossover(0.9),
                  new BitFlipMutation(1.0 / 64))
              .setM(10)
              .setN(10)
              .setRho(0.6)
              .setFbs(0.9)
              .setFbr(0.1)
              .setFa(0.1)
              .setPd(0.1)
              .setAttemptsToSettle(3)
              .setComparator(new ObjectiveComparator<>(0))
              .setMaxEvaluations(20)
              .build();
      algorithm.run();
      return algorithm.result().stream().map(s -> s.variables().toString()).toList();
    }

    @Test
    @DisplayName("given the same seed, when running twice, then the results are equal")
    void givenTheSameSeed_whenRunningTwice_thenTheResultsAreEqual() {
      // Act
      List<String> first = runWithSeed(1, this::run);
      List<String> second = runWithSeed(1, this::run);

      // Assert
      assertEquals(first, second);
    }
  }
}
