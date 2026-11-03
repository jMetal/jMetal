package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.qualityindicator.impl.AverageHausdorffDistance;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GeneralizedSpread;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume;

/**
 * The matrix that SolutionListUtils.getMatrixWithObjectiveValues returns shares its rows with the
 * objectives of the solutions, so an indicator that changes its input would change the solutions of
 * the algorithm.
 */
@DisplayName("The quality indicators do not modify the front that they receive")
class QualityIndicatorsDoNotModifyTheirInputTest {

  static Stream<Arguments> indicators() {
    return Stream.of(
        Arguments.of("Epsilon", (Function<double[][], QualityIndicator>) Epsilon::new),
        Arguments.of("GD", (Function<double[][], QualityIndicator>) GenerationalDistance::new),
        Arguments.of("IGD", (Function<double[][], QualityIndicator>) InvertedGenerationalDistance::new),
        Arguments.of("IGD+", (Function<double[][], QualityIndicator>) InvertedGenerationalDistancePlus::new),
        Arguments.of("AHD", (Function<double[][], QualityIndicator>) AverageHausdorffDistance::new),
        Arguments.of("Spread", (Function<double[][], QualityIndicator>) Spread::new),
        Arguments.of("GSpread", (Function<double[][], QualityIndicator>) GeneralizedSpread::new),
        Arguments.of("HV (PISA)", (Function<double[][], QualityIndicator>) PISAHypervolume::new),
        Arguments.of("HV (WFG)", (Function<double[][], QualityIndicator>) WFGHypervolume::new),
        Arguments.of("NHV", (Function<double[][], QualityIndicator>) NormalizedHypervolume::new));
  }

  private static double[][] copy(double[][] matrix) {
    return Arrays.stream(matrix).map(double[]::clone).toArray(double[][]::new);
  }

  /** A front on a line, in a random order, with points that are repeated. */
  private static double[][] front(Random random, int objectives, int points) {
    double[][] front = new double[points][objectives];
    for (int i = 0; i < points; i++) {
      double t = (double) random.nextInt(points) / points;
      for (int j = 0; j < objectives; j++) {
        front[i][j] = j % 2 == 0 ? t : 1.0 - t;
      }
    }
    return front;
  }

  @ParameterizedTest(name = "{0} with {1} objectives")
  @MethodSource("cases")
  @DisplayName("given a front and a reference front, when computed, then neither is modified")
  void givenAFrontAndAReferenceFront_whenComputed_thenNeitherIsModified(
      String name, int objectives, Function<double[][], QualityIndicator> factory) {
    Random random = new Random(42);
    double[][] reference = front(random, objectives, 30);
    double[][] approximation = front(random, objectives, 20);
    double[][] referenceBefore = copy(reference);
    double[][] approximationBefore = copy(approximation);

    factory.apply(reference).compute(approximation);

    assertThat(reference).as(name + ": reference front").isDeepEqualTo(referenceBefore);
    assertThat(approximation).as(name + ": front").isDeepEqualTo(approximationBefore);
  }

  static Stream<Arguments> cases() {
    return indicators()
        .flatMap(
            arguments ->
                Stream.of(2, 3)
                    .map(
                        objectives ->
                            Arguments.of(arguments.get()[0], objectives, arguments.get()[1])));
  }
}
