//  PseudoRandom.java
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

package org.uma.jmetal.util.random;

import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

/**
 * Class representing a pseudo-random number generator
 */
public class PseudoRandom {

  /**
   * generator used to obtain the random values
   */
  private static IRandomGenerator random_ = null;
  private static RandomGenerator defaultGenerator_ = new RandomGenerator();

  /**
   * Constructor.
   * Creates a new instance of PseudoRandom.
   */
  private PseudoRandom() {
    if (random_ == null) {
      //this.random = new java.util.Random((long)seed);
      random_ = new RandomGenerator();
    }
  }

  public static void setRandomGenerator(IRandomGenerator generator) {
    random_ = generator;
  }

  /**
   * Returns a random int value using the Java random generator.
   *
   * @return A random int value.
   */
  public static int randInt() {
    if (random_ == null) {
      random_ = defaultGenerator_;
    }
    return random_.nextInt(Integer.MAX_VALUE);
  }

  /**
   * Returns a random double value using the PseudoRandom generator.
   * Returns A random double value.
   */
  public static double randDouble() {
    if (random_ == null) {
      random_ = defaultGenerator_;
    }
    return random_.nextDouble();
  }

  /**
   * Returns a random int value between a minimum bound and maximum bound using
   * the PseudoRandom generator.
   *
   * @param minBound The minimum bound.
   * @param maxBound The maximum bound.
   *                 Return A pseudo random int value between minBound and maxBound.
   */
  public static int randInt(int minBound, int maxBound) {
    if (random_ == null) {
      random_ = defaultGenerator_;
    }
    return minBound + random_.nextInt(maxBound - minBound);
  }

  /**
   * Returns a random double value between a minimum bound and a maximum bound
   * using the PseudoRandom generator.
   *
   * @param minBound The minimum bound.
   * @param maxBound The maximum bound.
   * @return A pseudo random double value between minBound and maxBound
   */
  public static double randDouble(double minBound, double maxBound) {
    if (random_ == null) {
      random_ = defaultGenerator_;
    }
    return minBound + random_.nextDouble() * (maxBound - minBound);
  }

  /**
   * Use the polar form of the Box-Muller transformation to obtain
   * a pseudo random number from a Gaussian distribution
   * Code taken from Maurice Clerc's implementation
   *
   * @param mean
   * @param standardDeviation
   * @return A pseudo random number
   */
  public static double randNormal(double mean, double standardDeviation) {
    double x1, x2, w, y1;

    do {
      x1 = 2.0 * randDouble() - 1.0;
      x2 = 2.0 * randDouble() - 1.0;
      w = x1 * x1 + x2 * x2;
    } while (w >= 1.0);

    w = Math.sqrt((-2.0 * Math.log(w)) / w);
    y1 = x1 * w;
    y1 = y1 * standardDeviation + mean;
    return y1;
  }


  /**
   * Ger a random point from an hypersphere (center = 0, radius = 1)
   * Code taken from Maurice Clerc's implementation
   *
   * @param dimension
   * @return A pseudo random point
   */
  public static double[] randSphere(int dimension) {
    int D = dimension;
    double[] x = new double[dimension];

    double length = 0;
    for (int i = 0; i < dimension; i++) {
      x[i] = 0.0;
    }

    // --------- Step 1. Direction

    for (int i = 0; i < D; i++) {
      x[i] = randNormal(0, 1);
      length += length + x[i] * x[i];
    }

    length = Math.sqrt(length);

    // --------- Step 2. Random radius

    double r = PseudoRandom.randDouble(0, 1);

    for (int i = 0; i < D; i++) {
      x[i] = r * x[i] / length;
    }

    return x;
  }

  /**
   * Ger a random point from an hypersphere
   * Code taken from Maurice Clerc's implementation
   *
   * @param center
   * @param radius
   * @return A pseudo random number
   */
  public static double[] randSphere(int dimension, double center, double radius) {
    int D = dimension;
    double[] x = new double[dimension];

    double length = 0;
    for (int i = 0; i < dimension; i++) {
      x[i] = 0.0;
    }

    // --------- Step 1. Direction

    for (int i = 0; i < D; i++) {
      x[i] = randNormal(0, 1);
      length += length + x[i] * x[i];
    }

    length = Math.sqrt(length);

    // --------- Step 2. Random radius

    double r = PseudoRandom.randDouble(0, 1);

    for (int i = 0; i < D; i++) {
      x[i] = center + radius * r * x[i] / length;
    }

    return x;
  }


  public static void main(String[] args) throws JMetalException {
    int numberOfPoints;
    String fileName;

    if (args.length != 2) {
      throw new JMetalException("Usage: PseudoRandom numberOfPoints outputFileName");
    }
    numberOfPoints = Integer.valueOf(args[0]);
    fileName = args[1];

    try {
      FileOutputStream fos = new FileOutputStream(fileName);
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      BufferedWriter bw = new BufferedWriter(osw);

      double[] x;
      for (int i = 0; i < numberOfPoints; i++) {
        x = randSphere(2);
        bw.write("" + (0 + 500 * x[0]) + " " + (0 + 500 * x[1]));
        bw.newLine();
      }

      bw.close();
    } catch (IOException e) {
      Configuration.logger.log(Level.SEVERE, "Error acceding to the file", e);
    }

  }
}
