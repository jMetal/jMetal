package org.uma.jmetal.qualityindicator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.Hypervolume3D;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume;

@DisplayName("Unit tests for the hypervolume with reference points of any sign")
class HypervolumeReferencePointSignTest {
  private static final double EPSILON = 1e-9;
  private static final String INDICATORS =
      "org.uma.jmetal.qualityindicator.HypervolumeReferencePointSignTest#indicators";

  static Stream<Arguments> indicators() {
    Function<double[], Hypervolume> wfg = WFGHypervolume::new;
    Function<double[], Hypervolume> hypervolume3D = Hypervolume3D::new;
    return Stream.of(
        Arguments.of("WFG", wfg, 3),
        Arguments.of("3D", hypervolume3D, 3),
        Arguments.of("WFG", wfg, 2),
        Arguments.of("WFG", wfg, 4));
  }

  private static double[] filled(int length, double value) {
    double[] values = new double[length];
    Arrays.fill(values, value);
    return values;
  }

  @Nested
  @DisplayName("When the reference point is built from a point")
  class WhenTheReferencePointIsBuiltFromAPoint {
    @ParameterizedTest(name = "{0}, {2} objectives")
    @MethodSource(INDICATORS)
    @DisplayName("given a negative reference point, then the box of a point is measured from it")
    void givenANegativeReferencePoint_whenComputing_thenTheBoxIsMeasuredFromIt(
        String name, Function<double[], Hypervolume> factory, int objectives) {
      // Arrange: the box between (-3, ..., -3) and (-1, ..., -1) has a side of 2
      Hypervolume hypervolume = factory.apply(filled(objectives, -1.0));

      // Act
      double value = hypervolume.compute(new double[][] {filled(objectives, -3.0)});

      // Assert
      assertThat(value).isEqualTo(Math.pow(2.0, objectives), offset(EPSILON));
    }

    @ParameterizedTest(name = "{0}, {2} objectives")
    @MethodSource(INDICATORS)
    @DisplayName("given a reference point and the same one translated, then the value is the same")
    void givenATranslation_whenComputing_thenTheValueIsTheSame(
        String name, Function<double[], Hypervolume> factory, int objectives) {
      // Arrange
      double[][] front = new double[3][objectives];
      for (int i = 0; i < 3; i++) {
        Arrays.fill(front[i], 0.5);
        front[i][0] = 0.1 + 0.3 * i;
        front[i][1] = 0.7 - 0.3 * i;
      }
      double original = factory.apply(filled(objectives, 1.0)).compute(front);

      // Act & Assert
      for (double shift : new double[] {-1.5, -10.0, 7.0}) {
        double[][] moved = new double[3][objectives];
        for (int i = 0; i < 3; i++) {
          for (int j = 0; j < objectives; j++) {
            moved[i][j] = front[i][j] + shift;
          }
        }
        double translated = factory.apply(filled(objectives, 1.0 + shift)).compute(moved);

        assertThat(translated).isEqualTo(original, offset(EPSILON));
      }
    }

    @Test
    @DisplayName("given a reference point of mixed signs, then it is recovered as it is")
    void givenAReferencePointOfMixedSigns_whenAskingForIt_thenItIsRecoveredAsItIs() {
      // Arrange
      double[] referencePoint = {-1.0, 2.0, 0.5, -0.25};

      // Act & Assert
      assertThat(new WFGHypervolume(referencePoint).getReferencePoint())
          .containsExactly(referencePoint);
    }

    @Test
    @DisplayName("given a reference point of mixed signs, then a point has the volume of its box")
    void givenAReferencePointOfMixedSigns_whenComputing_thenTheValueIsTheBox() {
      // Arrange: the box between (-3, 1, 0) and (-1, 2, 0.5) has the volume 2 * 1 * 0.5
      double[] referencePoint = {-1.0, 2.0, 0.5};
      double[][] front = {{-3.0, 1.0, 0.0}};

      // Act & Assert
      assertThat(new Hypervolume3D(referencePoint).compute(front))
          .isEqualTo(1.0, offset(EPSILON));
      assertThat(new WFGHypervolume(referencePoint).compute(front))
          .isEqualTo(1.0, offset(EPSILON));
    }
  }

  @Nested
  @DisplayName("When computing the contributions of three objectives")
  class WhenComputingTheContributions {
    @Test
    @DisplayName("given a negative reference point, then the contributions are those of the box")
    void givenANegativeReferencePoint_whenComputing_thenTheContributionsAreThoseOfTheBox() {
      // Arrange: with the points shifted by -10, the reference point (2, 2, 2) is (-8, -8, -8)
      double[][] front = {{-9.0, -8.5, -8.5}, {-9.0, -9.0, -9.0}};
      var hypervolume = new Hypervolume3D(new double[] {-8.0, -8.0, -8.0});

      // Act
      double[] contributions = hypervolume.computeContributions(front);

      // Assert: the second point dominates the first one, and the hypervolumes are 1 and 0.25
      assertThat(contributions[0]).isEqualTo(0.0, offset(EPSILON));
      assertThat(contributions[1]).isEqualTo(0.75, offset(EPSILON));
    }
  }
}
