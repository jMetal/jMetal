package org.uma.jmetal.qualityindicator.impl.hypervolume.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Exact hypervolume implementation for three-objective fronts.
 *
 * <p>The internal 3D kernel is a faithful reimplementation of the Guerreiro-Fonseca 2018
 * hypervolume contribution/update line, supported by the 2006 Fonseca-Paquete-Lopez-Ibanez
 * dimension-sweep formulation. jMetal's current direct-reference-point semantics are preserved:
 * points on or outside the reference point contribute zero instead of raising an error.
 *
 * <p>References:
 *
 * <ol>
 *   <li>Andreia P. Guerreiro and Carlos M. Fonseca. "Computing and Updating Hypervolume
 *       Contributions in Up to Four Dimensions." IEEE Transactions on Evolutionary Computation,
 *       22(3):449-463, 2018. https://doi.org/10.1109/TEVC.2017.2729550
 *   <li>Carlos M. Fonseca, Luis Paquete, and Manuel Lopez-Ibanez. "An Improved Dimension-Sweep
 *       Algorithm for the Hypervolume Indicator." Proceedings of the 2006 IEEE Congress on
 *       Evolutionary Computation (CEC 2006), pages 1157-1163, 2006.
 *       https://doi.org/10.1109/CEC.2006.1688440
 * </ol>
 */
@SuppressWarnings("serial")
public class Hypervolume3D extends Hypervolume {
  private static final Comparator<PointRecord> LEXICOGRAPHIC_ORDER =
      Comparator.comparingDouble((PointRecord point) -> point.z)
          .thenComparingDouble(point -> point.y)
          .thenComparingDouble(point -> point.x);

  public Hypervolume3D() {}

  public Hypervolume3D(double[] referencePoint) {
    super(referencePoint);
  }

  public Hypervolume3D(double[][] referenceFront) {
    super(referenceFront);
  }

  @Override
  public QualityIndicator newInstance() {
    return new Hypervolume3D();
  }

  @Override
  public double compute(double[][] front) {
    Check.notNull(front);

    if (front.length == 0) {
      return 0.0;
    }

    double[] referencePoint = referencePoint();
    validateThreeDimensionalFront(front, referencePoint);

    PreparedFront preparedFront = PreparedFront.prepare(front, referencePoint);
    if (preparedFront.activePoints.length == 0) {
      return 0.0;
    }

    return evaluate(preparedFront.activePoints, referencePoint, false).hypervolume;
  }

  public double[] computeContributions(double[][] front) {
    Check.notNull(front);

    if (front.length == 0) {
      return new double[0];
    }

    double[] referencePoint = referencePoint();
    validateThreeDimensionalFront(front, referencePoint);

    PreparedFront preparedFront = PreparedFront.prepare(front, referencePoint);
    if (preparedFront.activePoints.length == 0) {
      return new double[front.length];
    }

    if (preparedFront.fastContributionEligible()) {
      Evaluation evaluation = evaluate(preparedFront.activePoints, referencePoint, true);
      return evaluation.contributions;
    }

    double totalHypervolume = evaluate(preparedFront.activePoints, referencePoint, false).hypervolume;
    double[] contributions = new double[front.length];
    for (int omittedIndex = 0; omittedIndex < front.length; omittedIndex++) {
      PreparedFront reducedFront = PreparedFront.prepare(removePoint(front, omittedIndex), referencePoint);
      double reducedHypervolume =
          reducedFront.activePoints.length == 0
              ? 0.0
              : evaluate(reducedFront.activePoints, referencePoint, false).hypervolume;
      contributions[omittedIndex] = totalHypervolume - reducedHypervolume;
    }

    return contributions;
  }

  @Override
  public String description() {
    return "Exact 3D Hypervolume quality indicator";
  }

  @Override
  public String name() {
    return "HV3D";
  }

