package org.uma.jmetal.util.pseudorandom.impl;

import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * Extended pseudo random number generator based on the decorator pattern.
 * Two new methods are added: randNormal() and randSphere()
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ExtendedPseudoRandomGenerator implements PseudoRandomGenerator {
  private final PseudoRandomGenerator randomGenerator ;

  public ExtendedPseudoRandomGenerator(PseudoRandomGenerator randomGenerator) {
    this.randomGenerator = randomGenerator ;
  }

  @Override
  public int nextInt(int lowerBound, int upperBound) {
    return randomGenerator.nextInt(lowerBound, upperBound) ;
  }

  @Override
  public double nextDouble(double lowerBound, double upperBound) {
    return randomGenerator.nextDouble(lowerBound, upperBound);
  }

  @Override
  public double nextDouble() {
    return randomGenerator.nextDouble();
  }

  @Override
  public void setSeed(long seed) {
    randomGenerator.setSeed(seed);
  }

  @Override
  public long getSeed() {
    return randomGenerator.getSeed() ;
  }

  @Override
  public String getName() {
    return randomGenerator.getName() + " (extended)";
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
  public double randNormal(double mean, double standardDeviation) {
    double x1, x2, w, y1;

    do {
      x1 = 2.0 * randomGenerator.nextDouble() - 1.0;
      x2 = 2.0 * randomGenerator.nextDouble() - 1.0;
      w = x1 * x1 + x2 * x2;
    } while (w >= 1.0);

    w = Math.sqrt((-2.0 * Math.log(w)) / w);
    y1 = x1 * w;
    y1 = y1 * standardDeviation + mean;
    return y1;
  }

  /**
   * Get a random point from an hypersphere (center = 0, radius = 1)
   * Code taken from Maurice Clerc's implementation
   *
   * @param dimension
   * @return A pseudo random point
   */
  public double[] randSphere(int dimension) {
    int D = dimension;
    double[] x = new double[dimension];

    double length = 0;
    for (int i = 0; i < dimension; i++) {
      x[i] = 0.0;
    }

    // --------- Step 1. Direction

    for (int i = 0; i < D; i++) {
      x[i] = this.randNormal(0, 1);
      length += length + x[i] * x[i];
    }

    length = Math.sqrt(length);

    // --------- Step 2. Random radius

    double r = randomGenerator.nextDouble(0, 1);

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
  public double[] randSphere(int dimension, double center, double radius) {
    int d = dimension;
    double[] x = new double[dimension];

    double length = 0;
    for (int i = 0; i < dimension; i++) {
      x[i] = 0.0;
    }

    // --------- Step 1. Direction

    for (int i = 0; i < d; i++) {
      x[i] = randNormal(0, 1);
      length += length + x[i] * x[i];
    }

    length = Math.sqrt(length);

    // --------- Step 2. Random radius

    double r = randomGenerator.nextDouble(0, 1);

    for (int i = 0; i < d; i++) {
      x[i] = center + radius * r * x[i] / length;
    }

    return x;
  }
}
