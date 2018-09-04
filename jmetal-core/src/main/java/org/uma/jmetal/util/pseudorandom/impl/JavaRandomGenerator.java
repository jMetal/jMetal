package org.uma.jmetal.util.pseudorandom.impl;

import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

import java.util.Random;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class JavaRandomGenerator implements PseudoRandomGenerator {
  private Random rnd ;
  private long seed ;
  private static final String NAME = "JavaRandomGenerator" ;

  /** Constructor */
  public JavaRandomGenerator() {
    this(System.currentTimeMillis());
  }

  /** Constructor */
  public JavaRandomGenerator(long seed) {
    this.seed = seed ;
    rnd = new Random(seed) ;
  }

  @Override
  public long getSeed() {
    return seed ;
  }

  @Override
  public int nextInt(int lowerBound, int upperBound) {
    return lowerBound + rnd.nextInt((upperBound - lowerBound + 1)) ;
  }

  @Override
  public double nextDouble(double lowerBound, double upperBound) {
    return lowerBound + rnd.nextDouble()*(upperBound - lowerBound) ;
  }

  @Override public double nextDouble() {
    return nextDouble(0.0, 1.0);
  }

  @Override
  public void setSeed(long seed) {
    this.seed = seed ;
    rnd.setSeed(seed);
  }

  @Override
  public String getName() {
    return NAME ;
  }
}
