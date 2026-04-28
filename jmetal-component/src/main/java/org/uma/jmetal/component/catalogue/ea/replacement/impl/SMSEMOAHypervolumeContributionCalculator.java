package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.Hypervolume3D;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.WFGHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Computes hypervolume contributions for SMS-EMOA replacement.
 */
final class SMSEMOAHypervolumeContributionCalculator {
  private double[][] pointBuffer = new double[0][0];
  private double[] contributionBuffer = new double[0];

  double[] compute(List<? extends Solution<?>> front, double[] referencePoint) {
    Check.notNull(front);
    Check.notNull(referencePoint);

    if (front.isEmpty()) {
      return new double[0];
    }

    int numberOfObjectives = referencePoint.length;
    copyObjectives(front, numberOfObjectives);
    double[][] points = Arrays.copyOf(pointBuffer, front.size());

    if (numberOfObjectives == 2) {
      return computeExactTwoDimensionalContributions(points, referencePoint);
    }

    if (numberOfObjectives == 3) {
      return new Hypervolume3D(referencePoint).computeContributions(points);
    }

    return computeExactContributionsWithWfg(points, referencePoint);
  }

  private double[] computeExactTwoDimensionalContributions(
      double[][] points, double[] referencePoint) {
    ProjectedRectangle[] rectangles = new ProjectedRectangle[points.length];
    for (int index = 0; index < points.length; index++) {
      rectangles[index] =
          new ProjectedRectangle(
              Math.max(0.0, referencePoint[0] - points[index][0]),
              Math.max(0.0, referencePoint[1] - points[index][1]),
              index);
    }

    return computeAnchoredRectangleContributions(rectangles, points.length);
  }

  private double[] computeAnchoredRectangleContributions(
      ProjectedRectangle[] rectangles, int numberOfPoints) {
    double[] contributions = ensureContributionBuffer(numberOfPoints);
    Arrays.fill(contributions, 0.0);

    if (rectangles.length == 0) {
      return contributions;
    }

    Arrays.sort(
        rectangles,
        Comparator.comparingDouble((ProjectedRectangle rectangle) -> rectangle.x)
            .thenComparingDouble(rectangle -> rectangle.y));

    double[] maximumY = new double[rectangles.length];
    double[] secondMaximumY = new double[rectangles.length];
    int[] maximumIndex = new int[rectangles.length];
    int[] maximumCount = new int[rectangles.length];

    double currentMaximumY = Double.NEGATIVE_INFINITY;
    double currentSecondMaximumY = 0.0;
    int currentMaximumIndex = -1;
    int currentMaximumCount = 0;

    for (int index = rectangles.length - 1; index >= 0; index--) {
      double rectangleY = rectangles[index].y;
      if (rectangleY > currentMaximumY) {
        currentSecondMaximumY =
            currentMaximumY == Double.NEGATIVE_INFINITY ? 0.0 : currentMaximumY;
        currentMaximumY = rectangleY;
        currentMaximumIndex = rectangles[index].originalIndex;
        currentMaximumCount = 1;
      } else if (Double.compare(rectangleY, currentMaximumY) == 0) {
        currentMaximumCount++;
      } else if (rectangleY > currentSecondMaximumY) {
        currentSecondMaximumY = rectangleY;
      }

      maximumY[index] = currentMaximumY;
      secondMaximumY[index] = currentSecondMaximumY;
      maximumIndex[index] = currentMaximumIndex;
      maximumCount[index] = currentMaximumCount;
    }

    double previousX = 0.0;
    for (int index = 0; index < rectangles.length; index++) {
      double width = rectangles[index].x - previousX;
      if (width > 0.0 && maximumCount[index] == 1) {
        double uniqueHeight = maximumY[index] - secondMaximumY[index];
        if (uniqueHeight > 0.0) {
          contributions[maximumIndex[index]] += width * uniqueHeight;
        }
      }
      previousX = rectangles[index].x;
    }

    return contributions;
  }

  private double[] computeExactContributionsWithWfg(double[][] points, double[] referencePoint) {
    double[] contributions = ensureContributionBuffer(points.length);

    if (points.length == 0) {
      return contributions;
    }

    int numberOfObjectives = referencePoint.length;
    double[][] normalizedFront = normalizeFront(points, referencePoint);
    double scaleFactor = referencePointVolume(referencePoint);
    WFGHypervolume hypervolume = new WFGHypervolume(referencePoint);

    double totalHypervolume = hypervolume.compute(normalizedFront) * scaleFactor;
    if (points.length == 1) {
      contributions[0] = totalHypervolume;
      return contributions;
    }

    double[][] reducedFront = new double[points.length - 1][numberOfObjectives];
    for (int omittedIndex = 0; omittedIndex < points.length; omittedIndex++) {
      int reducedIndex = 0;
      for (int pointIndex = 0; pointIndex < points.length; pointIndex++) {
        if (pointIndex == omittedIndex) {
          continue;
        }

        System.arraycopy(
            normalizedFront[pointIndex], 0, reducedFront[reducedIndex], 0, numberOfObjectives);
        reducedIndex++;
      }

      contributions[omittedIndex] = totalHypervolume - hypervolume.compute(reducedFront) * scaleFactor;
    }

    return contributions;
  }

  private double[][] normalizeFront(double[][] points, double[] referencePoint) {
    double[][] normalizedFront = new double[points.length][referencePoint.length];
    for (int pointIndex = 0; pointIndex < points.length; pointIndex++) {
      for (int objective = 0; objective < referencePoint.length; objective++) {
        normalizedFront[pointIndex][objective] =
            referencePoint[objective] == 0.0
                ? 0.0
                : Math.min(1.0, Math.max(0.0, points[pointIndex][objective] / referencePoint[objective]));
      }
    }
    return normalizedFront;
  }

  private double referencePointVolume(double[] referencePoint) {
    double volume = 1.0;
    for (double coordinate : referencePoint) {
      volume *= coordinate;
    }
    return volume;
  }

  private void copyObjectives(List<? extends Solution<?>> front, int numberOfObjectives) {
    ensurePointBuffer(front.size(), numberOfObjectives);

    for (int pointIndex = 0; pointIndex < front.size(); pointIndex++) {
      System.arraycopy(front.get(pointIndex).objectives(), 0, pointBuffer[pointIndex], 0,
          numberOfObjectives);
    }
  }

  private void ensurePointBuffer(int numberOfPoints, int numberOfObjectives) {
    if (pointBuffer.length < numberOfPoints
        || (pointBuffer.length > 0 && pointBuffer[0].length != numberOfObjectives)) {
      pointBuffer = new double[numberOfPoints][numberOfObjectives];
      return;
    }

    for (int pointIndex = 0; pointIndex < numberOfPoints; pointIndex++) {
      if (pointBuffer[pointIndex] == null || pointBuffer[pointIndex].length != numberOfObjectives) {
        pointBuffer[pointIndex] = new double[numberOfObjectives];
      }
    }
  }

  private double[] ensureContributionBuffer(int numberOfPoints) {
    if (contributionBuffer.length != numberOfPoints) {
      contributionBuffer = new double[numberOfPoints];
    }
    return contributionBuffer;
  }

  private static class ProjectedRectangle {
    private final double x;
    private final double y;
    private final int originalIndex;

    private ProjectedRectangle(double x, double y, int originalIndex) {
      this.x = x;
      this.y = y;
      this.originalIndex = originalIndex;
    }
  }
}
