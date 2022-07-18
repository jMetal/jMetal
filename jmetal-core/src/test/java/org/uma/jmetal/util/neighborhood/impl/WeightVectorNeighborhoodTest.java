package org.uma.jmetal.util.neighborhood.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class WeightVectorNeighborhoodTest {
  private static final double EPSILON = 0.000000000001;

  @Test
  public void shouldDefaultConstructorBeCorrectlyInitialized() {
    WeightVectorNeighborhood<?> weightVectorNeighborhood = new WeightVectorNeighborhood<>(100, 20);

    assertEquals(100, weightVectorNeighborhood.getNumberOfWeightVectors());
    assertEquals(20, weightVectorNeighborhood.neighborhoodSize());
    assertEquals(2, weightVectorNeighborhood.getWeightVectorSize());
    assertEquals(0.0, weightVectorNeighborhood.getWeightVector()[0][0], EPSILON);
    assertEquals(1.0, weightVectorNeighborhood.getWeightVector()[0][1], EPSILON);
    assertEquals(
        0.0101010101010101010101, weightVectorNeighborhood.getWeightVector()[1][0], EPSILON);
    assertEquals(0.989898989898989898, weightVectorNeighborhood.getWeightVector()[1][1], EPSILON);
    assertEquals(1.0, weightVectorNeighborhood.getWeightVector()[99][0], EPSILON);
    assertEquals(0.0, weightVectorNeighborhood.getWeightVector()[99][1], EPSILON);

    assertArrayEquals(
        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19},
        weightVectorNeighborhood.getNeighborhood()[0]);
    assertArrayEquals(
        new int[] {69, 70, 68, 71, 67, 72, 66, 73, 65, 64, 74, 75, 63, 76, 62, 77, 61, 78, 60, 79},
        weightVectorNeighborhood.getNeighborhood()[69]);
  }

  @Test
  public void shouldConstructorRaiseAnExceptionIfTheWeightFileDoesNotExist() {
    final var populationSize = 100;
    final var neighborSize = 20;
    final var weightVectorSize = 2;
    try {
      new WeightVectorNeighborhood<>(
          populationSize, weightVectorSize, neighborSize, "NonExistingFileVector");
    } catch (FileNotFoundException e) {
      // e.printStackTrace();
    }
  }

  @Test
  public void shouldGetNeighborsWorksProperlyWithTwoObjectives() {
    final var populationSize = 100;
    final var neighborSize = 20;
    var weightVectorNeighborhood =
        new WeightVectorNeighborhood<DoubleSolution>(populationSize, neighborSize);

    List<DoubleSolution> solutionList = new ArrayList<>(populationSize);
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    var bound = populationSize;
      for (var i = 0; i < bound; i++) {
          solutionList.add(problem.createSolution());
      }

    var neighbors = weightVectorNeighborhood.getNeighbors(solutionList, 0);

    assertEquals(neighborSize, neighbors.size());
    assertSame(solutionList.get(0), neighbors.get(0));
    assertSame(solutionList.get(19), neighbors.get(19));

    neighbors = weightVectorNeighborhood.getNeighbors(solutionList, 69);

    assertEquals(neighborSize, neighbors.size());
    assertSame(solutionList.get(69), neighbors.get(0));
    assertSame(solutionList.get(79), neighbors.get(19));
  }
}
