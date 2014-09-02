package org.uma.jmetal2.encoding.variable;

import org.uma.jmetal2.core.Variable;

import java.util.BitSet;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public interface Binary extends Variable<BitSet> {
  public BitSet getValue() ;
  void setValue(BitSet value) ;
}
