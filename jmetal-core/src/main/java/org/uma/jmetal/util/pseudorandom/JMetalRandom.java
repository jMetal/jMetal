package org.uma.jmetal.util.pseudorandom;

import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

/**
 * Created by ajnebro on 24/10/14.
 */
public class JMetalRandom {
  private static JMetalRandom instance ;
  private PseudoRandomGenerator randomGenerator ;

  private JMetalRandom() {
    randomGenerator = new JavaRandomGenerator() ;
  }

  public JMetalRandom getInstance() {
    if (instance == null) {
      instance = new JMetalRandom() ;
    }
    return instance ;
  }

  public void setRandomGenerator(PseudoRandomGenerator randomGenerator) {
    this.randomGenerator = randomGenerator ;
  }

  public int nextInt() {
    return nextInt(0, 1) ;
  }

  public int nextInt(int lowerBound, int upperBound) {
    return randomGenerator.nextInt(lowerBound, upperBound) ;
  }

  public double nextDouble() {
    return nextDouble(0.0, 1.0) ;
  }

  public double nextDouble(double lowerBound, double upperBound) {
    return randomGenerator.nextDouble(lowerBound, upperBound) ;
  }

  public long nextLong() {
    return randomGenerator.nextLong(0, 1) ;
  }

  public long nextLong(long lowerBound, long upperBound) {
    return randomGenerator.nextLong(lowerBound, upperBound) ;
  }

  public float nextFloat() {
    return randomGenerator.nextFloat((float)0.0, (float)1.0) ;
  }

  public float nextFloat(float lowerBound, float upperBound) {
    return randomGenerator.nextFloat(lowerBound, upperBound) ;
  }

  public byte[] nextBytes(int count) {
    return randomGenerator.nextBytes(count) ;
  }
}
