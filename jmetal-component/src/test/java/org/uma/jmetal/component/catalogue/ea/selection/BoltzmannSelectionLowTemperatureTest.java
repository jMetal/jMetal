package org.uma.jmetal.component.catalogue.ea.selection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.ea.selection.impl.BoltzmannSelection;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("BoltzmannSelection with low temperatures and large populations")
class BoltzmannSelectionLowTemperatureTest {
  private static final Comparator<DoubleSolution> BY_FIRST_OBJECTIVE =
      Comparator.comparingDouble(s -> s.objectives()[0]);

  private static List<DoubleSolution> population(int size) {
    List<DoubleSolution> population = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      DoubleSolution solution = new FakeDoubleProblem(2, 2, 0).createSolution();
      solution.objectives()[0] = i;
      population.add(solution);
    }
    return population;
  }

  @Test
  @DisplayName("given a temperature close to zero, when selecting, then the best solution is always chosen")
  void givenAVeryLowTemperature_whenSelecting_thenTheBestSolutionIsAlwaysChosen() {
    JMetalRandom.getInstance().setSeed(1);
    List<DoubleSolution> population = population(5);
    var selection = new BoltzmannSelection<DoubleSolution>(200, BY_FIRST_OBJECTIVE, 0.001);

    List<DoubleSolution> matingPool = selection.select(population);

    // The probability of the second best is exp(-1000) times the one of the best
    assertThat(matingPool).hasSize(200).allMatch(s -> s == population.get(0));
  }

  @Test
  @DisplayName("given a population larger than 709 times the temperature, when selecting, then the best ones are preferred and no value is NaN")
  void givenALargePopulation_whenSelecting_thenTheBestOnesArePreferred() {
    JMetalRandom.getInstance().setSeed(1);
    List<DoubleSolution> population = population(1000);
    var selection = new BoltzmannSelection<DoubleSolution>(500, BY_FIRST_OBJECTIVE, 0.5);

    List<DoubleSolution> matingPool = selection.select(population);

    // The probability of ranking i is proportional to exp(-i / 0.5): almost all of them are in the
    // first ten
    long inTheFirstTen =
        matingPool.stream().filter(s -> s.objectives()[0] < 10).count();
    assertThat(inTheFirstTen).isEqualTo(500);
    assertThat(IntStream.range(0, matingPool.size()).map(i -> (int) matingPool.get(i).objectives()[0]).max().orElse(-1))
        .isLessThan(10);
  }
}
