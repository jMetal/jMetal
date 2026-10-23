package org.uma.jmetal.component.catalogue.pso.velocityupdate.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

@DisplayName("Unit tests for class SPS2011VelocityUpdate")
class SPS2011VelocityUpdateTest {
  @Nested
  @DisplayName("When creating it")
  class WhenCreatingIt {
    @Test
    @DisplayName("given a seeded generator, when creating it, then JMetalRandom keeps the generator")
    void givenASeededGenerator_whenCreatingIt_thenJMetalRandomKeepsTheGenerator() {
      // Arrange
      PseudoRandomGenerator previous = JMetalRandom.getInstance().getRandomGenerator();
      var generator = new JavaRandomGenerator(42);
      JMetalRandom.getInstance().setRandomGenerator(generator);

      try {
        // Act
        var velocityUpdate = new SPS2011VelocityUpdate(1.0, 2.0, 1.0, 2.0, new ZDT1());

        // Assert
        assertThat(JMetalRandom.getInstance().getRandomGenerator()).isSameAs(generator);
        assertThat(JMetalRandom.getInstance().getSeed()).isEqualTo(42);
        assertThat(velocityUpdate.randomGenerator.getSeed()).isEqualTo(42);
      } finally {
        JMetalRandom.getInstance().setRandomGenerator(previous);
      }
    }
  }
}
