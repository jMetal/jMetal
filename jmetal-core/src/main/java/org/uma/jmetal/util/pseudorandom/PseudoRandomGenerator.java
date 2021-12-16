package org.uma.jmetal.util.pseudorandom;

import java.io.Serializable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface PseudoRandomGenerator extends Serializable {
  int nextInt(int lowerBound, int upperBound) ;
  double nextDouble(double lowerBound, double upperBound) ;
  double nextDouble() ;
  void setSeed(long seed) ;
  long getSeed() ;
  String getName() ;
}
