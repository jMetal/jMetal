package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.problem.BinaryProblem;

import java.util.BitSet;

public abstract class BinaryProblemImpl<S extends Solution<? extends BitSet>> extends GenericProblemImpl<S>
  implements BinaryProblem<S> {

  protected int [] bitsPerVariable ;

  public int getNumberOfBits(int index) {
    return bitsPerVariable[index] ;
  }

}
