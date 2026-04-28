package org.uma.jmetal.qualityindicator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.Hypervolume3D;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

class Hypervolume3DTest {
  private static final double EPSILON = 1.0e-10;

  @Test
  void shouldComputeTheExpectedThreeDimensionalHypervolume() {
    double[] referencePoint = {5.0, 5.0, 5.0};
    double[][] front = {
      {1.0, 4.0, 4.0},
      {2.0, 2.0, 3.0},
      {4.0, 1.0, 2.0}
    };

    Hypervolume3D hypervolume = new Hypervolume3D(referencePoint);

    assertEquals(
        computeExactHypervolumeByInclusionExclusion(front, referencePoint),
        hypervolume.compute(front),
        EPSILON);
  }

  @Test
  void shouldComputeTheExpectedHypervolumeContributions() {
    double[] referencePoint = {5.0, 5.0, 5.0};
    double[][] front = {
      {1.0, 4.0, 4.0},
      {2.0, 2.0, 3.0},
      {4.0, 1.0, 2.0}
    };

    Hypervolume3D hypervolume = new Hypervolume3D(referencePoint);

    assertArrayEquals(
        computeExactContributionsByLeaveOneOut(front, referencePoint),
        hypervolume.computeContributions(front),
        EPSILON);
  }

  @Test
  void shouldMatchTheOracleOnDuplicatesDominatedPointsAndReferenceBoundaryCases() {
    double[] referencePoint = {5.0, 5.0, 5.0};
    double[][] front = {
      {1.0, 3.0, 3.0},
      {2.0, 2.0, 2.0},
      {3.0, 1.0, 1.0},
      {2.5, 2.5, 2.5},
      {2.0, 2.0, 2.0},
      {5.0, 1.0, 1.0},
      {6.0, 0.5, 0.5}
    };

    Hypervolume3D hypervolume = new Hypervolume3D(referencePoint);

    assertEquals(
        computeExactHypervolumeByInclusionExclusion(front, referencePoint),
        hypervolume.compute(front),
        EPSILON);
    assertArrayEquals(
        computeExactContributionsByLeaveOneOut(front, referencePoint),
        hypervolume.computeContributions(front),
        EPSILON);
  }

  @Test
  void shouldMatchTheOracleOnRandomSmallThreeDimensionalFronts() {
    double[] referencePoint = {5.0, 5.0, 5.0};
    Hypervolume3D hypervolume = new Hypervolume3D(referencePoint);
    Random random = new Random(17L);

    for (int size = 1; size <= 6; size++) {
      for (int iteration = 0; iteration < 100; iteration++) {
        double[][] front = randomFront(size, random);

        assertEquals(
            computeExactHypervolumeByInclusionExclusion(front, referencePoint),
            hypervolume.compute(front),
            EPSILON);
        assertArrayEquals(
            computeExactContributionsByLeaveOneOut(front, referencePoint),
            hypervolume.computeContributions(front),
            EPSILON);
      }
    }
  }

  @Test
  void shouldRejectFrontsThatAreNotThreeDimensional() {
    Hypervolume3D hypervolume = new Hypervolume3D(new double[] {5.0, 5.0, 5.0});

    assertThrows(
        InvalidConditionException.class,
        () -> hypervolume.compute(new double[][] {{1.0, 2.0}, {2.0, 1.0}}));
  }

  private double[][] randomFront(int size, Random random) {
    double[][] front = new double[size][3];
    for (int pointIndex = 0; pointIndex < size; pointIndex++) {
      for (int objective = 0; objective < 3; objective++) {
        front[pointIndex][objective] = -1.0 + 7.0 * random.nextDouble();
      }
    }
    return front;
  }

  private double[] computeExactContributionsByLeaveOneOut(
      double[][] front, double[] referencePoint) {
    double[] contributions = new double[front.length];
    double totalHypervolume = computeExactHypervolumeByInclusionExclusion(front, referencePoint);

    for (int omittedIndex = 0; omittedIndex < front.length; omittedIndex++) {
      contributions[omittedIndex] =
          totalHypervolume
              - computeExactHypervolumeByInclusionExclusion(
                  removePoint(front, omittedIndex), referencePoint);
    }

    return contributions;
  }

  private double[][] removePoint(double[][] front, int omittedIndex) {
    double[][] reducedFront = new double[front.length - 1][front[0].length];
    int reducedIndex = 0;
    for (int pointIndex = 0; pointIndex < front.length; pointIndex++) {
      if (pointIndex == omittedIndex) {
        continue;
      }

      System.arraycopy(front[pointIndex], 0, reducedFront[reducedIndex], 0, front[0].length);
      reducedIndex++;
    }

    return reducedFront;
  }

  private double computeExactHypervolumeByInclusionExclusion(
      double[][] front, double[] referencePoint) {
    double hypervolume = 0.0;
    int totalMasks = 1 << front.length;

    for (int mask = 1; mask < totalMasks; mask++) {
      double intersectionVolume = 1.0;
      for (int objective = 0; objective < referencePoint.length; objective++) {
        double lowerBound = Double.NEGATIVE_INFINITY;
        for (int pointIndex = 0; pointIndex < front.length; pointIndex++) {
          if ((mask & (1 << pointIndex)) == 0) {
            continue;
          }

          lowerBound = Math.max(lowerBound, front[pointIndex][objective]);
        }

        intersectionVolume *= Math.max(0.0, referencePoint[objective] - lowerBound);
      }

      if (Integer.bitCount(mask) % 2 == 1) {
        hypervolume += intersectionVolume;
      } else {
        hypervolume -= intersectionVolume;
      }
    }

    return hypervolume;
  }
}
