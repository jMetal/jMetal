package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;

@DisplayName("Unit tests for class CMOEAMSSelection")
class CMOEAMSSelectionTest {
  /** Four vectors at 0, 10, 45 and 90 degrees: the second is the closest to another one. */
  private static final double[][] FOUR_DIRECTIONS = {
    {1.0, 0.0},
    {Math.cos(Math.toRadians(10.0)), Math.sin(Math.toRadians(10.0))},
    {1.0, 1.0},
    {0.0, 2.0}
  };

  @Nested
  @DisplayName("When selecting the next population")
  class WhenSelectingTheNextPopulation {
    @Test
    @DisplayName("given fewer non-dominated solutions than N, then those of lowest fitness are"
        + " added")
    void givenFewerNonDominatedSolutionsThanN_whenSelecting_thenThoseOfLowestFitnessAreAdded() {
      // Arrange
      double[] fitness = {0.4, 2.3, 1.5, 0.3, 3.1};
      double[][] objectives = new double[5][2];

      // Act
      int[] selected = CMOEAMSSelection.environmentalSelection(fitness, objectives, 3);

      // Assert
      assertArrayEquals(new int[] {0, 2, 3}, selected);
    }

    @Test
    @DisplayName("given ties and NaN in the fitness, then the first solutions win the ties and NaN"
        + " goes last")
    void givenTiesAndNaN_whenSelecting_thenTheFirstSolutionsWinAndNaNGoesLast() {
      // Arrange
      double[] fitness = {Double.NaN, 1.5, 1.5, 0.2};
      double[][] objectives = new double[4][2];

      // Act
      int[] selected = CMOEAMSSelection.environmentalSelection(fitness, objectives, 3);

      // Assert
      assertArrayEquals(new int[] {1, 2, 3}, selected);
    }

    @Test
    @DisplayName("given exactly N non-dominated solutions, then they are selected")
    void givenExactlyNNonDominatedSolutions_whenSelecting_thenTheyAreSelected() {
      // Arrange
      double[] fitness = {0.4, 2.3, 0.2, 0.3};
      double[][] objectives = new double[4][2];

      // Act
      int[] selected = CMOEAMSSelection.environmentalSelection(fitness, objectives, 3);

      // Assert
      assertArrayEquals(new int[] {0, 2, 3}, selected);
    }

    @Test
    @DisplayName("given more non-dominated solutions than N, then they are truncated by cosine"
        + " distance")
    void givenMoreNonDominatedSolutionsThanN_whenSelecting_thenTheyAreTruncated() {
      // Arrange
      double[] fitness = {0.4, 0.45, 0.3, 0.2};

      // Act
      int[] three = CMOEAMSSelection.environmentalSelection(fitness, FOUR_DIRECTIONS, 3);
      int[] two = CMOEAMSSelection.environmentalSelection(fitness, FOUR_DIRECTIONS, 2);

      // Assert
      assertArrayEquals(new int[] {0, 2, 3}, three);
      assertArrayEquals(new int[] {0, 3}, two);
    }

    @Test
    @DisplayName("given more non-dominated solutions than N, then the dominated ones do not take"
        + " part in the truncation")
    void givenMoreNonDominatedSolutionsThanN_whenSelecting_thenTheDominatedOnesDoNotTakePart() {
      // Arrange: a dominated solution at 44 degrees, close to the third one
      double[][] objectives = new double[5][];
      System.arraycopy(FOUR_DIRECTIONS, 0, objectives, 0, 4);
      objectives[4] = new double[] {Math.cos(Math.toRadians(44.0)), Math.sin(Math.toRadians(44.0))};
      double[] fitness = {0.4, 0.45, 0.3, 0.2, 2.5};

      // Act
      int[] selected = CMOEAMSSelection.environmentalSelection(fitness, objectives, 3);

      // Assert
      assertArrayEquals(new int[] {0, 2, 3}, selected);
    }

    @Test
    @DisplayName("given more solutions to select than there are, then it is rejected")
    void givenMoreSolutionsToSelectThanThereAre_whenSelecting_thenItIsRejected() {
      // Arrange
      double[] fitness = {0.4, 0.5};
      double[][] objectives = new double[2][2];

      // Act & Assert
      assertThrows(
          ValueOutOfRangeException.class,
          () -> CMOEAMSSelection.environmentalSelection(fitness, objectives, 3));
    }
  }

