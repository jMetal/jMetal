package org.uma.jmetal3.encoding;

import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal3.core.Solution;

import java.util.BitSet;

/**
 * Created by Antonio on 03/09/14.
 */
public interface BinarySolution extends Solution<BitSet> {
  public int getNumberOfBits() ;
 }
