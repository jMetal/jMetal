package org.uma.jmetal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

@DisplayName("Unit tests for the grid of class AdaptiveGrid")
class AdaptiveGridLocationTest {
  private static final int BISECTIONS = 2;
  private static final int CELLS_PER_OBJECTIVE = 4;

  private AdaptiveGrid<DoubleSolution> grid;

  @BeforeEach
  void setUp() {
    grid = new AdaptiveGrid<>(BISECTIONS, 2);
  }

  private static DoubleSolution solution(double f1, double f2) {
    DoubleSolution solution = new DefaultDoubleSolution(List.of(Bounds.create(0.0, 1.0)), 2, 0);
    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    return solution;
  }

  /** Cell of a solution: the position in the first objective plus 4 times that of the second. */
  private static int cell(int positionInFirstObjective, int positionInSecondObjective) {
    return positionInFirstObjective + CELLS_PER_OBJECTIVE * positionInSecondObjective;
  }

  @Nested
  @DisplayName("When the grid covers [0, 8] in both objectives")
  class WhenTheGridCoversZeroToEight {
    @BeforeEach
    void setUp() {
      grid.updateGrid(List.of(solution(0.0, 0.0), solution(8.0, 8.0)));
    }

    @ParameterizedTest(name = "value {0} is in the position {1}")
    @CsvSource({
      "0.0, 0",
      "0.5, 0",
      "2.0, 0",
      "2.5, 1",
      "4.0, 1",
      "4.5, 2",
      "6.0, 2",
      "6.5, 3",
      "7.9, 3",
      "8.0, 3"
    })
    @DisplayName("given a value of an objective, then it is in the position of its quarter")
    void givenAValue_whenLocating_thenItIsInThePositionOfItsQuarter(double value, int position) {
      // Act
      int firstObjective = grid.location(solution(value, 0.0));
      int secondObjective = grid.location(solution(0.0, value));

      // Assert
      assertThat(firstObjective).isEqualTo(cell(position, 0));
      assertThat(secondObjective).isEqualTo(cell(0, position));
    }

    @Test
    @DisplayName("given a solution of the grid, then its cell combines both positions")
    void givenASolution_whenLocating_thenItsCellCombinesBothPositions() {
      // Act
      int location = grid.location(solution(2.5, 6.5));

      // Assert
      assertThat(location).isEqualTo(cell(1, 3));
    }

    @ParameterizedTest(name = "({0}, {1})")
    @CsvSource({"-0.1, 4.0", "4.0, -0.1", "8.1, 4.0", "4.0, 8.1", "9.0, -1.0"})
    @DisplayName("given a solution out of the limits of the grid, then its location is -1")
    void givenASolutionOutOfTheLimits_whenLocating_thenItsLocationIsMinusOne(
        double f1, double f2) {
      // Act
      int location = grid.location(solution(f1, f2));

      // Assert
      assertThat(location).isEqualTo(-1);
    }

    @Test
    @DisplayName("given the solutions that built it, then each cell has its density")
    void givenTheSolutionsThatBuiltIt_whenAskingTheDensity_thenEachCellHasItsDensity() {
      // Assert
      assertThat(grid.getLocationDensity(cell(0, 0))).isEqualTo(1);
      assertThat(grid.getLocationDensity(cell(3, 3))).isEqualTo(1);
      assertThat(grid.getLocationDensity(cell(1, 1))).isZero();
      assertThat(grid.occupiedHypercubes()).isEqualTo(2);
    }
  }

  @Nested
  @DisplayName("When the objectives have negative values")
  class WhenTheObjectivesHaveNegativeValues {
    @Test
    @DisplayName(
        "given objectives that are all negative, then the grid covers from the minimum to the"
            + " maximum, not up to zero")
    void givenOnlyNegativeValues_whenUpdatingTheGrid_thenTheGridCoversTheirRange() {
      // Arrange: [-10, -2] in both objectives, so the quarters are 2 wide
      grid.updateGrid(List.of(solution(-10.0, -10.0), solution(-2.0, -2.0)));

      // Act
      int lowerQuarter = grid.location(solution(-9.0, -9.0));
      int secondQuarter = grid.location(solution(-7.0, -7.0));
      int thirdQuarter = grid.location(solution(-5.0, -5.0));
      int upperQuarter = grid.location(solution(-2.5, -2.5));
      int aboveTheMaximum = grid.location(solution(-1.0, -2.0));

      // Assert
      assertThat(lowerQuarter).isEqualTo(cell(0, 0));
      assertThat(secondQuarter).isEqualTo(cell(1, 1));
      assertThat(thirdQuarter).isEqualTo(cell(2, 2));
      assertThat(upperQuarter).isEqualTo(cell(3, 3));
      assertThat(aboveTheMaximum).isEqualTo(-1);
    }

    @Test
    @DisplayName("given an objective that is all negative, then only its limits are negative")
    void givenOneNegativeObjective_whenUpdatingTheGrid_thenTheOtherIsNotAffected() {
      // Arrange: the first objective in [-8, 0] and the second in [0, 8]
      grid.updateGrid(List.of(solution(-8.0, 0.0), solution(0.0, 8.0)));

      // Act
      int location = grid.location(solution(-5.0, 6.5));

      // Assert
      assertThat(location).isEqualTo(cell(1, 3));
    }

