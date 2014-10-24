package org.uma.jmetal.util.pseudorandom;

import org.uma.jmetal.util.pseudorandom.impl.JavaRandom;

import java.util.Random;

/**
 * Created by ajnebro on 24/10/14.
 */
public class JMetalRandom {
  private static JMetalRandom instance;
  private PseudoRandomGenerator rnd;

  private JMetalRandom() {
    rnd = new JavaRandom();
  }

  public void setRandomGenerator(PseudoRandomGenerator newRandom) {
    rnd = newRandom ;
  }

  public static JMetalRandom getInstance() {
    if(instance == null) {
      instance = new JMetalRandom();
    }
    return instance;
  }

  public int nextInt(int lowerBound, int upperBound) {
    return rnd.nextInt(lowerBound, upperBound) ;
  }

  public int nextInt() {
    return nextInt(0, 1) ;
  }

  public double nextDouble(double lowerBound, double upperBound) {
    return rnd.nextDouble(lowerBound, upperBound) ;
  }

  public double nextDouble() {
    return nextDouble(0.0, 1.0) ;
  }

  public long nextLong(long lowerBound, long upperBound) {
    return rnd.nextLong(lowerBound, upperBound) ;
  }

  public long nextLong() {
    return nextLong(0, 1) ;
  }

  public float nextFloat(float lowerBound, float upperBound) {
    return rnd.nextFloat(lowerBound, upperBound) ;
  }

  public float nextFloat() {
    return rnd.nextFloat((float)0.0, (float)1.0) ;
  }
}
