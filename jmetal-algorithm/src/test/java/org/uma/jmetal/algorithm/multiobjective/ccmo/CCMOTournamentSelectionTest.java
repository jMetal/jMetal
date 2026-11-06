package org.uma.jmetal.algorithm.multiobjective.ccmo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

@DisplayName("Unit tests for class CCMOTournamentSelection")
class CCMOTournamentSelectionTest {
  private final double[] fitness = {0.5, 0.2, 0.2};

  @Nested
  @DisplayName("When deciding a tournament")
  class WhenDecidingATournament {

    @Test
    @DisplayName("given different fitness values, when deciding, then the lower one wins")
    void givenDifferentFitnessValues_whenDeciding_thenTheLowerOneWins() {
      // Act and Assert
      assertEquals(1, CCMOTournamentSelection.winner(0, 1, fitness));
      assertEquals(1, CCMOTournamentSelection.winner(1, 0, fitness));
    }

    @Test
    @DisplayName("given a tie, when deciding, then the first one in the population wins")
    void givenATie_whenDeciding_thenTheFirstOneInThePopulationWins() {
      // Act and Assert
      assertEquals(1, CCMOTournamentSelection.winner(1, 2, fitness));
      assertEquals(1, CCMOTournamentSelection.winner(2, 1, fitness));
      assertEquals(2, CCMOTournamentSelection.winner(2, 2, fitness));
    }
  }

  @Nested
  @DisplayName("When running tournaments")
  class WhenRunningTournaments {
    private PseudoRandomGenerator originalGenerator;
    private PseudoRandomGenerator generator;

    @BeforeEach
    void setUp() {
      originalGenerator = JMetalRandom.getInstance().getRandomGenerator();
      generator = mock(PseudoRandomGenerator.class);
      JMetalRandom.getInstance().setRandomGenerator(generator);
    }

    @AfterEach
    void tearDown() {
      JMetalRandom.getInstance().setRandomGenerator(originalGenerator);
    }

    @Test
    @DisplayName(
        "given random candidates, when running tournaments, then each one returns the winner of"
            + " two candidates drawn with replacement")
    void givenRandomCandidates_whenRunningTournaments_thenEachOneReturnsTheWinner() {
      // Arrange
      when(generator.nextInt(0, 2)).thenReturn(0, 1, 2, 2, 2, 1);

      // Act
      int[] winners = CCMOTournamentSelection.execute(fitness, 3);

      // Assert
      assertArrayEquals(new int[] {1, 2, 1}, winners);
    }
  }
}