    @Test
    @DisplayName("given values of both signs, then the grid covers the whole range")
    void givenValuesOfBothSigns_whenUpdatingTheGrid_thenTheGridCoversTheWholeRange() {
      // Arrange: [-4, 4] in both objectives
      grid.updateGrid(List.of(solution(-4.0, -4.0), solution(4.0, 4.0)));

      // Act & Assert
      assertThat(grid.location(solution(-4.0, -4.0))).isEqualTo(cell(0, 0));
      assertThat(grid.location(solution(0.5, 0.5))).isEqualTo(cell(2, 2));
      assertThat(grid.location(solution(4.0, 4.0))).isEqualTo(cell(3, 3));
    }
  }

  @Nested
  @DisplayName("When the grid is updated with special lists")
  class WhenTheGridIsUpdatedWithSpecialLists {
    @Test
    @DisplayName("given an empty list, then the grid has no occupied cells")
    void givenAnEmptyList_whenUpdatingTheGrid_thenThereAreNoOccupiedCells() {
      // Act
      grid.updateGrid(List.of());

      // Assert
      assertThat(grid.occupiedHypercubes()).isZero();
    }

    @Test
    @DisplayName("given a single solution, then it is in the first cell")
    void givenASingleSolution_whenUpdatingTheGrid_thenItIsInTheFirstCell() {
      // Arrange
      DoubleSolution only = solution(-3.0, 5.0);

      // Act
      grid.updateGrid(List.of(only));

      // Assert
      assertThat(grid.location(only)).isEqualTo(cell(0, 0));
      assertThat(grid.occupiedHypercubes()).isEqualTo(1);
      assertThat(grid.getMostPopulatedHypercube()).isEqualTo(cell(0, 0));
    }

    @Test
    @DisplayName("given solutions in the same cell, then the cell is the most populated")
    void givenSolutionsInTheSameCell_whenUpdatingTheGrid_thenItIsTheMostPopulated() {
      // Arrange
      List<DoubleSolution> solutions =
          List.of(solution(0.0, 0.0), solution(7.0, 7.0), solution(6.6, 6.7), solution(7.5, 7.9));

      // Act
      grid.updateGrid(solutions);

      // Assert
      assertThat(grid.getMostPopulatedHypercube()).isEqualTo(cell(3, 3));
      assertThat(grid.getLocationDensity(cell(3, 3))).isEqualTo(3);
      assertThat(grid.occupiedHypercubes()).isEqualTo(2);
    }
  }

  @Nested
  @DisplayName("When a solution out of the limits is added")
  class WhenASolutionOutOfTheLimitsIsAdded {
    @Test
    @DisplayName("given a solution out of the limits, then the grid is rebuilt to include it")
    void givenASolutionOutOfTheLimits_whenUpdatingTheGrid_thenTheGridIncludesIt() {
      // Arrange
      List<DoubleSolution> solutions = List.of(solution(0.0, 0.0), solution(4.0, 4.0));
      DoubleSolution outside = solution(8.0, -4.0);
      grid.updateGrid(solutions);

      // Act
      grid.updateGrid(outside, solutions);

      // Assert: the grid covers [0, 8] in the first objective and [-4, 4] in the second
      assertThat(grid.location(outside)).isEqualTo(cell(3, 0));
      assertThat(grid.location(solution(4.0, 4.0))).isEqualTo(cell(1, 3));
    }

    @Test
    @DisplayName("given a solution inside the limits, then the grid is not rebuilt")
    void givenASolutionInsideTheLimits_whenUpdatingTheGrid_thenTheGridIsNotRebuilt() {
      // Arrange
      List<DoubleSolution> solutions = List.of(solution(0.0, 0.0), solution(8.0, 8.0));
      grid.updateGrid(solutions);

      // Act
      grid.updateGrid(solution(4.5, 4.5), solutions);

      // Assert
      assertThat(grid.getLocationDensity(cell(2, 2))).isZero();
      assertThat(grid.occupiedHypercubes()).isEqualTo(2);
    }
  }

  @Nested
  @DisplayName("When adding and removing solutions")
  class WhenAddingAndRemovingSolutions {
    @BeforeEach
    void setUp() {
      grid.updateGrid(List.of(solution(0.0, 0.0), solution(8.0, 8.0)));
    }

    @Test
    @DisplayName("given a location, when a solution is added, then its density grows")
    void givenALocation_whenASolutionIsAdded_thenItsDensityGrows() {
      // Act
      grid.addSolution(cell(1, 2));
      grid.addSolution(cell(1, 2));

      // Assert
      assertThat(grid.getLocationDensity(cell(1, 2))).isEqualTo(2);
      assertThat(grid.getMostPopulatedHypercube()).isEqualTo(cell(1, 2));
      assertThat(grid.occupiedHypercubes()).isEqualTo(3);
    }

    @Test
    @DisplayName("given a location with one solution, when it is removed, then it is free")
    void givenALocationWithOneSolution_whenItIsRemoved_thenItIsFree() {
      // Act
      grid.removeSolution(cell(3, 3));

      // Assert
      assertThat(grid.getLocationDensity(cell(3, 3))).isZero();
      assertThat(grid.occupiedHypercubes()).isEqualTo(1);
    }

    @Test
    @DisplayName("given the most populated location, when a solution is removed, then it changes")
    void givenTheMostPopulatedLocation_whenASolutionIsRemoved_thenTheMostPopulatedChanges() {
      // Arrange
      grid.addSolution(cell(1, 1));
      grid.addSolution(cell(1, 1));
      grid.addSolution(cell(2, 2));

      // Act
      grid.removeSolution(cell(1, 1));
      grid.removeSolution(cell(1, 1));

      // Assert
      assertThat(grid.getLocationDensity(cell(1, 1))).isZero();
      assertThat(grid.getMostPopulatedHypercube()).isNotEqualTo(cell(1, 1));
    }
  }
}
