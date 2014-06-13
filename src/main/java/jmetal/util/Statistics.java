//  Statistics.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.util;

import java.util.Vector;

/**
 * This class provides methods for computing some statistics
 */
public class Statistics {

  /**
   * Calculates the median of a vector considering the positions indicated by
   * the parameters first and last
   *
   * @param vector
   * @param first  index of first position to consider in the vector
   * @param last   index of last position to consider in the vector
   * @return The median
   */
  public static Double calculateMedian(Vector<Double> vector, int first, int last) {
    double median = 0.0;

    int size = last - first + 1;
    if (size % 2 != 0) {
      median = vector.elementAt(first + size / 2);
    } else {
      median = (vector.elementAt(first + size / 2 - 1) +
        vector.elementAt(first + size / 2)) / 2.0;
    }

    return median;
  }

  /**
   * Calculates the median of a vector
   * @param vector
   * @return The median
   */
  public static Double calculateMedian(Vector<Double> vector) {
    return calculateMedian(vector, 0, vector.size()-1) ;
  }

  /**
   * Calculates the interquartile range (IQR) of a vector of Doubles
   *
   * @param vector
   * @return The IQR
   */
  public static Double calculateIQR(Vector vector) {
    double q3 = 0.0;
    double q1 = 0.0;

    if (vector.size() > 1) {
      // == 1 implies IQR = 0
      if (vector.size() % 2 != 0) {
        q3 = calculateMedian(vector, vector.size() / 2 + 1, vector.size() - 1);
        q1 = calculateMedian(vector, 0, vector.size() / 2 - 1);
      } else {
        q3 = calculateMedian(vector, vector.size() / 2, vector.size() - 1);
        q1 = calculateMedian(vector, 0, vector.size() / 2 - 1);
      }
    }
    return q3 - q1;
  }

  public static Double calculateMean(Vector<Double> vector) {
    double sum = 0.0 ;
    for (int i = 0; i < vector.size(); i++) {
      double val = vector.elementAt(i);
      sum += val;
    }

    return sum / vector.size();
  }

  public static Double calculateStandardDeviation(Vector<Double> vector) {
    double sqsum = 0.0 ;
    double mean ;
    double stdDeviation ;
    for (int i = 0; i < vector.size(); i++) {
      double val = vector.elementAt(i);

      sqsum += val * val;
    }

    // Mean
    mean = calculateMean(vector) ;

    // Standard deviation
    if (sqsum / vector.size() - mean * mean < 0.0) {
      stdDeviation = 0.0;
    } else {
      stdDeviation = Math.sqrt(sqsum / vector.size() - mean * mean);
    }

    return stdDeviation ;
  }

  public static Double calculateStandardDeviation(Vector<Double> vector, double mean) {
    double sqsum = 0.0 ;
    double stdDeviation ;
    for (int i = 0; i < vector.size(); i++) {
      double val = vector.elementAt(i);
      sqsum += val * val;
    }

    // Standard deviation
    if (sqsum / vector.size() - mean * mean < 0.0) {
      stdDeviation = 0.0;
    } else {
      stdDeviation = Math.sqrt(sqsum / vector.size() - mean * mean);
    }

    return stdDeviation ;
  }
}
