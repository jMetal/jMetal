package org.uma.jmetal.util.pseudorandom.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

@DisplayName("Unit tests for class ExtendedPseudoRandomGenerator")
class ExtendedPseudoRandomGeneratorTest {
  private PseudoRandomGenerator previousGenerator;

  @BeforeEach
  void setUp() {
    previousGenerator = JMetalRandom.getInstance().getRandomGenerator();
  }

  @AfterEach
  void tearDown() {
    JMetalRandom.getInstance().setRandomGenerator(previousGenerator);
  }

  private static List<Double> draws(PseudoRandomGenerator generator) {
    return IntStream.range(0, 10).mapToObj(i -> generator.nextDouble()).toList();
  }

  @Nested
  @DisplayName("When it draws from JMetalRandom")
  class WhenItDrawsFromJMetalRandom {
    @Test
    @DisplayName("given a generator set after creating it, when drawing, then it uses that one")
    void givenAGeneratorSetAfterCreatingIt_whenDrawing_thenItUsesThatOne() {
      // Arrange
      var extended = new ExtendedPseudoRandomGenerator();
      JMetalRandom.getInstance().setRandomGenerator(new JavaRandomGenerator(7));

      // Act
      List<Double> values = draws(extended);

      // Assert
      assertThat(values).isEqualTo(draws(new JavaRandomGenerator(7)));
      assertThat(extended.getSeed()).isEqualTo(7);
    }

    @Test
    @DisplayName("given a seed set after creating it, when drawing, then it follows the seed")
    void givenASeedSetAfterCreatingIt_whenDrawing_thenItFollowsTheSeed() {
      // Arrange
      var extended = new ExtendedPseudoRandomGenerator();
      JMetalRandom.getInstance().setSeed(11);

      // Act
      List<Double> values = draws(extended);

      // Assert
      JMetalRandom.getInstance().setSeed(11);
      assertThat(values).isEqualTo(draws(JMetalRandom.getInstance().getRandomGenerator()));
    }
  }
}