  private Evaluation evaluate(
      PointSeed[] activePoints, double[] referencePoint, boolean computeContributions) {
    PointRecord[] points = createPointRecords(activePoints);
    Arrays.sort(points, LEXICOGRAPHIC_ORDER);

    PointRecord sentinelX =
        PointRecord.sentinel(referencePoint[0], Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    PointRecord sentinelY =
        PointRecord.sentinel(Double.NEGATIVE_INFINITY, referencePoint[1], Double.NEGATIVE_INFINITY);

    preprocess(points, sentinelX, sentinelY);

    List<PointRecord> activeByY = new ArrayList<>(points.length + 2);
    activeByY.add(sentinelX);
    activeByY.add(sentinelY);

    double area = 0.0;
    double hypervolume = 0.0;
    double previousZ = points[0].z;

    for (PointRecord point : points) {
      hypervolume += area * (point.z - previousZ);
      previousZ = point.z;

      point.resetSweepState();

      updateVolumesY(point, activeByY, point.cx);

      point.area = computeAreaY(point, activeByY, point.cx);
      area += point.area;

      DelimiterList delimiters = new DelimiterList(removeDominatedY(point, activeByY, point.cx));
      addY(point.cx, delimiters.byY, null);
      delimiters.rebuildX();
      addX(point.cy, delimiters, null);
      delimiters.rebuildY();
      point.delimiters = delimiters;

      addY(point, activeByY, point.cx);

      updateOuterDelimiterAlongY(point);
      updateOuterDelimiterAlongX(point);
    }

    hypervolume += area * (referencePoint[2] - previousZ);

    double[] contributions = null;
    if (computeContributions) {
      contributions = new double[points.length];
      for (PointRecord point : activeByY) {
        if (point.isSentinel()) {
          continue;
        }
        point.contribution += point.area * (referencePoint[2] - point.lastUpdateZ);
      }

      for (PointRecord point : points) {
        contributions[point.originalIndex] = point.contribution;
      }
    }

    return new Evaluation(hypervolume, contributions);
  }

  private void preprocess(PointRecord[] points, PointRecord sentinelX, PointRecord sentinelY) {
    List<PointRecord> activeByY = new ArrayList<>(points.length + 2);
    activeByY.add(sentinelX);
    activeByY.add(sentinelY);

    for (PointRecord point : points) {
      PointRecord outerDelimiterX = outerDelimiterX(point, activeByY);
      removeDominatedY(point, activeByY, outerDelimiterX);
      point.cx = outerDelimiterX;
      point.cy = nextY(activeByY, outerDelimiterX);
      addY(point, activeByY, outerDelimiterX);
    }
  }

  private void updateOuterDelimiterAlongY(PointRecord point) {
    PointRecord outerDelimiterY = point.cy;
    if (outerDelimiterY.isSentinel()) {
      return;
    }

    PointRecord jointPoint = PointRecord.maximum(point, outerDelimiterY);
    outerDelimiterY.area -=
        computeAreaY(jointPoint, outerDelimiterY.delimiters.byY, outerDelimiterY.delimiters.headY());
    removeDominatedY(jointPoint, outerDelimiterY.delimiters.byY, outerDelimiterY.delimiters.headY());
    outerDelimiterY.delimiters.rebuildX();
    outerDelimiterY.delimiters.byY.remove(0);
    outerDelimiterY.delimiters.rebuildX();
    addY(point, outerDelimiterY.delimiters.byY, null);
    outerDelimiterY.delimiters.rebuildX();
  }

  private void updateOuterDelimiterAlongX(PointRecord point) {
    PointRecord outerDelimiterX = point.cx;
    if (outerDelimiterX.isSentinel()) {
      return;
    }

    PointRecord jointPoint = PointRecord.maximum(point, outerDelimiterX);
    outerDelimiterX.area -=
        computeAreaX(jointPoint, outerDelimiterX.delimiters, outerDelimiterX.delimiters.headX());
    removeDominatedX(jointPoint, outerDelimiterX.delimiters, outerDelimiterX.delimiters.headX());
    outerDelimiterX.delimiters.byX.remove(0);
    outerDelimiterX.delimiters.rebuildY();
    addX(point, outerDelimiterX.delimiters, null);
    outerDelimiterX.delimiters.rebuildY();
  }

  private void updateVolumesY(PointRecord point, List<PointRecord> byY, PointRecord outerDelimiterX) {
    PointRecord next = nextY(byY, outerDelimiterX);
    updateContributionState(outerDelimiterX, point.z);

    while (point.x <= next.x) {
      PointRecord dominatedPoint = next;
      next = nextY(byY, dominatedPoint);
      updateContributionState(dominatedPoint, point.z);
    }

    updateContributionState(next, point.z);
  }

  private void updateContributionState(PointRecord point, double z) {
    if (point.isSentinel()) {
      return;
    }

    point.contribution += point.area * (z - point.lastUpdateZ);
    point.lastUpdateZ = z;
  }

  private double computeAreaY(PointRecord point, List<PointRecord> byY, PointRecord outerDelimiterX) {
    PointRecord next = nextY(byY, outerDelimiterX);
    double area = (outerDelimiterX.x - point.x) * (next.y - point.y);

    while (point.x <= next.x) {
      PointRecord dominatedPoint = next;
      next = nextY(byY, dominatedPoint);
      area += (dominatedPoint.x - point.x) * (next.y - dominatedPoint.y);
    }

    return area;
  }

  private double computeAreaX(PointRecord point, DelimiterList delimiters, PointRecord outerDelimiterY) {
    PointRecord next = nextX(delimiters.byX, outerDelimiterY);
    double area = (next.x - point.x) * (outerDelimiterY.y - point.y);

    while (point.y <= next.y) {
      PointRecord dominatedPoint = next;
      next = nextX(delimiters.byX, dominatedPoint);
      area += (next.x - dominatedPoint.x) * (dominatedPoint.y - point.y);
    }

    return area;
  }

  private ArrayList<PointRecord> removeDominatedY(
      PointRecord point, List<PointRecord> byY, PointRecord outerDelimiterX) {
    ArrayList<PointRecord> removedPoints = new ArrayList<>();
    PointRecord next = nextY(byY, outerDelimiterX);

    while (point.x <= next.x) {
      PointRecord dominatedPoint = next;
      next = nextY(byY, dominatedPoint);
      removedPoints.add(dominatedPoint);
    }

    byY.removeAll(removedPoints);
    return removedPoints;
  }

  private void removeDominatedX(
      PointRecord point, DelimiterList delimiters, PointRecord outerDelimiterY) {
    ArrayList<PointRecord> removedPoints = new ArrayList<>();
    PointRecord next = nextX(delimiters.byX, outerDelimiterY);

    while (point.y <= next.y) {
      PointRecord dominatedPoint = next;
      next = nextX(delimiters.byX, dominatedPoint);
      removedPoints.add(dominatedPoint);
    }

    delimiters.byX.removeAll(removedPoints);
    delimiters.byY.removeAll(removedPoints);
  }

  private PointRecord outerDelimiterX(PointRecord point, List<PointRecord> byY) {
    PointRecord outerDelimiterX = byY.get(0);
    int index = 0;

    while (index + 1 < byY.size()
        && byY.get(index + 1).y < point.y
        && byY.get(index + 1).x > point.x) {
      index++;
      outerDelimiterX = byY.get(index);
    }

    return outerDelimiterX;
  }

  private PointRecord nextY(List<PointRecord> byY, PointRecord point) {
    return byY.get(byY.indexOf(point) + 1);
  }

  private PointRecord nextX(List<PointRecord> byX, PointRecord point) {
    return byX.get(byX.indexOf(point) + 1);
  }

  private void addY(PointRecord point, List<PointRecord> byY, PointRecord previous) {
    if (previous == null) {
      byY.add(0, point);
      return;
    }

    byY.add(byY.indexOf(previous) + 1, point);
  }

  private void addX(PointRecord point, DelimiterList delimiters, PointRecord previous) {
    if (previous == null) {
      delimiters.byX.add(0, point);
      return;
    }

    delimiters.byX.add(delimiters.byX.indexOf(previous) + 1, point);
  }

  private PointRecord[] createPointRecords(PointSeed[] activePoints) {
    PointRecord[] points = new PointRecord[activePoints.length];
    for (int index = 0; index < activePoints.length; index++) {
      points[index] =
          new PointRecord(
              activePoints[index].coordinates[0],
              activePoints[index].coordinates[1],
              activePoints[index].coordinates[2],
              activePoints[index].originalIndex);
    }

    return points;
  }

  private double[][] removePoint(double[][] front, int omittedIndex) {
    double[][] reducedFront = new double[front.length - 1][3];
    int reducedIndex = 0;
    for (int pointIndex = 0; pointIndex < front.length; pointIndex++) {
      if (pointIndex == omittedIndex) {
        continue;
      }

      System.arraycopy(front[pointIndex], 0, reducedFront[reducedIndex], 0, 3);
      reducedIndex++;
    }

    return reducedFront;
  }

  private double[] referencePoint() {
    Check.notNull(referenceFront);
    Check.that(referenceFront.length > 0, "The reference front is empty");

    int numberOfObjectives = referenceFront[0].length;
    double[] referencePoint = new double[numberOfObjectives];
    for (double[] referenceVector : referenceFront) {
      Check.that(
          referenceVector.length == numberOfObjectives,
          "The reference front contains points with inconsistent dimensions");
      for (int objective = 0; objective < numberOfObjectives; objective++) {
        referencePoint[objective] = Math.max(referencePoint[objective], referenceVector[objective]);
      }
    }

    return referencePoint;
  }

  private void validateThreeDimensionalFront(double[][] front, double[] referencePoint) {
    Check.that(referencePoint.length == 3, "The reference point must be three-dimensional");
    for (double[] point : front) {
      Check.that(point.length == 3, "All points must be three-dimensional");
    }
  }

  private static boolean weaklyDominates(double[] first, double[] second) {
    return first[0] <= second[0] && first[1] <= second[1] && first[2] <= second[2];
  }

  private static final class PreparedFront {
    private final PointSeed[] activePoints;
    private final int originalSize;

    private PreparedFront(PointSeed[] activePoints, int originalSize) {
      this.activePoints = activePoints;
      this.originalSize = originalSize;
    }

    private static PreparedFront prepare(double[][] front, double[] referencePoint) {
      List<PointSeed> strictPoints = new ArrayList<>(front.length);
      for (int index = 0; index < front.length; index++) {
        double x = Math.min(front[index][0], referencePoint[0]);
        double y = Math.min(front[index][1], referencePoint[1]);
        double z = Math.min(front[index][2], referencePoint[2]);

        if (x >= referencePoint[0] || y >= referencePoint[1] || z >= referencePoint[2]) {
          continue;
        }

        if (containsCoordinates(strictPoints, x, y, z)) {
          continue;
        }

        strictPoints.add(new PointSeed(new double[] {x, y, z}, index));
      }

      boolean[] dominated = new boolean[strictPoints.size()];
      for (int index = 0; index < strictPoints.size(); index++) {
        if (dominated[index]) {
          continue;
        }

        for (int challenger = 0; challenger < strictPoints.size(); challenger++) {
          if (index == challenger) {
            continue;
          }

          double[] point = strictPoints.get(index).coordinates;
          double[] other = strictPoints.get(challenger).coordinates;
          if (weaklyDominates(other, point)) {
            dominated[index] = true;
            break;
          }
        }
      }

      List<PointSeed> activePoints = new ArrayList<>(strictPoints.size());
      for (int index = 0; index < strictPoints.size(); index++) {
        if (!dominated[index]) {
          activePoints.add(strictPoints.get(index));
        }
      }

      return new PreparedFront(activePoints.toArray(new PointSeed[0]), front.length);
    }

    private boolean fastContributionEligible() {
      return activePoints.length == originalSize;
    }

    private static boolean containsCoordinates(List<PointSeed> points, double x, double y, double z) {
      for (PointSeed point : points) {
        if (Double.compare(point.coordinates[0], x) == 0
            && Double.compare(point.coordinates[1], y) == 0
            && Double.compare(point.coordinates[2], z) == 0) {
          return true;
        }
      }

      return false;
    }
  }

  private static final class PointSeed {
    private final double[] coordinates;
    private final int originalIndex;

    private PointSeed(double[] coordinates, int originalIndex) {
      this.coordinates = coordinates;
      this.originalIndex = originalIndex;
    }
  }

  private static final class Evaluation {
    private final double hypervolume;
    private final double[] contributions;

    private Evaluation(double hypervolume, double[] contributions) {
      this.hypervolume = hypervolume;
      this.contributions = contributions;
    }
  }

  private static final class DelimiterList {
    private List<PointRecord> byY;
    private List<PointRecord> byX;

    private DelimiterList(List<PointRecord> byY) {
      this.byY = byY;
      this.byX = new ArrayList<>(byY);
      rebuildX();
    }

    private PointRecord headY() {
      return byY.get(0);
    }

    private PointRecord headX() {
      return byX.get(0);
    }

    private void rebuildX() {
      byX = new ArrayList<>(byY);
      byX.sort(Comparator.comparingDouble(point -> point.x));
    }

    private void rebuildY() {
      byY = new ArrayList<>(byX);
      byY.sort(Comparator.comparingDouble(point -> point.y));
    }
  }

  private static final class PointRecord {
    private final double x;
    private final double y;
    private final double z;
    private final int originalIndex;

    private PointRecord cx;
    private PointRecord cy;
    private DelimiterList delimiters;
    private double area;
    private double contribution;
    private double lastUpdateZ;

    private PointRecord(double x, double y, double z, int originalIndex) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.originalIndex = originalIndex;
    }

    private static PointRecord sentinel(double x, double y, double z) {
      return new PointRecord(x, y, z, -1);
    }

    private static PointRecord maximum(PointRecord first, PointRecord second) {
      return new PointRecord(
          Math.max(first.x, second.x),
          Math.max(first.y, second.y),
          Math.max(first.z, second.z),
          -1);
    }

    private boolean isSentinel() {
      return originalIndex < 0;
    }

    private void resetSweepState() {
      area = 0.0;
      contribution = 0.0;
      lastUpdateZ = z;
      delimiters = null;
    }
  }
}
