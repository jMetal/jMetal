package org.uma.jmetal.encoding;

import org.uma.jmetal.core.Solution;

import java.util.BitSet;

/**
 * Created by Antonio on 03/09/14.
 */
public interface BinarySolution extends Solution<BitSet> {
  public int getNumberOfBits(int index) ;
  public int getTotalNumberOfBits() ;
 }
