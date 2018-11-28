package org.uma.jmetal.util.pseudorandom;

import java.io.Serializable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface PseudoRandomGenerator extends Serializable {
  public int nextInt(int lowerBound, int upperBound) ;
  public double nextDouble(double lowerBound, double upperBound) ;
  public double nextDouble() ;
  public void setSeed(long seed) ;
  public long getSeed() ;
  public String getName() ;
}
