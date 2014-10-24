package org.uma.jmetal.util.pseudorandom;

import org.uma.jmetal.util.pseudorandom.impl.ApacheRandomUtilsGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

/**
 * Created by ajnebro on 24/10/14.
 */
public class JMetalRandom {
  private static JMetalRandom instance ;
  private PseudoRandomGenerator randomGenerator ;

  private JMetalRandom() {
    randomGenerator = new ApacheRandomUtilsGenerator() ;
  }

  public static JMetalRandom getInstance() {
    if (instance == null) {
      instance = new JMetalRandom() ;
    }
    return instance ;
  }

  public void setRandomGenerator(PseudoRandomGenerator randomGenerator) {
    this.randomGenerator = randomGenerator;
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

  public byte[] nextBytes(int count) {
    return randomGenerator.nextBytes(count) ;
  }
}
