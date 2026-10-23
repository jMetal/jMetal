package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.FeasibilityRulesCriterion;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.ImprovedEpsilonCriterion;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.problem.multiobjective.Tanaka;
import org.uma.jmetal.problem.multiobjective.cf.CF10;
import org.uma.jmetal.problem.multiobjective.cf.CF4;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.BestSolutionsArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle;

/**
 * Integration tests for MOEA/D with the subproblem update criteria that handle constraints.
 *
 * <p>The result of a single run depends on the seed: on CF4 and CF10 the normalized hypervolume
 * of a run is zero for many seeds, because its front does not enter the box of the reference
 * front, so a threshold on one run fails by chance whenever the random numbers change. Each test
 * runs the algorithm with several seeds and checks the median of the runs: of the normalized IGD+
 * (which, unlike the hypervolume, does not saturate) and of the feasibility ratio, and that some
 * run gets a good hypervolume.
 */
@DisplayName("Integration tests for MOEA/D with constraint handling subproblem update criteria")
class ConstrainedMOEADBuilderIT {
  private static final String WEIGHT_VECTOR_DIRECTORY = "../resources/weightVectorFiles/moead";
  private static final String REFERENCE_FRONTS_DIRECTORY = "../resources/referenceFronts/";
  private static final String REFERENCE_FRONTS_RSG_DIRECTORY = "../resources/referenceFrontsRSG/";

  private static final long[] SEEDS = LongStream.rangeClosed(1, 15).toArray();

  /** The quality of the front of a run, normalized with the bounds of the reference front. */
  private record Run(double hypervolume, double igdPlus, double feasibilityRatio, int size) {}

  /** The runs of the algorithm with each of the seeds. */
  private record Runs(List<Run> runs) {
    double median(ToDoubleFunction<Run> value) {
      double[] values = runs.stream().mapToDouble(value).sorted().toArray();
      return values[values.length / 2];
    }

    double best(ToDoubleFunction<Run> value) {
      return runs.stream().mapToDouble(value).max().orElseThrow();
    }

    long count(Predicate<Run> condition) {
      return runs.stream().filter(condition).count();
    }

    @Override
    public String toString() {
      return runs.toString();
    }
  }

