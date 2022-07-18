package org.uma.jmetal.util.legacy.front.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenVectors;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.PointSolution;

/**
 * A Front is a  list of points. This class includes utilities to work with {@link Front} objects.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@Deprecated
public class FrontUtils {
  /**
   * Gets the maximum values for each objectives in a front
   *
   * @param front A front of objective values
   * @return double [] An array with the maximum values for each objective
   */
  public static double[] getMaximumValues(Front front) {
    if (front == null) {
      throw new NullFrontException();
    } else if (front.getNumberOfPoints() == 0) {
      throw new EmptyFrontException();
    }

    var numberOfObjectives = front.getPoint(0).getDimension();

    var maximumValue = new double[10];
    var count = 0;
      for (var i1 = 0; i1 < numberOfObjectives; i1++) {
        var negativeInfinity = Double.NEGATIVE_INFINITY;
          if (maximumValue.length == count) maximumValue = Arrays.copyOf(maximumValue, count * 2);
          maximumValue[count++] = negativeInfinity;
      }
      maximumValue = Arrays.copyOfRange(maximumValue, 0, count);

      for (var i = 0; i < front.getNumberOfPoints(); i++) {
      for (var j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getValue(j) > maximumValue[j]) {
          maximumValue[j] = front.getPoint(i).getValue(j);
        }
      }
    }

    return maximumValue;
  }

  /**
   * Gets the minimum values for each objectives in a given front
   *
   * @param front The front
   * @return double [] An array with the minimum value for each objective
   */
  public static double[] getMinimumValues(@Nullable Front front) {
    if (front == null) {
      throw new NullFrontException();
    } else if (front.getNumberOfPoints() == 0) {
      throw new EmptyFrontException();
    }

    var numberOfObjectives = front.getPoint(0).getDimension();

    var minimumValue = new double[10];
    var count = 0;
      for (var i1 = 0; i1 < numberOfObjectives; i1++) {
        var maxValue = Double.MAX_VALUE;
          if (minimumValue.length == count) minimumValue = Arrays.copyOf(minimumValue, count * 2);
          minimumValue[count++] = maxValue;
      }
      minimumValue = Arrays.copyOfRange(minimumValue, 0, count);

      for (var i = 0; i < front.getNumberOfPoints(); i++) {
      for (var j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getValue(j) < minimumValue[j]) {
          minimumValue[j] = front.getPoint(i).getValue(j);
        }
      }
    }

    return minimumValue;
  }

  /**
   * Gets the distance between a point and the nearest one in a front. If a distance equals to 0
   * is found, that means that the point is in the front, so it is excluded
   *
   * @param point The point
   * @param front The front that contains the other points to calculate the distances
   * @return The minimum distance between the point and the front
   */
  public static double distanceToNearestPoint(Point point, Front front) {
    return distanceToNearestPoint(point, front, new EuclideanDistanceBetweenVectors());
  }

  /**
   * Gets the distance between a point and the nearest one in a front. If a distance equals to 0
   * is found, that means that the point is in the front, so it is excluded
   *
   * @param point The point
   * @param front The front that contains the other points to calculate the distances
   * @return The minimum distance between the point and the front
   */
  public static double distanceToNearestPoint(Point point, @Nullable Front front, Distance<double[], double[]> distance) {
    if (front == null) {
      throw new NullFrontException();
    } else if (front.getNumberOfPoints() == 0) {
      throw new EmptyFrontException();
    } else if (point == null) {
      throw new JMetalException("The point is null");
    }

    var minDistance = Double.MAX_VALUE;

    for (var i = 0; i < front.getNumberOfPoints(); i++) {
      var aux = distance.compute(point.getValues(), front.getPoint(i).getValues());
      if ((aux < minDistance) && (aux > 0.0)) {
        minDistance = aux;
      }
    }

    return minDistance;
  }

  /**
   * Gets the distance between a point and the nearest one in a given front. The Euclidean distance
   * is assumed
   *
   * @param point The point
   * @param front The front that contains the other points to calculate the
   *              distances
   * @return The minimum distance between the point and the front
   */
  public static double distanceToClosestPoint(Point point, Front front) {
    return distanceToClosestPoint(point, front, new EuclideanDistanceBetweenVectors());
  }

  /**
   * Gets the distance between a point and the nearest one in a given front
   *
   * @param point The point
   * @param front The front that contains the other points to calculate the
   *              distances
   * @return The minimum distance between the point and the front
   */
  public static double distanceToClosestPoint(@Nullable Point point, @Nullable Front front, Distance<double[], double[]> distance) {
    if (front == null) {
      throw new NullFrontException();
    } else if (front.getNumberOfPoints() == 0) {
      throw new EmptyFrontException();
    } else if (point == null) {
      throw new JMetalException("The point is null");
    }

    var minDistance = distance.compute(point.getValues(), front.getPoint(0).getValues());

    for (var i = 1; i < front.getNumberOfPoints(); i++) {
      var aux = distance.compute(point.getValues(), front.getPoint(i).getValues());
      if (aux < minDistance) {
        minDistance = aux;
      }
    }

    return minDistance;
  }

  /**
   * This method receives a normalized pareto front and return the inverted one.
   * This method is for minimization problems
   *
   * @param front The pareto front to inverse
   * @return The inverted pareto front
   */
  public static Front getInvertedFront(@Nullable Front front) {
    if (front == null) {
      throw new NullFrontException();
    } else if (front.getNumberOfPoints() == 0) {
      throw new EmptyFrontException();
    }

    var numberOfDimensions = front.getPoint(0).getDimension();
    @NotNull Front invertedFront = new ArrayFront(front.getNumberOfPoints(), numberOfDimensions);

    for (var i = 0; i < front.getNumberOfPoints(); i++) {
      for (var j = 0; j < numberOfDimensions; j++) {
        if (front.getPoint(i).getValue(j) <= 1.0
                && front.getPoint(i).getValue(j) >= 0.0) {
          invertedFront.getPoint(i).setValue(j, 1.0 - front.getPoint(i).getValue(j));
        } else if (front.getPoint(i).getValue(j) > 1.0) {
          invertedFront.getPoint(i).setValue(j, 0.0);
        } else if (front.getPoint(i).getValue(j) < 0.0) {
          invertedFront.getPoint(i).setValue(j, 1.0);
        }
      }
    }
    return invertedFront;
  }

  /**
   * Given a front, converts it to an array of double values
   *
   * @param front
   * @return A front as double[][] array
   */
  public static double[][] convertFrontToArray(Front front) {
    if (front == null) {
      throw new NullFrontException();
    }

    var arrayFront = new double[front.getNumberOfPoints()][];

    for (var i = 0; i < front.getNumberOfPoints(); i++) {
      arrayFront[i] = new double[front.getPoint(i).getDimension()];
      for (var j = 0; j < front.getPoint(i).getDimension(); j++) {
        arrayFront[i][j] = front.getPoint(i).getValue(j);
      }
    }

    return arrayFront;
  }

  /**
   * Given a front, converts it to a Solution set of PointSolutions
   *
   * @param front
   * @return A front as a List<FrontSolution>
   */
  /*
  public static List<PointSolution> convertFrontToSolutionList(Front front) {
    if (front == null) {
      throw new JMetalException("The front is null");
    }

    int numberOfObjectives ;
    int solutionSetSize = front.getNumberOfPoints() ;
    if (front.getNumberOfPoints() == 0) {
      numberOfObjectives = 0 ;
    } else {
      numberOfObjectives = front.getPoint(0).getDimension();
    }
    List<PointSolution> solutionSet = new ArrayList<>(solutionSetSize) ;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      PointSolution solution = new PointSolution(numberOfObjectives);
      for (int j = 0 ; j < numberOfObjectives; j++) {
        solution.setObjective(j, front.getPoint(i).getValue(j));
      }

      solutionSet.add(solution) ;
    }

    return solutionSet ;
  }
*/

  /**
   * Given a front, converts it to a Solution set of PointSolutions
   *
   * @param front
   * @return A front as a List<FrontSolution>
   */

  public static List<PointSolution> convertFrontToSolutionList(@Nullable Front front) {
    if (front == null) {
      throw new NullFrontException();
    }

    int numberOfObjectives;
    var solutionSetSize = front.getNumberOfPoints();
    if (front.getNumberOfPoints() == 0) {
      numberOfObjectives = 0;
    } else {
      numberOfObjectives = front.getPoint(0).getDimension();
    }
    List<PointSolution> solutionSet = new ArrayList<>(solutionSetSize);

    for (var i = 0; i < front.getNumberOfPoints(); i++) {
      var solution = new PointSolution(numberOfObjectives);
      for (var j = 0; j < numberOfObjectives; j++) {
        solution.objectives()[j] = front.getPoint(i).getValue(j);
      }

      solutionSet.add(solution);
    }

    return solutionSet;
  }

  @SuppressWarnings("serial")
  private static class NullFrontException extends JMetalException {
    public NullFrontException() {
      super("The front is null");
    }
  }

  @SuppressWarnings("serial")
  private static class EmptyFrontException extends JMetalException {
    public EmptyFrontException() {
      super("The front is empty");
    }
  }
}
