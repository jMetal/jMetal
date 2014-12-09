package org.uma.jmetal.util.binarySet;

import java.util.BitSet;

/**
 * Class representing a bit set including a method to get the total number of bits
 * Created by Antonio J. Nebro on 9/12/14.
 */
public class BinarySet extends BitSet {
  private int numberOfBits ;

  public BinarySet(int numberOfBits) {
    super(numberOfBits) ;
    this.numberOfBits = numberOfBits ;
  }

  /**
   * Returns the total number of bits
   * @return
   */
  public int getBinarySetLength() {
    return numberOfBits;
  }
}