  @Nested
  @DisplayName("When truncating")
  class WhenTruncating {
    @Test
    @DisplayName("given solutions, then the one whose sorted distances are lexicographically"
        + " smallest is removed first")
    void givenSolutions_whenTruncating_thenTheLexicographicallySmallestIsRemovedFirst() {
      // Act
      boolean[] one = CMOEAMSSelection.truncation(FOUR_DIRECTIONS, 1);
      boolean[] two = CMOEAMSSelection.truncation(FOUR_DIRECTIONS, 2);

      // Assert: the first two tie in their nearest neighbour, and the second one is closer to the
      // third; then the third, at 45 degrees from the other two, goes
      assertArrayEquals(new boolean[] {false, true, false, false}, one);
      assertArrayEquals(new boolean[] {false, true, true, false}, two);
    }

    @Test
    @DisplayName("given a tie in all the distances, then the first solution is removed")
    void givenATieInAllTheDistances_whenTruncating_thenTheFirstSolutionIsRemoved() {
      // Arrange
      double[][] objectives = {{1.0, 0.0}, {0.0, 1.0}};

      // Act
      boolean[] deleted = CMOEAMSSelection.truncation(objectives, 1);

      // Assert
      assertArrayEquals(new boolean[] {true, false}, deleted);
    }

    @Test
    @DisplayName("given scaled objective vectors, then the cosine distance ignores the scale")
    void givenScaledObjectiveVectors_whenTruncating_thenTheScaleIsIgnored() {
      // Arrange
      double[][] scaled = new double[FOUR_DIRECTIONS.length][];
      for (int i = 0; i < scaled.length; i++) {
        double factor = i + 1.0;
        scaled[i] = new double[] {factor * FOUR_DIRECTIONS[i][0], factor * FOUR_DIRECTIONS[i][1]};
      }

      // Act
      boolean[] deleted = CMOEAMSSelection.truncation(scaled, 2);

      // Assert
      assertArrayEquals(new boolean[] {false, true, true, false}, deleted);
    }

    @Test
    @DisplayName("given no solution to remove, then none is removed")
    void givenNoSolutionToRemove_whenTruncating_thenNoneIsRemoved() {
      // Act
      boolean[] deleted = CMOEAMSSelection.truncation(FOUR_DIRECTIONS, 0);

      // Assert
      assertArrayEquals(new boolean[4], deleted);
    }
  }

  @Nested
  @DisplayName("When selecting parents by tournament")
  class WhenSelectingParentsByTournament {
    @Test
    @DisplayName("given pairs of random solutions, then the one with the lowest fitness wins")
    void givenPairsOfRandomSolutions_whenSelecting_thenTheLowestFitnessWins() {
      // Arrange
      double[] fitness = {3.0, 1.0, 2.0};
      List<int[]> bounds = new ArrayList<>();
      BoundedRandomGenerator<Integer> random = sequence(bounds, 0, 1, 2, 0, 0, 0, 2, 1);

      // Act
      int[] selected = CMOEAMSSelection.tournamentSelection(fitness, 4, random);

      // Assert
      assertArrayEquals(new int[] {1, 2, 0, 1}, selected);
      assertEquals(8, bounds.size());
      bounds.forEach(range -> assertArrayEquals(new int[] {0, 2}, range));
    }

    @Test
    @DisplayName("given equal fitness values, then the first solution wins")
    void givenEqualFitnessValues_whenSelecting_thenTheFirstSolutionWins() {
      // Arrange
      double[] fitness = {1.0, 1.0};
      BoundedRandomGenerator<Integer> random = sequence(new ArrayList<>(), 1, 0, 0, 1);

      // Act
      int[] selected = CMOEAMSSelection.tournamentSelection(fitness, 2, random);

      // Assert
      assertArrayEquals(new int[] {0, 0}, selected);
    }

    @Test
    @DisplayName("given a NaN fitness, then it loses")
    void givenANaNFitness_whenSelecting_thenItLoses() {
      // Arrange
      double[] fitness = {Double.NaN, 5.0};
      BoundedRandomGenerator<Integer> random = sequence(new ArrayList<>(), 0, 1);

      // Act
      int[] selected = CMOEAMSSelection.tournamentSelection(fitness, 1, random);

      // Assert
      assertArrayEquals(new int[] {1}, selected);
    }

    private BoundedRandomGenerator<Integer> sequence(List<int[]> bounds, int... values) {
      AtomicInteger next = new AtomicInteger();
      return (lowerBound, upperBound) -> {
        bounds.add(new int[] {lowerBound, upperBound});
        return values[next.getAndIncrement()];
      };
    }
  }
}
