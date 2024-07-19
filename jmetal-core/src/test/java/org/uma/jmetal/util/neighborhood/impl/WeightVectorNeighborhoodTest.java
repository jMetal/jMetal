package org.uma.jmetal.util.neighborhood.impl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class WeightVectorNeighborhoodTest {
  private static final double EPSILON = 0.000000000001;

  @Test
  void shouldDefaultConstructorBeCorrectlyInitialized() {
    WeightVectorNeighborhood<?> weightVectorNeighborhood = new WeightVectorNeighborhood<>(100, 20);

    Assertions.assertEquals(100, weightVectorNeighborhood.getNumberOfWeightVectors());
    Assertions.assertEquals(20, weightVectorNeighborhood.neighborhoodSize());
    Assertions.assertEquals(2, weightVectorNeighborhood.getWeightVectorSize());
    Assertions.assertEquals(0.0, weightVectorNeighborhood.getWeightVector()[0][0], EPSILON);
    Assertions.assertEquals(1.0, weightVectorNeighborhood.getWeightVector()[0][1], EPSILON);
    Assertions.assertEquals(0.0101010101010101010101,
        weightVectorNeighborhood.getWeightVector()[1][0], EPSILON);
    Assertions.assertEquals(0.989898989898989898, weightVectorNeighborhood.getWeightVector()[1][1],
        EPSILON);
    Assertions.assertEquals(1.0, weightVectorNeighborhood.getWeightVector()[99][0], EPSILON);
    Assertions.assertEquals(0.0, weightVectorNeighborhood.getWeightVector()[99][1], EPSILON);

    Assertions.assertArrayEquals(
        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19},
        weightVectorNeighborhood.getNeighborhood()[0]);
    Assertions.assertArrayEquals(
        new int[] {69, 70, 68, 71, 67, 72, 66, 73, 65, 64, 74, 75, 63, 76, 62, 77, 61, 78, 60, 79},
        weightVectorNeighborhood.getNeighborhood()[69]);
  }

  @Test
  void shouldConstructorRaiseAnExceptionIfTheWeightFileDoesNotExist() {
    final int populationSize = 100;
    final int neighborSize = 20;
    final int weightVectorSize = 2;
    try {
      new WeightVectorNeighborhood<>(
          populationSize, weightVectorSize, neighborSize, "NonExistingFileVector");
    } catch (FileNotFoundException e) {
      // e.printStackTrace();
    }
  }

  @Test
  void shouldGetNeighborsWorksProperlyWithTwoObjectives() {
    final int populationSize = 100;
    final int neighborSize = 20;
    WeightVectorNeighborhood<DoubleSolution> weightVectorNeighborhood =
        new WeightVectorNeighborhood<>(populationSize, neighborSize);

    List<DoubleSolution> solutionList = new ArrayList<>(populationSize);
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    IntStream.range(0, populationSize).forEach(i -> solutionList.add(problem.createSolution()));

    List<DoubleSolution> neighbors;
    neighbors = weightVectorNeighborhood.getNeighbors(solutionList, 0);

    Assertions.assertEquals(neighborSize, neighbors.size());
    Assertions.assertSame(solutionList.get(0), neighbors.get(0));
    Assertions.assertSame(solutionList.get(19), neighbors.get(19));

    neighbors = weightVectorNeighborhood.getNeighbors(solutionList, 69);

    Assertions.assertEquals(neighborSize, neighbors.size());
    Assertions.assertSame(solutionList.get(69), neighbors.get(0));
    Assertions.assertSame(solutionList.get(79), neighbors.get(19));
  }
}
