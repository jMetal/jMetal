package org.uma.jmetal2.encoding.variable.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal2.core.Variable;
import org.uma.jmetal2.encoding.variable.Binary;
import org.uma.jmetal2.encoding.variable.Real;

import java.util.BitSet;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public class SimpleBinary implements Binary {
  private BitSet bits ;

  /** Default constructor */
  public SimpleBinary(int numberOfBits) {
    bits = new BitSet(numberOfBits);

    for (int i = 0; i < numberOfBits; i++) {
      if (PseudoRandom.randDouble() < 0.5) {
        bits.set(i, true);
      } else {
        bits.set(i, false);
      }
    }
  }

  /** Copy constructor */
  public SimpleBinary(SimpleBinary binaryVariable) {
    bits = (BitSet)binaryVariable.bits.clone() ;
  }

  @Override
  public BitSet getValue() {
    return bits;
  }

  @Override
  public void setValue(BitSet bits) {
    this.bits = bits ;
  }

  @Override
  public Variable copy() {
    return new SimpleBinary(this);
  }
}
