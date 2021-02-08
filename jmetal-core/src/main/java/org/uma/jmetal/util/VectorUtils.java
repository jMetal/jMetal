package org.uma.jmetal.util;

import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenVectors;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

public class VectorUtils {
  /**
   * Method that apply a dominance test between two vectors. It is assumed that the vectors have
   * been properly tested before calling this method to ensure that they have the same length
   *
   * @param vector1
   * @param vector2
   * @return 0 if the vectors are non-dominated, -1 if vector1 dominates vector2, and 1 if vector2
   *     dominates vector 1
   */
  public static int dominanceTest(double[] vector1, double[] vector2) {
    int bestIsOne = 0;
    int bestIsTwo = 0;
    int result;
    for (int i = 0; i < vector1.length; i++) {
      double value1 = vector1[i];
      double value2 = vector2[i];
      if (value1 != value2) {
        if (value1 < value2) {
          bestIsOne = 1;
        }
        if (value2 < value1) {
          bestIsTwo = 1;
        }
      }
    }
    result = Integer.compare(bestIsTwo, bestIsOne);
    return result;
  }

  /**
   * @param filePath the file need to read
   * @return referenceVectors. referenceVectors[i][j] means the i-th vector's j-th value
   * @throws JMetalException if error while read file
   */
  public static double[][] readVectors(String filePath, String separator) throws IOException {
    double[][] referenceVectors;
    String path = filePath;

    URL url = VectorUtils.class.getClassLoader().getResource(filePath);
    if (url != null) {
      try {
        path = Paths.get(url.toURI()).toString();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    }

    List<String> vectorStrList = Files.readAllLines(Paths.get(path));

    referenceVectors = new double[vectorStrList.size()][];
    for (int i = 0; i < vectorStrList.size(); i++) {
      String vectorStr = vectorStrList.get(i);
      String[] objectArray = vectorStr.split(separator);
      referenceVectors[i] = new double[objectArray.length];
      for (int j = 0; j < objectArray.length; j++) {
        referenceVectors[i][j] = Double.parseDouble(objectArray[j]);
      }
    }

    return referenceVectors;
  }

  public static double[][] readVectors(String filePath) throws IOException {
    return readVectors(filePath, "\\s+") ;
  }

  /**
   * Checks whether a vector is dominated by a front
   *
   * @param vector
   * @param front
   * @return
   */
  public static boolean isVectorDominatedByAFront(double[] vector, double[][] front) {
    boolean result = false;

    int i = 0;
    while (!result && (i < front.length)) {
      if (VectorUtils.dominanceTest(vector, front[i]) == 1) {
        result = true;
      }
      i++;
    }

    return result;
  }

  public static double distanceToClosestVector(double[] vector, double[][] front) {
    return distanceToClosestVector(vector, front, new EuclideanDistanceBetweenVectors());
  }

  public static double distanceToClosestVector(
      double[] vector, double[][] front, Distance<double[], double[]> distance) {
    Check.notNull(vector);
    Check.notNull(front);
    Check.that(front.length > 0, "The front is empty");

    double minDistance = distance.compute(vector, front[0]);

    for (int i = 1; i < front.length; i++) {
      double aux = distance.compute(vector, front[i]);
      if (aux < minDistance) {
        minDistance = aux;
      }
    }

    return minDistance;
  }

  public static double distanceToNearestVector(double[] vector, double[][] front) {
    return distanceToNearestVector(vector, front, new EuclideanDistanceBetweenVectors());
  }

  public static double distanceToNearestVector(
      double[] vector, double[][] front, Distance<double[], double[]> distance) {
    Check.notNull(vector);
    Check.notNull(front);
    Check.that(front.length > 0, "The front is empty");

    double minDistance = Double.MAX_VALUE;

    for (int i = 0; i < front.length; i++) {
      double aux = distance.compute(vector, front[i]);
      if ((aux < minDistance) && (aux > 0.0)) {
        minDistance = aux;
      }
    }

    return minDistance;
  }

  /**
   * This method receives a normalized front and return the inverted one.
   * This method is for minimization problems
   *
   * @param front The front
   * @return The inverted front
   */
  public static double[][] getInvertedFront(double[][] front) {
    Check.notNull(front);
    Check.that(front.length > 0, "The front is empty");

    int numberOfDimensions = front[0].length;
    double[][] invertedFront = new double[front.length][numberOfDimensions] ;

    for (int i = 0; i < front.length; i++) {
      for (int j = 0; j < numberOfDimensions; j++) {
        if (front[i][j] <= 1.0 && front[i][j] >= 0.0) {
          invertedFront[i][j] =  1.0 - front[i][j];
        } else if (front[i][j] > 1.0) {
          invertedFront[i][j] = 0.0 ;
        } else if (front[i][j] < 0.0) {
          invertedFront[i][j] = 1.0 ;
        }
      }
    }
    return invertedFront;
  }
}
