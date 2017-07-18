package org.uma.jmetal.util.pseudorandom.impl;

import org.apache.commons.math3.random.MersenneTwister;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class MersenneTwisterGenerator implements PseudoRandomGenerator {
  private MersenneTwister rnd ;
  private long seed ;
  private static final String name = "MersenneTwister" ;

  /** Constructor */
  public MersenneTwisterGenerator() {
    this(System.currentTimeMillis());
  }

  /** Constructor */
  public MersenneTwisterGenerator(long seed) {
    this.seed = seed ;
    rnd = new MersenneTwister(seed) ;
  }

  @Override
  public long getSeed() {
    return seed ;
  }

  @Override
  public int nextInt(int lowerBound, int upperBound) {
    return lowerBound + rnd.nextInt((upperBound - lowerBound) + 1) ;
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
    return name ;
  }
}