  private static Runs run(Supplier<List<DoubleSolution>> algorithm, String referenceFrontFile)
      throws IOException {
    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile, ",");
    double[] minimum = NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront);
    double[] maximum = NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront);
    double[][] normalizedReferenceFront =
        NormalizeUtils.normalize(referenceFront, minimum, maximum);

    List<Run> runs = new ArrayList<>();
    for (long seed : SEEDS) {
      JMetalRandom.getInstance().setSeed(seed);
      List<DoubleSolution> result = algorithm.get();

      double[][] front =
          NormalizeUtils.normalize(
              SolutionListUtils.getMatrixWithObjectiveValues(result), minimum, maximum);
      runs.add(
          new Run(
              new PISAHypervolume(normalizedReferenceFront).compute(front),
              new InvertedGenerationalDistancePlus(normalizedReferenceFront).compute(front),
              ConstraintHandling.feasibilityRatio(result),
              result.size()));
    }
    return new Runs(runs);
  }

  @Nested
  @DisplayName("When using feasibility rules")
  class WhenUsingFeasibilityRules {
    @Test
    @DisplayName("given Srinivas, when running MOEA/D, then the median front is close")
    void givenSrinivas_whenRunningMOEAD_thenTheMedianFrontIsCloseToTheReferenceFront()
        throws IOException {
      // Arrange
      Supplier<List<DoubleSolution>> moead =
          () -> {
            Problem<DoubleSolution> problem = new Srinivas();
            EvolutionaryAlgorithm<DoubleSolution> algorithm =
                new MOEADBuilder<>(
                        problem,
                        100,
                        new SBXCrossover(0.9, 20.0),
                        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
                        WEIGHT_VECTOR_DIRECTORY,
                        new RandomPermutationCycle(100),
                        false)
                    .setTermination(new TerminationByEvaluations(25000))
                    .setSubproblemUpdateCriterion(new FeasibilityRulesCriterion<>())
                    .build();
            algorithm.run();
            return algorithm.result();
          };

      // Act
      Runs runs = run(moead, REFERENCE_FRONTS_DIRECTORY + "Srinivas.csv");

      // Assert
      assertThat(runs.runs()).as(runs.toString()).allMatch(r -> r.size() > 0);
      assertThat(runs.median(Run::feasibilityRatio)).as(runs.toString()).isEqualTo(1.0);
      assertThat(runs.median(Run::igdPlus)).as(runs.toString()).isLessThan(0.02);
      assertThat(runs.median(Run::hypervolume)).as(runs.toString()).isGreaterThan(0.50);
    }

    @Test
    @DisplayName("given CF4, when running MOEA/D with an archive, then the median front is close")
    void givenCF4_whenRunningMOEADWithAnArchive_thenTheMedianFrontIsFeasibleAndClose()
        throws IOException {
      // Arrange
      Supplier<List<DoubleSolution>> moead =
          () -> {
            Problem<DoubleSolution> problem = new CF4();
            Archive<DoubleSolution> archive =
                new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), 91);
            EvolutionaryAlgorithm<DoubleSolution> algorithm =
                new MOEADBuilder<>(
                        problem,
                        91,
                        new SBXCrossover(0.9, 20.0),
                        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
                        WEIGHT_VECTOR_DIRECTORY,
                        new RandomPermutationCycle(91),
                        false)
                    .setTermination(new TerminationByEvaluations(30000))
                    .setSubproblemUpdateCriterion(new FeasibilityRulesCriterion<>())
                    .setEvaluation(new SequentialEvaluationWithArchive<>(problem, archive))
                    .build();
            algorithm.run();
            return archive.solutions();
          };

      // Act
      Runs runs = run(moead, REFERENCE_FRONTS_RSG_DIRECTORY + "CF4.csv");

      // Assert
      assertThat(runs.runs()).as(runs.toString()).allMatch(r -> r.size() > 0);
      assertThat(runs.median(Run::feasibilityRatio)).as(runs.toString()).isEqualTo(1.0);
      assertThat(runs.median(Run::igdPlus)).as(runs.toString()).isLessThan(0.5);
      assertThat(runs.best(Run::hypervolume)).as(runs.toString()).isGreaterThan(0.12);
    }
  }

  @Nested
  @DisplayName("When using the improved epsilon criterion")
  class WhenUsingTheImprovedEpsilonCriterion {
    @Test
    @DisplayName("given Tanaka, when running MOEA/D-DE, then the median front is close")
    void givenTanaka_whenRunningMOEADDE_thenTheMedianFrontIsCloseToTheReferenceFront()
        throws IOException {
      // Arrange
      int evaluations = 30000;
      Supplier<List<DoubleSolution>> moeadDE =
          () -> {
            Problem<DoubleSolution> problem = new Tanaka();
            EvolutionaryAlgorithm<DoubleSolution> algorithm =
                new MOEADDEBuilder(
                        problem,
                        100,
                        1.0,
                        0.5,
                        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
                        WEIGHT_VECTOR_DIRECTORY,
                        new RandomPermutationCycle(100),
                        false)
                    .setTermination(new TerminationByEvaluations(evaluations))
                    .setSubproblemUpdateCriterion(
                        new ImprovedEpsilonCriterion<>((int) (0.8 * evaluations / 100)))
                    .build();
            algorithm.run();
            return algorithm.result();
          };

      // Act
      Runs runs = run(moeadDE, REFERENCE_FRONTS_DIRECTORY + "Tanaka.csv");

      // Assert
      assertThat(runs.runs()).as(runs.toString()).allMatch(r -> r.size() > 0);
      assertThat(runs.median(Run::feasibilityRatio)).as(runs.toString()).isEqualTo(1.0);
      assertThat(runs.median(Run::igdPlus)).as(runs.toString()).isLessThan(0.02);
      assertThat(runs.median(Run::hypervolume)).as(runs.toString()).isGreaterThan(0.28);
    }

    @Test
    @DisplayName("given CF10, when running MOEA/D-DE with an archive, then a run reaches the front")
    void givenCF10_whenRunningMOEADDEWithAnArchive_thenARunReachesTheFront()
        throws IOException {
      // Arrange
      int evaluations = 50000;
      Supplier<List<DoubleSolution>> moeadDE =
          () -> {
            Problem<DoubleSolution> problem = new CF10();
            Archive<DoubleSolution> archive =
                new BestSolutionsArchive<>(new NonDominatedSolutionListArchive<>(), 91);
            EvolutionaryAlgorithm<DoubleSolution> algorithm =
                new MOEADDEBuilder(
                        problem,
                        91,
                        1.0,
                        0.5,
                        new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
                        WEIGHT_VECTOR_DIRECTORY,
                        new RandomPermutationCycle(91),
                        false)
                    .setTermination(new TerminationByEvaluations(evaluations))
                    .setSubproblemUpdateCriterion(
                        new ImprovedEpsilonCriterion<>((int) (0.8 * evaluations / 91)))
                    .setEvaluation(new SequentialEvaluationWithArchive<>(problem, archive))
                    .build();
            algorithm.run();
            return archive.solutions();
          };

      // Act
      Runs runs = run(moeadDE, REFERENCE_FRONTS_RSG_DIRECTORY + "CF10.csv");

      // Assert
      assertThat(runs.runs()).as(runs.toString()).allMatch(r -> r.size() > 0);
      assertThat(runs.median(Run::feasibilityRatio)).as(runs.toString()).isEqualTo(1.0);
      assertThat(runs.median(Run::igdPlus)).as(runs.toString()).isLessThan(0.75);
      assertThat(runs.best(Run::hypervolume)).as(runs.toString()).isGreaterThan(0.20);
      // The runs either reach the front (IGD+ about 0.02) or stay on a local front (0.1 or more)
      assertThat(runs.count(r -> r.igdPlus() < 0.05)).as(runs.toString()).isPositive();
    }
  }
}
