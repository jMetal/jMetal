package org.uma.jmetal.util.front.imp;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.PointUtils;

/**
 * A Front is a  list of points. This class includes utilities to work with fronts.
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class FrontUtils {

  /**
   * Gets the maximum values for each objectives in a front
   *
   * @param front A front of objective values
   * @return double [] An array with the maximum values for each objective
   */
  public static double[] getMaximumValues(Front front) {
    if (front == null) {
      throw new JMetalException("The front is null") ;
    }

    int numberOfObjectives = front.getPoint(0).getNumberOfDimensions() ;

    double[] maximumValue = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      maximumValue[i] = Double.NEGATIVE_INFINITY;
    }

    for (int i = 0 ; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getDimensionValue(j) > maximumValue[j]) {
          maximumValue[j] = front.getPoint(i).getDimensionValue(j);
        }
      }
    }

    return maximumValue;
  }

  /**
   * Gets the minimum values for each objectives in a given front
   *
   * @param front The front
   * @return double [] An array of noOjectives values whit the minimum values
   * for each objective
   */
  public static double[] getMinimumValues(Front front) {
    if (front == null) {
      throw new JMetalException("The front is null") ;
    }

    int numberOfObjectives = front.getPoint(0).getNumberOfDimensions() ;

    double[] minimumValue = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      minimumValue[i] = Double.MAX_VALUE;
    }

    for (int i = 0 ; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (front.getPoint(i).getDimensionValue(j) < minimumValue[j]) {
          minimumValue[j] = front.getPoint(i).getDimensionValue(j);
        }
      }
    }

    return minimumValue;
  }

  /**
   * This method receives a front and two points, one with maximum values
   * and the other one with minimum, and returns a normalized front.
   *
   * @param front        A front of points.
   * @param maximumValues The maximum values allowed
   * @param minimumValues The minimum values allowed
   * @return the normalized front
   */
  public static Front getNormalizedFront(Front front, double[] maximumValues, double[] minimumValues) {
    Front normalizedFront = new ArrayFront(front) ;
    int numberOfPointDimensions = front.getPoint(0).getNumberOfDimensions() ;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfPointDimensions; j++) {
        normalizedFront.getPoint(i).setDimensionValue(j, (front.getPoint(i).getDimensionValue(j)
            - minimumValues[j]) /
            (maximumValues[j] - minimumValues[j]));
      }
    }
    return normalizedFront;
  }

  /**
   * Gets the distance between a point and the nearest one in a front. If a distance equals to 0
   * if found, that means that the point is in the front, so it is excluded
   *
   * @param point The point
   * @param front The front that contains the other points to calculate the distances
   * @return The minimum distance between the point and the front
   */
  public static double distanceToNearestPoint(Point point, Front front) {
    double minDistance = Double.MAX_VALUE;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      double aux = PointUtils.euclideanDistance(point, front.getPoint(i));
      if ((aux < minDistance) && (aux > 0.0)) {
        minDistance = aux;
      }
    }

    return minDistance;
  }

  /**
   * Gets the distance between a point and the nearest one in a given front
   *
   * @param point The point
   * @param front The front that contains the other points to calculate the
   *              distances
   * @return The minimun distance between the point and the front
   */
  public static double distanceToClosestPoint(Point point, Front front) {
    double minDistance = PointUtils.euclideanDistance(point, front.getPoint(0));

    for (int i = 1; i < front.getNumberOfPoints(); i++) {
      double aux = PointUtils.euclideanDistance(point, front.getPoint(i));
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
  public static Front getInvertedFront(Front front) {
    int numberOfDimensions = front.getPoint(0).getNumberOfDimensions() ;
    Front invertedFront = new ArrayFront(front.getNumberOfPoints(), numberOfDimensions);

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfDimensions; j++) {
        if (front.getPoint(i).getDimensionValue(j)  <= 1.0
            && front.getPoint(i).getDimensionValue(j) >= 0.0) {
          invertedFront.getPoint(i).setDimensionValue(j, 1.0 - front.getPoint(i).getDimensionValue(j));
        } else if (front.getPoint(i).getDimensionValue(j) > 1.0) {
          invertedFront.getPoint(i).setDimensionValue(j, 0.0) ;
        } else if (front.getPoint(i).getDimensionValue(j) < 0.0) {
          invertedFront.getPoint(i).setDimensionValue(j, 1.0) ;
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
    double[][] arrayFront = new double[front.getNumberOfPoints()][] ;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      arrayFront[i] = new double[front.getPoint(i).getNumberOfDimensions()] ;
      for (int j = 0 ; j < front.getPoint(i).getNumberOfDimensions(); j++) {
        arrayFront[i][j] = front.getPoint(i).getDimensionValue(j) ;
      }
    }

    return arrayFront ;
  }
}
