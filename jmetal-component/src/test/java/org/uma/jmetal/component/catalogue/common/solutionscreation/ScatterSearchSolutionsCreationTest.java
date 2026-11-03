package org.uma.jmetal.component.catalogue.common.solutionscreation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.ScatterSearchSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for ScatterSearchSolutionsCreation")
class ScatterSearchSolutionsCreationTest {
  private static final DoubleProblem PROBLEM = new ZDT1(1);

  private static List<DoubleSolution> create(long seed, int numberOfSolutions, int subRanges) {
    // The first values of the generators with consecutive small seeds are correlated
    JMetalRandom.getInstance().setSeed(seed * 104729L + 12345L);

    return new ScatterSearchSolutionsCreation(PROBLEM, numberOfSolutions, subRanges).create();
  }

  @ParameterizedTest(name = "seed {0}")
  @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20})
  @DisplayName("given two sub-ranges, when two solutions are created, then they are in different sub-ranges")
  void givenTwoSubRanges_whenTwoSolutionsAreCreated_thenTheyAreInDifferentSubRanges(long seed) {
    List<DoubleSolution> solutions = create(seed, 2, 2);

    boolean firstInTheLowerHalf = solutions.get(0).variables().get(0) < 0.5;
    boolean secondInTheLowerHalf = solutions.get(1).variables().get(0) < 0.5;
    assertThat(firstInTheLowerHalf).isNotEqualTo(secondInTheLowerHalf);
  }

  @Test
  @DisplayName("given one solution in each of two sub-ranges, when the next one is created, then both sub-ranges have the same probability")
  void givenOneSolutionInEachSubRange_whenTheNextIsCreated_thenBothHaveTheSameProbability() {
    // After two solutions the reverse frequencies are equal, so the third solution is in the
    // lower half with probability 1/2; with the sub-range chosen by value > frequency instead of
    // value >= frequency the probability is one
    long inTheLowerHalf =
        IntStream.range(0, 400)
            .filter(seed -> create(seed, 3, 2).get(2).variables().get(0) < 0.5)
            .count();

    assertThat(inTheLowerHalf / 400.0).isBetween(0.4, 0.6);
  }

  @Test
  @DisplayName("given as many sub-ranges as solutions, when created, then the sub-ranges are used more evenly than by chance")
  void givenFourSubRanges_whenFourSolutionsAreCreated_thenTheFirstTwoAreInDifferentSubRanges() {
    for (long seed = 1; seed <= 20; seed++) {
      List<DoubleSolution> solutions = create(seed, 2, 4);

      int first = (int) (solutions.get(0).variables().get(0) * 4);
      int second = (int) (solutions.get(1).variables().get(0) * 4);
      assertThat(second).as("seed %d", seed).isNotEqualTo(first);
    }
  }
}
