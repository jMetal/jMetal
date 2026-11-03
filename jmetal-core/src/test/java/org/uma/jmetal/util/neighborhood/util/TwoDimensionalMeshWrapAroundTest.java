package org.uma.jmetal.util.neighborhood.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.neighborhood.impl.C25;
import org.uma.jmetal.util.neighborhood.impl.C49;

@DisplayName("Unit tests for the wrap-around of TwoDimensionalMesh")
class TwoDimensionalMeshWrapAroundTest {

  private static List<Integer> cells(int rows, int columns) {
    return IntStream.range(0, rows * columns).boxed().toList();
  }

  @Nested
  @DisplayName("When a neighbor is beyond the border of the mesh")
  class WhenTheNeighborIsBeyondTheBorder {

    @ParameterizedTest(name = "{0} x {1} mesh, shift ({2}, {3})")
    @CsvSource({
      "5, 5, -2, 0",
      "5, 5, 0, -2",
      "5, 5, -2, -2",
      "5, 5, -3, 2",
      "6, 4, -3, -3",
      "6, 4, 3, 3",
      "4, 6, -2, -3",
      "3, 3, -1, -1",
      "3, 3, -2, 1"
    })
    @DisplayName("given any cell, when the shift is taken, then the mesh wraps around like a torus")
    void givenAnyCell_whenTheShiftIsTaken_thenTheMeshWrapsAround(
        int rows, int columns, int rowShift, int columnShift) {
      // Arrange
      TwoDimensionalMesh<Integer> mesh =
          new TwoDimensionalMesh<>(rows, columns, new int[][] {{rowShift, columnShift}});
      List<Integer> cells = cells(rows, columns);

      for (int cell = 0; cell < rows * columns; cell++) {
        // Act
        int neighbor = mesh.getNeighbors(cells, cell).get(0);

        // Assert
        int expectedRow = Math.floorMod(cell / columns + rowShift, rows);
        int expectedColumn = Math.floorMod(cell % columns + columnShift, columns);
        assertThat(neighbor)
            .as("neighbor of cell %d", cell)
            .isEqualTo(expectedRow * columns + expectedColumn);
      }
    }
  }

  @Nested
  @DisplayName("When the neighborhood covers the whole mesh")
  class WhenTheNeighborhoodCoversTheWholeMesh {

    private List<IntegerSolution> solutions(int size) {
      return IntStream.range(0, size).mapToObj(i -> mock(IntegerSolution.class)).toList();
    }

    @ParameterizedTest(name = "cell {0}")
    @CsvSource({"0", "4", "20", "24", "12", "2", "10"})
    @DisplayName("given C25 on a 5 x 5 mesh, when the neighbors are taken, then each other cell once")
    void givenC25On5x5_whenTheNeighborsAreTaken_thenEachOtherCellAppearsOnce(int cell) {
      // Arrange
      List<IntegerSolution> solutions = solutions(25);
      C25<IntegerSolution> neighborhood = new C25<>(5, 5);

      // Act
      List<IntegerSolution> neighbors = neighborhood.getNeighbors(solutions, cell);

      // Assert
      assertThat(neighbors)
          .hasSize(24)
          .doesNotContain(solutions.get(cell))
          .doesNotHaveDuplicates();
    }

    @ParameterizedTest(name = "cell {0}")
    @CsvSource({"0", "6", "42", "48", "24", "3", "21"})
    @DisplayName("given C49 on a 7 x 7 mesh, when the neighbors are taken, then each other cell once")
    void givenC49On7x7_whenTheNeighborsAreTaken_thenEachOtherCellAppearsOnce(int cell) {
      // Arrange
      List<IntegerSolution> solutions = solutions(49);
      C49<IntegerSolution> neighborhood = new C49<>(7, 7);

      // Act
      List<IntegerSolution> neighbors = neighborhood.getNeighbors(solutions, cell);

      // Assert
      assertThat(neighbors)
          .hasSize(48)
          .doesNotContain(solutions.get(cell))
          .doesNotHaveDuplicates();
    }
  }
}
